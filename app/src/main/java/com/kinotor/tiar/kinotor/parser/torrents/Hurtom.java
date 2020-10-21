package com.kinotor.tiar.kinotor.parser.torrents;

import android.os.AsyncTask;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemTorrent;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskTorrentCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLEncoder;

/**
 * Created by Tiar on 02.2018.
 */

public class Hurtom extends AsyncTask<Void, Void, Void> {
    private String title;
    private ItemHtml item;
    private ItemTorrent torrent;
    private OnTaskTorrentCallback callback;

    public Hurtom(ItemHtml item, OnTaskTorrentCallback callback) {
        this.item = item;
        this.callback = callback;

        torrent = new ItemTorrent();
        if (item.getSubTitle(0).toLowerCase().contains("error"))
            title = item.getTitle(0).trim();
        else title = item.getSubTitle(0).trim();
        if (title.contains("("))
            title = title.split("\\(")[0].trim();
        if (title.contains("["))
            title = title.split("\\[")[0].trim();
        title = title.replace("э", "е");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(torrent);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        parseBitru(Getdata(title));
        return null;
    }

    private void parseBitru(Document data) {
        if (data != null) {
            if (data.html().contains("class=\"prow1\"")) {
                Elements list = data.select(".prow1");
                for (Element entry : list) {
                    String title = "error";
                    String description = "error";
                    String sid = "error";
                    String lich = "error";
                    String source = "Hurtom";
                    String type = "error";
                    String size = "error";
                    String link = "error";
                    String linkMagnet = "error";
                    String linkTorrent = "error";

                    if (entry.html().contains("topictitle genmed")) {
                        title = entry.select(".topictitle.genmed").text();
                        link  = Statics.HURTOM_URL + "/" + entry.select(".topictitle.genmed a").attr("href");
                    }
                    if (entry.html().contains("gensmall"))
                        size = entry.selectFirst(".gensmall").text();
                    if (entry.html().contains("Seeders"))
                        sid = entry.select("td[title^='Seeders']").text();
                    else sid = "0";
                    if (entry.html().contains("Завантажують"))
                        lich = entry.select("td[title^='Завантажують']").text();
                    else lich = "0";
                    linkTorrent = "parse hurtom";

                    if (size.contains("MB")){
                        if (size.contains("."))
                            size = size.split("\\.")[0].trim();
                        else size = size.split("MB")[0].trim();

                        float s = Float.parseFloat(size)/1000;
                        size = String.format("%.2f", s) + " GB";
                    }
                    size = size.replace(",", ".");


                    if ((" "+title+" ").toLowerCase().contains((" "+this.title+" ").toLowerCase()) &&
                            !title.trim().equals("error") && !title.isEmpty()) {
                        torrent.setTorTitle(title);
                        torrent.setTorUrl(linkTorrent);
                        torrent.setUrl(link);
                        torrent.setTorSize(size.trim());
                        torrent.setTorMagnet(linkMagnet);
                        torrent.setTorSid(sid.trim());
                        torrent.setTorLich(lich.trim());
                        torrent.setTorContent(source);
                    }
                }
            }
        }
    }

    private Document Getdata(String title) {
        try {
            String n = URLEncoder.encode(title, "UTF-8");
            String url = Statics.HURTOM_URL + "/tracker.php?nm=" + n;
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}