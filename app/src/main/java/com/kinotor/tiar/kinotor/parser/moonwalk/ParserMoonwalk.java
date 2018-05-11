package com.kinotor.tiar.kinotor.parser.moonwalk;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.ui.DetailActivity;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;
import com.kinotor.tiar.kinotor.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 19.02.2018.
 */

public class ParserMoonwalk extends AsyncTask<Void, Void, Void> {
    private String search_title, year, type;
    private final String TOKEN = "997e626ac4d9ce453e6c920785db8f45";
    private ItemHtml itempath;
    private OnTaskVideoCallback callback;
    private ItemVideo items;


    public ParserMoonwalk(ItemHtml item, OnTaskVideoCallback callback) {
        this.itempath = item;
        this.callback = callback;
        this.items = new ItemVideo();

        if (itempath.getTitle(0).contains("("))
            search_title = new Utils().replaceTitle(itempath.getTitle(0).split("\\(")[0]);
        else search_title = new Utils().replaceTitle(itempath.getTitle(0));
        type = itempath.getType(0);
        year = itempath.getDate(0);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(items);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        AllList(GetData(search_title));
        return null;
    }

    private void AllList(Document doc) {
        if (doc != null) {
            if (items == null) items = new ItemVideo();
            String[] array = doc.body().text().split("\\},\\{\"t");
            for (String anArray : array) {
                String title_m = "error", year_m = "error", year_n = year, url = "error",
                        season = "error", episode = "error", translator = "error",
                        id = "error", id_trans = "error", type_m = "error", title_en = "error";

                if (anArray.contains("itle_ru") && !anArray.contains("itle_ru\":null")) {
                    title_m = anArray.split("itle_ru\":\"")[1].split("\"")[0].trim();
                }
                if (anArray.contains("title_en") && !anArray.contains("title_en\":null")) {
                    title_en = anArray.split("title_en\":\"")[1].split("\"")[0].trim()
                            .replace("\\u0026", "&");
                }

                boolean en_title = false;
                if (!itempath.getSubTitle(0).equals("error") && !title_en.equals("error")) {
                    String en_curr = itempath.getSubTitle(0).toLowerCase().replace("ё", "е")
                            .replace(".", "-").replace("'", "").trim();
                    String en_this = title_en.toLowerCase().replace("ё", "е")
                            .replace(".", "-").replace("'", "").trim();
                    if (en_curr.contains(en_this) || en_this.contains(en_curr)) {
                        en_title = true;
                    }
                }
                if (anArray.contains("year\":") && !anArray.contains("year\":null")) {
                    year_m = anArray.split("year\":")[1].split(",")[0].trim();
                } else if (anArray.contains("year\":null") && en_title) year_m = year_n;
                if (year_n.equals("serial") && en_title) year_n = year_m;
                if (DetailActivity.url.contains("animevost") || DetailActivity.url.contains("coldfilm"))
                    year_m = year_n;


                String sname = search_title.toLowerCase().replace("ё", "е")
                        .replace(".", "-").trim();
                String stitle = title_m.toLowerCase().replace("ё", "е")
                        .replace(".", "-").trim();
                if (year_n.contains("serial")) year_n = year_m;

                Log.d(TAG, "AllList: " + sname + "/" + stitle);
                Log.d(TAG, "AllList: " + en_title + " " + itempath.getSubTitle(0) + "/" + title_en);
                Log.d(TAG, "AllList: " + year_n + "/" + year_m);

                boolean tit = (sname + ".").contains(stitle + ".") || (sname + ",").contains(stitle + ",")
                        || (sname + " ").contains(stitle + " ") || (sname + ":").contains(stitle + ":") ||
                        (sname + ";").contains(stitle + ";") ||
                        (stitle + ".").contains(sname + ".") || (stitle + ",").contains(sname + ",")
                        || (stitle + " ").contains(sname + " ") || (stitle + ":").contains(sname + ":") ||
                        (stitle + ";").contains(sname + ";");

                if (tit && (year_n.trim().equals(year_m.trim()) || en_title)) {
                    if (anArray.contains("kinopoisk_id"))
                        id = anArray.split("kinopoisk_id\":")[1].split(",")[0];
                    if (anArray.contains("kinopoisk_id\":null") && anArray.contains("world_art_id"))
                        id = anArray.split("world_art_id\":")[1].split(",")[0] + "world_art";
                    if (anArray.contains("translator_id"))
                        id_trans = anArray.split("translator_id\":")[1].split(",")[0];

                    if (anArray.contains("season_episodes_count")) {
                        String all = anArray.split("season_episodes_count\":")[1].split("\\]\\}\\]")[0];
                        String[] ep = all.split(",");
                        episode = ep[ep.length - 1].replace("\"episodes\":[", "");

                        String[] seas = all.split("\"season_number\":");
                        season = seas[seas.length - 1].split(",")[0];
                    } else if (anArray.contains("episodes_count")) {
                        season = anArray.split("seasons_count\":")[1].split(",")[0];
                        episode = anArray.split("episodes_count\":")[1].split(",")[0];
                    }

                    if (anArray.contains("translator"))
                        translator = anArray.contains("translator\":null") ? "Неизвестный" :
                                anArray.split("translator\":\"")[1].split("\"")[0];

                    if (anArray.contains("type"))
                        type_m = anArray.split("type\":\"")[1].split("\"")[0];

                    if (anArray.contains("category\":\"anime") && itempath.getType(0).contains("anime"))
                        type_m = this.type;
                    if (anArray.contains("category\":\"anime") && !itempath.getType(0).contains("anime"))
                        type_m = "error";

                    if (anArray.contains("iframe_url"))
                        url = anArray.split("iframe_url\":\"")[1].split("\"")[0];


                    Log.d(TAG, "AllList: " + this.type + "/" + type_m);
                    if (this.type.contains(type_m)) {
                        if (season.equals("error")) items.setTitle("catalog video");
                        else items.setTitle("catalog serial");
                        items.setType(title_m + "\nmoonwalk");
                        items.setToken(TOKEN);
                        items.setId_trans(id_trans);
                        items.setId(id);
                        items.setUrl(url);
                        items.setSeason(season);
                        items.setEpisode(episode);
                        items.setTranslator(translator);
                        Log.d("AllList", "Moonwalk: " + translator + " add " + year_m);
                    }
                }
            }
        }
    }

    private Document GetData(String name){
        Log.d(TAG, "GetData: "+ name.trim() +"/"+ itempath.getSubTitle(0) +" "+ year);
        name = name.trim().replace(" ", "%20");
        name = name.replaceAll("ё", "е");
        final String url = "http://moonwalk.cc/api/videos.json?api_token=" + TOKEN + "&title=" + name;
        try {
            Document htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(5000).ignoreContentType(true).referrer("moonwalk.cc").get();
            Log.d(TAG, "GetdataMoonwalk: get connected to " + url);
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "GetdataMoonwalk: connected false to " + url);
            return null;
        }
    }
}
