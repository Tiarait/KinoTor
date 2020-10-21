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

public class MoonwalkSeries extends AsyncTask<Void, Void, Void> {
    private String id, id_trans, cur_season;
    private final String TOKEN = "6eb82f15e2d7c6cbb2fdcebd05a197a2";
    private ItemVideo items;
    private OnTaskVideoCallback callback;

    public MoonwalkSeries(String id, String id_trans, String season, OnTaskVideoCallback callback) {
        this.id = id;
        this.cur_season = season;
        this.id_trans = id_trans;
        this.callback = callback;

        items = new ItemVideo();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(items);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (id.contains("serial\":")) getSeries(id);
        else if (id.contains("title_ru\":\"")) getSeries2(id);
        else parse(GetData(id, id_trans));
        return null;
    }

    private void parse(Document doc) {
        if (doc != null) getSeries(doc.body().text());
    }

    private void getSeries(String doc) {
        if (doc != null) {
            String episode = "error", translator = "error", url = "error", desc = "error";

            int cur_s = Integer.parseInt(cur_season.trim());

            if (doc.contains("title_ru\":\""))
                episode = doc.split("title_ru\":\"")[1].split("\",")[0];
            else if (doc.contains("title_ru\": \""))
                episode = doc.split("title_ru\": \"")[1].split("\",")[0];
            if (doc.contains("translator\":\""))
                translator = doc.split("translator\":\"")[1].split("\",")[0];
            else if (doc.contains("translator\": \""))
                translator = doc.split("translator\": \"")[1].split("\",")[0];
            items.setTitle("series back");
            items.setType("moonwalk");
            items.setToken(TOKEN);
            items.setId_trans(id_trans);
            items.setId(doc);
            items.setSeason(cur_season);
            items.setUrl(url);
            items.setEpisode(episode.replace("[", "").trim());
            items.setTranslator(translator);

            String iframe_url = "error";
            if (doc.contains("iframe_url\":\""))
                iframe_url = doc.split("iframe_url\":\"")[1].split("\",")[0];

            String[] array = doc.split("\\{\"season_");
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
                if (array[cur_s].contains("episodes\":[0"))
                    items.setEpisode(String.valueOf(i));
                else items.setEpisode(series_arr[i].trim());
                if (series_arr[i].contains("\""))
                    series_arr[i] = series_arr[i].replaceAll("\"", "");
                if (!series_arr[i].contains("http://"))
                    series_arr[i] = iframe_url + "?episode=" + series_arr[i] + "&season=" + cur_season;
                url = series_arr[i];
                items.setTitle("series");
                items.setType("moonwalk");
                items.setToken(TOKEN);
                items.setId_trans(id_trans);
                items.setId(doc);
                items.setSeason(cur_season);
                items.setUrl(url);
                items.setTranslator(translator);
            }
        }
    }

    private void getSeries2(String doc) {
        if (doc != null) {
            String episode = "error", translator = "error", url = "error";

            int cur_s = Integer.parseInt(cur_season.trim());

            if (doc.contains("title_ru\":\""))
                episode = doc.split("title_ru\":\"")[1].split("\",")[0];
            else if (doc.contains("title_ru\": \""))
                episode = doc.split("title_ru\": \"")[1].split("\",")[0];
            if (doc.contains("translator\":\""))
                translator = doc.split("translator\":\"")[1].split("\",")[0];
            else if (doc.contains("translator\": \""))
                translator = doc.split("translator\": \"")[1].split("\",")[0];
            items.setTitle("series back");
            items.setType("moonwalk");
            items.setToken(TOKEN);
            items.setId_trans(id_trans);
            items.setId(doc);
            items.setSeason(cur_season);
            items.setUrl(url);
            items.setEpisode(episode.replace("[", "").trim());
            items.setTranslator(translator);

            String iframe_url = "error";
            if (doc.contains("iframe_url\":\""))
                iframe_url = doc.split("iframe_url\":\"")[1].split("\",")[0];

            String[] array = doc.split("episodes\":\\[");
            //если колво сезонов меньше последнего сезона
            for (int i = 1; i < array.length; i ++){
                String numb = array[i].split("number\":")[1].split(",")[0].trim();
                if (numb.equals(cur_season))
                    cur_s = i;
            }
            //разные форматы hdgo и moonwalk
            String series = "";
            if (array[cur_s].contains("episodes_tokens"))
                series = array[cur_s].split("episodes_tokens")[0].split("\\]")[0];
            String[] series_arr = series.split(",");

            //построение списка
            for (int i = 0; i < series_arr.length; i ++){
                if (array[cur_s].contains("episodes\":[0"))
                    items.setEpisode(String.valueOf(i));
                else items.setEpisode(series_arr[i].trim());
                if (series_arr[i].contains("\""))
                    series_arr[i] = series_arr[i].replaceAll("\"", "");
                if (!series_arr[i].contains("http://"))
                    series_arr[i] = iframe_url + "?episode=" + series_arr[i] + "&season=" + cur_season;
                url = series_arr[i];
                items.setTitle("series");
                items.setType("moonwalk");
                items.setToken(TOKEN);
                items.setId_trans(id_trans);
                items.setId(doc);
                items.setSeason(cur_season);
                items.setUrl(url);
                items.setTranslator(translator);
            }
        }
    }

    private Document GetData(String id, String id_trans){
        final String url;
        Log.e("test", "GetSeasonMoonwalk: " + id);
        if (id.contains("token=")) {
            url = "http://moonwalk.cc/api/serial.json?api_token="+ TOKEN +
                    "&" + id;
        } else {
            id = id.contains("world_art") ? "world_art_id=" + id.replace("world_art", "") : "kinopoisk_id=" + id;
            if (id_trans.equals("null"))
                url = "http://moonwalk.cc/api/serial_episodes.json?api_token=" + TOKEN +
                        "&" + id;
            else url = "http://moonwalk.cc/api/serial_episodes.json?api_token=" + TOKEN +
                    "&" + id + "&translator_id=" + id_trans;
        }
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