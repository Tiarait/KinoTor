package com.kinotor.tiar.kinotor.parser;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 30.01.2018.
 */

public class ParserAmcet extends AsyncTask<Void, Void, Void> {
    private String url;
    private ArrayList<ItemHtml> items;
    private ItemHtml itempath;
    private OnTaskCallback callback;

    public ParserAmcet(String url, ArrayList<ItemHtml> items, ItemHtml itempath, OnTaskCallback callback) {
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
                    quality = "error parsing", rating = "error parsing";
            String name = "error", subname = "error", year = "error parsing", country = "error parsing",
                    genre = "error parsing", time = "error parsing", translator = "error parsing",
                    director = "error parsing", actors = "error parsing", description_t = "error parsing",
                    iframe = "error", type;
            String moretitle = "error", moreurl = "error", moreimg = "error", moreseason = "0",
                    moreseries = "0", morequality = "error";
            String season = "0", series = "0";
            if (data.html().contains("short_content") && !data.html().contains("full-story")) {
                Elements allEntries = data.select(".short_content");
                for (Element entry : allEntries) {
                    if (entry.html().contains("short_header")) {
                        title = entry.select(".short_header").first().text().trim();
                        if (entry.html().contains("short_info")) {
                            if (entry.select(".short_info").html().contains("<a href"))
                                title = title + " (" + entry.select(".short_info a").first().text() + ")";
                        }
                        if (url.contains("page/"))
                            url_entry = entry.select(".short_header a").first().attr("href");
                        else url_entry = entry.select("a").first().attr("href");
                        if (url.contains("http://cameleo.xyz"))
                            url_entry = Statics.AMCET_URL + "/" + url_entry.split("/")[url_entry.split("/").length-1];
                    }

                    if (entry.html().contains("serieslabel")) {
                        if (entry.select(".serieslabel").first().text().contains("сезон ")) {
                            season = entry.select(".serieslabel").first().text().split(" сезон")[0];
                            series = entry.select(".serieslabel").first().text()
                                    .split(" сезон ")[1].split(" сер")[0];
                        }
                    } else if (entry.html().contains("qulabel")) {
                        quality = entry.select(".qulabel").first().text();
                        season = "0";
                        series = "0";
                    }
                    if (entry.html().contains("img src")) {
                        img = Statics.AMCET_URL + entry.select("img").first().attr("src");
                    }
                    if (entry.html().contains("short_info")) {
                        genre = entry.select(".short_info").text().trim();
                    }
                    if (entry.html().contains("imdb")) {
                        rating = entry.select(".imdb").text().trim();
                        if (rating.contains("0.000") && entry.html().contains(".kinopoisk"))
                            rating = entry.select(".kinopoisk").text().trim();
                    } else if (entry.html().contains(".kinopoisk"))
                        rating = entry.select(".kinopoisk").text().trim();
                    else rating = "error";
                    if (rating.contains("0.000")) rating = "0.00";

                    itempath.setTitle(title);
                    itempath.setImg(img);
                    itempath.setUrl(url_entry);
                    itempath.setQuality(quality);
                    itempath.setRating(rating);
                    itempath.setVoice(translator);
                    itempath.setGenre(genre);
                    try {
                        itempath.setSeason(Integer.parseInt(season.replace(" ", "")));
                        itempath.setSeries(Integer.parseInt(series.replace(" ", "")));
                    } catch (Exception e) {
                        itempath.setSeason(0);
                        itempath.setSeries(0);
                    }
                    items.add(itempath);
                }
            } else {
                if (data.html().contains("news-title"))
                    name = data.select("#news-title").text();
                if (data.html().contains("orig_title"))
                    subname = replaceSutitle(data.select(".orig_title").text().replace("/","")
                            .replace("'", ""));
                if (data.html().contains("poster cf"))
                    img = Statics.AMCET_URL + data.select(".poster img").attr("src");
                if (data.html().contains("movie-info")){
                    String all = data.select(".movie-info").text();
                    if (all.contains("Год:")) year = all.split("Год:")[1].split("Звук:")[0];
                    if (all.contains("Страна:")) country = all.split("Страна:")[1].split("Время:")[0].trim();
                    if (all.contains("Время:")) time = all.split("Время:")[1].trim().split("Год:")[0];
                    else country = country.split("Год:")[0];
                    if (all.contains("Звук:")) translator = all.split("Звук:")[1].split("Режиссер:")[0];
                    if (all.contains("Жанр:")) genre = all.split("Жанр:")[1].split("Страна:")[0].trim();
                    if (all.contains("Режиссер:")) director = all.split("Режиссер:")[1].split("Актеры:")[0];
                    if (all.contains("Актеры:")) actors = all.split("Актеры:")[1];
                    if (translator.contains("Актеры:")) translator = translator.split("Актеры:")[0];

                    if (genre.contains("Время:")) genre = genre.split("Время:")[0];
                    if (genre.contains("Год:")) genre = genre.split("Год:")[0];
                }
                if (data.html().contains("poster-qulabel"))
                    quality = data.select(".poster-qulabel").text();
                if (data.html().contains("post_content cf"))
                    description_t = data.select(".post_content.cf").text();
                if (data.html().contains("moonwalk_video"))
                    iframe = data.select("#moonwalk_video").attr("src");

                if (genre.contains("сериал") || data.html().contains("poster-serieslabel"))
                    type = "serial";
                else if (genre.contains("фильм")) type = "movie";
                else type = "movie";
                if (genre.contains("аниме")) type += " anime";

                Elements allMore = data.select(".short_content");
                for (Element more : allMore) {
                    moreseason = "0";
                    moreseries = "0";
                    moretitle = more.select(".short_header").text().trim();
                    moreurl = more.select("a").first().attr("href");
                    moreimg = Statics.AMCET_URL + more.select("img").first().attr("src");
                    if (more.html().contains("serieslabel")) {
                        String seas = more.select(".serieslabel").text();
                        morequality =  "HD";
                        if (seas.contains("сезон")) {
                            moreseason = seas.split("сезон")[0].trim();
                            if (seas.contains("серия"))
                                moreseries = seas.split("сезон")[1]
                                        .split("серия")[0].trim();
                        }
                        if (moreseason.contains("season")){
                            moreseason = "0";
                            moreseries = "0";
                        }
                    }
                    if (more.html().contains("qulabel")) {
                        morequality = more.select(".qulabel").text().trim();
                    }
                    itempath.setMoreTitle(moretitle);
                    itempath.setMoreUrl(moreurl);
                    itempath.setMoreImg(moreimg);
                    itempath.setMoreQuality(morequality);
                    itempath.setMoreVoice("error");
                    itempath.setMoreSeason(moreseason);
                    itempath.setMoreSeries(moreseries);
                }

                Elements allImg = data.select(".post_content.staff a");
                for (Element preimg : allImg) {
                    itempath.setPreImg(preimg.attr("href"));
                }
                if (data.html().contains("poster-serieslabel")) {
                    if (data.select(".poster-serieslabel").first().text().contains("сезон ")) {
                        season = data.select(".poster-serieslabel").first().text().split(" сезон")[0];
                        series = data.select(".poster-serieslabel").first().text()
                                .split(" сезон ")[1].split(" сер")[0];
                    }
                }
                if (data.html().contains("imdb rki")) {
                    rating = data.select(".imdb.rki").text().trim();
                    if (rating.contains("0.000") && data.html().contains(".kinopoisk.rki"))
                        rating = data.select(".kinopoisk.rki").text().trim();
                } else if (data.html().contains(".kinopoisk.rki"))
                    rating = data.select(".kinopoisk.rki").text().trim();
                else rating = "error";
                if (rating.contains("0.000")) rating = "0.00";

                itempath.setUrl(url);
                itempath.setTitle(name);
                itempath.setImg(img);
                itempath.setSubTitle(subname);
                itempath.setQuality(quality);
                itempath.setVoice(translator);
                itempath.setRating(rating);
                itempath.setDescription(description_t);
                itempath.setDate(year);
                itempath.setCountry(country);
                itempath.setGenre(genre);
                itempath.setDirector(director);
                itempath.setActors(actors);
                itempath.setTime(time);
                itempath.setIframe(iframe);
                itempath.setType(type);
                itempath.setPreImg("error");
                try {
                    itempath.setSeason(Integer.parseInt(season.replace(" ", "")));
                    itempath.setSeries(Integer.parseInt(series.replace(" ", "")));
                } catch (Exception ignore){}
            }
        } else
            Log.d(TAG, "ParseHtml: data error");
    }

    private String replaceSutitle (String s){
        s = s.contains("One Piece") ? "One Piece" : s;
        s = s.contains("Nanatsu no Taizai") ? "Nanatsu no Taizai" : s;
        s = s.contains("Dragon Ball Super") ? "Dragon Ball Super" : s;
        return s;
    }

    private Document Getdata(String url) {
        if (url.startsWith("11https://sim-sim.appspot.com")) {
            //Getdata(getLocation(url));
            return null;
        } else {
            try {
                Document htmlDoc;
                if (url.contains("http://cameleo.xyz/r?url="))
                    url = "http://cameleo.xyz/r?url=" +
                            url.split("r\\?url=")[1].replace("/","%2F");
                if (url.contains("page") || url.contains(".html")) {
                    htmlDoc = Jsoup.connect(url)
                            .header("Content-Language", "en-US")
                            .data("xsort", "1")
                            .data("xs_field", ItemMain.xs_field)
                            .data("xs_value", ItemMain.xs_value)
                            .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                            .timeout(10000).ignoreContentType(true).post();
                    Log.d(TAG, "GetdataAmcet: get connected to 1");
                } else {
                    htmlDoc = Jsoup.connect(url + "&titleonly=3")
                            .header("Content-Language", "en-US")
                            .referrer(Statics.AMCET_URL + "/films/")
                            .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                            .timeout(10000).ignoreContentType(true).get();
                    Log.d(TAG, "GetdataAmcet: get connected to 2");
                }
                Log.d(TAG, "GetdataAmcet: get connected to " + url);
                Log.d(TAG, "GetdataAmcet: get connected to " + htmlDoc.location());
                return htmlDoc;
            } catch (Exception e) {
                Log.d(TAG, "GetdataAmcet: connected false to " + url);
                e.printStackTrace();
                return null;
            }
        }
    }
}
