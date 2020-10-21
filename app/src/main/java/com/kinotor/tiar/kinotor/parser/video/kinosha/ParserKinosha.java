package com.kinotor.tiar.kinotor.parser.video.kinosha;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class ParserKinosha extends AsyncTask<Void, Void, Void> {
    private String search_title, type;
    private ItemHtml itempath;
    private OnTaskVideoCallback callback;
    private ItemVideo items;

    public ParserKinosha(ItemHtml item, OnTaskVideoCallback callback){
        this.itempath = item;
        this.callback = callback;
        this.items = new ItemVideo();

        search_title = item.getTitle(0).trim();
        if (search_title.contains("("))
            search_title = search_title.split("\\(")[0].trim();
        if (search_title.contains("["))
            search_title = search_title.split("\\[")[0].trim();
        search_title = search_title.trim().replace("\u00a0", " ");
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
            if (data.html().contains("id=\"dle-content\"")) {
                Elements allEntries = data.select(".item");
                for (Element entry : allEntries) {
                    String title_m = "error", url = "error", season = "error", episode = "error",
                            translator = "error", id = "error", year = "", type_m = "error";

                    if (entry.html().contains("class=\"title\"")) {
                        title_m = entry.select(".title").first().text().trim();
                        if (title_m.contains(" (")) {
                            year = title_m.split(" \\(")[1].split("\\)")[0].trim();
                            title_m = title_m.split(" \\(")[0].trim();
                        }
                        url = entry.select(".title").first().attr("href");
                        if (url.contains("/") && url.contains("-"))
                            id = url.split("/")[url.split("/").length-1].split("-")[0];
                        else id = "0";
                    }
                    if (entry.html().contains("class=\"serial-info\"")) {
                        season = entry.select(".se").text().split(" ")[0];
                        episode = entry.select(".ep").text().split(" ")[0];
                        type_m = "serial";
                    } else {
                        type_m = "movie";
                    }
                    if (entry.html().contains("class=\"about\"")) {
                        Element about = entry.select(".about").first();
                        if (about.html().contains("class=\"li\""))
                            translator = about.select(".li").last().text().trim();
                    }
                    String q = "";
                    if (entry.html().contains("class=\"li link-cat\"")) {
                        q = " (" + entry.select(".li.link-cat").text().trim() + ")";
                    }
                    String t_m = title_m.toLowerCase().replace("ё", "е").trim();
                    String s = itempath.getTitle(0).contains("(") ?
                            itempath.getTitle(0).split("\\(")[0].trim().replace("ё", "е") :
                            itempath.getTitle(0).trim().replace("ё", "е");
                    if (s.contains("[")) s = s.split("\\[")[0].trim();
                    boolean tit = t_m.equals(s.toLowerCase());


//                    Log.e(TAG, "ParserKinosha: ||" + s + "||" + t_m+"||");
//                    Log.e(TAG, "ParserKinosha: " + this.type + " " + type_m);
                    boolean tp = true;
                    if (!this.type.contains("error"))
                        tp = this.type.contains(type_m);
                    if (tp && tit) {
                        if (type_m.equals("movie")) items.setTitle("catalog video");
                        else items.setTitle("catalog serial");
                        items.setType(title_m + " " + year + q + "\nkinosha");
                        items.setToken("");
                        items.setId_trans("");
                        items.setId(id);
                        items.setUrl(url);
                        items.setUrlTrailer("error");
//                        items.setUrlSite("error");
                        items.setSeason(season);
                        items.setEpisode(episode);
                        items.setTranslator(translator);
                        Log.d(TAG, "ParserKinosha: " + translator + " add");
                    }
                }
            }
        } else
            Log.d(TAG, "ParseHtml: data error");
    }

    private Document Getdata(String s) {
        if (s.contains("[")) s = s.split("\\[")[0].trim();
        String n = s.trim().replace("\u00a0", "%20").trim();
        n = n.trim().replace(" ", "%20");

        String url = Statics.KINOSHA_URL + "/search/f:" + n;
        try {
            return Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .timeout(5000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
