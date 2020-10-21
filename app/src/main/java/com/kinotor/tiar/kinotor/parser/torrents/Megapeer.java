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

public class Megapeer extends AsyncTask<Void, Void, Void> {
    private String title, curtitle, torS = "";
    private ItemHtml item;
    private ItemTorrent torrent;
    private OnTaskTorrentCallback callback;
    String title_ru;
    String title_en;
    String date;

    public Megapeer(ItemHtml item, OnTaskTorrentCallback callback) {
        this.item = item;
        this.callback = callback;
//        torS = Statics.torS;
        torrent = new ItemTorrent();
        if (item.getSubTitle(0).toLowerCase().contains("error"))
            curtitle = item.getTitle(0).trim();
        else curtitle = item.getSubTitle(0).trim();
        if (curtitle.contains("("))
            curtitle = curtitle.split("\\(")[0].trim();
        if (curtitle.contains("["))
            curtitle = curtitle.split("\\[")[0].trim();
        title = curtitle + " " + item.getDate(0);


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
        parseMegapeer(Getdata(n));
        return null;
    }

    private void parseMegapeer(Document data) {
        if (data != null) {
            if (data.html().contains("tor-tbl")) {
                Elements list = data.select("#tor-tbl tr");
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

                    source = "Megapeer";
                    if (entry.html().contains("t-title")) {
                        title = entry.select(".t-title a").text();
                        link  = Statics.MEGAPEER_URL + "/" + entry.select(".t-title a").attr("href");
                    }
//                    if (entry.html().contains("ellips"))
//                        description = entry.select(".ellips span").last().text();
//                    if (entry.html().contains("b-tmp"))
//                        type = entry.select(".b-tmp").attr("href");
                    if (entry.html().contains("gr-button tr-dl dl-stub")) {
                        size = entry.select(".gr-button.tr-dl.dl-stub").text();
                        linkTorrent = Statics.MEGAPEER_URL + "/" + entry.select(".gr-button.tr-dl.dl-stub").attr("href");
                    }
                    if (entry.html().contains("seedmed"))
                        sid = entry.select(".seedmed").text();
                    else sid = "0";
                    lich = "0";
                    linkMagnet = "parse";
//                    if (link.contains("?id="))
//                        linkTorrent = Statics.BITRU_URL + "/download.php?id=" +
//                                link.split("\\?id=")[1];
//                    if (type.contains("?tmp="))
//                        type = type.split("\\?tmp=")[1];

                    if (size.contains("MB")){
                        if (size.contains("."))
                            size = size.split("\\.")[0].trim();
                        else size = size.split("MB")[0].trim();

                        float s = Float.parseFloat(size)/1000;
                        size = String.format("%.2f", s) + " GB";
                    }
                    size = size.replace(",", ".");


//                    boolean t = (" "+title+" ").toLowerCase().contains((" "+this.title_ru+" ").toLowerCase()) ||
//                            (" "+title+" ").toLowerCase().contains((" "+this.title_en+" ").toLowerCase());
//                    if (t) {
                    if (!title.trim().equals("error") && !title.isEmpty()) {
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
            String n = URLEncoder.encode(title.replace("error","").trim(), "UTF-8");

            String url = Statics.MEGAPEER_URL + "/browse.php?search=" + n;
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}