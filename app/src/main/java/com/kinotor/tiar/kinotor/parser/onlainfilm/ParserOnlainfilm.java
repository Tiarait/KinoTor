package com.kinotor.tiar.kinotor.parser.onlainfilm;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;
import com.kinotor.tiar.kinotor.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class ParserOnlainfilm extends AsyncTask<Void, Void, Void> {
    private String search_title, type;
    private ItemHtml itempath;
    private OnTaskVideoCallback callback;
    private ItemVideo items;

    public ParserOnlainfilm(ItemHtml item, OnTaskVideoCallback callback){
        this.itempath = item;
        this.callback = callback;
        this.items = new ItemVideo();

        if (itempath.getTitle(0).contains("("))
            search_title = itempath.getTitle(0).split("\\(")[0];
        else search_title = itempath.getTitle(0);
        search_title = replaceTitle(search_title.trim().replace("\u00a0", " "));
        type = itempath.getType(0);
    }

    @Override
    protected void onPostExecute(Void aVoid) {callback.OnCompleted(items); }

    @Override
    protected Void doInBackground(Void... voids) {
        ParseHtml(Getdata(search_title));
        return null;
    }

    private void ParseHtml(Document data) {
        if (data != null) {
            String title_m = "error", url = "error", season = "error", episode = "error",
                    translator = "error", id = "error", year = "", type_m = "error";
            Log.d("PrserOnlainfilm", data.select(".poisk-zapros").text());
            if (data.html().contains("class=\"films-list")) {
                Elements allEntries = data.select(".film-view");
                for (Element entry : allEntries) {
                    if (entry.html().contains("class=\"fv-title")) {
                        title_m = entry.select(".fv-title").first().text().trim();
                        url = "http://onlainfilm.co" +
                                entry.select(".fv-title").first().attr("href");
                        if (title_m.contains(" сезон") ||
                                (title_m.contains(",") && title_m.contains(" ..."))) {
                            if (title_m.contains(" сезон"))
                                title_m = title_m.split(" сезон")[0].trim();
                            else if (title_m.contains(" ..."))
                                title_m = title_m.split(" \\.\\.\\.")[0];
                            if (title_m.contains(",")) {
                                season = title_m.split(",")[title_m.split(",").length - 1];
                            } else season = "1";
                        }
                        if (title_m.contains(" 1,")) {
                            title_m = title_m.split(" 1,")[0].trim();
                        }

                    }
                    if (entry.html().contains("class=\"fv-image\"")) {
                        episode = entry.select(".fv-image span").text();
                        if (episode.contains("| "))
                            episode = episode.split("\\| ")[1];
                        if (episode.contains("серия")) {
                            episode = episode.split(" серия")[0];
                            type_m = "serial";
                            if (season.contains("error"))
                                season = "1";
                        } else type_m = "movie";
                    }
                    if (entry.html().contains("class=\"fvi-other\"")) {
                        if (entry.select(".fvi-other").html().contains("Перевод</span>"))
                            translator = entry.select(".fvi-other").html()
                                    .split("Перевод</span>")[1].split("</div>")[0].trim();
                        else translator = "Неизвестный";
                        if (entry.select(".fvi-other").html().contains("Год</span>"))
                            year = entry.select(".fvi-other").html()
                                    .split("Год</span>")[1].split("</div>")[0].trim();
                        translator = translator.replace("\"", "").trim();
                    }
                    String q = "";
                    if (entry.html().contains("class=\"film-quality\"")) {
                        q = " (" + entry.select(".film-quality").text().trim() + ")";
                    }

                    String t_m = title_m.toLowerCase().replace("ё", "е").trim();
                    String t_n = search_title.toLowerCase().replace("ё", "е").trim();
                    boolean tit = new Utils().trueTitle(t_m, t_n);
//                    if (tit) {
//                        boolean titN = false;
//                        for (int i = 1; i < 5; i++) {
//                            if (!search_title.contains(" " + i) && !title_m.contains(" " + i)) {
//                                titN = true;
//                                break;
//                            } else if (search_title.contains(" " + i) && title_m.contains(" " + i)) {
//                                titN = true;
//                                break;
//                            }
//                        }
//                        tit = titN;
//                    }

                    Log.d(TAG, "ParserOnlainfilm: " + this.type + " " + type_m);
                    Log.d(TAG, "ParserOnlainfilm: " + tit + "|" + title_m + "|" + search_title);

                    if (this.type.contains(type_m) && tit) {
                        if (type_m.equals("movie")) items.setTitle("catalog video");
                        else items.setTitle("catalog serial");
                        items.setType(title_m + " " + year + q + "\nonlainfilm");
                        items.setToken("");
                        items.setId_trans("");
                        items.setId(id);
                        items.setUrl(url);
                        items.setSeason(season);
                        items.setEpisode(episode);
                        items.setTranslator(translator);
                        Log.d(TAG, "ParserOnlainfilm: " + translator + " add");
                    }
                }
            }
        } else
            Log.d(TAG, "ParseHtml: data error");
    }

    private String replaceTitle(String t) {
        if (t.trim().equals("Агенты «Щ.И.Т.»") || t.trim().equals("Агенты ЩИТ"))
            return "Агенты Щ.И.Т.";
        if (t.trim().equals("Арчер"))
            return "Спецагент Арчер";
        else return t;
    }

    private Document Getdata(String s) {
        String n = s.trim().replace("\u00a0", " ").trim();

        String url = "http://onlainfilm.co/load/";
        try {
            Document htmlDoc = Jsoup.connect(url)
                    .data("query", n)
                    .data("a", "2")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(5000).ignoreContentType(true).post();

            Log.d(TAG, "Getdata: get connected to " + htmlDoc.location() + n);
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "Getdata: connected false to " + url + n);
            return null;
        }
    }
}
