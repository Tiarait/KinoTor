package com.kinotor.tiar.kinotor.parser.hdgo;

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

public class HdgoSeason extends AsyncTask<Void, Void, Void> {
    private String id;
    private final String TOKEN = "2c4lbb21dje7yo7aysht52fj&k";
    private OnTaskVideoCallback callback;
    private ItemVideo items;

    public HdgoSeason(String id, OnTaskVideoCallback callback) {
        this.id = id;
        this.callback = callback;

        items = new ItemVideo();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(items);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        getSeson(GetData(id));
        return null;
    }

    private void getSeson(Document doc) {
        if (doc != null) {
            String[] array = doc.body().text().split(",\"description")[0].split("\\{\"season_");
            String title = "error", season = "error", episode = "error", translator = "error";

            if (doc.body().text().contains("title_ru\":\""))
                episode = doc.body().text().split("title_ru\":\"")[1].split("\",")[0];
            else if (doc.body().text().contains("title_ru\": \""))
                episode = doc.body().text().split("title_ru\": \"")[1].split("\",")[0];
            if (doc.body().text().contains("translator\":\""))
                translator = doc.body().text().split("translator\":\"")[1].split("\",")[0];
            else if (doc.body().text().contains("translator\": \""))
                translator = doc.body().text().split("translator\": \"")[1].split("\",")[0];

            items.setTitle("season back");
            items.setType("hdgo");
            items.setToken(TOKEN);
            items.setId(id);
            items.setId_trans("null");
            items.setSeason(season);
            items.setEpisode(episode);
            items.setTranslator(translator);

            //i = 0 - description season
            for (int i = 1; i < array.length; i ++){
                season = array[i].split("number\":")[1].split(",")[0].trim();
                if (array[i].contains("episodes_count"))
                    episode = array[i].split("episodes_count\":")[1].split(",")[0].trim();

                items.setTitle("season");
                items.setType("hdgo");
                items.setToken(TOKEN);
                items.setId(id);
                items.setId_trans("null");
                items.setSeason(season);
                items.setEpisode(episode);
                items.setTranslator(translator);
            }
        }
    }

    private Document GetData(String id){
        final String url = "http://hdgo.cc/api/serial_episodes.json?token="+ TOKEN +
                "&id=" + id;
        try {
            Document htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(5000).ignoreContentType(true).referrer("hdgo.cc").get();
            Log.d(TAG, "GetSeasonHDGO: get connected to " + url);
            return htmlDoc;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "GetSeasonHDGO: error connected to " + url);
            return null;
        }
    }
}
