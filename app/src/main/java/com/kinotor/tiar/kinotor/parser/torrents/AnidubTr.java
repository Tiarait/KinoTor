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

public class AnidubTr extends AsyncTask<Void, Void, Void> {
    private String title, torS;
    private ItemHtml item;
    private ItemTorrent torrent;
    private OnTaskTorrentCallback callback;
    String title_ru;
    String title_en;
    String date;

    public AnidubTr(ItemHtml item, OnTaskTorrentCallback callback) {
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
        parseAnidub(postData(n));
        return null;
    }

    private void parseAnidub(Document data) {
        if (data != null) {
            if (data.html().contains("search_post")) {
                Elements list = data.select(".search_post");
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

                    source = "Anidub";
                    if (entry.html().contains("h2")) {
                        title = entry.select("h2 a").text();
                        link  = entry.select("h2 a").attr("href");
                    }
                    if (title.contains("/")) {
                        String t1 = title.split("/")[0].toLowerCase().trim();
                        String t2 = title.split("/")[1].toLowerCase().trim();
                        if (t1.contains("[")) {
                            t1 = t1.split("\\[")[0].trim();
                        }
                        if (t2.contains("[")) {
                            t2 = t2.split("\\[")[0].trim();
                        }
                        if (t1.equals(title_ru.toLowerCase()) || t1.equals(title_en.toLowerCase()) ||
                                t2.equals(title_ru.toLowerCase()) || t2.equals(title_en.toLowerCase())) {
                            Document doc = getData(link);
                            if (doc != null) {
                                if (doc.html().contains("_info")) {
                                    Elements listtor = doc.select("div[id$='_info']");
                                    for (Element entrytor : listtor) {
                                        Log.e("an", entrytor.html());
                                        if (entrytor.html().contains("torrent_h"))
                                            linkTorrent = Statics.ANIDUB_TR_URL +
                                                    entrytor.select(".torrent_h a").attr("href");
                                        if (entrytor.html().contains("list torrentname"))
                                            title = entrytor.select(".list.torrentname").text()
                                                    .replace("Имя файла:", "").trim();
                                        if (entrytor.html().contains("list down"))
                                            size = entrytor.select(".list.down").text();
                                        if (size.contains("Размер:"))
                                            size = size.split("Размер:")[1].trim().split(" ")[0].trim() + " GB";
                                        else size = "error";

                                        if (entrytor.html().contains("li_distribute_m"))
                                            sid = entrytor.select(".li_distribute_m").text();
                                        else sid = "0";
                                        if (entrytor.html().contains("li_swing_m"))
                                            lich = entrytor.select(".li_swing_m").text();
                                        else lich = "0";

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
                                } else Log.e("anidub_tr", "torrent null");
                            } else Log.e("anidub_tr", "data null");
                        }
                    } else Log.e("anidub_tr", "title-"+title);
                }
            }
        }
    }

    private Document postData(String title) {
        try {
            String n = title;
            Log.e("anidub_tr", n);
            return Jsoup.connect(Statics.ANIDUB_TR_URL)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Cookie", Statics.ANIDUB_TR_COOCKIE.replace(",",";")+";")
                    .data("do", "search")
                    .data("subaction", "search")
                    .data("story", n)
                    .timeout(10000).ignoreContentType(true).post();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private Document getData(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .header("Cookie", Statics.ANIDUB_TR_COOCKIE.replace(",",";")+";")
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}