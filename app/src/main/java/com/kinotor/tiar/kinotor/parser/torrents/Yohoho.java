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
 * Created by Tiar on 08.2018.
 */

public class Yohoho extends AsyncTask<Void, Void, Void> {
    private String title, torS;
    private ItemHtml item;
    private ItemTorrent torrent;
    private OnTaskTorrentCallback callback;
    String title_ru;
    String title_en;
    String date;

    public Yohoho(ItemHtml item, OnTaskTorrentCallback callback) {
        this.item = item;
        this.callback = callback;

        torS = Statics.torS;
        torrent = new ItemTorrent();
        if (item.getSubTitle(0).toLowerCase().contains("error"))
            title = item.getTitle(0).trim();
        else title = item.getSubTitle(0).trim();

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
        
        parseYohoho(Getdata(n));
        return null;
    }

    private void parseYohoho(Document data) {
        if (data != null) {
            if (data.html().contains("class=\"torrent\"")) {
                Elements all = data.select("tr");
                for (Element tor : all) {
                    String magnet = "error", name = "error", size = "error";
                    boolean check;
                    String sid = "-";
                    String lich = "-";
                    if (tor.html().contains("magnet:?xt=urn:btih"))
                        magnet = tor.select("a[href^='magnet:?xt=urn:btih']").attr("href");
                    if (tor.html().contains("class=\"td-btn\""))
                        name = tor.select(".td-btn").text();
                    if (tor.html().contains("text-muted text-center"))
                        size = tor.select(".text-muted.text-center").last().text();

                    size = size.replace("&nbsp", " ").replace("\u00A0"," ")
                    .replace("ГБ", "GB");


                    if (size.contains("МБ")){
                        if (size.contains("."))
                            size = size.split("\\.")[0].trim();
                        else size = size.split("МБ")[0].trim();

                        float s = Float.parseFloat(size)/1000;
                        size = String.format("%.2f", s) + " GB";
                    }
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
                        torrent.setTorTitle(name.replace("Скачать", ""));
                        torrent.setTorUrl("error");
                        torrent.setUrl("error");
                        torrent.setTorSize(size.trim());
                        torrent.setTorMagnet(magnet);
                        torrent.setTorSid(sid.trim());
                        torrent.setTorLich(lich.trim());
                        torrent.setTorContent("yohoho");
                    }
                }
            }
        }
    }

    private Document Getdata(String title) {
        title = title.trim();

        try {
            String n = URLEncoder.encode(title.replace("error","").trim(), "UTF-8");

            String url = "https://4h0y.yohoho.cc/?title=" + n;
            return Jsoup.connect(url)
                    .validateTLSCertificates(false)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}