package com.kinotor.tiar.kinotor.parser.video.anidub;

import android.os.AsyncTask;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskUrlCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class AnidubUrl extends AsyncTask<Void, Void, Void> {
    private String url, core;
    private String[] quality_arr, url_arr;
    private OnTaskUrlCallback callback;

    public AnidubUrl(String url, OnTaskUrlCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(quality_arr, url_arr);
    }

    @Override
    protected Void doInBackground(Void... voids) {
//        if (url.contains("anidub")){
//            if (url.contains("/player/"))
//                core = url.split("/player/")[0] + "/";
//            else core = Statics.ANIDUB_URL + "/";
//
//        } else setUrl();
        if (url.endsWith(".html")) {
            Document d = getData(url);
            if (d != null) {
                if (d.html().contains("id=\"our1\"")) {
                    Element pl = d.select("#our1").first();
                    if (pl.html().contains("<select")) {
                        Element ifrm = pl.select("select").first();
                        for (Element e : ifrm.select("option")) {
                            String val = e.attr("value");
                            String txt = e.text();
                            if (val.contains("|"))
                                val = val.split("\\|")[0].trim();
                            if (txt.contains(" - "))
                                txt = txt.split(" - ")[txt.split(" - ").length - 1].trim();
                            addSeries(val, txt);
                            break;
                        }
                    }
                }
            }
        } else if (url.contains("video.php?")) setUrl();
        return null;
    }

    private void addSeries(String u, String t) {
        Document d = getData(u);
        if (d != null) {
            if (d.html().contains("var source = '")) {
                url = "https://anime.anidub.com/player/" +
                        d.html().split("var source = '")[1].split("'")[0].trim();
                setUrl();
            }
        }
    }

    private void setUrl(){
        final ArrayList<String> q = new ArrayList<>();
        final ArrayList<String> u = new ArrayList<>();
//        if (!url.startsWith("http")) url = core + url;
//        Document d = null;
        String qual = "HLS (m3u8)";
        q.add(qual);
        u.add(url);
        add(q, u);

//        if (url.contains("chunk.m3u8"))
//            url = url.split("chunk\\.m3u8")[0] + "index.m3u8";
//        Log.e("Anidub", "doInBackground: "+url);
//        if (d != null) {
//            qual = d.html();
//            if (qual.contains("1080")) {
//                q.add("1080 (m3u8)");
//                u.add(url);
//            }
//            if (qual.contains("720")) {
//                q.add("720 (m3u8)");
//                u.add(url);
//            }
//            if (qual.contains("480p")) {
//                q.add("480 (m3u8)");
//                u.add(url);
//            }
//            if (qual.contains("360")) {
//                q.add("360 (m3u8)");
//                u.add(url);
//            }
//
//            if (!q.isEmpty())
//                add(q, u);
//        } else {
//            if (url.contains("http")){
//                q.add(qual);
//                u.add(url);
//                add(q, u);
//            } else {
//                q.add("Видео недоступно");
//                u.add("error");
//                add(q, u);
//            }
//        }
    }
    private Document getData(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .referrer(Statics.ANIDUB_URL)
                    .ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private void add(ArrayList<String> q,  ArrayList<String> u) {
        quality_arr = q.toArray(new String[q.size()]);
        url_arr = u.toArray(new String[u.size()]);
    }
}