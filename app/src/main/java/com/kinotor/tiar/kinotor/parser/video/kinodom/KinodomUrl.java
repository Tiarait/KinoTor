package com.kinotor.tiar.kinotor.parser.video.kinodom;

import android.os.AsyncTask;

import com.kinotor.tiar.kinotor.utils.OnTaskUrlCallback;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class KinodomUrl extends AsyncTask<Void, Void, Void> {
    private String url;
    private String[] quality_arr, url_arr;
    private OnTaskUrlCallback callback;

    public KinodomUrl(String url, OnTaskUrlCallback callback) {
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
        if (url.contains("[")){
            String qual = url.split("\\[")[1];
            url = url.split("\\[")[0];
            setUrl(qual);
        } else {
            setUrl(url);
        }
//        setUrl(url);
    }

    private void setUrl(String qual){
        final ArrayList<String> q = new ArrayList<>();
        final ArrayList<String> u = new ArrayList<>();

        if (qual.contains("480")) {
            q.add("480 (mp4)");
            u.add(url + "480.mp4");
        }
        if (qual.contains("720p")) {
            q.add("720 (mp4)");
            u.add(url + "720.mp4");
        } else if (qual.contains("720")) {
            q.add("720 (mp4)");
            u.add(url + "720.mp4");
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
}