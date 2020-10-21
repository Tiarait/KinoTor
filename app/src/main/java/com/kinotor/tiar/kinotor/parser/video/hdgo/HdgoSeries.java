package com.kinotor.tiar.kinotor.parser.video.hdgo;

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

public class HdgoSeries extends AsyncTask<Void, Void, Void> {
    private String id, cur_season;
    private final String TOKEN = "2c4lbb21dje7yo7aysht52fj&k";
    private ItemVideo items;
    private OnTaskVideoCallback callback;

    public HdgoSeries(String id, String season, OnTaskVideoCallback callback) {
        this.id = id;
        this.cur_season = season;
        this.callback = callback;

        items = new ItemVideo();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(items);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        getSeries(GetData(id));
        return null;
    }

    private void getSeries(Document doc) {
        if (doc != null) {
            String episode = "error", translator = "error", url = "error";

            int cur_s = Integer.parseInt(cur_season.trim());

            if (doc.body().text().contains("title_ru\":\""))
                episode = doc.body().text().split("title_ru\":\"")[1].split("\",")[0];
            else if (doc.body().text().contains("title_ru\": \""))
                episode = doc.body().text().split("title_ru\": \"")[1].split("\",")[0];
            if (doc.body().text().contains("translator\":\""))
                translator = doc.body().text().split("translator\":\"")[1].split("\",")[0];
            else if (doc.body().text().contains("translator\": \""))
                translator = doc.body().text().split("translator\": \"")[1].split("\",")[0];
            items.setTitle("series back");
            items.setType("hdgo");
            items.setToken(TOKEN);
            items.setId_trans("null");
            items.setId(id);
            items.setUrl(url);
            items.setSeason(cur_season);
            items.setEpisode(episode);
            items.setTranslator(translator);

//            String iframe_url = "error";
//            if (doc.body().text().contains("iframe_url\":\""))
//                iframe_url = doc.body().text().split("iframe_url\":\"")[1].split("\",")[0];
//            else Log.d(TAG, "ParseHdgoSeries: " + doc.body().text());

            String[] array = doc.body().text().split("\\{\"season_");
            //если колво сезонов меньше последнего сезона
            for (int i = 1; i < array.length; i ++){
                String numb = array[i].split("number\":")[1].split(",")[0].trim();
                if (numb.equals(cur_season))
                    cur_s = i;
            }
            //разные форматы hdgo и moonwalk
            String series = "";
            if (array[cur_s].contains("episodes\":["))
                series = array[cur_s].split("episodes\":\\[")[1].split("\\]")[0];
            else if (array[cur_s].contains("episodes\": ["))
                series = array[cur_s].split("episodes\": \\[")[1].split("\\]")[0];
            String[] series_arr = series.split(",");
            //построение списка
            for (int i = 0; i < series_arr.length; i ++){
                if (series_arr[i].contains("\""))
                    series_arr[i] = series_arr[i].replace("\"", "");
                if (!series_arr[i].contains("http://"))
                    series_arr[i] = series_arr[i] + "?episode=" + (i+1) + "&season" + cur_season;
                url = series_arr[i];
                items.setTitle("series");
                items.setType("hdgo");
                items.setToken(TOKEN);
                items.setId_trans("null");
                items.setId(id);
                items.setUrl(url);
                items.setSeason(cur_season);
                items.setEpisode(String.valueOf(i + 1));
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