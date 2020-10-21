package com.kinotor.tiar.kinotor.parser.video.kinoxa;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskUrlCallback;
import com.kinotor.tiar.kinotor.utils.Utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class KinoxaTrailerUrl extends AsyncTask<Void, Void, Void> {
    private String url;
    private String[] quality_arr, url_arr;
    private OnTaskUrlCallback callback;
    ArrayList<String> q = new ArrayList<>();
    ArrayList<String> u = new ArrayList<>();

    public KinoxaTrailerUrl(String url, OnTaskUrlCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(quality_arr, url_arr);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        getMp4(GetData());
        return null;
    }

    private void getMp4(Document document) {
        if (document != null) {
            if (document.text().contains("file:\"")) {
                setUrl(document.text().split("file:\"")[1].split("\"")[0]);
            } else {
                q.add("видео не доступно");
                u.add("error");
                add(q, u);
            }
        } else {
            Log.e("rty-1", url);
            q.add("видео не доступно");
            u.add("error");
            add(q, u);
        }
    }

    private void setUrl(String qual){
        if (qual.contains("youtube")) {
            q.add("youtube");
            u.add(qual);
        } else {
            q.add("...");
            u.add(qual);
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

    private Document GetData() {
        try {
            return Jsoup.connect(url)
                    .header("Upgrade-Insecure-Requests","1")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false)
                    .timeout(5000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}