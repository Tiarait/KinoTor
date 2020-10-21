package com.kinotor.tiar.kinotor.parser.video.animevost;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by Tiar on 02.2018.
 */

public class ParserVAnimevost extends AsyncTask<Void, Void, Void> {
    private String search_title, type;
    private ItemHtml itempath;
    private OnTaskVideoCallback callback;
    private ItemVideo items;

    public ParserVAnimevost(ItemHtml item, OnTaskVideoCallback callback){
        this.itempath = item;
        this.callback = callback;
        this.items = new ItemVideo();

        if (!item.getSubTitle(0).toLowerCase().contains("error")) {
            search_title = item.getSubTitle(0);
        } else {
            search_title = item.getTitle(0).trim();
            if (search_title.contains("("))
                search_title = search_title.split("\\(")[0].trim();
            if (search_title.contains("["))
                search_title = search_title.split("\\[")[0].trim();
        }
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
            if (data.html().contains("class=\"shortstoryHead\"")) {
                for (Element entry : data.select(".shortstoryHead")){
                    String title_m, title_en = "error", url, season = "error", episode = "error",
                            translator, type_m;
                    if (entry.html().contains("<a")) {
                        title_m = entry.select("a").text().replace("\u00a0", " ").trim();
                        url = entry.select("a").attr("href");
                        translator = "AnimeVost";
                        String t = type.contains("movie") ? "/tv-speshl/" : "/tv/";
                        String s = type.contains("movie") ? "-film/" : "/ona/";

                        if (title_m.contains("/")) {
                            title_en = title_m.split("/")[1].trim();
                            if (title_en.contains("[")) {
                                title_en = title_en.split("\\[")[0].trim();
                            }
                            if (title_en.contains("(")) {
                                title_en = title_en.split("\\(")[0].trim();
                            }
                            title_en = title_en.replace("Second Season","");
                        }

//                        Log.e("test", "ParseHtml: "+title_m);
                        if ((title_m.toLowerCase().contains(search_title.replace("'","").toLowerCase()) ||
                                search_title.replace("'","").toLowerCase().contains(title_en.toLowerCase())) &&
                                (url.contains(t) || url.contains(s) || url.contains("/ova/"))) {
                            if (title_m.contains("[")) {
                                type_m = "serial anime";
                                episode = title_m.split("\\[")[1].split("\\]")[0].trim();
                                if (episode.contains("из"))
                                    episode = episode.split("из")[0].trim();
                                if (episode.contains("-")) episode = episode.split("-")[1].trim();
                                season = "1";
                                title_m = title_m.split("\\[")[0].trim();
                            } else type_m = "movie anime";

                            if (title_m.contains("/")) {
                                title_m = title_m.split("/")[1].trim();
                            }

                            if (!title_m.contains("error")) {
                                if (type_m.contains("movie")) items.setTitle("catalog video");
                                else items.setTitle("catalog serial");
                                items.setType(title_m + "\nanimevost");
                                items.setToken("");
                                items.setId_trans("");
                                items.setId("error");
                                items.setUrl(url);
                                items.setUrlTrailer("error");
//                            items.setUrlSite("error");
                                items.setSeason(season.trim());
                                items.setEpisode(episode.trim());
                                items.setTranslator(translator);
                            }
                        }
                    }
                }
            } else Log.d("Animevost", "ParseHtml: data search error");
        } else
            Log.d("Animevost", "ParseHtml: data error");
    }

    private Document Getdata(String s) {
        String n = s.trim().replace("\u00a0", " ").trim();

        String url = Statics.ANIMEVOST_URL + "/";
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .data("do", "search")
                    .data("subaction", "search")
                    .data("search_start", "1")
                    .data("story", n)
                    .timeout(10000).ignoreContentType(true).post();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
