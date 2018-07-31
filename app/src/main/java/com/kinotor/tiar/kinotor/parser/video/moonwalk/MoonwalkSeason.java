package com.kinotor.tiar.kinotor.parser.video.moonwalk;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class MoonwalkSeason extends AsyncTask<Void, Void, Void> {
    private String id, id_trans;
    private final String TOKEN = "997e626ac4d9ce453e6c920785db8f45";
    private ItemVideo items;
    private OnTaskVideoCallback callback;

    public MoonwalkSeason(String id, String id_trans, OnTaskVideoCallback callback) {
        this.id = id;
        this.id_trans = id_trans;
        this.callback = callback;

        items = new ItemVideo();
    }


    @Override
    protected Void doInBackground(Void... voids) {
        if (id.contains("serial\":")) getSeson(id);
        else parse(GetData(id, id_trans));
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(items);
    }

    private void parse(Document doc) {
        if (doc != null) getSeson(doc.body().text());
    }

    private void getSeson(String doc) {
        if (doc != null) {
            String[] array = doc.split(",\"description")[0].split("\\{\"season_");
            String season = "error", episode = "error", translator = "error";

            if (doc.contains("title_ru\":\""))
                episode = doc.split("title_ru\":\"")[1].split("\",")[0];
            else if (doc.contains("title_ru\": \""))
                episode = doc.split("title_ru\": \"")[1].split("\",")[0];
            if (doc.contains("translator\":\""))
                translator = doc.split("translator\":\"")[1].split("\",")[0];
            else if (doc.contains("translator\": \""))
                translator = doc.split("translator\": \"")[1].split("\",")[0];

            items.setTitle("season back");
            items.setType("moonwalk");
            items.setToken(TOKEN);
            items.setId(doc.trim());
            items.setUrl(doc.trim());
            items.setId_trans(id_trans);
            items.setSeason(season.replace("[", "").trim());
            items.setEpisode(episode.replace("[", "").trim());
            items.setTranslator(translator.trim());

            //i = 0 - description season
            for (int i = 1; i < array.length; i ++){
                season = array[i].split("number\":")[1].split(",")[0].trim();
                if (array[i].contains("episodes_count"))
                    episode = array[i].split("episodes_count\":")[1].split(",")[0].trim();
                if (array[i].contains("episodes\":")) {
                    episode = array[i].split("episodes\":")[1].split("\\]")[0];
                    episode = episode.split(",")[episode.split(",").length - 1].trim();
                }
                if (array[i].contains("episodes\":[0")) {
                    try{
                        episode = Integer.parseInt(episode) - 1 +"";
                    } catch (Exception ignored){}
                }
                items.setTitle("season");
                items.setType("moonwalk");
                items.setToken(TOKEN);
                items.setId(doc.trim());
                items.setUrl(doc.trim());
                items.setId_trans(id_trans);
                items.setSeason(season.replace("[", "").trim());
                items.setEpisode(episode.replace("[", "").trim());
                items.setTranslator(translator.trim());
            }
        }
    }

    private Document GetData(String id, String id_trans){
        final String url;
        id = id.contains("world_art") ? "world_art_id=" + id.replace("world_art", "") : "kinopoisk_id=" + id;
        if (id_trans.equals("null"))
            url = "http://moonwalk.cc/api/serial_episodes.json?api_token="+ TOKEN +
                    "&" + id;
        else url = "http://moonwalk.cc/api/serial_episodes.json?api_token="+ TOKEN +
                "&" + id + "&translator_id=" + id_trans;
        try {
            Document htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(5000).ignoreContentType(true).referrer("hdgo.cc").get();
            Log.d(TAG, "GetSeasonMoonwalk: get connected to " + url);
            return htmlDoc;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "GetSeasonMoonwalk: error connected to " + url);
            return null;
        }
    }
}
