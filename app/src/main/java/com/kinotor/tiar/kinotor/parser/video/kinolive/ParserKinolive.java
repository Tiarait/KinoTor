package com.kinotor.tiar.kinotor.parser.video.kinolive;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URLEncoder;

/**
 * Created by Tiar on 02.2018.
 */

public class ParserKinolive extends AsyncTask<Void, Void, Void> {
    private String search_title, type;
    private ItemHtml itempath;
    private OnTaskVideoCallback callback;
    private ItemVideo items;

    public ParserKinolive(ItemHtml item, OnTaskVideoCallback callback){
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
        if (search_title != null)
            ParseHtml(Getdata(search_title));
        return null;
    }

    private void ParseHtml(Document data) {
        if (data != null) {
            if (data.html().contains("id=\"dle-content\"")) {
                String t = data.selectFirst("#dle-content").html();
                if (t.contains("<h1>")){
                    for (String entry : t.split("<h1>")){
//                        Log.e("test", "Kinolive : " + entry);
                        String title_m = "error", title_en = "error", url = "error", season = "error", episode = "error",
                                translator = "Неизвестный", year = "", q = "", type_m = "movie";
                        if (entry.contains("</h1>")) {
                            title_m = entry.split("</h1>")[0].replace("\u00a0", " ").trim();
                        }

                        if (entry.contains("Перевод: ")){
                            translator = entry.split("Перевод: ")[1].split("<br>")[0].trim();
                            if (translator.contains("|"))
                                translator = translator.split("\\|")[0].trim();
                        }
                        if (entry.contains("Качество: ")){
                            q = entry.split("Качество: ")[1].split("<br>")[0].trim();
                            if (q.contains("start-->")){
                                q = q.split("start-->")[1];
                            }
                            if (q.contains("<!--"))
                                q = q.split("<!--")[0];
                        }
//                        if (entry.contains("Год: ")){
//                            year = entry.split("Год: ")[1].trim().split(" ")[0].trim();
//                        } else
                        if (entry.contains("Год выхода: 1")){
                            year = "1" + entry.split("Год выхода: 1")[1].split(" ")[0].trim();
                        } else if (entry.contains("Год выхода: 2")){
                            year = "2" + entry.split("Год выхода: 2")[1].split(" ")[0].trim();
                        } else if (entry.contains("Год премьеры: 1")){
                            year = "1" + entry.split("Год премьеры: 1")[1].split(" ")[0].trim();
                        } else if (entry.contains("Год премьеры: 2")){
                            year = "2" + entry.split("Год премьеры: 2")[1].split(" ")[0].trim();
                        } else if (entry.contains("Год выхода: <a")){
                            year = entry.split("Год выхода: <a")[1].split("</a>")[0].split(">")[1].trim();
                        } else if (entry.contains("Год выхода: ")){
                            year = entry.split("Год выхода: ")[1].trim().split(" ")[0].trim();
                        } else if (entry.contains("Год премьеры: ")){
                            year = entry.split("Год премьеры: ")[1].trim().split(" ")[0].trim();
                        }
                        if (entry.contains("Оригинальное название: ")){
                            title_en = entry.split("Оригинальное название: ")[1].split("<br>")[0].trim();
                        }
                        if (title_m.contains(" сезон")) {
                            type_m = "serial";
                            season = title_m.split(" сезон")[0].trim();
                            if (season.contains(" "))
                                season = season.split(" ")[season.split(" ").length - 1].trim();
                            Log.e("test", "ParseHtml: "+season+"|"+title_m);

                            if (season.contains("("))
                                season = season.split("\\(")[1];

                            if (title_m.contains("- "+season)){
                                title_m = title_m.split("- "+season)[0].trim();
                            } else if (title_m.contains(season)){
                                title_m = title_m.split(season)[0].trim();
                            }
                            if (title_m.endsWith("(")){
                                title_m = title_m.replace("(", "").replace(")","");
                            }
                            if (season.contains("-"))
                                season = season.split("-")[1];
                        }

                        if (entry.contains("<span style=\"color:#FFFF00\">") && entry.contains("</span>")){
                            String span = entry.split("<span style=\"color:#FFFF00\">")[1].split("</span>")[0];
                            if (span.contains("-->")) span = span.split("-->")[1].trim();
                            if (span.contains("<!--")) span = span.split("<!--")[0].trim();
                            if (span.contains("сери")) {
                                type_m = "serial";
                                episode = span.split("сери")[0].trim();
                                if (episode.contains("-"))
                                    episode = episode.split("-")[1].trim();
                            }
                            if (season.equals("error") && !episode.equals("error")){
                                season = "1";
                            }
                        } else if (entry.contains("<span style=\"color:#FF0000\">") && entry.contains("</span>")){
                            String span = entry.split("<span style=\"color:#FF0000\">")[1].split("</span>")[0];
                            if (span.contains("-->")) span = span.split("-->")[1].trim();
                            if (span.contains("<!--")) span = span.split("<!--")[0].trim();
                            if (span.contains("сери")) {
                                type_m = "serial";
                                episode = span.split("сери")[0].trim();
                                if (episode.contains("-"))
                                    episode = episode.split("-")[1].trim();
                            }
                            if (season.equals("error") && !episode.equals("error")){
                                season = "1";
                            }
                        } else if (entry.contains("<span style=\"color:#FF6600\">") && entry.contains("</span>")){
                            String span = entry.split("<span style=\"color:#FF6600\">")[1].split("</span>")[0];
                            if (span.contains("-->")) span = span.split("-->")[1].trim();
                            if (span.contains("<!--")) span = span.split("<!--")[0].trim();
                            if (span.contains("сери")) {
                                type_m = "serial";
                                episode = span.split("сери")[0].trim();
                                if (episode.contains("-"))
                                    episode = episode.split("-")[1].trim();
                            }
                            if (season.equals("error") && !episode.equals("error")){
                                season = "1";
                            }
                        } else if (entry.contains("<span style=\"color:#FF6666\">") && entry.contains("</span>")){
                            String span = entry.split("<span style=\"color:#FF6666\">")[1].split("</span>")[0];
                            if (span.contains("-->")) span = span.split("-->")[1].trim();
                            if (span.contains("<!--")) span = span.split("<!--")[0].trim();
                            if (span.contains("сери")) {
                                type_m = "serial";
                                episode = span.split("сери")[0].trim();
                                if (episode.contains("-"))
                                    episode = episode.split("-")[1].trim();
                            }
                            if (season.equals("error") && !episode.equals("error")){
                                season = "1";
                            }
                        }
                        if (entry.contains("<div class=\"more\">")){
                            String more = entry.split("<div class=\"more\">")[1];
                            if (more.contains("href=\""))
                                url = more.split("href=\"")[1].split("\"")[0].trim();
                        }
                        if (title_en.contains("<font")) {
                            title_en = title_en.split("<font")[1].trim();
                            if (title_en.contains("\">")) {
                                title_en = title_en.split("\">")[1].split("<")[0].trim();
                            }
                        }
                        for (int i = 1; i < 11; i++)
                            title_m = title_m.replace("#"+i+":","").trim();
                        boolean tit =
                                title_m.toLowerCase().replace(":","").trim()
                                        .equals(search_title.toLowerCase().replace(":","").trim()) ||
                                        (title_en.toLowerCase().replace(":","").trim()
                                        .equals(itempath.getSubTitle(0).replace(":","").toLowerCase().trim()) &&
                                        !title_en.toLowerCase().equals("error"));

//                        Log.e("Kinolive", title_m.toLowerCase() + "|" + search_title.toLowerCase());
//                        Log.e("Kinolive", title_en.toLowerCase() + "|" + itempath.getSubTitle(0).toLowerCase());
//                        Log.d("Kinolive", String.valueOf(tit) +"|"+type_m+"/"+type);
                        year = year.replace("</h3>","").trim();
                        if (this.type.contains(type_m) && tit && !title_m.contains("error")) {
                            if (translator.contains("<a")){
                                translator = translator.split("<a")[0]
                                        .replace("@","")
                                        .replace("[","")
                                        .replace("]","").trim();
                            }
                            Document l = getUrlData(url);
                            if (l.html().contains("<iframe")) {
                                String ifrm = l.selectFirst("iframe").attr("src").trim();
                                if (ifrm.contains("?file="))
                                    ifrm = ifrm.split("\\?file=")[1].trim();
                                else if (ifrm.startsWith("//"))
                                    ifrm = "http" + ifrm.trim();
                                if (ifrm.startsWith("/player"))
                                    ifrm = Statics.KINOLIVE_URL + ifrm.trim();

                                if (translator.contains("<!--colorend-->"))
                                    translator = translator.split("<!--colorend-->")[0].trim();
                                if (type_m.equals("movie")) items.setTitle("catalog video");
                                else items.setTitle("catalog serial");
                                if (episode.length() > 4 || episode.contains("error")) episode = "X";
                                items.setType(title_m + " " + year + " " + q + "\nkinolive");
                                items.setToken("");
                                items.setId_trans("");
                                items.setId("error");
                                items.setUrl(ifrm);
                                items.setUrlTrailer("error");
//                            items.setUrlSite("error");
                                items.setSeason(season);
                                items.setEpisode(episode.trim());
                                items.setTranslator(translator.replace("&amp;", "")
                                        .replace("  "," ").trim());
                                Log.d("ParserKinolive", "add " + translator + "|" + title_m + " add");
                            } else Log.e("ParserKinolive", "no iframe " + url);
                        }
                    }
                }
            }
        } else
            Log.d("Kinolive", "ParseHtml: data error");
    }

    private Document getUrlData(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private Document Getdata(String s) {
        String n = s.trim().replace("\u00a0", " ").trim();

        String url = Statics.KINOLIVE_URL + "/index.php?do=search";
        try {
//            Log.e("Kinolive", URLEncoder.encode(n, "windows-1251"));
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .data("do", "search")
                    .data("subaction", "search")
                    .data("search_start", "1")
                    .data("full_search", "1")
                    .data("result_from:", "1")
                    .data("story", URLEncoder.encode(n, "windows-1251").replace("+", " "))
                    .timeout(10000).ignoreContentType(true).post();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
