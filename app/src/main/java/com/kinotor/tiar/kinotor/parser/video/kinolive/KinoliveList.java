package com.kinotor.tiar.kinotor.parser.video.kinolive;

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

public class KinoliveList extends AsyncTask<Void, Void, Void> {
    private String url, trans;
    private String se;
    private OnTaskVideoCallback callbackVideo;
    private ItemVideo items;
    private boolean series;
    private ArrayList<String> videoList = new ArrayList<>();
    private ArrayList<String> videoListName = new ArrayList<>();

//    public KinoliveList(String url, OnTaskVideoCallback callback, boolean season, String trans) {
//        this.url = url;
//        this.season = season;
//        this.callbackVideo = callback;
//        this.trans = trans;
//
//        items = new ItemVideo();
//    }

    public KinoliveList(String url, OnTaskVideoCallback callback, boolean series, String trans, String s) {
        this.url = url;
        this.series = series;
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
        if (url.contains(".txt")) {
            if (url.contains("trueurl[")){
                url = url.split("trueurl\\[")[1].split("\\]")[0].trim();
            }
            Document d = getData(url);
            if (d != null ) {
                url = d.text();
                parseSeries();
            } else {
                back();
                Log.e("Kinolive", "doInBackground: error " + url);
            }
        } else
            parseSeries();
        return null;
    }

    private void back(){
        Log.e("kinolive", "back: "+url);
        items.setTitle("season back");
        items.setType("kinolive");
        items.setUrl(url);
        items.setId(url);
        items.setId_trans(url);
        items.setSeason("error");
        items.setEpisode("error");
        items.setTranslator(trans);
    }

    private void parseSeries() {
        items.setTitle("season back");
        items.setType("kinolive");
        items.setUrl(url);
        items.setId("error");
        items.setId_trans("error");
        items.setSeason(se);
        items.setEpisode("error");
        items.setTranslator(trans);


        addSeries(url);
    }

    private void addSeries(String s) {
        if (s.contains("\"},{\"")) {
            String[] curSe = s.split("\"\\},\\{\"");
            for (int i = 0; i < curSe.length; i++) {
                String u = curSe[i].contains("file\":\"") ?
                        curSe[i].split("file\":\"")[1].replace("\"", "")
                                .replace("{", "").replace("[", "")
                                .replace("(", "").replace("}", "")
                                .replace("]", "")
                                .replace(")", ""): "error";
                String ss;
                if (curSe[i].contains("comment\":\""))
                    ss = curSe[i].split("comment\":\"")[1].split("\"")[0];
                else ss = String.valueOf(i+1);
                videoList.add(u);
                videoListName.add("s"+se+"e"+String.valueOf(i+1));
                items.setTitle("series");
                items.setType("kinolive");
                items.setUrl(u);
                items.setUrlTrailer("error");
                items.setToken(url);
                items.setId("error");
                items.setId_trans("error");
                items.setSeason(se);
                items.setEpisode(ss);
                items.setTranslator(trans);
            }
        } else {
            String u = s.contains("file\":\"") ?
                    s.split("file\":\"")[1].split("\"")[0] : "error";
            videoList.add(u);
            videoListName.add("s"+se+"e"+String.valueOf(1));
            items.setTitle("series");
            items.setType("kinolive");
            items.setUrl(u);
            items.setUrlTrailer("error");
            items.setToken(url);
            items.setId("error");
            items.setId_trans("error");
            items.setSeason(se);
            items.setEpisode(String.valueOf(1));
            items.setTranslator(trans);
        }
    }

    private Document getData(String url) {
        try {
            Log.e("Kinolive", "getData: "+url );
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}