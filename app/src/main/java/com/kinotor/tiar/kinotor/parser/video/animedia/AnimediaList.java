package com.kinotor.tiar.kinotor.parser.video.animedia;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class AnimediaList extends AsyncTask<Void, Void, Void> {
    private String url, trans;
    private String se;
    private OnTaskVideoCallback callbackVideo;
    private ItemVideo items;
    private ArrayList<String> videoList = new ArrayList<>();
    private ArrayList<String> videoListName = new ArrayList<>();

    public AnimediaList(String url, OnTaskVideoCallback callback, String trans, String s) {
        this.url = url;
        this.callbackVideo = callback;
        this.trans = trans;
        this.se = s.trim();

        items = new ItemVideo();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Statics.videoList = videoList.toArray(new String[videoList.size()]);
        Statics.videoListName = videoListName.toArray(new String[videoListName.size()]);
        callbackVideo.OnCompleted(items);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        back();
        if (url.contains("<a ")){
            url = url.split("<a ")[1];
            if (url.trim().contains("href=\"")) {
                url = url.split("href=\"")[1].split("\"")[0].trim();
                if (!url.contains(Statics.ANIMEDIA_URL))
                    url = Statics.ANIMEDIA_URL + url;
                Document d = getData(url);
                if (d != null) {
                    if (d.html().contains("file: \"")) {
                        String u = d.html().split("file: \"")[1].split("\"")[0];
                        Document dd = getData(u);
                        if (dd != null) {
                            if (dd.html().contains("{")) {
                                for (String a : dd.text().split("\\{")) {
                                    if (a.contains("file\":\"") && a.contains("title\":\"")) {
                                        String ur = a.split("file\":\"")[1].split("\"")[0];
                                        if (ur.trim().startsWith("//") && !ur.contains("http"))
                                            ur = "http:" + ur;
                                        else if (ur.trim().startsWith("://") && !ur.contains("http"))
                                            ur = "http" + ur;
//                                        Log.e("AnimediaList", "doInBackground1tta: " + ur);
                                        String tt = a.split("title\":\"")[1].split("\"")[0];
                                        addSeries(ur, tt);
                                    } else Log.e("AnimediaList", "doInBackground1a: " + a);
                                }
                            } else Log.e("AnimediaList", "doInBackgrounddd: " + dd.text());
                        }
                    }
                }
            }
        } else {
            Log.e("AnimediaList", "doInBackground: "+url);
        }
        return null;
    }

    private void back(){
        items.setTitle("season back");
        items.setType("animedia");
        items.setUrl(url);
        items.setId("error");
        items.setId_trans("error");
        items.setSeason("error");
        items.setEpisode("error");
        items.setTranslator(trans);
    }

    private void addSeries(String u, String t) {
//        Log.e("AnimediaList", "doInBackground1aUu: " + u);
        videoList.add(u);
        videoListName.add(t);
        items.setTitle("series");
        items.setType("animedia");
        items.setUrl(u);
        items.setUrlTrailer("error");
        items.setToken(url);
        items.setId("error");
        items.setId_trans("error");
        items.setSeason(se);
        items.setEpisode(t);
        items.setTranslator(trans);
    }

    private Document getData(String url) {
        try {
//            Log.e("AnimediaList", "getData: "+url );
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}