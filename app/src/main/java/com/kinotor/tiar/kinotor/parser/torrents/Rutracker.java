package com.kinotor.tiar.kinotor.parser.torrents;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemTorrent;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskTorrentCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Tiar on 02.2018.
 */

public class Rutracker extends AsyncTask<Void, Void, Void> {
    private String title, torS;
    private ItemHtml item;
    private ItemTorrent torrent;
    private OnTaskTorrentCallback callback;
    String title_ru;
    String title_en;
    String date;

    public Rutracker(ItemHtml item, OnTaskTorrentCallback callback) {
        this.item = item;
        this.callback = callback;

        torS = Statics.torS;
        torrent = new ItemTorrent();
        if (item.getSubTitle(0).toLowerCase().contains("error"))
            title = item.getTitle(0).trim();
        else title = item.getSubTitle(0).trim();
        if (title.contains("("))
            title = title.split("\\(")[0].trim();
        if (title.contains("["))
            title = title.split("\\[")[0].trim();

        this.title_ru = item.getTitle(0).trim();
        this.title_en = item.getSubTitle(0).trim();
        this.date = item.getDate(0).trim();
        if (title_ru.contains("("))
            title_ru = title_ru.split("\\(")[0].trim();
        if (title_ru.contains("["))
            title_ru = title_ru.split("\\[")[0].trim();

        if (title_en.contains("("))
            title_en = title_en.split("\\(")[0].trim();
        if (title_en.contains("["))
            title_en = title_en.split("\\[")[0].trim();

        if (date.contains("."))
            date = date.split("\\.")[date.split("\\.").length - 1];
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(torrent);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String n;
        if (torS.contains("title") || torS.contains("orig") || torS.contains("year")) {
            n = "";
            if (torS.contains("title") && !this.title_ru.contains("error"))
                n += this.title_ru;
            if (torS.contains("orig") && !this.title_en.contains("error"))
                n += " " + this.title_en;
            if (torS.contains("year") && !this.date.contains("error"))
                n += " " + this.date;
        } else n = title;
        parseBitru(Getdata(n));
        return null;
    }

    private void parseBitru(Document data) {
        if (data != null) {
            if (data.html().contains("tCenter hl-tr")) {
                Elements list = data.select(".tCenter.hl-tr");
                for (Element entry : list) {
                    String title = "error";
                    String description = "error";
                    String sid = "error";
                    String lich = "error";
                    String source = "error";
                    String type = "error";
                    String size = "error";
                    String link = "error";
                    String linkMagnet = "error";
                    String linkTorrent = "error";

                    source = "Rutracker";
                    if (entry.html().contains("med tLink")) {
                        title = entry.select(".med.tLink").text();
                        link  = Statics.RUTRACKER_URL + "/forum/" + entry.select(".med.tLink.hl-tags.bold")
                                .attr("href");
                    }
                    if (entry.html().contains("small tr-dl dl-stub"))
                        size = entry.select(".small.tr-dl.dl-stub").text().replace("&nbsp;","").trim();
//                    if (entry.html().contains("small tr-dl dl-stub"))
//                        linkTorrent = Statics.RUTRACKER_URL + "/forum/" + entry.select(".small.tr-dl.dl-stub")
//                                .attr("href");
                    linkMagnet = "parse rutracker";

                    if (entry.html().contains("seedmed"))
                        sid = entry.select(".seedmed").text();
                    else sid = "0";
                    if (entry.html().contains("row4 leechmed"))
                        lich = entry.select(".row4.leechmed").text();
                    else lich = "0";

                    if (size.contains("MB")){
                        if (size.contains("."))
                            size = size.split("\\.")[0].trim();
                        else size = size.split("MB")[0].trim();

                        float s = Float.parseFloat(size)/1000;
                        size = String.format("%.2f", s) + " GB";
                    }
                    size = size.replace(",", ".");

                    if (!title.trim().equals("error") && !title.isEmpty()) {
                        torrent.setTorTitle(title);
                        torrent.setTorUrl(linkTorrent);
                        torrent.setUrl(link);
                        torrent.setTorSize(size.trim());
                        torrent.setTorMagnet(linkMagnet);
                        torrent.setTorSid(sid.trim());
                        torrent.setTorLich(lich.trim());
                        torrent.setTorContent(source);
                    } else Log.e("Rutracker", "no title");
                }
            } else Log.e("Rutracker", "tCenter hl-tr");
        } else Log.e("Rutracker", "null");
    }

    private Document Getdata(String title) {
        try {
            String n = title.replace("error","").trim();

            String url = Statics.RUTRACKER_URL + "/forum/tracker.php?nm="+n;
            Log.e("ru", "- "+url);
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .header("Cookie", Statics.RUTRACKER_COOCKIE.replace(",",";")+";")
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}