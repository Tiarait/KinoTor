package com.kinotor.tiar.kinotor.parser.video.anidub;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class AnidubList extends AsyncTask<Void, Void, Void> {
    private String url, trans;
    private String se;
    private OnTaskVideoCallback callbackVideo;
    private ItemVideo items;
    private ArrayList<String> videoList = new ArrayList<>();
    private ArrayList<String> videoListName = new ArrayList<>();


    public AnidubList(String url, OnTaskVideoCallback callback, String trans, String s) {
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
        if (url.contains(Statics.ANIDUB_URL)){
            Document d = getData(url);
            if (d.html().contains("id=\"our1\"")) {
                Element pl = d.select("#our1").first();
                if (pl.html().contains("<select")){
                    Element ifrm = pl.select("select").first();
                    back();
                    for (Element e : ifrm.select("option")){
                        String val = e.attr("value");
                        String txt = e.text();
//                        Log.e("test", "doInBackground: "+val );
                        if (val.contains("|"))
                            val = val.split("\\|")[0].trim();
                        if (txt.contains(" - "))
                            txt = txt.split(" - ")[txt.split(" - ").length-1].trim();
//                        Log.e("test2", "doInBackground: "+val );
                        addSeries(val, txt);
                    }
                } else {
                    back();
                    Log.e("Anidub", "doInBackground1: "+pl.html());
                }
            } else {
                back();
                Log.e("Anidub", "doInBackground: "+d.html());
            }
        } else {
            back();
            Log.e("Anidub", "doInBackground: "+url);
        }
        return null;
    }

    private void back(){
        items.setTitle("season back");
        items.setType("anidub");
        items.setUrl(url);
        items.setId("error");
        items.setId_trans("error");
        items.setSeason("error");
        items.setEpisode("error");
        items.setTranslator(trans);
    }

    private void addSeries(String u, String t) {
        Document d = getData(u);
        if (d != null) {
            if (d.html().contains("var source = '")) {
                String ur = "https://anime.anidub.com/player/" +
                        d.html().split("var source = '")[1].split("'")[0].trim();
                Log.e("Anidub", "addSeries: "+ur);
                videoList.add(ur);
                videoListName.add("s"+se+"e"+t.replace(" серия",""));
                items.setTitle("series");
                items.setType("anidub");
                items.setUrl(ur);
                items.setUrlTrailer("error");
                items.setToken(url);
                items.setId("error");
                items.setId_trans("error");
                items.setSeason(se);
                items.setEpisode(t);
                items.setTranslator(trans);
            }
        }
    }

    private Document getData(String url) {
        try {
            Log.e("Anidub", "getData: "+url );
            return Jsoup.connect(url)
                    .referrer(Statics.ANIDUB_URL)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}