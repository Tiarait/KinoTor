package com.kinotor.tiar.kinotor.parser.video.kinopub;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskUrlCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class KinopubUrl extends AsyncTask<Void, Void, Void> {
    private String url;
    private String[] quality_arr, url_arr;
    private OnTaskUrlCallback callback;

    public KinopubUrl(String url, OnTaskUrlCallback callback) {
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
        Log.e("Kinopub", "u "+url);
        if (url.contains("/item/view/")){
            setUrl(getList(getData(url)));
        } else {
            setUrl(url);
        }
    }

    private String getList(Document data){
        String u = "";
        if (data != null) {
            String d = data.html();
            if (d.contains("class=\"dropdown-menu")) {
                for (Element v : data.select(".dropdown-menu")) {
                    if (v.html().contains("Файл mp4") || v.html().contains("HLS плейлист")) {
                        u = v.html();
                        break;
                    }
                }
            } else Log.e("Kinopub", "dropdown not found");
        }
        return u;
    }

    private void setUrl(String qual){
        if (qual == null)
            qual = "";
        final ArrayList<String> q = new ArrayList<>();
        final ArrayList<String> u = new ArrayList<>();
        if (qual.contains("dropdown-header")) {
            if (qual.contains("<li>")) {
                for (String qq : qual.split("<li>")) {
                    String uu = "";
                    String qu;
                    if (qq.contains("href=\"")) {
                        uu = qq.split("href=\"")[1].split("\"")[0].trim();
                    }
                    if (qq.contains("\">")) {
                        qu = qq.split("\">")[1].split("<")[0].trim();
                    } else qu = "...";

                    if (!uu.isEmpty()  && !u.contains(uu)) {
                        u.add(uu);
                        if (uu.contains(".m3u8"))
                            q.add(qu + " (m3u8)");
                        else if (uu.contains(".mp4"))
                            q.add(qu + " (mp4)");
                        else q.add(qu);
                    }
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

    private Document getData(String url) {
        try {
            return Jsoup.connect(url)
                    .header("Cookie", Statics.KINOPUB_COOCKIE
                            .replace("{","")
                            .replace("}","")
                            .replace(" , ",";")
                            .replace(",",";"))
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}