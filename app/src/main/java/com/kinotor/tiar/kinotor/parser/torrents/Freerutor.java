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

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class Freerutor extends AsyncTask<Void, Void, Void> {
    private ItemHtml item;
    private ItemTorrent torrent;
    private OnTaskTorrentCallback callback;
    String title_ru, torS;
    String title_en;
    String date;

    public Freerutor(ItemHtml item, OnTaskTorrentCallback callback) {
        this.item = item;
        this.callback = callback;

        torS = Statics.torS;
        torrent = new ItemTorrent();

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
        String title;
        if (item.getSubTitle(0).toLowerCase().contains("error"))
            title = item.getTitle(0).trim();
        else {
            if (item.getTitle(0).contains("("))
                title = item.getTitle(0).split("\\(")[0].trim();
            else title = item.getTitle(0).trim();
        }
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
        FreeRutorMe(getData(n));
        return null;
    }

    private void FreeRutorMe(Document data) {
        if (data != null) {
            Elements allEntries = data.select(".fr_viewn_in.fr_viewn_new");
            for (Element entry : allEntries) {
                String category = "error";
                if (entry.html().contains("lastcat fr_bor"))
                    category = entry.select(".lastcat.fr_bor a").first().attr("href");
                if (category.contains("filmy") || category.contains("mult") || category.contains("serialy") ||
                        category.contains("tv")){
                    if (entry.html().contains("titlelast fr_bor fr_borleft")){
                        String name = entry.select(".titlelast a").attr("title");
                        String sid = entry.select(".frsl_s").text().trim();
                        String size = entry.select(".frs.fr_bor.fr_borleft").text();
//                        Log.d(TAG, "FreeRutorMe: "+ item.getTitle(0) + "|" + name + " " + sid);
                        if (size.contains("MB")){
                            if (size.contains("."))
                                size = size.split("\\.")[0].trim();
                            else size = size.split("MB")[0].trim();

                            float s = Float.parseFloat(size)/1000;
                            size = String.format("%.2f", s) + " GB";
                        }
                        size = size.replace(",", ".");

                        if (!name.trim().equals("error") && !name.isEmpty()) {
                            torrent.setTorTitle(name);
                            torrent.setTorSize(size);
                            torrent.setTorSid(sid);
                            torrent.setTorLich(entry.select(".frsl_p").text());
                            torrent.setTorContent("freerutor");
                            torrent.setTorUrl("error");
                            torrent.setUrl(entry.select(".titlelast a").attr("href"));
                            torrent.setTorMagnet("parse");
                        }
                    }
                }
            }
        }
    }

    private Document getData(String title) {
        try {
            String n = URLEncoder.encode(title.replace("error","").trim(), "UTF-8");

//            Log.e("test", "getData: "+Statics.FREERUTOR_URL + "/?do=search&subaction=search&story=" + n);
            Document htmlDoc = Jsoup.connect(Statics.FREERUTOR_URL + "/?do=search&subaction=search&story=" + n)
                    .validateTLSCertificates(false)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(10000).ignoreContentType(true).get();
            Log.d(TAG, "Getdata: FreeRutorMe " + title);
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "Getdata: FreeRutorMe error " + title);
            e.printStackTrace();
            return null;
        }
    }
}
