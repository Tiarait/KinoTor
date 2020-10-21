package com.kinotor.tiar.kinotor.parser.video.animedia;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.utils.OnTaskUrlCallback;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class AnimediaUrl extends AsyncTask<Void, Void, Void> {
    private String url;
    private String[] quality_arr, url_arr;
    private OnTaskUrlCallback callback;

    public AnimediaUrl(String url, OnTaskUrlCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(quality_arr, url_arr);
    }

    @Override
    protected Void doInBackground(Void... voids) {
//        Document d = getData(url);
//        if (d.html().contains("file: \"")) {
//            url = d.html().split("file: \"")[1].split("\"")[0];
//            if (url.startsWith("//"))
//                url = "http:"+url;
//            else if (url.startsWith("://"))
//                url = "http"+url;
            Log.d("Animedia", "doInBackground: "+url);
            setUrl();
//        } else Log.e("Animedia", "doInBackground1: "+d.html());
        return null;
    }

    private void setUrl(){
        final ArrayList<String> q = new ArrayList<>();
        final ArrayList<String> u = new ArrayList<>();
//        Document d = getData(url);
        String type = " (m3u8)";
        if (url.endsWith(".m3u8")) {
            q.add("HLS (m3u8)");
            u.add(url.trim());
            add(q, u);
        }

        if (q.isEmpty()){
            q.add("Видео недоступно");
            u.add("error");
            add(q, u);
        }
    }
    private void add(ArrayList<String> q,  ArrayList<String> u) {
        quality_arr = q.toArray(new String[q.size()]);
        url_arr = u.toArray(new String[u.size()]);
    }
}