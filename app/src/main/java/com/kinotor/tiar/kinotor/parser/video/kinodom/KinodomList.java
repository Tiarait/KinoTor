package com.kinotor.tiar.kinotor.parser.video.kinodom;

import android.os.AsyncTask;

import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class KinodomList extends AsyncTask<Void, Void, Void> {
    private String url, trans;
    private String se;
    private OnTaskVideoCallback callbackVideo;
    private ItemVideo items;
    private boolean season = false, series = false;
    private ArrayList<String> videoList = new ArrayList<>();
    private ArrayList<String> videoListName = new ArrayList<>();

    public KinodomList(String url, OnTaskVideoCallback callback, boolean season, String trans) {
        this.url = url;
        this.season = season;
        this.callbackVideo = callback;
        this.trans = trans;

        items = new ItemVideo();
    }

    public KinodomList(String url, OnTaskVideoCallback callback, boolean series, String trans, String s) {
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
        if (season)
            parseSeason(url);
        else if (series)
            parseSeries(url);
        return null;
    }

    private void parseSeason(String doc) {
        items.setTitle("season back");
        items.setType("kinodom");
        items.setUrl(url);
        items.setId("error");
        items.setId_trans("error");
        items.setSeason("error");
        items.setEpisode("error");
        items.setTranslator(trans);

        doc = doc.replace(" ","");
        if (doc.contains("]},{") && doc.contains("comment\":\"Сезон")) {
            String[] array = doc.split("\\]\\},\\{");
            for (String anArray : array) {
                addSeason(anArray);
            }
        } else {
            addSeason(doc);
        }
    }

    private void addSeason(String s){
        String season, episode;
        if (s.contains("comment\":\"Сезон"))
            season = s.split("comment\":\"Сезон")[1].split("\"")[0];
        else season = "0";
        if (s.contains("comment\":\"Серия")) {
            episode = s.split("comment\":\"Серия")[s.split("comment\":\"Серия").length - 1]
                    .split("\"")[0];
        } else episode = "0";

        if (episode.contains("-"))
            episode = episode.split("-")[1].trim();

        items.setTitle("season");
        items.setType("kinodom");
        items.setUrl(url);
        items.setId(url);
        items.setId_trans("error");
        items.setSeason(season);
        items.setEpisode(episode);
        items.setTranslator(trans);
    }

    private void parseSeries(String doc) {
        items.setTitle("series back");
        items.setType("kinodom");
        items.setUrl(doc);
        items.setId("error");
        items.setId_trans("error");
        items.setSeason(se);
        items.setEpisode("error");
        items.setTranslator(trans);


        doc = doc.replace(" ","");
        if (doc.contains("]},{")) {
            String[] array = doc.split("\\]\\},\\{");
            for (String anArray : array) {
                addSeries(anArray);
            }
        } else {
            addSeries(doc);
        }
    }

    private void addSeries(String s) {
        String season;
        if (s.contains("comment\":\"Сезон"))
            season = s.split("comment\":\"Сезон")[1].split("\"")[0];
        else season = "0";

//        Log.e("test1", "parseSeries: "+season+"||"+se );
//        Log.e("test2", "parseSeries: "+s );

        if (season.equals(this.se) && s.contains("comment\":\"Серия")) {
            String[] curSe = s.split("comment\":\"Серия");
            for (int i = 1; i < curSe.length; i++) {
                String ep = curSe[i].split("\"")[0].trim();
                if (ep.contains("("))
                    ep = ep.split("\\(")[0].trim();

                String u = curSe[i].contains("file\":\"") ?
                        curSe[i].split("file\":\"")[1].split("\"")[0] : "error";
//                Log.e("test4", "parseSeries: "+u );
                videoList.add(u);
                videoListName.add("s"+se+"e"+ep);
                items.setTitle("series");
                items.setType("kinodom");
                items.setUrl(u);
                items.setToken(url);
                items.setId("error");
                items.setId_trans("error");
                items.setSeason(se);
                items.setEpisode(ep);
                items.setTranslator(trans);
            }
        }
    }
}