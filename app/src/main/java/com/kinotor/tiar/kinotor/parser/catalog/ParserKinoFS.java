package com.kinotor.tiar.kinotor.parser.catalog;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskCallback;
import com.kinotor.tiar.kinotor.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 30.01.2018.
 */

public class ParserKinoFS extends AsyncTask<Void, Void, Void> {
    private String url;
    private ArrayList<ItemHtml> items;
    private ItemHtml itempath;
    private OnTaskCallback callback;

    public ParserKinoFS(String url, ArrayList<ItemHtml> items, ItemHtml itempath, OnTaskCallback callback) {
        this.url = url;
        if (items != null) this.items = items;
        else this.items = new ArrayList<>();
        if (itempath != null) this.itempath = itempath;
        else this.itempath = new ItemHtml();
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(items, itempath);
        //if (page.equals("detail")) DetailActivity.loadDataDetail();
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ParseHtml(Getdata(url));
        return null;
    }

    private void ParseHtml(Document data) {
        if (data != null) {
            String title = "error parsing", url_entry = "error parsing", img = "error parsing",
                    quality = "error parsing", rating = "error parsing", kpId = "error";
            String name = "error", subname = "error", year = "error parsing", country = "error parsing",
                    genre = "error parsing", time = "error parsing", translator = "error parsing",
                    director = "error parsing", actors = "error parsing", description_t = "error parsing",
                    iframe = "error", type;
            String moretitle = "error", moreurl = "error", moreimg = "error", moreseason = "0",
                    moreseries = "0", morequality = "error";
            String season = "0", series = "0";
            if (data.html().contains("movie-item")) {
                Log.e(TAG, "ParseHtml: ");
                Elements allEntries = data.select(".movie-item");
                for (Element entry : allEntries) {
                    if (entry.html().contains("movie-title")) {
                        title = entry.select(".movie-title").first().text().trim();
                        if (title.contains("(") && title.contains("сезон"))
                            title = title.split("\\(")[0];
                        url_entry = entry.select(".movie-title").first().attr("href");
                        if (!url_entry.startsWith(Statics.KINOFS_URL)) url_entry = Statics.KINOFS_URL + url_entry;
                    }

                    if (entry.html().contains("movie-desc")) {
                        String t = entry.select(".movie-desc").first().text();
                        if (t.contains("Жанр:")) {
                            genre = t.split("Жанр:")[1].trim();
                            if (genre.contains("Страна:"))
                                genre = genre.split("Страна:")[0].trim();
                        }
                        if (t.contains("Качество:")) {
                            season = "0";
                            series = "0";
                            quality = t.split("Качество:")[1].trim().split(" ")[0].trim();
                        } else if (t.contains("Добавлено:")) {
                            if (t.contains("сезон")) {
                                season = t.split("Добавлено:")[1].split("сезон")[0].trim();
                                if (t.contains("серия"))
                                    series = t.split(", ")[1].split("серия")[0].trim();
                            }
                            else {
                                season = "0";
                                series = "0";
                            }
                        }
                    }

                    if (entry.html().contains("img src")) {
                        img = Statics.KINOFS_URL + entry.select("img").first().attr("src");
                    }
                    boolean hide = quality.toLowerCase().contains("ts") && Statics.hideTs;

                    if (!entry.html().contains("<span>Трейлер</span>") && !title.contains("error") && !hide) {
                        itempath.setTitle(title);
                        itempath.setSubTitle(subname);
                        itempath.setDate(year);
                        itempath.setCountry(country);
                        itempath.setImg(img);
                        itempath.setUrl(url_entry);
                        itempath.setQuality(quality);
                        itempath.setVoice(translator);
                        itempath.setRating(rating);
                        itempath.setGenre(Utils.renGenre(genre));
                        try {
                            itempath.setSeason(Integer.parseInt(season.replace(" ", "")));
                            itempath.setSeries(Integer.parseInt(series.replace(" ", "")));
                        } catch (Exception e) {
                            itempath.setSeason(0);
                            itempath.setSeries(0);
                        }

                        items.add(itempath);
                    }
                }
            } else {
                if (data.html().contains("itemprop=\"name\""))
                    name = data.select(".mc-right h1").text();
                if (data.html().contains("m-origin"))
                    subname = data.select(".m-origin").text().replace("/","")
                            .replace("'", "");
                if (data.html().contains("m-img"))
                    img = Statics.KINOFS_URL + data.select(".m-img img").attr("src");
                if (data.html().contains("m-info")){
                    String all = data.select(".m-info").text();
                    if (all.contains("Год:")) year = all.split("Год:")[1].split("Страна:")[0];
                    if (all.contains("Страна:")) country = all.split("Страна:")[1].split("Время:")[0].trim();
                    if (all.contains("Время:")) time = all.split("Время:")[1].trim().split("Жанр:")[0];
                    if (all.contains("Жанр:")) genre = all.split("Жанр:")[1].split("В ролях:")[0].trim();
                    if (all.contains("Перевод:")) translator = all.split("Перевод:")[1].split("В ролях:")[0];
                    if (all.contains("В ролях:")) actors = all.split("В ролях:")[1].split("Режиссер:")[0];
                    if (all.contains("Режиссер:")) director = all.split("Режиссер:")[1];

                    if (genre.contains("Перевод:")) genre = genre.split("Перевод:")[0].trim();
                    if (genre.contains("Режиссер:")) genre = genre.split("Режиссер:")[0].trim();

                    if (all.contains("Качество:")) quality = all.split("Качество:")[1].split("Год:")[0];
                }
                if (data.html().contains("m-desc full-text"))
                    description_t = data.select(".m-desc.full-text").html().trim();
                description_t = description_t.replace("\"", "").trim();
                if (data.html().contains("<iframe")) {
                    String ifrm = data.select("iframe").attr("src");
                    if (ifrm.contains("moonwalk")) iframe = ifrm;
                }
                if (data.html().contains("rat"))
                    rating = data.select(".rat").text().trim();
                if (data.html().contains("class=\"kinopoisk\"")) {
                    kpId = data.select(".kinopoisk").attr("data-movie").trim();
                    Statics.KP_ID = kpId;
                }

                if (genre.toLowerCase().contains("сериал") || data.html().contains("poster-serieslabel") ||
                        iframe.contains("/serial/"))
                    type = "serial";
                else if (genre.toLowerCase().contains("фильм") || iframe.contains("/video/")) type = "movie";
                else type = "movie";
                if (genre.toLowerCase().contains("аниме")) type += " anime";

//                Log.e("ttt", "ParseHtml: "+type );

                Elements allMore = data.select(".side-movie");
                for (Element more : allMore) {
                    moreseason = "0";
                    moreseries = "0";
                    moretitle = more.select(".side-movie-title").text().trim();
                    moreurl = more.attr("href");
                    moreimg = Statics.KINOFS_URL + more.select("img").first().attr("src");

                    itempath.setMoreTitle(moretitle);
                    itempath.setMoreUrl(moreurl);
                    itempath.setMoreImg(moreimg);
                    itempath.setMoreQuality(morequality);
                    itempath.setMoreVoice("error");
                    itempath.setMoreSeason(moreseason);
                    itempath.setMoreSeries(moreseries);
                }

                if (!name.contains("error")) {
                    itempath.setUrl(url);
                    itempath.setTitle(name);
                    itempath.setImg(img);
                    itempath.setSubTitle(subname);
                    itempath.setQuality(quality);
                    itempath.setVoice(translator);
                    itempath.setRating(rating);
                    itempath.setDescription(description_t);
                    itempath.setDate(year);
                    itempath.setKpId(kpId);
                    itempath.setCountry(country);
                    itempath.setGenre(Utils.renGenre(genre));
                    itempath.setDirector(director);
                    itempath.setActors(actors);
                    itempath.setTime(time);
                    itempath.setIframe(iframe);
                    itempath.setType(type);
                    itempath.setPreImg("error");
                    try {
                        itempath.setSeason(Integer.parseInt(season.replace(" ", "")));
                        itempath.setSeries(Integer.parseInt(series.replace(" ", "")));
                    } catch (Exception ignore) {
                    }
                }
            }
        } else
            Log.d(TAG, "ParseHtml: data error");
    }

    private Document Getdata(String url) {
        try {
            if (Statics.ProxyUse.contains("kinofs") && Statics.ProxyCur.contains(":") && !Statics.ProxyCur.contains("адрес:порт")){
                System.setProperty("http.proxyHost", Statics.ProxyCur.split(":")[0].trim());
                System.setProperty("http.proxyPort", Statics.ProxyCur.split(":")[1].trim());
            } else {
                System.clearProperty("http.proxyHost");
                System.clearProperty("http.proxyPort");
            }
//            Proxy proxy = new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("socks.zaborona.help", 1488));
            if (url.contains("/load/поиск/")){
                return Jsoup.connect(url.replace("поиск/", ""))
                        .data("query", ItemMain.xs_search)
                        .data("a", "2")
//                        .proxy(proxy)
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .timeout(30000).ignoreContentType(true).post();
            } else return Jsoup.connect(url)
//                    .proxy(proxy)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(30000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
