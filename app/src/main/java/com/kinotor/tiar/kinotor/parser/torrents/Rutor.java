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

public class Rutor extends AsyncTask<Void, Void, Void> {
    private String title, torS;
    private ItemHtml item;
    private ItemTorrent torrent;
    private OnTaskTorrentCallback callback;
    String title_ru;
    String title_en;
    String date;

    public Rutor(ItemHtml item, OnTaskTorrentCallback callback) {
        this.item = item;
        this.callback = callback;

        torS = Statics.torS;
        torrent = new ItemTorrent();
        if (item.getSubTitle(0).toLowerCase().contains("error"))
            title = item.getTitle(0).trim();
        else title = item.getSubTitle(0).trim();
        if (!item.getDate(0).toLowerCase().contains("error") && !item.getDate(0).contains(".")) {
            if (title.contains("("))
                title = title.split("\\(")[0].trim();
            if (title.contains("["))
                title = title.split("\\[")[0].trim();
            title = title+" "+ item.getDate(0).trim();
        }

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
        parseRutor(Getdata(n));
        return null;
    }

    private void parseRutor(Document data) {
        if (data != null) {
            if (data.html().contains("index")) {
                Elements list = data.select("#index tr");
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

                    source = "Rutor";
                    if (entry.html().contains("/torrent/")) {
                        title = entry.select("a[href^='/torrent/']").text();
                        link  = Statics.RUTOR_URL + entry.select("a[href^='/torrent/']").attr("href");
                    }

                    if (entry.html().contains("align=\"right\"")) {
                        size = entry.select("td[align='right']").last().text();
                        description = entry.select("td").first().text();
                    }
                    if (link.contains("/torrent/")) {
                        linkTorrent = Statics.RUTOR_URL + "/download/" + link.split("/torrent/")[1].split("/")[0];
                        //linkTorrent = linkTorrent.replace("http://d.rutor.info", Statics.urlRutor)
                    }
                    if (entry.html().contains("magnet:")) {
                        linkMagnet = entry.select("a[href^='magnet:']").attr("href");
                    } else linkMagnet = "error";
                    //else "http://tparser.org/magnet.php?t=12" + link.split("torrent/")[1].split("/")[0]

                    if (entry.html().contains("green"))
                        sid = entry.select(".green").text().trim();
                    if (entry.html().contains("red"))
                        lich = entry.select(".red").text().trim();
                    size = size.replace(",",".");
                    if (size.contains("MB")){
                        if (size.contains("."))
                            size = size.split("\\.")[0].trim();
                        else size = size.split("MB")[0].trim();

                        float s = Float.parseFloat(size)/1000;
                        size = String.format("%.2f", s) + " GB";
                    }
                    size = size.replace(",", ".");

//                    Log.e("test", "parseRutor: "+title );
//                    Log.e("test", "parseRutor: "+this.title+item.getDate(0) );

//                    String tt = this.title.replace(item.getDate(0),"").trim();

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

            String url = Statics.RUTOR_URL + "/search/0/0/100/1/" + n.replace("%26", "AND");
            Log.d("Rutor", "Getdata: "+url);
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}