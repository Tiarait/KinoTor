package com.kinotor.tiar.kinotor.parser.catalog;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskCallback;
import com.kinotor.tiar.kinotor.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Tiar on 08.2018.
 */
public class ParserColdfilm extends AsyncTask<Void, Void, Void> {
    private String url;
    private ArrayList<ItemHtml> items;
    private ItemHtml itempath;
    private OnTaskCallback callback;

    private String url_entry = "error ", img = "error ", kpId = "error",
            quality = "error ", rating = "error ";
    private String name = "error", subname = "error", year = "error ", country = "error ",
            genre = "error ", time = "error ", translator = "error ",
            director = "error ", actors = "error ", description_t = "error ",
            iframe = "error", type = "error";
    private String moretitle = "error", moreurl = "error", moreimg = "error", moreseason = "0",
            moreseries = "0", morequality = "error";
    private String season = "0", series = "0";


    public ParserColdfilm(String url, ArrayList<ItemHtml> items, ItemHtml itempath, OnTaskCallback callback) {
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

    private void defVal(){
        url_entry = "error ";
        img = "error ";
        quality = "error ";
        rating = "error ";
        name = "error";
        subname = "error";
        year = "error ";
        kpId = "error";
        country = "error ";
        genre = "error ";
        time = "error ";
        translator = "error ";
        director = "error ";
        actors = "error ";
        description_t = "error ";
        iframe = "error";
        type = "error";
        season = "0";
        series = "0";
    }
    private void defMore(){
        moretitle = "error";
        moreurl = "error";
        moreimg = "error";
        moreseason = "0";
        moreseries = "0";
        morequality = "error";
    }

    private void itemSet(){

//        if (!translator.trim().equals("Профессиональный (многоголосый)"))
//            translator = translator.replace("Профессиональный (многоголосый)","").trim();
//        Log.e("test", "ParseHtml1: "+type);
        itempath.setUrl(url_entry);
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
        try {
            itempath.setSeason(Integer.parseInt(season.replace(" ", "")));
            itempath.setSeries(Integer.parseInt(series.replace(" ", "")));
        } catch (Exception e){
            itempath.setSeason(0);
            itempath.setSeries(0);
        }
        items.add(itempath);
    }

    private void ParseHtml(Document data) {
        if (data != null) {
            //video page
            if (data.html().contains("kino-inner-full")) {
                defVal();
                type = "serial";
                translator = "ColdFilm";
                quality = "HD";
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
                if (data.html().contains("k-rate-full"))
                    rating = data.select(".k-rate-full ul").attr("title");
                rating = rating.replace("Рейтинг: ", "");
                description_t = "Перевод: Профессиональный многоголосый закадровый - ColdFilm";

                if (data.html().contains("player-box visible full-text")) {
                    if (data.select(".player-box.visible.full-text").html().contains("<iframe"))
                        iframe = data.select(".player-box.visible.full-text iframe").first().attr("src");
                }
                Log.e("iframe", "ParseHtml: "+iframe );
                if (data.html().contains("kino-date icon-left"))
                    year = data.select(".kino-date").first().text();
                year = year.contains("\"") ? year.replaceAll("\"", "") : year;

                Elements allTorrents = data.select("a[href$=\".torrent\"]");
                for (Element torrent : allTorrents) {
                    String torrents = torrent.attr("href");
                    String[] arr = torrents.split("/");
                    String tor_name = arr[arr.length - 1].split("\\.torrent")[0];

                    if (!itempath.tortitle.contains(tor_name)) {
                        itempath.setTorUrl(torrents);
                        itempath.setTorU(url);
                        itempath.setTorTitle(tor_name);
                        itempath.setTorSize("error");
                        itempath.setTorMagnet("error");
                        itempath.setTorContent("coldfilm");
                        itempath.setTorLich("x");
                        itempath.setTorSid("x");
                    }
                }

                Elements allMore = data.select(".rel-kino");
                for (Element more : allMore) {
                    defMore();
                    moreseason = "0";
                    moreseries = "0";
                    moretitle = more.select("img").first().attr("alt").trim();
                    moreurl = more.select("a").first().attr("href");
                    moreimg = "error";
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

                if (!name.contains("error")) {
                    itemSet();
                }
            } else if (url.contains("/search/")) {
                //sres-wrap
                Elements allEntries = data.select(".sres-wrap");
                for (Element entry : allEntries) {
                    defVal();
                    type = "serial";
                    translator = "ColdFilm";
                    quality = "HD";
                    if (entry.html().contains("sres-text")) {
                        name = entry.select(".sres-text h2").first().text();
                        if (name.contains("сезон")) {
                            season = name.split("сезон")[0];
                            String[] arr = season.split(" ");
                            season = arr[arr.length - 1].replaceAll("\u00a0", "").trim();
                            if (name.contains("серия")) {
                                series = name.split("сезон")[1].split("серия")[0].replaceAll("\u00a0", "").trim();
                                if (series.contains("-"))
                                    series = series.split("-")[1];
                            }
                            name = name.split(season)[0];
                        } else name = "error";
                        url_entry = entry.attr("href");
                        if (!url_entry.contains("://"))
                            url_entry = Statics.COLDFILM_URL + url_entry;
                        if (entry.html().contains("sres-img") && entry.html().contains("img src")) {
                            img = entry.select(".sres-img img").first().attr("src");
                            if (!img.contains("://"))
                                img = Statics.COLDFILM_URL + img;
                        }
                        if (entry.html().contains("sres-desc"))
                            description_t = entry.select(".sres-desc").first().text();
                        if (description_t.contains("Обзор]") || description_t.contains("Трейлер]") ||
                                description_t.contains("Удалено по просьбе правообладателя"))
                            name = "error";

                        if (!name.contains("error"))
                            itemSet();
                    }
                }
                //main page
            } else {
                Elements allEntries = data.select(".kino-item");
                for (Element entry : allEntries) {
                    defVal();
                    type = "serial";
                    translator = "ColdFilm";
                    quality = "HD";
                    if (entry.html().contains("kino-title")) {
                        name = entry.select(".kino-title a").first().text();
                        if (name.contains("сезон")) {
                            season = name.split("сезон")[0];
                            String[] arr = season.split(" ");
                            season = arr[arr.length - 1].replaceAll("\u00a0", "").trim();
                            if (name.contains("серия")) {
                                series = name.split("сезон")[1].split("серия")[0].replaceAll("\u00a0", "").trim();
                                if (series.contains("-"))
                                    series = series.split("-")[1];
                            }
                            name = name.split(season)[0];
                        } else name = "error";
                        url_entry = entry.select(".kino-title a").first().attr("href");
                        if (!url_entry.contains("://"))
                            url_entry = Statics.COLDFILM_URL + url_entry;
                    }
                    if (entry.html().contains("kino-img")) {
                        img = entry.select(".kino-img img").first().attr("src");
                        if (!img.contains("://"))
                            img = Statics.COLDFILM_URL + img;
                    }
                    if (entry.html().contains("u-star-rating-20"))
                        rating = entry.select(".u-star-rating-20").attr("title");
                    else rating = "error";
                    rating = rating.replace("Рейтинг: ", "");
//                    if (entry.html().contains("kino-date"))
//                        year = entry.select(".kino-date").first().text();
//                    if (entry.html().contains("kino-desc"))
//                        description = entry.select(".kino-desc").first().text();
                    if (description_t.contains("Обзор]") || description_t.contains("Трейлер]"))
                        name = "error";

                    if (!name.contains("error"))
                        itemSet();
                }
            }
        } else
            Log.e("test", "ParseHtml data null" );

    }
    
    private Document Getdata(String url) {
        try {
            Log.d("mydebug","get connected to " + url);
            String ref = Statics.COLDFILM_URL;

            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .ignoreContentType(true).referrer(ref).get();
        } catch (Exception e) {
            Log.d("mydebug","connected false to " + url);
            e.printStackTrace();
            return null;
        }
    }
}
