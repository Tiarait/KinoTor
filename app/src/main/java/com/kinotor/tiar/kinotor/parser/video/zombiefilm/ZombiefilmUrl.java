package com.kinotor.tiar.kinotor.parser.video.zombiefilm;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskUrlCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class ZombiefilmUrl extends AsyncTask<Void, Void, Void> {
    private String url;
    private String[] quality_arr, url_arr;
    private OnTaskUrlCallback callback;

    public ZombiefilmUrl(String url, OnTaskUrlCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(quality_arr, url_arr);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (url.contains("/embed/")) {
            Document d = getData(url);
            if (d != null) {
                if (d.html().contains("hlsList: {")) {
                    url = d.html().split("hlsList: \\{")[1].split("\\}")[0];
                    setUrl();
                } else Log.e("Delivembed", "data hls error " + d.html());
            } else Log.d("Delivembed", "data vid error " + url);
        } else {
            setUrl();
        }
        return null;
    }

    private void setUrl(){
        final ArrayList<String> q = new ArrayList<>();
        final ArrayList<String> u = new ArrayList<>();
        String qual;
        if (url.contains(",")) {
            for (String urlend : url.split(",")) {
//                urlend = urlend.replace("\"","");
                if (urlend.contains("\":\"")) {
                    qual = urlend.split("\":\"")[0].trim() + " (m3u8)";
                    url =  urlend.split("\":\"")[1].trim();
                    if (url.startsWith("//")) url = "https:" + url;
                    q.add(qual.replace("\"",""));
                    u.add(url.replace("\"",""));
                    add(q, u);

//                    q.add(qual.replace("(m3u8)", "(ENG m3u8)"));
//                    u.add(url.replace("master.m3u8", "index-a2.m3u8"));
//                    add(q, u);
                } else if (urlend.contains("http")) {
                    qual = "... (m3u8)";
                    if (urlend.contains("/v1"))
                        qual = "720 (m3u8)";
                    else if (urlend.contains("/v2"))
                        qual = "480 (m3u8)";
                    else if (urlend.contains("/v3"))
                        qual = "1080 (m3u8)";
                    url =  "http" + urlend.split("http")[1].trim();

                    q.add(qual);
                    u.add(url);
                    add(q, u);
                }
            }
        } else {
            if (url.contains("\":\"")) {
                qual = url.split("\":\"")[0].trim() + " (m3u8)";
                url =  url.split("\":\"")[1].trim();
                if (url.startsWith("//")) url = "https:" + url;
                q.add(qual);
                u.add(url);
                add(q, u);
            } else if (url.contains(".m3u8")) {
                qual = "... (m3u8)";
                url =  "http" + url.split("http")[1].trim();
                q.add(qual);
                u.add(url);
                add(q, u);
            } else {
                q.add("Видео недоступно");
                u.add("error");
                add(q, u);
            }
        }
    }
    private Document getData(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .referrer(Statics.ZOMBIEFILM_URL_True)
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