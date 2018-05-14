package com.kinotor.tiar.kinotor.parser;

import android.os.AsyncTask;
import android.text.TextUtils;
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

/**
 * Created by Tiar on 24.09.2017.
 */

public class ParserHtml extends AsyncTask<Void, Void, Void> {
    private String url;
    private ArrayList<ItemHtml> items;
    private ItemHtml itempath;
    private OnTaskCallback callback;

    public ParserHtml(String url, ArrayList<ItemHtml> items, ItemHtml itempath, OnTaskCallback callback){
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
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ParseHtml(Getdata(url));
        return null;
    }

    private void ParseHtml(Document data) {
        if (data != null) {
//            if (itemDetail == null) itemDetail = new ItemDetail();
            String title = "error parsing", url_entry = "error parsing", img = "error parsing",
                    date = "error parsing", description = "error parsing", voice = "error parsing",
                    quality = "error parsing";
            String name = "error", year = "error parsing", country = "error parsing", genre = "error parsing",
                    time = "error parsing", quality_t = "error parsing", translator = "error parsing",
                    director = "error parsing", actors = "error parsing", description_t = "error parsing",
                    subname = "error", tor_magnet = "error parsing", tor_size = "error parsing",
                    iframe = "error", type;
            String moretitle = "error", moreurl = "error", moreimg = "error", moreseason = "0",
                    moreseries = "0", morequality = "error";
            String season = "", series = "0";
            if (url.contains(Statics.KOSHARA_URL)) {
                //main page
                if (!data.html().contains("full-article")) {
                    Elements allEntries = data.select(".movie-item");
                    for (Element entry : allEntries) {
                        if (entry.html().contains("movie-title")) {
                            title = entry.select(".movie-title").first().text();
                            if (title.contains("сезон)")) {
                                season = title.split(" сезон\\)")[0];
                                if (season.contains(")"))
                                    if (season.contains(") ("))
                                        season = season.split("\\) \\(")[1];
                                    else season = season.split("\\)\\(")[1];
                                else season = season.split("\\(")[1];
                                title = title.split(" сезон")[0];
                                if (title.contains(")"))
                                    title = title.split("\\)")[0] + ")";
                                else title = title.split("\\(")[0];
                            }
                            url_entry = entry.select(".movie-title").first().attr("href");
                        }
                        if (entry.html().contains("movie-img")) {
                            img = entry.select(".movie-img img").first().attr("src");
                            if (!img.contains("://"))
                                img = Statics.KOSHARA_URL + img;
                            quality = entry.select(".movie-img span").first().text();
                            if (!title.contains(")"))
                                title = entry.select(".movie-img img").first().attr("alt");
                            if (title.contains("сезон)")) {
                                season = title.split(" сезон\\)")[0];
                                if (season.contains(")"))
                                    if (season.contains(") ("))
                                        season = season.split("\\) \\(")[1];
                                    else season = season.split("\\)\\(")[1];
                                else season = season.split("\\(")[1];
                                title = title.split(" сезон")[0];
                                if (title.contains(")"))
                                    title = title.split("\\)")[0] + ")";
                                else title = title.split("\\(")[0];
                            }
                        }
                        if (entry.html().contains("movie-date"))
                            date = entry.select(".movie-date").first().text();
                        if (entry.html().contains("movie-text"))
                            description = entry.select(".movie-text").first().text();
                        if (entry.html().contains("movie-series"))
                            voice = entry.select(".movie-series").first().text();

                        if (quality.contains("WEB-DLRip"))
                            quality = "WEB-DLRip";
                        if (description.contains("Описание: "))
                            description = description.replace("Описание: ", "");

                        description = description.split("1400Mb")[0].split("2100Mb")[0];

                        items.add(AddItem(itempath, title, url_entry, img, date,
                                description + "...", voice, quality, season, series));
                    }
                    //search
                    Elements allSearch = data.select(".sres-wrap");
                    for (Element search : allSearch) {
                        if (search.html().contains("sres-text"))
                            title = search.select(".sres-text h2").first().text();
                        url_entry = search.attr("href");
                        if (search.html().contains("sres-img")) {
                            img = search.select(".sres-img img").first().attr("src");
                            if (!img.contains("://"))
                                img = Statics.KOSHARA_URL + img;
                        }
                        if (search.html().contains("sres-date"))
                            date = search.select(".sres-date").first().text();
                        if (search.html().contains("sres-desc"))
                            description = search.select(".sres-desc").first().text();


                        date = date.split(",")[0];
                        description = description.split("1400Mb")[0].split("2100Mb")[0];

                        items.add(AddItem(itempath, title, url_entry, img, date,
                                description + "...", voice, quality, season, series));
                    }
                }
                //video page
                else if (data.html().contains("full-article")) {
                    Elements details = data.select(".full-article");
                    if (details.html().contains("side-title"))
                        subname = details.select(".side-title").first().text();
                    if (data.html().contains("m-img"))
                        img = Statics.KOSHARA_URL + data.select(".m-img img").attr("src");
                    if (details.html().contains("h1"))
                        name = details.select("h1").first().text();
                    else
                        name = details.html().split("смотреть онлайн ")[1].split(" в хорошем качестве")[0];
                    if (details.html().contains("в хорошем качестве")) {
                        String n = details.html().split("смотреть онлайн ")[1].split(" в хорошем качестве")[0];
                        if (n.contains("сезон)")) {
                            season = n.split(" сезон\\)")[0];
                            if (season.contains(")"))
                                if (season.contains(") ("))
                                    season = season.split("\\) \\(")[1];
                                else season = season.split("\\)\\(")[1];
                            else season = season.split("\\(")[1];
                            season = season.trim();
                        } else if (n.contains("мини-сериал")) {
                            if (n.contains("сезон"))
                                season = n.split(" сезон\\)")[0].split("\\(")[1].trim();
                            else
                                season = "1";
                        }
                    }
                    if (details.html().contains("m-info")) {
                        year = details.select(".m-info").first().text().split("Год:")[1].split("Страна:")[0].trim();
                        country = details.select(".m-info").first().text().split("Страна:")[1].split("Перевод:")[0].trim();
                        translator = details.select(".m-info").first().text().split("Перевод:")[1].split("Жанр:")[0];
                        genre = details.select(".m-info").first().text().split("Жанр:")[1].split("Качество:")[0].trim();
                        quality_t = details.select(".m-info").first().text().split("Качество:")[1].split("Режиссёр:")[0];
                        director = details.select(".m-info").first().text().split("Режиссёр:")[1].split("В Ролях:")[0];
                        actors = details.select(".m-info").first().text().split("В Ролях:")[1].split("Время:")[0];
                        time = details.select(".m-info").first().text().split("Время:")[1].trim();
                    }
                    if (details.html().contains("m-desc"))
                        description_t = details.select(".m-desc").first().text().split("1400Mb")[0].split("2100Mb")[0];

                    if (details.html().contains("player-box visible full-text"))
                        iframe = details.select(".player-box.visible.full-text iframe").first().attr("src");
                    if (iframe.contains("error")) iframe = details.select("iframe").first().attr("src");
                    Elements allTorrents = data.select(".torrent");
                    for (Element torrent : allTorrents) {
                        String torrents = Statics.KOSHARA_URL + torrent.select(".title a").first().attr("href");
                        String tor_name = torrent.select(".info_d").first().text();
                        if (tor_name.contains("_KOSHARA"))
                            tor_name = tor_name.split("_KOSHARA")[0];
                        tor_size = torrent.select(".cont").first().text().split("Размер: ")[1].split("Последняя")[0];
                        tor_magnet = torrent.select("a[href^='magnet']").first().attr("href");
                        String tor_content;
                        if (torrent.select(".li_list_a3").html().contains("class=\"folder"))
                            tor_content = torrent.select(".folder").first().text();
                        else tor_content = "koshara";
                        if (tor_content.contains("файлов)") || tor_content.contains("файла)"))
                            tor_content = "koshara (" + tor_content.split("\\(")[1].split("\\)")[0] + ")";

                        if (!itempath.tortitle.contains(tor_name)) {
                            itempath.setTorUrl(torrents);
                            itempath.setTorTitle(tor_name);
                            itempath.setTorSize(tor_size);
                            itempath.setTorMagnet(tor_magnet);
                            itempath.setTorContent(tor_content);
                            itempath.setTorLich("x");
                            itempath.setTorSid("x");
                        }
                    }

                    if (genre.contains("сериал") || genre.contains("мини сериал")) type = "serial";
                    else if (genre.contains("фильм") && !genre.contains("мультфильм")) type = "movie";
                    else if (!season.contains("error") && !season.isEmpty()) type = "serial";
                    else type = "movie";

//                    Log.d("qwer", "ParseHtml: " + type + " " + season + " " + genre);

                    if (genre.contains("аниме")) type += " anime";

                    Elements allMore = data.select(".rel-movie");
                    for (Element more : allMore) {
                        moreseason = "0";
                        moretitle = more.select("img").first().attr("alt").trim();
                        moreurl = more.select("a").first().attr("href");
                        moreimg = Statics.KOSHARA_URL + more.select("img").first().attr("data-src");
                        if (moretitle.contains("сезон")) {
                            moreseason = moretitle.split("сезон")[0].trim();
                            if (moreseason.contains("("))
                                moreseason = moreseason.split("\\(")[1];
                            else moreseason = "0";
                        }
                        itempath.setMoreTitle(moretitle);
                        itempath.setMoreUrl(moreurl);
                        itempath.setMoreImg(moreimg);
                        itempath.setMoreQuality(morequality);
                        itempath.setMoreVoice("error");
                        itempath.setMoreSeason(moreseason);
                        itempath.setMoreSeries(moreseries);
                    }

                    Elements allImg = data.select(".text_spoiler img");
                    for (Element preimg : allImg) {
                        itempath.setPreImg(preimg.attr("src"));
                    }
                    if (name.contains("СЕЗОН)") || name.contains("сезон)"))
                        name = name.split("\\(")[0];

                    itempath.setUrl(url);
                    itempath.setTitle(name);
                    itempath.setImg(img);
                    itempath.setSubTitle(subname);
                    itempath.setQuality(quality_t);
                    itempath.setVoice(translator.trim());
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
                        itempath.setSeason(Integer.parseInt(season));
                    } catch (Exception e) {
                        itempath.setSeason(0);
                    }
                    itempath.setSeries(0);
                }
            } else if (url.contains("coldfilm")) {
                voice = "Coldfilm";
                //video page
                if (data.html().contains("kino-inner-full")) {
                    type = "serial";
                    if (data.html().contains("kino-h")) {
                        name = data.select(".kino-h").first().text();
                        if (name.contains(" сезон")) {
                            season = name.split(" сезон")[0];
                            String[] arr = season.split(" ");
                            season = arr[arr.length - 1].replaceAll("\u00a0", "").trim();
                        } else season = "1";
                        if (name.contains(" серия")) {
                            series = name.split(" сезон")[1].split(" серия")[0].replaceAll("\u00a0", "").trim();
                            if (series.contains("-"))
                                series = series.split("-")[1];
                        }
                    }
                    if (name.contains("[Смотреть")) name = name.split("\\[Смотреть")[0];
                    if (name.contains(" сезон")) {
                        name = name.split(" сезон")[0];
                        name = name.substring(0, name.length() - 2);
                    }
                    if (data.html().contains("kino-desc full-text"))
                        img = Statics.COLDFILM_URL + data.select(".kino-desc.full-text img").attr("src");
                    translator = "ColdFilm";
                    quality_t = "HD";
                    description_t = "Перевод: Профессиональный многоголосый закадровый - ColdFilm";

                    if (data.html().contains("player-box visible full-text"))
                        iframe = data.select(".player-box.visible.full-text iframe").first().attr("src");
                    if (data.html().contains("kino-date icon-left"))
                        year = data.select(".kino-date").first().text();
                    year = year.contains("\"") ? year.replaceAll("\"", "") : year;

                    Elements allTorrents = data.select("a[href$=\".torrent\"]");
                    for (Element torrent : allTorrents) {
                        String torrents = torrent.attr("href");
                        String[] arr = torrents.split("/");
                        String tor_name = arr[arr.length - 1].split(".torrent")[0];

                        if (!itempath.tortitle.contains(tor_name)) {
                            itempath.setTorUrl(torrents);
                            itempath.setTorTitle(tor_name);
                            itempath.setTorSize(tor_size);
                            itempath.setTorMagnet(tor_magnet);
                            itempath.setTorContent("coldfilm.info");
                            itempath.setTorLich("x");
                            itempath.setTorSid("x");
                        }
                    }

                    Elements allMore = data.select(".rel-kino");
                    for (Element more : allMore) {
                        moreseason = "0";
                        moreseries = "0";
                        moretitle = more.select("img").first().attr("alt").trim();
                        moreurl = more.select("a").first().attr("href");
                        moreimg = "https://";
                        if (moretitle.contains(" сезон")) {
                            moreseason = moretitle.split(" сезон")[0].trim();
                            String moreseas[] = moreseason.split(" ");
                            moreseason = moreseas[moreseas.length - 1];
                            if (moretitle.contains(" серия"))
                                moreseries = moretitle.split(" сезон")[1].split(" серия")[0].trim();
                        }
                        itempath.setMoreTitle(moretitle);
                        itempath.setMoreUrl(moreurl);
                        itempath.setMoreImg(moreimg);
                        itempath.setMoreQuality(morequality);
                        itempath.setMoreVoice("error");
                        itempath.setMoreSeason(moreseason);
                        itempath.setMoreSeries(moreseries);
                    }

                    itempath.setUrl(url);
                    itempath.setTitle(name);
                    itempath.setSubTitle(subname);
                    itempath.setImg(img);
                    itempath.setQuality(quality_t);
                    itempath.setVoice(translator.trim());
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
                        itempath.setSeries(Integer.parseInt(series));
                        itempath.setSeason(Integer.parseInt(season));
                    } catch (Exception e) {
                        itempath.setSeries(0);
                        itempath.setSeason(0);
                    }
                } else if (url.contains("/search/")) {
                    //sres-wrap
                    Elements allEntries = data.select(".sres-wrap");
                    for (Element entry : allEntries) {
                        if (entry.html().contains("sres-text")) {
                            title = entry.select(".sres-text h2").first().text();
                            if (title.contains("сезон")) {
                                season = title.split("сезон")[0];
                                String[] arr = season.split(" ");
                                season = arr[arr.length - 1].replaceAll("\u00a0", "").trim();
                                if (title.contains("серия")) {
                                    series = title.split("сезон")[1].split("серия")[0].replaceAll("\u00a0", "").trim();
                                    if (series.contains("-"))
                                        series = series.split("-")[1];
                                }
                                title = title.split(season)[0];
                            } else title = "error";
                            url_entry = entry.attr("href");
                            if (!url_entry.contains("://"))
                                url_entry = Statics.COLDFILM_URL + url_entry;
                            if (entry.html().contains("sres-img") && entry.html().contains("img src")) {
                                img = entry.select(".sres-img img").first().attr("src");
                                if (!img.contains("://"))
                                    img = Statics.COLDFILM_URL + img;
                            }
                            if (entry.html().contains("sres-desc"))
                                description = entry.select(".sres-desc").first().text();
                            if (description.contains("Обзор]") || description.contains("Трейлер]") ||
                                    description.contains("Удалено по просьбе правообладателя"))
                                title = "error";

                            if (!title.contains("error"))
                                items.add(AddItem(itempath, title, url_entry, img, date,
                                        description + "...", voice, quality, season, series));
                        }
                    }
                    //main page
                } else {
                    Elements allEntries = data.select(".kino-item");
                    for (Element entry : allEntries) {
                        if (entry.html().contains("kino-title")) {
                            title = entry.select(".kino-title a").first().text();
                            if (title.contains("сезон")) {
                                season = title.split("сезон")[0];
                                String[] arr = season.split(" ");
                                season = arr[arr.length - 1].replaceAll("\u00a0", "").trim();
                                if (title.contains("серия")) {
                                    series = title.split("сезон")[1].split("серия")[0].replaceAll("\u00a0", "").trim();
                                    if (series.contains("-"))
                                        series = series.split("-")[1];
                                }
                                title = title.split(season)[0];
                            } else title = "error";
                            url_entry = entry.select(".kino-title a").first().attr("href");
                            if (!url_entry.contains("://"))
                                url_entry = Statics.COLDFILM_URL + url_entry;
                        }
                        if (entry.html().contains("kino-img")) {
                            img = entry.select(".kino-img img").first().attr("src");
                            if (!img.contains("://"))
                                img = Statics.COLDFILM_URL + img;
                        }
                        if (entry.html().contains("kino-date"))
                            date = entry.select(".kino-date").first().text();
                        if (entry.html().contains("kino-desc"))
                            description = entry.select(".kino-desc").first().text();
                        if (description.contains("Обзор]") || description.contains("Трейлер]"))
                            title = "error";

                        if (!title.contains("error"))
                            items.add(AddItem(itempath, title, url_entry, img, date,
                                    description + "...", voice, quality, season, series));
                    }
                }
            }
        }
    }

    private ItemHtml AddItem (ItemHtml htmlItem, String title, String url, String img, String date, String description,
                              String voice, String quality, String season, String series){
        htmlItem.setTitle(title);
        htmlItem.setUrl(url);
        htmlItem.setImg(img);
        htmlItem.setDate(date);
        htmlItem.setDescription(description);
        htmlItem.setVoice(voice.trim());
        htmlItem.setQuality(quality);
        try {
            if (TextUtils.isDigitsOnly(season)) htmlItem.setSeason(Integer.parseInt(season));
            else htmlItem.setSeason(99);
            if (TextUtils.isDigitsOnly(series)) htmlItem.setSeries(Integer.parseInt(series));
            else htmlItem.setSeries(99);
        } catch (Exception e) {
            htmlItem.setSeason(0);
            htmlItem.setSeries(0);
        }

        return htmlItem;
    }

    private Document Getdata(String url) {
        try {
            Log.d("mydebug","get connected to " + url);
            Document htmlDoc;
            String ref = "";
            if (ItemMain.cur_url.contains(Statics.KOSHARA_URL) || url.contains(Statics.KOSHARA_URL))
                ref = Statics.KOSHARA_URL;
            if (ItemMain.cur_url.contains(Statics.COLDFILM_URL) || url.contains(Statics.COLDFILM_URL))
                ref = Statics.COLDFILM_URL;
            htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(10000).ignoreContentType(true).referrer(ref).get();
            Log.d("mydebug","get connected to " + url);
            return htmlDoc;
        } catch (Exception e) {
            Log.d("mydebug","connected false to " + url);
            e.printStackTrace();
            return null;
        }
    }
}
