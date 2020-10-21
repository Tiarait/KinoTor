package com.kinotor.tiar.kinotor.parser.video.kinolive;

import android.os.AsyncTask;

import com.kinotor.tiar.kinotor.utils.OnTaskUrlCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class KinoliveUrl extends AsyncTask<Void, Void, Void> {
    private String url;
    private String[] quality_arr, url_arr;
    private OnTaskUrlCallback callback;

    public KinoliveUrl(String url, OnTaskUrlCallback callback) {
        if (url.contains("trueurl"))
            this.url = url.split("trueurl")[0].trim();
        else this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(quality_arr, url_arr);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        setUrl();
        return null;
    }

    private void setUrl(){
        final ArrayList<String> q = new ArrayList<>();
        final ArrayList<String> u = new ArrayList<>();
        url = url.replace("{", "").replace("[", "")
                .replace("(", "").replace("}", "")
                .replace("]", "")
                .replace(")", "").trim();
        Document d = getData(url);
        if (d != null) {
            if (d.html().contains("http") && d.html().contains(".m3u8"))
                url = "http" + d.html().split("http")[1].split("\\.m3u8")[0] + ".m3u8";
        }

        if (url.contains(".1080p")){
            q.add("1080 (m3u8)");
            u.add(url);
        }
        if (url.contains(".1O8Op")){
            q.add("1080 (m3u8)");
            u.add(url);
        }
        if (url.contains(".HD1080")){
            q.add("1080 (m3u8)");
            u.add(url);
        }
        if (url.contains(".720p")){
            q.add("720 (m3u8)");
            u.add(url);
        }
        if (url.contains(".480p")){
            q.add("480 (m3u8)");
            u.add(url);
        }
        if (url.contains(".360p")){
            q.add("360 (m3u8)");
            u.add(url);
        }

        if (!q.isEmpty())
            add(q, u);
        else {
            if (url.contains("http")){
                q.add("... (mp4)");
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