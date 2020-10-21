package com.kinotor.tiar.kinotor.parser.video.zombiefilm;

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

public class ZombiefilmList extends AsyncTask<Void, Void, Void> {
    private String url, trans;
    private String se;
    private OnTaskVideoCallback callbackVideo;
    private ItemVideo items;
    private boolean season = false, series = false;
    private ArrayList<String> videoList = new ArrayList<>();
    private ArrayList<String> videoListName = new ArrayList<>();

    public ZombiefilmList(String url, OnTaskVideoCallback callback, boolean season, String trans) {
        this.url = url;
        this.season = season;
        this.callbackVideo = callback;
        this.trans = trans;

        items = new ItemVideo();
    }

    public ZombiefilmList(String url, OnTaskVideoCallback callback, boolean series, String trans, String s) {
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
        items.setType("zombiefilm");
        items.setUrl(url);
        items.setId("error");
        items.setId_trans("error");
        items.setSeason("error");
        items.setEpisode("error");
        items.setTranslator(trans);

        doc = doc.replace(" ","");
        if (doc.contains("\"id\":")) {
            String[] array = doc.split("\"id\":");
            for (String anArray : array) {
                addSeason(anArray);
            }
        } else {
            addSeason(doc);
        }
    }

    private void addSeason(String s){
        String season, episode;
        url = "https://api.videobalancer.net/contents/video/by-season/?id=" + s.split(",")[0].trim() +
                "&host=zombie-film.com";
        if (s.contains("season\":"))
            season = s.split("season\":")[1].split(",")[0];
        else season = "0";
        Document docses = Getdata(url);
        if (docses != null) {
            if (docses.html().contains("episode\":"))
                episode = docses.html().split("episode\":")
                        [docses.html().split("episode\":").length - 1].split(",")[0];
            else episode = "0";
            items.setTitle("season");
            items.setType("zombiefilm");
            items.setUrl(docses.text());
            items.setId(url);
            items.setId_trans("error");
            items.setSeason(season);
            items.setEpisode(episode);
            items.setTranslator(trans);
        }
    }

    private void parseSeries(String doc) {
        items.setTitle("series back");
        items.setType("zombiefilm");
        items.setUrl(doc);
        items.setId("error");
        items.setId_trans("error");
        items.setSeason(se);
        items.setEpisode("error");
        items.setTranslator(trans);


        doc = doc.replace(" ","");
        if (doc.contains("{\"id\":")) {
            String[] array = doc.split("\\{\"id\":");
            for (String anArray : array) {
                addSeries(anArray);
            }
        } else {
            addSeries(doc);
        }
    }

    private void addSeries(String s) {
        String id = s.split(",")[0];

        String u = "error";
        if (s.contains("urlQuality\":{"))
            u = s.split("urlQuality\":\\{")[1].split("\\}")[0];
        if (s.contains("episode\":")) {
            String ep = s.split("episode\":")[1].split(",")[0].trim();
            videoList.add(u);
            videoListName.add("s" + se + "e" + ep);
            items.setTitle("series");
            items.setType("zombiefilm");
            items.setUrl(u);
            items.setToken(url);
            items.setId(id);
            items.setId_trans("error");
            items.setSeason(se);
            items.setEpisode(ep);
            items.setTranslator(trans);
        }
    }

    private Document Getdata(String url) {
        try {
            return Jsoup.connect(url)
                    .referrer(Statics.ZOMBIEFILM_URL)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            Log.e("ZombieFilmList", url);
            e.printStackTrace();
            return null;
        }
    }
}