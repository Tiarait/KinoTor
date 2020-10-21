package com.kinotor.tiar.kinotor.parser.catalog;

import android.os.AsyncTask;

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
public class ParserMyhit extends AsyncTask<Void, Void, Void> {
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


    public ParserMyhit(String url, ArrayList<ItemHtml> items, ItemHtml itempath, OnTaskCallback callback) {
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

//        Log.e("test", "ParseHtml1: "+name+url_entry);
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
            if (url.contains("/star/") || url.contains("/soundtrack/") || url.contains("/video/") || url.contains("/selection/")){
                if (url.contains("/star/")){
                    Element entry = data.selectFirst(".col-xs-10.col-md-8.conten.fullstory");
                    name = entry.selectFirst("h1").text().trim();
                    subname = entry.selectFirst("h4").text().trim();
                    if (entry.html().contains("class=\"img-rounded img-responsive\""))
                        img = Statics.MYHIT_URL + entry.select(".img-rounded.img-responsive").attr("src").trim();

                    if (entry.html().contains("list-unstyled")) {
                        Elements allLines = entry.select(".list-unstyled li");
                        for (Element line : allLines) {
                            if (line.text().contains("Место рождения:"))
                                country = line.text().replace("Место рождения:", "").trim();
                            if (line.text().contains("Дата рождения:"))
                                year = line.text().replace("Дата рождения:", "").trim();
                            if (line.text().contains("Фильмов с участием:"))
                                description_t = line.text().trim().replace("error","");
                            if (line.text().contains("Первый фильм:"))
                                description_t += "\n<br>"+line.text().trim().replace("error","");
                            if (line.text().contains("Последний фильм:"))
                                description_t += "\n<br>"+line.text().trim().replace("error","");
                        }
                    }
                    Document dd = Getdata(this.url +"filmography/");
                    if (dd != null) {
                        if (dd.html().contains("table table-hover")){
                            for (Element more : dd.select(".table.table-hover tr")) {
                                defMore();
                                if (more.html().contains("<a")) {
                                    moreurl = Statics.MYHIT_URL + more.select("a").last().attr("href").trim();
                                    moretitle = more.select("a").last().text().trim();
                                }
                                if (more.html().contains("<img"))
                                    moreimg = Statics.MYHIT_URL + more.selectFirst("img").attr("src");

                                if (!moreurl.contains("error")) {
                                    itempath.setMoreTitle(moretitle);
                                    itempath.setMoreUrl(moreurl);
                                    itempath.setMoreImg(moreimg);
                                    itempath.setMoreQuality(morequality);
                                    itempath.setMoreVoice("error");
                                    itempath.setMoreSeason(moreseason);
                                    itempath.setMoreSeries(moreseries);
                                }
                            }
                        }
                    }
                    if (!name.contains("error"))
                        itemSet();
                }
            }else if (data.html().contains("class=\"col-xs-10 col-md-8 conten fullstory\"") &&
                    data.html().contains("class=\"breadcrumb\"")){
                Element entry = data.selectFirst(".col-xs-10.col-md-8.conten.fullstory");
                String ee = entry.html();
                defVal();
//                Log.e("test", "ParseHtml001: ");

                name = entry.selectFirst("h1").text().trim();
                if (entry.selectFirst("h1").html().contains("<a"))
                    year = entry.selectFirst("h1 a").text().trim();
                if (ee.contains("<h4"))
                    subname = entry.selectFirst("h4").text().trim();
                url_entry = this.url;

                Element allLine = data.selectFirst(".list-unstyled");
                Elements allLines = allLine.select("li");
                for (Element line : allLines) {
                    if (line.text().contains("Страна:"))
                        country = line.text().replace("Страна:", "").trim();
                    if (line.text().contains("Год:"))
                        year = line.text().replace("Год:", "").trim();
                    else if (line.text().contains("В ролях:"))
                        actors = line.text().replace("В ролях:", "").trim();
                    else if (line.text().contains("Жанр:"))
                        genre = line.text().replace("Жанр:", "").trim();
                    else if (line.text().contains("Режиссер:"))
                        director = line.text().replace("Режиссер:", "").trim();
                    else if (line.text().contains("Звук:"))
                        translator = line.text().replace("Звук:", "").trim();
                    else if (line.text().contains("Продолжительность:"))
                        time = line.text().replace("Продолжительность:", "").trim();
                    else if (line.text().contains("Качество:"))
                        quality = line.text().replace("Качество:", "").trim();
                }
                if (ee.contains("itemprop=\"description\"")){
                    description_t = entry.selectFirst("div[itemprop='description']").text();
                }

                if (ee.contains("list-unstyled")){
                    actors = entry.select(".list-unstyled").last().text();
                    if (actors.contains("В ролях:"))
                        actors = actors.replace("В ролях:", "").trim();
                    else actors = "error";
                }
                if (ee.contains("film_look_to")){
                    addMore(entry, "film_look_to");
                } else if (ee.contains("serial_look_to")){
                    addMore(entry, "serial_look_to");
                }

                if (ee.contains("class=\"img-rounded img-responsive\""))
                    img = Statics.MYHIT_URL + entry.select(".img-rounded.img-responsive").attr("src").trim();

                if (ee.contains("class=\"minus") && ee.contains("class=\"plus")) {
                    try {
                        int plus = Integer.parseInt(entry.select(".plus").first().text().trim());
                        int minus = Integer.parseInt(entry.select(".minus").first().text().trim());
                        rating = "SITE["+ (plus - minus) +"]";
                    } catch (Exception e) {
                        if (ee.contains("class=\"ratio-str\"")) {
                            rating = "SITE[" + entry.select(".ratio-str").text()
                                    .replace(",",".")
                                    .replace("%","").trim() +"]";
                        }
                        e.printStackTrace();
                    }
                } else if (ee.contains("class=\"ratio-str\"")) {
                    rating = "SITE[" + entry.select(".ratio-str").text()
                            .replace(",",".")
                            .replace("%","").trim() +"]";
                }

                if (name.contains("сезон)")){
                    season = name.split("сезон\\)")[0].trim();
                    if (season.contains("-")){
                        season = season.split("-")[1].trim();
                    } else if (season.contains("(")){
                        season = season.split("\\(")[1].trim();
                    }
                    name = name.split("\\(")[0].trim();
                }
                type = "movie";
                if (url_entry.contains("/serial/"))
                    type = "serial";


//                Log.e("test", "ParseHtml001: "+name+year);
                if (year.contains("-"))
                    year = year.split("-")[0].trim();
                if (!name.contains(year))
                    name += " (" + year + ")";

                if (!name.contains("error"))
                    itemSet();
            } else if (data.html().contains("class=\"row")) {
                Elements allEntries = data.select(".row");
                for (Element entry : allEntries) {
                    defVal();
                    String ee = entry.html();
                    if (ee.contains("class=\"col-xs-9\"")) {
                        name = entry.select(".col-xs-9 b a").text().trim();
                        url_entry = Statics.MYHIT_URL + entry.select(".col-xs-9 b a").attr("href").trim();
                    }
                    if (ee.contains("pull-right text-right")) {
                        Element allL = entry.selectFirst(".pull-right.text-right");
                        Elements allLines = allL.select(".list-unstyled li");
                        for (Element line : allLines) {
                            if (line.text().contains("Рейтинг:"))
                                rating = line.text().replace("Рейтинг:", "")
                                        .replace(",",".")
                                        .replace("%","").trim();
                            if (line.text().contains("Качество:"))
                                quality = line.text().replace("Качество:", "").trim();
                            if (line.text().contains("Звук:"))
                                translator = line.text().replace("Звук:", "").trim();
                        }
                    }
                    if (ee.contains("col-xs-9")) {
                        if (entry.select(".col-xs-9").html().contains("list-unstyled")) {
                            Element allL = entry.select(".list-unstyled").last();
                            if (allL.html().contains("<li")) {
                                Elements allLines = allL.select("li");
                                for (Element line : allLines) {
                                    if (line.text().contains("Жанр:"))
                                        genre = line.text().replace("Жанр:", "").trim();
                                    else if (line.text().contains("Год:"))
                                        year = line.text().replace("Год:", "").trim().replace(".","");
                                    else if (line.text().contains("Страна:"))
                                        country = line.text().replace("Страна:", "").trim();
                                    else if (line.text().contains("Режиссер:"))
                                        director = line.text().replace("Режиссер:", "").trim();
                                    else if (line.text().contains("В ролях:"))
                                        actors = line.text().replace("В ролях:", "").trim();
                                    else if (line.text().contains("Сезон:"))
                                        season = line.text().replace("Сезон:", "").trim();
                                    else if (line.text().contains("Дата рождения:"))
                                        year = line.text().replace("Дата рождения:", "").trim().replace(".","");
                                    else description_t = line.text().trim();
                                }
                            }
                        }
                    }
                    if (year.contains(".")){
                        if (year.split("\\.").length > 1){
                            year = year.split("\\.")[year.split("\\.").length-1];
                        }
                    }
                    if (year.contains("-"))
                        year = year.split("-")[0].trim();
                    if (season.contains(" серия")){
                        series = season.split(" серия")[0].trim();
                        if (series.contains("-")){
                            series = series.split("-")[1].trim();
                        } else if (series.contains("(")){
                            series = series.split("\\(")[1].trim();
                        }
                    }
                    if (season.contains("-")){
                        season = season.split("-")[1].trim();
                    }
                    if (season.contains("(")){
                        season = season.split("\\(")[0].trim();
                    }
                    if (translator.equals("-"))
                        translator = "error";

                    if (ee.contains("class=\"img-rounded img-responsive\""))
                        img = Statics.MYHIT_URL + entry.select(".img-rounded.img-responsive").attr("src").trim();

                    type = "movie";
                    if (url_entry.contains("/serial/"))
                        type = "serial";
//                    Log.e("test", "ParseHtml00: " + name);

                    if (name.contains("/")) {
                        if (name.split("/").length > 2) {
                            name = "error";
                        } else {
                            subname = name.split("/")[1].trim();
                            name = name.split("/")[0].trim();
                        }
                    }

                    if (!name.contains(year))
                        name += " (" + year + ")";
                    boolean hide = quality.toLowerCase().contains("ts") && Statics.hideTs;

                    if (!name.contains("error") && !hide)
                        itemSet();
                }
            }
        }

    }

    private void addMore (Element entry, String w) {
        Elements allMores = entry.select(".row.metrika[data-metrika='"+w+"'] .col-xs-3.text-center");
        for (Element more : allMores) {
            defMore();
            moreurl = Statics.MYHIT_URL + more.selectFirst("a").attr("href").trim();
            moretitle = more.selectFirst("a").attr("title").replace(" - смотреть онлайн","").trim();
//                        Log.e("test", "ParseHtml: "+more.selectFirst("a").html() );
//                        Log.e("test", "ParseHtml: "+moretitle );

            moreimg = Statics.MYHIT_URL + more.selectFirst("img").attr("src");
            morequality = more.select(".text-center").last().text();
            if (morequality.contains(","))
                morequality = morequality.split(",")[morequality.split(",").length-1].trim();
            if (morequality.contains("сери"))
                morequality = "error";

            if (!moreurl.isEmpty()) {
                itempath.setMoreTitle(moretitle);
                itempath.setMoreUrl(moreurl);
                itempath.setMoreImg(moreimg);
                itempath.setMoreQuality(morequality);
                itempath.setMoreVoice("error");
                itempath.setMoreSeason(moreseason);
                itempath.setMoreSeries(moreseries);
            }
        }
    }

    private Document Getdata(String url) {
//        Log.e("test", "film: "+url );
        try {
            return Jsoup.connect(url)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false)
                    .timeout(30000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
