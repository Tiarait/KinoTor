package com.kinotor.tiar.kinotor.parser.video.moonwalk;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.ui.DetailActivity;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;
import com.kinotor.tiar.kinotor.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URLEncoder;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 19.02.2018.
 */

public class ParserMoonwalk extends AsyncTask<Void, Void, Void> {
    private String search_title, year, type;
    private final String TOKEN = "6eb82f15e2d7c6cbb2fdcebd05a197a2";
    private ItemHtml itempath;
    private OnTaskVideoCallback callback;
    private ItemVideo items;
    private boolean engSearch = false;


    public ParserMoonwalk(ItemHtml item, OnTaskVideoCallback callback) {
        this.itempath = item;
        this.callback = callback;
        this.items = new ItemVideo();

        if (!item.getSubTitle(0).toLowerCase().contains("error"))
            search_title = item.getSubTitle(0);
        else search_title = item.getTitle(0);
        search_title = search_title.trim().replace("\u00a0", " ");
        type = itempath.getType(0);
        year = itempath.getDate(0);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(items);
    }

    @Override
    protected Void doInBackground(Void... voids) {
//        Log.e("test", "doInBackground: "+type);
        if (Statics.KP_ID.contains("error")) {
            if (!Statics.MOON_ID.contains("error"))
                getMoonKp(GetDataMoon(Statics.MOON_ID));
            else AllListTitle(GetDataTitle(search_title));
        } else AllListId(GetDataId(Statics.KP_ID));
        return null;
    }

    private void AllListTitle(Document doc) {
        if (doc != null) {
            Log.e("test", "AllListMoon eng title: " + doc.text());

            if (items == null) items = new ItemVideo();
            String[] array = doc.body().text().split("\\},\\{\"t");
            for (String anArray : array) {
                String title_m = "error", year_m = "error", year_n = year.toLowerCase().trim(), url = "error",
                        season = "error", episode = "error", translator = "error", tok = "error", kp = "error",
                        id_trans = "error", type_m = "error", title_en = "error", trailer = "error";
                String q = "";

                if (anArray.contains("itle_ru") && !anArray.contains("itle_ru\":null")) {
                    title_m = anArray.split("itle_ru\":\"")[1].split("\"")[0].trim()
                    .replace("\\t", "");
                }
                if (anArray.contains("title_en") && !anArray.contains("title_en\":null")) {
                    title_en = anArray.split("title_en\":\"")[1].split("\"")[0].trim()
                            .replace("\\u0026", "&");
                }
                if (anArray.contains("source_type\":") && !anArray.contains("source_type\":null")) {
                    q = " (" + anArray.split("source_type\":\"")[1].split("\"")[0].trim() + ")";
                }
                if (anArray.contains("token\":\"")) {
                    tok = "token="+anArray.split("token\":\"")[1].split("\"")[0].trim();
                }
//                else Log.e("test", "AllListMoon: "+anArray);
//                Log.e("test", "AllListMoon token: "+tok);
                if (anArray.contains("kinopoisk_id\":") && !anArray.contains("kinopoisk_id\":null")) {
                    kp = anArray.split("kinopoisk_id\":")[1].split(",")[0].trim();
                } else if (anArray.contains("world_art_id\":") && !anArray.contains("world_art_id\":null")) {
                    kp = "world_art" + anArray.split("world_art_id\":")[1].split(",")[0].trim();
                }

                //Проверка на совпадение eng названия
                String yearTrue = "";
                String en_curr = itempath.getSubTitle(0).toLowerCase().replace("ё", "е")
                        .replace(".", "-").replace("'", "").trim();
                String en_this = title_en.toLowerCase().replace("ё", "е")
                        .replace(".", "-").replace("'", "").trim();
                boolean en_title = en_curr.equals(en_this);
                //------------------------------------------------

                if (anArray.contains("year\":") && !anArray.contains("year\":null")) {
                    year_m = anArray.split("year\":")[1].split(",")[0].trim();
                    yearTrue = year_m;
                } else if (anArray.contains("year\":null") && en_title) {
                    year_m = year_n;
                }
                if (year_n.equals("serial") && en_title) year_n = year_m;
                if (DetailActivity.url.contains("animevost") || DetailActivity.url.contains("coldfilm"))
                    year_m = year_n;

                String sname = itempath.getTitle(0).toLowerCase().replace("ё", "е")
                        .replace(".", "-").replace("("+itempath.getDate(0)+")","").trim();
                String stitle;

                if (itempath.getTitle(0).trim().toLowerCase()
                        .equals(itempath.getSubTitle(0).trim().toLowerCase())) {
                    stitle = en_this.toLowerCase().replace("ё", "е")
                            .replace(".", "-").trim();
                } else {
                    stitle = title_m.toLowerCase().replace("ё", "е")
                            .replace(".", "-").trim();
                }
                if (year_n.contains("serial")) year_n = year_m;
                if (year_m.contains("error")) year_m = year_n;

                boolean tit = new Utils().trueTitle(sname, stitle);

//                Log.d("AllListMoon", "AllListMoon name: " + tit + " " + sname + "/" + stitle);
//                Log.d("AllListMoon", "AllListMoon eng: " + en_title + " " + itempath.getSubTitle(0) + "/" + title_en);
//                Log.d("AllListMoon", "AllListMoon year: " + year_n + "/" + year_m + "/true-" + yearTrue);

                if (tit && year_n.trim().equals(year_m.trim()) || en_title) {
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
                    if (this.type.contains("error"))
                        this.type = type_m;

                    if (anArray.contains("\"iframe_url\":"))
                        url = anArray.split("\"iframe_url\":\"")[1].split("\"")[0];
                    if (anArray.contains("trailer_iframe_url\":") && !anArray.contains("trailer_iframe_url\":null"))
                        trailer = anArray.split("trailer_iframe_url\":\"")[1].split("\"")[0];

                    String tr = trailer.contains("error") ? "" : " [+trailer]";

//                    Log.d(TAG, "AllListMoon: " + this.type + "/" + type_m);
//                    if (tit && en_title && year_n.equals(year_m))
//                        this.type = type_m;
                    if (this.type.trim().contains(type_m.trim()) && !title_m.equals("error")) {
                        if (DetailActivity.url.contains(Statics.FILMIX_URL)){
//                            Log.e(TAG, "AllListMoon: "+yearTrue+"|"+itempath.getDate(0) );
//                            Log.e(TAG, "AllListMoon: "+itempath.getSubTitle(0)+"|"+title_en );
                            boolean enT = itempath.getSubTitle(0).toLowerCase().equals(title_en.toLowerCase()) ||
                                    itempath.getSubTitle(0).toLowerCase().contains("error");
                            if (yearTrue.trim().equals(itempath.getDate(0).trim()) || enT){
                                if (Statics.KP_ID.contains("error"))
                                    Statics.KP_ID = kp;
                                if (kp.contains("error"))
                                    kp = tok;
//                                Log.e("test", "AllListMoon kp: new-" + kp +"|"+ Statics.KP_ID);

                                if (season.equals("error")) items.setTitle("catalog video");
                                else items.setTitle("catalog serial");
                                if (title_m.trim().toLowerCase().equals("error") || title_m.trim().toLowerCase().equals("null"))
                                    title_m = title_en;
                                if (yearTrue.trim().toLowerCase().equals("error")) yearTrue = "";
                                if (q.trim().toLowerCase().equals("error")) q = "";
                                items.setType(title_m + " " + yearTrue + q + "\nmoonwalk" + tr);
                                items.setToken(TOKEN);
                                items.setId_trans(id_trans);
                                items.setId(kp);
                                items.setUrl(url);
                                items.setUrlTrailer(trailer);
                                items.setSeason(season.replace("[", "").trim());
                                items.setEpisode(episode.replace("[", "").trim());
                                items.setTranslator(translator);
                            }
                        } else {

                            if (Statics.KP_ID.contains("error"))
                                Statics.KP_ID = kp;
//                            else Log.e("test", "AllListMoon kp: new-" + kp +"|"+ Statics.KP_ID);
                            if (kp.contains("error"))
                                kp = tok;
                            if (season.equals("error")) items.setTitle("catalog video");
                            else items.setTitle("catalog serial");
                            if (title_m.trim().toLowerCase().equals("error") || title_m.trim().toLowerCase().equals("null"))
                                title_m = title_en;
                            if (yearTrue.trim().toLowerCase().equals("error")) yearTrue = "";
                            if (q.trim().toLowerCase().equals("error")) q = "";
                            items.setType(title_m + " " + yearTrue + q + "\nmoonwalk" + tr);
                            items.setToken(TOKEN);
                            items.setId_trans(id_trans);
                            items.setId(kp);
                            items.setUrl(url);
//                        items.setUrlSite("error");
                            items.setUrlTrailer(trailer);
                            items.setSeason(season.replace("[", "").trim());
                            items.setEpisode(episode.replace("[", "").trim());
                            items.setTranslator(translator);
//                            Log.d("AllListMoon", "Moonwalk: " + translator + " add " + title_m + "|||" + yearTrue);
                        }
                    }
                }
            }
        } else if (!engSearch) {

            if (itempath.getTitle(0).contains("("))
                search_title = new Utils().replaceTitle(itempath.getTitle(0).split("\\(")[0]);
            else search_title = new Utils().replaceTitle(itempath.getTitle(0));

//            Log.e("test", "AllListMoon eng title: " + search_title);
            engSearch = true;
            AllListTitle(GetDataTitle(search_title));
        }
    }

    private void AllListId(Document doc) {
        if (doc != null) {
            if (items == null) items = new ItemVideo();
            String[] array = doc.body().text().split("\\},\\{\"t");
            for (String anArray : array) {
                String title_m = "error", title_en = "error", year_m = "error", url = "error",
                        season = "error", episode = "error", translator = "error",
                        id = "error", id_trans = "error", trailer = "error";
                String q = "";

                if (anArray.contains("itle_ru") && !anArray.contains("itle_ru\":null")) {
                    title_m = anArray.split("itle_ru\":\"")[1].split("\"")[0].trim();
                }
                if (anArray.contains("itle_en") && !anArray.contains("itle_en\":null")) {
                    title_en = anArray.split("itle_en\":\"")[1].split("\"")[0].trim();
                }
                if (anArray.contains("source_type\":") && !anArray.contains("source_type\":null"))
                    q = " (" + anArray.split("source_type\":\"")[1].split("\"")[0].trim() + ")";
                if (anArray.contains("year\":") && !anArray.contains("year\":null"))
                    year_m = anArray.split("year\":")[1].split(",")[0].trim();
                if (anArray.contains("kinopoisk_id\":") && !anArray.contains("kinopoisk_id\":null"))
                    id = anArray.split("kinopoisk_id\":")[1].split(",")[0];
                if (anArray.contains("translator_id\":") && !anArray.contains("translator_id\":null"))
                    id_trans = anArray.split("translator_id\":")[1].split(",")[0];
                if (anArray.contains("season_episodes_count\":")) {
                    String all = anArray.split("season_episodes_count\":")[1].split("\\]\\}\\]")[0];
                    String[] ep = all.split(",");
                    episode = ep[ep.length - 1].replace("\"episodes\":[", "");
                    String[] seas = all.split("\"season_number\":");
                    season = seas[seas.length - 1].split(",")[0];
                } else if (anArray.contains("episodes_count\":")) {
                    season = anArray.split("seasons_count\":")[1].split(",")[0];
                    episode = anArray.split("episodes_count\":")[1].split(",")[0];
                }

                if (anArray.contains("translator\":"))
                    translator = anArray.contains("translator\":null") ? "Неизвестный" :
                            anArray.split("translator\":\"")[1].split("\"")[0];

                if (anArray.contains("\"iframe_url\":"))
                    url = anArray.split("\"iframe_url\":\"")[1].split("\"")[0];
                if (anArray.contains("trailer_iframe_url\":") && !anArray.contains("trailer_iframe_url\":null"))
                    trailer = anArray.split("trailer_iframe_url\":\"")[1].split("\"")[0];

                String tr = trailer.contains("error") ? "" : " [+trailer]";

                if (season.equals("error")) items.setTitle("catalog video");
                else items.setTitle("catalog serial");
                if (title_m.trim().toLowerCase().equals("error") || title_m.trim().toLowerCase().equals("null"))
                    title_m = title_en;
                if (year_m.trim().toLowerCase().equals("error")) year_m = "";
                if (q.trim().toLowerCase().equals("error")) q = "";
                items.setType(title_m + " " + year_m + q + "\nmoonwalk" + tr);
                items.setToken(TOKEN);
                items.setId_trans(id_trans);
                items.setId(id);
                items.setUrl(url);
                items.setUrlTrailer(trailer);
                items.setSeason(season.replace("[", "").trim());
                items.setEpisode(episode.replace("[", "").trim());
                items.setTranslator(translator);
//                Log.d("AllListMoon", "Moonwalk: " + translator + " add " + year_m);
            }
        }
    }

    private void getMoonKp(Document doc) {
        if (doc != null) {
            if (doc.text().contains("kinopoisk_id\":") && !doc.text().contains("kinopoisk_id\":null")) {
                if (Statics.KP_ID.contains("error"))
                    Statics.KP_ID = doc.text().split("kinopoisk_id\":")[1].split(",")[0].trim();
                else Log.e("test", "AllListMoon kp: new-" + doc.text().split("kinopoisk_id\":")[1].split(",")[0].trim() +"|"+ Statics.KP_ID);
            }
//            Log.d(TAG, "AllListMoon kp: " + Statics.KP_ID);
            if (Statics.KP_ID.contains("error"))
                AllListTitle(GetDataTitle(search_title));
            else AllListId(GetDataId(Statics.KP_ID));
        }
    }

    private Document GetDataMoon(String s){
        try {
            String url = "http://moonwalk.cc/api/movie.json?api_token=" + TOKEN + "&token=" + s;
            Log.d(TAG, "GetdataMoonwalk: get connected to " + url);
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(5000).ignoreContentType(true).referrer("moonwalk.cc").get();
        } catch (Exception e) {
            try {
                String url = "http://moonwalk.cc/api/serial.json?api_token=" + TOKEN + "&token=" + s;
                Log.d(TAG, "GetdataMoonwalk: get connected to " + url);
                return Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .timeout(5000).ignoreContentType(true).referrer("moonwalk.cc").get();
            } catch (IOException e1) {
                e1.printStackTrace();
                return null;
            }
        }
    }

    private Document GetDataTitle(String s){
        Log.d(TAG, "GetDataTitle: "+ s.trim() +"/"+ itempath.getSubTitle(0) +" "+ year);
        String n = s.trim().replace("\u00a0", " ").replace("ё", "е");

        String url = "http://moonwalk.cc/api/videos.json?api_token=" + TOKEN + "&title=";
        try {
            n = URLEncoder.encode(n, "UTF-8");
            Document htmlDoc = Jsoup.connect(url + n)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(5000).ignoreContentType(true).referrer("moonwalk.cc").get();
            Log.d(TAG, "GetdataMoonwalk: get connected to " + htmlDoc.location());
            return htmlDoc;
        } catch (Exception e) {
            Log.e(TAG, "GetdataMoonwalk: connected false to " + url + n);
            return null;
        }
    }

    private Document GetDataId(String s){
        Log.d(TAG, "GetDataId: "+ s.trim() +"/"+ itempath.getSubTitle(0) +"/"+ s);
        String url;
        if (s.contains("[pornolab]")) url = "http://moonwalk.cc/api/videos.json?api_token=" + TOKEN + "&pornolab_id=" +
                s.replace("[pornolab]", "").trim();
        else url = "http://moonwalk.cc/api/videos.json?api_token=" + TOKEN + "&kinopoisk_id=" + s.trim();
        try {
            Document htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(5000).ignoreContentType(true).referrer("moonwalk.cc").get();
            Log.d(TAG, "GetdataMoonwalk: get connected to " + htmlDoc.location());
            return htmlDoc;
        } catch (Exception e) {
            Log.e(TAG, "GetdataMoonwalk: connected false to " + url);
            if (!s.contains("[pornolab]"))
                AllListId(GetDataId("[pornolab]"+s));
            return null;
        }
    }
}
