package com.kinotor.tiar.kinotor.parser.video.kinohd;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.utils.OnTaskUrlCallback;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class KinohdUrl extends AsyncTask<Void, Void, Void> {
    private String url;
    private String[] quality_arr, url_arr;
    private OnTaskUrlCallback callback;

    public KinohdUrl(String url, OnTaskUrlCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(quality_arr, url_arr);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.e("kinohdUrl", "setUrl: "+url );
        setUrl();
        return null;
    }

    private void setUrl(){
        final ArrayList<String> q = new ArrayList<>();
        final ArrayList<String> u = new ArrayList<>();
        url = url.replace("\"","");
        if (url.contains("[4K UHD]")){
            String ur = url.split("\\[4K UHD\\]")[1].trim();
            if (ur.contains(","))
                ur = ur.split(",")[0].trim();
            q.add("4K UHD (mp4)");
            u.add(ur);
            add(q, u);
        }
        if (url.contains("[1080]")){
            String ur = url.split("\\[1080\\]")[1].trim();
            if (ur.contains(","))
                ur = ur.split(",")[0].trim();
            if (!u.toString().contains(ur)) {
                q.add("1080 (mp4)");
                u.add(ur);
                add(q, u);
            }
        }
        if (url.contains("[720]")){
            String ur = url.split("\\[720\\]")[1].trim();
            if (ur.contains(","))
                ur = ur.split(",")[0].trim();
            if (!u.toString().contains(ur)) {
                q.add("720 (mp4)");
                u.add(ur);
                add(q, u);
            }
        }
        if (url.contains("[480]")){
            String ur = url.split("\\[480\\]")[1].trim();
            if (ur.contains(","))
                ur = ur.split(",")[0].trim();
            if (!u.toString().contains(ur)) {
                q.add("480 (mp4)");
                u.add(ur);
                add(q, u);
            }
        }
        if (url.contains("[360]")){
            String ur = url.split("\\[360\\]")[1].trim();
            if (ur.contains(","))
                ur = ur.split(",")[0].trim();
            if (!u.toString().contains(ur)) {
                q.add("360 (mp4)");
                u.add(ur);
                add(q, u);
            }
        }

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

    private void add(ArrayList<String> q,  ArrayList<String> u) {
        quality_arr = q.toArray(new String[q.size()]);
        url_arr = u.toArray(new String[u.size()]);
    }
}