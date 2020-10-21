package com.kinotor.tiar.kinotor.parser.video.farsihd;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.utils.OnTaskUrlCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class FarsihdUrl extends AsyncTask<Void, Void, Void> {
    private String url;
    private String[] quality_arr, url_arr;
    private OnTaskUrlCallback callback;

    public FarsihdUrl(String url, OnTaskUrlCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(quality_arr, url_arr);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        getMp4();
        return null;
    }

    private void getMp4() {
        setUrl(url);
    }

    private void setUrl(String qual){
        final ArrayList<String> q = new ArrayList<>();
        final ArrayList<String> u = new ArrayList<>();

        if (qual.contains("index.m3u8")) {
            q.add("HLS (m3u8)");
            u.add(url);

            Document doc = GetData(url);
            if (doc != null) {
                String d = doc.html();
                Log.e("qwe", doc.html());
                if (d.contains("/1080/")) {
                    q.add("1080 (m3u8)");
                    u.add(url.replace("/index.m3u8", "/1080/index.m3u8"));
                }
                if (d.contains("/720/")) {
                    q.add("720 (m3u8)");
                    u.add(url.replace("/index.m3u8", "/720/index.m3u8"));
                }
                if (d.contains("/480/")) {
                    q.add("480 (m3u8)");
                    u.add(url.replace("/index.m3u8", "/480/index.m3u8"));
                }
                if (d.contains("/360/")) {
                    q.add("360 (m3u8)");
                    u.add(url.replace("/index.m3u8", "/360/index.m3u8"));
                }
            }
        }
        if (!q.isEmpty())
            add(q, u);
        else {
            q.add("видео не доступно");
            u.add("error");
            add(q, u);
        }
    }

    private void add(ArrayList<String> q,  ArrayList<String> u) {
        quality_arr = q.toArray(new String[q.size()]);
        url_arr = u.toArray(new String[u.size()]);
    }


    private Document GetData(String url){
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(50000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}