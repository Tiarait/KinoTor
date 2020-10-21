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

import java.net.URLEncoder;

/**
 * Created by Tiar on 02.2018.
 */

public class Kinozal extends AsyncTask<Void, Void, Void> {
    private String title, torS;
    private ItemHtml item;
    private ItemTorrent torrent;
    private OnTaskTorrentCallback callback;
    String title_ru;
    String title_en;
    String date;

    public Kinozal(ItemHtml item, OnTaskTorrentCallback callback) {
        this.item = item;
        this.callback = callback;

        torS = Statics.torS;
                
        torrent = new ItemTorrent();
        title = item.getTitle(0).trim();
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
        
        parse(Getdata(n));
        return null;
    }

    private void parse(Document data) {
        if (data != null) {
//            Log.d("tr", "parseBa3a: 0");
            if (data.html().contains("class=\"t_peer w100p")) {
//                Log.d("tr", "parseBa3a: 1" + data.select(".lista"));

                Elements list = data.select(".t_peer.w100p tr");
                for (Element entry : list) {
                    String title = "error";
                    String sid = "error";
                    String lich = "error";
                    String source = "Kinozal";
                    String size = "error";
                    String link = "error";
                    String linkMagnet = "error";
                    String linkTorrent = "error";

//                    Log.e("kinozal", "parseBa3a: "+entry.html() );
//                    Log.e("kinozal", "parseBa3a00: "+entry.text() );
                    if (entry.html().contains("class=\"nam")) {
                        title = entry.selectFirst(".nam a").text();
                        link = Statics.KINOZAL_URL + entry.selectFirst(".nam a").attr("href");
                    }
                    linkMagnet = "parse kinozal";

                    if (entry.html().contains("sl_s"))
                        sid = entry.select(".sl_s").text().replace("\u00A0", " ").trim();
                    if (entry.html().contains("sl_p"))
                        lich = entry.select(".sl_p").text().replace("\u00A0", " ").trim();

                    for (Element s : entry.select(".s")) {
                        if (s.text().contains("ГБ") || s.text().contains("МБ") || s.text().contains("MБ"))
                            size = s.text().trim();
                    }

                    if (size.contains("MБ")) {
                        if (size.contains("."))
                            size = size.split("\\.")[0].trim();
                        else size = size.split("MБ")[0].trim();

                        float s = Float.parseFloat(size) / 1000;
                        size = String.format("%.2f", s) + " GB";
                    }
                    if (size.contains("МБ")) {
                        if (size.contains("."))
                            size = size.split("\\.")[0].trim();
                        else size = size.split("МБ")[0].trim();

                        float s = Float.parseFloat(size) / 1000;
                        size = String.format("%.2f", s) + " GB";
                    }
                    size = size.replace("ГБ", "GB").replace(",", ".").trim();

//                    boolean t = (" "+title+" ").toLowerCase().contains((" "+this.title_ru+" ").toLowerCase()) ||
//                            (" "+title+" ").toLowerCase().contains((" "+this.title_en+" ").toLowerCase());
//                    if (t) {
                    if (!title.equals("error") && !title.isEmpty()) {
                        if (!title.contains("error")) {
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
        } else Log.d("кинозал", "error data");
    }

    private Document Getdata(String title) {
        try {
            String n = URLEncoder.encode(title.replace("error","").trim(), "UTF-8");

            String url = Statics.KINOZAL_URL + "/browse.php?s=" + n;
            Log.e("test", "Getdata: "+url );
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}