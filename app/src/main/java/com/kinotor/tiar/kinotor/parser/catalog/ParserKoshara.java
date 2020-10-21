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
public class ParserKoshara extends AsyncTask<Void, Void, Void> {
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


    public ParserKoshara(String url, ArrayList<ItemHtml> items, ItemHtml itempath, OnTaskCallback callback) {
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

        if (!translator.trim().equals("Профессиональный (многоголосый)"))
            translator = translator.replace("Профессиональный (многоголосый)","").trim();
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
            if (url.endsWith("html")) {
                defVal();
                url_entry = url;
                Elements details = data.select(".full-story");
                String ee = details.html();
                if (ee.contains("orig_name"))
                    subname = details.select(".orig_name").first().text();
                if (data.html().contains("full-story__top__info-poster"))
                    img = Statics.KOSHARA_URL + data.select(".full-story__top__info-poster img").attr("src");
                if (ee.contains("full-story__top__titles"))
                    name = details.select(".full-story__top__titles h1").first().text();

                if (ee.contains("full-story__top__info-fields")) {
                    for (Element e : details.select(".full-story__top__info-fields li")){
                        if (e.text().contains("Год:"))
                            year = e.text().replace("Год:","").trim();
                        if (e.text().contains("Страна:"))
                            country = e.text().replace("Страна:","").trim();
                        if (e.text().contains("Качество:"))
                            quality = e.text().replace("Качество:","").trim();
                        if (e.text().contains("Перевод:"))
                            translator = e.text().replace("Перевод:","").trim();
                        if (e.text().contains("Жанр:"))
                            genre = e.text().replace("Жанр:","").trim();
                        if (e.text().contains("Режиссерский состав:"))
                            director = e.text().replace("Режиссерский состав:","").trim();
                        if (e.text().contains("В Ролях:"))
                            actors = e.text().replace("В Ролях:","").trim();
                        if (e.text().contains("Время:"))
                            time = e.text().replace("Время:","").trim();
                    }
                }

                if (ee.contains("full-story__top__info-descr")) {
                    description_t = details.select(".full-story__top__info-descr").first().text()
                            .replace("Сюжет:", "").trim();
                }
                if (ee.contains("class=\"psc") && ee.contains("class=\"msc")) {
                    try {
                        int plus = Integer.parseInt(details.select(".psc").first().text().trim());
                        int minus = Integer.parseInt(details.select(".mpsc").first().text().trim());
                        rating = "SITE["+ (plus - minus) +"]";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }


                if (description_t.contains("700Mb"))
                    description_t = description_t.split("700Mb")[0];
                if (description_t.contains("1400Mb"))
                    description_t = description_t.split("1400Mb")[0];
                if (description_t.contains("2100Mb"))
                    description_t = description_t.split("2100Mb")[0];
                if (description_t.contains("Мировые Рейтинги"))
                    description_t = description_t.split("Мировые Рейтинги")[0];

                if (ee.contains("iframe_block-player")) {
                    if (details.select("#iframe_block-player").html().contains("<iframe"))
                        iframe = details.select("#iframe_block-player iframe").first().attr("src");
//                    Log.e("iframe", "ParseHtml: wtf22 "+details.select("#iframe_block-player").html());
                } else Log.e("iframe", "ParseHtml: wtf");
//                Log.e("iframe", "ParseHtml: "+iframe);

                for (Element torrent : data.select(".torrent")) {
                    String torrents = Statics.KOSHARA_URL + torrent.select(".title a").first().attr("href");
                    String tor_name = torrent.select(".info_d").first().text();
                    if (tor_name.contains("_KOSHARA"))
                        tor_name = tor_name.split("_KOSHARA")[0];
                    String tor_size = torrent.select(".info_d-size").first().text();
                    tor_size = tor_size.replace("Gb", "GB").replace(",",".");
                    if (tor_size.contains("Mb")){
                        if (tor_size.contains("."))
                            tor_size = tor_size.split("\\.")[0].trim();
                        else tor_size = tor_size.split("Mb")[0].trim();

                        float s = Float.parseFloat(tor_size)/1000;
                        tor_size = String.format("%.2f", s) + " GB";
                    }
                    tor_size = tor_size.replace(",", ".");

                    String tor_magnet = torrent.select("a[href^='magnet']").first().attr("href");
                    String tor_content;
                    if (torrent.select(".li_list_a3").html().contains("class=\"folder"))
                        tor_content = torrent.select(".folder").first().text();
                    else tor_content = "koshara";
                    if (tor_content.contains("файлов)") || tor_content.contains("файла)"))
                        tor_content = "koshara (" + tor_content.split("\\(")[1].split("\\)")[0] + ")";

                    if (!itempath.tortitle.contains(tor_name)) {
                        itempath.setTorUrl(torrents);
                        itempath.setTorU(url);
                        itempath.setTorTitle(tor_name);
                        itempath.setTorSize(tor_size);
                        itempath.setTorMagnet(tor_magnet);
                        itempath.setTorContent(tor_content);
                        itempath.setTorLich("x");
                        itempath.setTorSid("x");
                    }
                }

                if (genre.contains("сериал") || genre.contains("мини сериал")) {
                    type = "serial";
                }
                else if (genre.contains("фильм") && !genre.contains("мультфильм")) type = "movie";
                else if (!season.contains("error") && !season.equals("0") && !season.isEmpty()) {
                    type = "serial";
                }
                else type = "movie";
                if (name.contains("сезон)")) {
                    type = "serial";
                    season = name.split("сезон\\)")[0];
                    if (season.contains(")"))
                        if (season.contains(") ("))
                            season = season.split("\\) \\(")[1];
                        else if (season.contains(")("))
                            season = season.split("\\)\\(")[1];
                    if (season.contains("("))
                        season = season.split("\\(")[1];
                    season = season.trim();
                } else if (name.contains("мини-сериал")) {
                    type = "serial";
                    if (name.contains("сезон"))
                        season = name.split(" сезон\\)")[0].split("\\(")[1].trim();
                    else
                        season = "1";
                }
                if (genre.contains("аниме")) type += " anime";
//                    Log.d("qwer", "ParseHtml: " + type);



                for (Element more : data.select(".full-story__related .top__slider_div__item")) {
                    moreseason = "0";
                    moretitle = more.select("span").last().text().trim();
                    moreurl = more.select("a").first().attr("href");
                    moreimg = more.select("img").first().attr("src");
                    if (!moreimg.contains("://"))
                        moreimg = Statics.KOSHARA_URL + moreimg;
//                    Log.e("test", "ParseHtml: "+moreimg );
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

                if (name.contains("СЕЗОН)") || name.contains("сезон)"))
                    name = name.split("\\(")[0];


                if (data.html().contains("kinopoisk.ru/rating/"))
                    kpId = ee.split("kinopoisk\\.ru/rating/")[1];
                else if (data.html().contains("allatv.online/"))
                    kpId = ee.split("allatv\\.online/")[1];
                if (kpId.contains("-id"))
                    kpId = kpId.split("-id")[0];
                if (kpId.contains("."))
                    kpId = kpId.split("\\.")[0];

                if (name.contains("("+year))
                    name = name.split("\\("+year)[0].trim();
//                Log.e("test", "ParseHtml: "+name);
                url_entry = this.url;
                if (!name.contains("error")) {
                    itemSet();
                }
            } else if (data.html().contains("top__slider_div__item") && data.html().contains("dle-content")) {
                Elements allEntries = data.select("#dle-content .top__slider_div__item");

//                Log.e("test", "ParseHtml " +data.select(".dle-content .top__slider_div__item").first().html() );
                for (Element entry : allEntries) {
                    defMore();
                    String entr = entry.html();
                    if (entr.contains("<a")) {
                        url_entry = entry.select("a").first().attr("href");
                    }
                    if (entr.contains("<span")) {
                        name = entry.select("span").first().text();
//                        Log.e("season", "ParseHtml: " + name );
                        if (name.contains("сезон)")) {
                            season = name.split("сезон\\)")[0];
                            if (season.contains(")"))
                                if (season.contains(") ("))
                                    season = season.split("\\) \\(")[1];
                                else if (season.contains(")("))
                                    season = season.split("\\)\\(")[1];
                            if (season.contains("("))
                                season = season.split("\\(")[1];
                        }
//                        Log.e("season", "ParseHtml: " + season );
                        if (name.contains("(")){
                            String y = name.split("\\(")[name.split("\\(").length-1];
                            if (y.contains(")"))
                                y = y.split("\\)")[0].trim();
                            if (y.length() < 5) year = y;
                            name = name.split("\\(")[0];
                        }

                    }
                    if (entr.contains("<img")) {
                        img = entry.select("img").first().attr("src");
                        if (!img.contains("://"))
                            img = Statics.KOSHARA_URL + img;
                    }
                    if (!year.contains("error"))
                        name += " (" + year + ")";
                    itemSet();
                }
            } else Log.e("ParserKoshara", "ParseHtml data not f" );
        } else
            Log.e("ParserKoshara", "ParseHtml data null" );

    }
    
    private Document Getdata(String url) {
        try {
            Log.d("ParserKoshara","get connected to " + url);
            String ref = Statics.KOSHARA_URL;

            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .ignoreContentType(true).referrer(ref).get();
        } catch (Exception e) {
            Log.d("ParserKoshara","connected false to " + url);
            e.printStackTrace();
            return null;
        }
    }
}
