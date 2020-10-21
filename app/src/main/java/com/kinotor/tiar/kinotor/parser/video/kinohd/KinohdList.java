package com.kinotor.tiar.kinotor.parser.video.kinohd;

import android.os.AsyncTask;

import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class KinohdList extends AsyncTask<Void, Void, Void> {
    private String url, trans, cn;
    private String se;
    private OnTaskVideoCallback callbackVideo;
    private ItemVideo items;
    private boolean season = false, series = false;
    private ArrayList<String> videoList = new ArrayList<>();
    private ArrayList<String> videoListName = new ArrayList<>();

    public KinohdList(String url, OnTaskVideoCallback callback, boolean season, String trans) {
        this.url = url;
        this.season = season;
        this.callbackVideo = callback;
        this.trans = trans;

        items = new ItemVideo();
    }

    public KinohdList(String url, OnTaskVideoCallback callback, boolean series, String trans, String s) {
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
        if (season && url.contains("/sz.txt?se=")) {
            cn = url.split("/sz\\.txt\\?se=")[1].split("&")[0];
            if (url.contains("file:\""))
                url = url.split("file:\"")[1].split("\"")[0].trim();
            parseSeason(getData(url));
        }
        else if (series)
            parseSeries(url);
        return null;
    }

    private void parseSeason(Document data) {
        items.setTitle("season back");
        items.setType("kinohd");
        items.setUrl(url);
        items.setId("error");
        items.setId_trans(cn);
        items.setSeason("error");
        items.setEpisode("error");
        items.setTranslator(trans);
        if (data != null) {
            if (data.text().contains("folder\": [")) {
                String doc = data.text().replace(" ", "").replace(cn + ".", "")
                        .replace("\n", "").replace("\r", "");
                url = doc;
                if (doc.contains("]},{")) {
                    String[] array = doc.split("\\]\\},\\{");
                    for (String anArray : array) {
                        addSeason(anArray);
                    }
                } else {
                    addSeason(doc);
                }
            }
        }
    }

    private void addSeason(String s){
        String season, episode;
        if (s.contains("id\":\"")) {
            season = s.split("id\":\"")[1].split("\"")[0];
            if (season.contains("."))
                season = s.split("\\.")[0];

            episode = String.valueOf(s.split("\"id\":\"").length - 1);

            if (season.contains(":\"Сезон"))
                season = season.split(":\"Сезон")[1].split("\"")[0];
        } else {
            season = "0";
            episode = "0";
        }

        items.setTitle("season");
        items.setType("kinohd");
        items.setUrl(s);
        items.setId(s);
        items.setId_trans(cn);
        items.setSeason(season);
        items.setEpisode(episode);
        items.setTranslator(trans);
    }

    private void parseSeries(String doc) {
        items.setTitle("series back");
        items.setType("kinohd");
        items.setUrl(doc);
        items.setId("error");
        items.setId_trans("error");
        items.setSeason(se);
        items.setEpisode("error");
        items.setTranslator(trans);


        addSeries(url);
    }

    private void addSeries(String s) {
        if (s.contains("},{")) {
            String[] curSe = s.split("\\},\\{");
            for (int i = 0; i < curSe.length; i++) {
                String u = curSe[i].contains("file\":\"") ?
                        curSe[i].split("file\":\"")[1].split("\"")[0] : "error";
                videoList.add(u);
                videoListName.add("s"+se+"e"+String.valueOf(i+1));
                items.setTitle("series");
                items.setType("kinohd");
                items.setUrl(u);
                items.setUrlTrailer("error");
                items.setToken(url);
                items.setId("error");
                items.setId_trans("error");
                items.setSeason(se);
                items.setEpisode(String.valueOf(i+1));
                items.setTranslator(trans);
            }
        } else {
            String u = s.contains("file\":\"") ?
                    s.split("file\":\"")[1].split("\"")[0] : "error";
            videoList.add(u);
            videoListName.add("s"+se+"e"+String.valueOf(1));
            items.setTitle("series");
            items.setType("kinohd");
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
            return Jsoup.connect(url)
                    .referrer(Statics.KINOHD_URL)
                    .header("Upgrade-Insecure-Requests","1")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}