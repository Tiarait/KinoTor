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

public class NNM extends AsyncTask<Void, Void, Void> {
    private String title, torS;
    private String magnet = "error";
    private ItemHtml item;
    private ItemTorrent torrent;
    private OnTaskTorrentCallback callback;
    String title_ru;
    String title_en;
    String date;

    public NNM(ItemHtml item, OnTaskTorrentCallback callback) {
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
        parse(Getdata(n));
        return null;
    }

    private void parse(Document data) {
        if (data != null) {
            if (data.html().contains("pline")) {
                Elements list = data.select(".pline tr");
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

                    source = "NNM";
                    if (entry.html().contains("<a")) {
                        title = entry.select("a").first().text();
                        link = "https:" + entry.select("a").first().attr("href");
                        size = entry.select("td").get(1).text();

                        sid = entry.select("td").get(2).text();
                        lich = entry.select("td").get(3).text();
                        linkMagnet = Statics.NNM_URL + entry.select("a[href^='/magnet']").attr("href");
                    }

                    if (size.contains("MB")){
                        if (size.contains("."))
                            size = size.split("\\.")[0].trim();
                        else size = size.split("MB")[0].trim();

                        float s = Float.parseFloat(size)/1000;
                        size = String.format("%.2f", s) + " GB";
                    }
                    size = size.replace(",", ".");


                    if (!title.equals("error") && !title.isEmpty()) {
                        torrent.setTorTitle(title);
                        torrent.setTorUrl(linkTorrent);
                        torrent.setUrl(link);
                        torrent.setTorSize(size.trim());
                        torrent.setTorMagnet(linkMagnet);
                        torrent.setTorSid(sid.trim());
                        torrent.setTorLich(lich.trim());
                        torrent.setTorContent(source);
                    } else Log.e("test", "parse: no tor");
                }
            } else if (data.html().contains("tablesorter")) {
                Elements list = data.select(".tablesorter tr");
                for (Element entry : list) {
                    String title = "error";
                    String description = "error";
                    String sid = "error";
                    String lich = "error";
                    String source = "NNM";
                    String type = "error";
                    String size = "error";
                    String link = "error";
                    String linkMagnet = "error";
                    String linkTorrent = "error";

                    if (entry.html().contains("genmed topictitle")) {
                        title = entry.select(".genmed.topictitle").first().text();
                        link = Statics.NNM_URL + "/forum/" + entry.select(".genmed.topictitle").first().attr("href");
                    }

//                    if (entry.html().contains("gensmall opened")) {
//                        source += " " + entry.select(".gensmall.opened").first().text().trim();
//                    }
                    if (entry.html().contains("gensmall")) {
                        for (Element e: entry.select(".gensmall")) {
                            if (e.text().contains("GB") || e.text().contains("MB")) {
                                size = e.text().trim()
                                        .replace(" G", "G")
                                        .replace(" M", "M");
                                if (size.contains(" "))
                                    size = size.split(" ")[1];
                                break;
                            }
                        }
                    }
                    if (entry.html().contains("seedmed"))
                        sid = entry.select(".seedmed").first().text();
                    if (entry.html().contains("leechmed"))
                        lich = entry.select(".leechmed").first().text();
                    if (entry.html().contains("download.php?"))
                        linkTorrent = Statics.NNM_URL + "/forum/" + entry.select("a[href^='download.php?']").attr("href");

                    size = size.replace(",", ".");


//                    if (!sid.trim().equals("0") && (" "+title+" ").toLowerCase().contains((" "+this.title+" ").toLowerCase())) {
//                        GetLocation location = new GetLocation(linkTorrent, location1 -> magnet = location1);
//                        location.execute();
//                        if (!magnet.startsWith("http:") && !magnet.startsWith("https:"))
//                            magnet = "http:" + magnet;
                    if (!title.equals("error") && !title.isEmpty()) {
                        if (size.contains("MB")){
                            if (size.contains("."))
                                size = size.split("\\.")[0].trim();
                            else size = size.split("MB")[0].trim();

                            float s = Float.parseFloat(size)/1000;
                            size = String.format("%.2f", s) + " GB";
                        }
                        torrent.setTorTitle(title);
                        torrent.setTorUrl(linkTorrent);
                        torrent.setUrl(link);
                        torrent.setTorSize(size.trim());
                        torrent.setTorMagnet(linkMagnet);
                        torrent.setTorSid(sid.trim());
                        torrent.setTorLich(lich.trim());
                        torrent.setTorContent(source);
                    } else Log.e("test", "parse: no tor");
                }
            } else Log.e("test", "parse: "+data.html());
        } else Log.e("test", "parse null data "+Statics.NNM_URL);
    }

    private Document Getdata(String title) {
        try {
            String n = URLEncoder.encode(title.replace("error","").trim(), "UTF-8");

            String url = Statics.NNM_URL;
            if (url.contains("searchtor")){
                url = Statics.NNM_URL + "/r/" + n;
            } else if (url.contains("nnm-club")) {
                url = Statics.NNM_URL + "/forum/tracker.php?nm=" + n;
            }
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}