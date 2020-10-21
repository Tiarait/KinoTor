package com.kinotor.tiar.kinotor.parser.video.farsihd;

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

public class FarsihdList extends AsyncTask<Void, Void, Void> {
    private String url, trans;
    private String se;
    private OnTaskVideoCallback callbackVideo;
    private ItemVideo items;
    private ArrayList<String> videoList = new ArrayList<>();
    private ArrayList<String> videoListName = new ArrayList<>();
    String idOctopus = "83e50473503411ac8d29b78a3506240f378910442d33104c77e5c2f1528cc91b";

    public FarsihdList(String url, OnTaskVideoCallback callback, String trans, String s) {
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
        Log.e("FarsihdList", "url: "+url);
        parseSeries(GetData(url));
        return null;
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

    private void parseSeries(Document doc) {
        items.setTitle("season back");
        items.setType("farsihd");
        items.setUrl(url);
        items.setId("error");
        items.setId_trans("error");
        items.setSeason(se);
        items.setEpisode("error");
        items.setTranslator(trans);


        if (doc != null) {
            String season = "error";
            int e = 0;
            if (doc.html().contains("seasons\"><option value=\""))
                season = doc.html().split("seasons\"><option value=\"")[1].split("\"")[0].trim();
            String episode = "error";
            if (doc.html().contains("class=\"dropdown\" name=\"episodes\">"))
                episode = doc.html().split("class=\"dropdown\" name=\"episodes\">")[1]
                        .split("</select>")[0].trim();
            if (episode.contains("<option")) {
                e = episode.split("<option").length;
                episode = String.valueOf(episode.split("<option").length - 1);
            }

            if (doc.html().contains("translation\"><option value=\""))
                idOctopus = doc.html().split("translation\"><option value=\"")[1].split("\"")[0].trim();

            Log.e("FarsihdList", "parseSeries: "+episode+" "+season);
            if (!episode.contains("error") && !episode.isEmpty() && !season.contains("error") && !season.isEmpty()) {
                for (int i = 1; i < e; i++) {
                    addSeries(season, String.valueOf(i));
                }
            }
        }
    }

    private void addSeries(String s, String e) {
        Log.e("FarsihdList", "url2: "+url + "?e="+e+"&s="+s+"&t="+idOctopus);
        Document doc = GetData(url + "?e="+e+"&s="+s+"&t="+idOctopus);
        if (doc != null) {
            String dh = doc.body().html().replace("&quot;","\"").trim();
            if (dh.contains("hls\":\"")) {
                String hls = dh.split("hls\":\"")[1].split("\"")[0]
                        .replace("\\", "").trim();
                if (!hls.contains("http:"))
                    hls = "http:" + hls;

                videoList.add(hls);
                videoListName.add("s"+s+"e"+e);
                items.setTitle("series");
                items.setType("farsihd");
                items.setUrl(hls);
                items.setToken(url);
                items.setId("error");
                items.setId_trans("error");
                items.setSeason(s);
                items.setEpisode(e);
                items.setTranslator(trans);
            } else Log.e("FarsihdList", "error ");
        } else Log.e("FarsihdList", "error2 ");
    }

    private Document GetData(String url){
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(50000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}