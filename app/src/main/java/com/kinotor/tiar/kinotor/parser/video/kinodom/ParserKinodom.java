package com.kinotor.tiar.kinotor.parser.video.kinodom;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URLEncoder;

/**
 * Created by Tiar on 02.2018.
 */

public class ParserKinodom extends AsyncTask<Void, Void, Void> {
    private String search_title;
    private ItemHtml itempath;
    private OnTaskVideoCallback callback;
    private ItemVideo items;

    public ParserKinodom(ItemHtml item, OnTaskVideoCallback callback){
        this.itempath = item;
        this.callback = callback;
        this.items = new ItemVideo();

        search_title = item.getTitle(0).trim();
        if (search_title.contains("("))
            search_title = search_title.split("\\(")[0].trim();
        if (search_title.contains("["))
            search_title = search_title.split("\\[")[0].trim();
        search_title = search_title.trim().replace("\u00a0", " ");
    }

    @Override
    protected void onPostExecute(Void aVoid) {callback.OnCompleted(items); }

    @Override
    protected Void doInBackground(Void... voids) {
        ParseHtml(getDataPost(search_title));
        return null;
    }

    private void ParseHtml(Document data) {
        if (data != null) {
//            Log.e("test", "ParseHtml: "+data.select("#dle-content") );
            if (data.html().contains("class=\"post shortstory")) {
                for (Element entry : data.select(".post.shortstory")){
                    String e = entry.html();
                    String title_m, title_en, url, season = "error", episode = "error";
                    if (e.contains("class=\"post-title\"")) {
                        title_m = entry.select(".post-title").text().replace("\u00a0", " ").trim();
                        url = entry.select("a").first().attr("href");

                        if (e.contains("class=\"post-title-eng")) {
                            title_en = entry.select(".post-title-eng").text()
                                    .replace("\u00a0", " ").trim();
                        } else title_en = "error";

                        if (search_title.contains("."))
                            search_title = search_title.split("\\.")[0].trim();

                        Log.e("kinodom", "ParseHtml: "+title_m.toLowerCase().trim()+"|"+search_title.toLowerCase().trim() );
                        Log.e("kinodom", "ParseHtml: "+title_en.toLowerCase().trim()+"|"+itempath.getSubTitle(0).toLowerCase().trim() );
                        boolean tit =
                                (title_en.toLowerCase().trim().equals(itempath.getSubTitle(0).toLowerCase().trim())
                                        && !itempath.getSubTitle(0).contains("error")
                                ) ||
                                title_m.toLowerCase().trim().contains(search_title.toLowerCase().trim());
                        if (itempath.getSubTitle(0).contains("error"))
                            tit = title_m.toLowerCase().trim().equals(search_title.toLowerCase().trim());
                        if (tit) {
                            Document doc = getData(url);
                            if (doc != null) {
                                String d = doc.body().html();
                                if (d.contains("pl=/")) {
                                    String pl = d.split("pl=/")[1].split("'")[0];
                                    if (d.contains("<span data-link=\"")) {
                                        for (String v : d.split("<span data-link=\"")) {
//                                            Log.e("Kinodom", "ParseHtml: "+v );
                                            String u = v.split("\">")[0];
                                            String t = v.split("\">")[1].split("</span>")[0].trim();
                                            if (!title_m.contains("error") && u.contains("/play/")) {
                                                Log.d("Kinodom", Statics.KINODOM_URL+"/"+pl+u+".json");
                                                Document docvid = getData(Statics.KINODOM_URL+"/"+pl+u+".json");
                                                if (docvid != null) {
                                                    String text = docvid.text().replace(" ", "");
                                                    if (text.contains("Сезон"))
                                                        season = text.split("Сезон")[text.split("Сезон").length - 1]
                                                                .split("\"")[0];
                                                    else season = "X";
                                                    if (text.contains("Серия"))
                                                        episode = text.split("Серия")[text.split("Серия").length - 1]
                                                                .split("\"")[0];
                                                    else episode = "X";
                                                    if (!title_en.contains("error") && !title_en.isEmpty())
                                                        title_en = "/" + title_en;
                                                    items.setTitle("catalog serial");
                                                    items.setType(title_m + title_en + "\nkinodom");
                                                    items.setToken("");
                                                    items.setId_trans("");
                                                    items.setId("error");
                                                    items.setUrl(text);
                                                    items.setUrlTrailer("error");
                                                    items.setSeason(season);
                                                    items.setEpisode(episode);
                                                    items.setTranslator(t);
                                                } else Log.d("Kinodom", "ParseHtml: wtf last");
                                            } else Log.d("Kinodom", "ParseHtml: wtf " + u);
                                        }
                                    } else Log.d("Kinodom", "ParseHtml: no span");
                                } else Log.d("Kinodom", "ParseHtml: no pl");
                            } else Log.d("Kinodom", "ParseHtml: data error 2");
                        } else Log.d("Kinodom", "ParseHtml: no equals title");
                    } else Log.d("Kinodom", "ParseHtml: no title");
                }
            } else Log.d("Kinodom", "ParseHtml: data search error");
        } else Log.d("Kinodom", "ParseHtml: data error wtf");
    }

    private Document getData(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Document getDataPost(String s) {
        String n = s.trim().replace("\u00a0", " ").trim();
        Log.e("Kinodom", "Getdata: "+n);
        String url = Statics.KINODOM_URL + "/index.php?do=search";
        try {
            n = URLEncoder.encode(n, "windows-1251");
            Log.e("Kinodom", "Getdata: "+n.replace("+", " "));

            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("X-Requested-With", "XMLHttpRequest")
                    //.header("Cookie", loginCookies)
                    .data("do", "search")
                    .data("subaction", "search")
                    .data("story", n.replace("+", " "))
                    .timeout(10000).ignoreContentType(true).post();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
