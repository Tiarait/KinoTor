package com.kinotor.tiar.kinotor.parser.catalog;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemCatalogUrls;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskCallback;
import com.kinotor.tiar.kinotor.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Tiar on 08.2018.
 */
public class ParserRufilmtv extends AsyncTask<Void, Void, Void> {
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


    public ParserRufilmtv(String url, ArrayList<ItemHtml> items, ItemHtml itempath, OnTaskCallback callback) {
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
        itempath.setUrl(url_entry.trim());
        itempath.setTitle(name.trim());
        itempath.setImg(img.trim());
        itempath.setSubTitle(subname.trim());
        itempath.setQuality(quality.trim());
        itempath.setVoice(translator.trim());
        itempath.setRating(rating.trim());
        itempath.setDescription(description_t.trim());
        itempath.setDate(year.trim());
        itempath.setKpId(kpId.trim());
        itempath.setCountry(country.trim());
        itempath.setGenre(Utils.renGenre(genre).trim());
        itempath.setDirector(director.trim());
        itempath.setActors(actors.trim());
        itempath.setTime(time.trim());
        itempath.setIframe(iframe.trim());
        itempath.setType(type.trim());
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

            if (data.html().contains("class=\"item-video")) {
                Elements allEntries = data.select(".item-video");
                for (Element entry : allEntries) {
                    defVal();
                    if (entry.html().contains("class=\"video-name\"")) {
                        name = entry.select(".video-name a").text().trim();
                        url_entry = Statics.RUFILMTV_URL + entry.select(".video-name a").attr("href").trim();
                    }
                    String allLines = entry.select(".tags.hidden-sm").text();

                    if (allLines.contains(","))
                    for (String line : allLines.split(",")) {
                        if (Arrays.toString(ItemCatalogUrls.countryRufilmtv).contains(line.trim()))
                            country += line + " ";
                        if (Arrays.toString(ItemCatalogUrls.sortRufilmtvYear).contains(line.trim()))
                            year = line.trim();
                        if (Arrays.toString(ItemCatalogUrls.cRufilmtvGenre).contains(line.trim()))
                            genre += line + " ";
                    }
                    if (!genre.trim().equals("error"))
                        genre = genre.replace("error","").trim();
                    if (!country.trim().equals("error"))
                        country = country.replace("error","").trim();

                    if (entry.html().contains("class=\"label-age\"")) {
                        rating = entry.select(".label-age").text().trim();
                    }
                    if (entry.html().contains("class=\"label-quality"))
                        quality = entry.select(".label-quality").text().trim();

                    if (entry.html().contains("class=\"image-poster\"")) {
                        img = entry.select(".image-poster img").attr("src").trim();
                    }


                    type = "movie";
                    if (entry.html().contains("class=\"label-season\"")) {
                        String l = entry.select(".label-season").text();
                        if (l.contains("сезон") || l.contains("серия") || l.contains("выпуск")) {
                            type = "serial";
                            if (l.contains(" сезон"))
                                season = l.split(" сезон")[0].trim();
                            else season = "1";
                            if (l.contains(" серия")) {
                                if (season.equals("error"))
                                    season = "1";
                                series = l.split(" серия")[0].trim();
                                if (series.contains(" "))
                                    series = series.split(" ")[series.split(" ").length-1].trim();
                            }
                            if (l.contains(" выпуск")) {
                                if (season.equals("error"))
                                    season = "1";
                                series = l.split(" выпуск")[0].trim();
                                if (series.contains(" "))
                                    series = series.split(" ")[series.split(" ").length-1].trim();
                            }
                        }
                        if (translator.contains("-"))
                            translator = translator.split("-")[0];

                        if (season.contains("-")) {
                            season = season.split("-")[1].trim();
                        } else if (season.contains(",")) {
                            season = season.split(",")[1].trim();
                        }
                        if (series.contains("-")) {
                            series = series.split("-")[1].trim();
                        } else if (series.contains(",")) {
                            series = series.split(",")[1].trim();
                        }
                    }
                    if (!name.contains(year) && !year.contains("error"))
                        name += " (" + year.trim() + ")";
                    boolean hide = quality.toLowerCase().contains("ts") && Statics.hideTs;

                    if (this.url.contains("/?s=")) {
                        Log.e("tt", "ParseHtml: "+this.url + name);
                        if (this.url.split("/\\?s=").length > 1) {
                            if (!name.contains("error") &&
                                    name.trim().toLowerCase().contains(this.url.split("/\\?s=")[1]
                                            .replace("+", " ").toLowerCase().trim()) && !hide)
                                itemSet();
                        } else if (!name.contains("error") && !hide)
                            itemSet();
                    } else if (!name.contains("error") && !hide)
                        itemSet();
                }
            } else if (data.html().contains("id=\"post-")) {
                Element entry = data.select("#page").first();
                defVal();

                if (entry.html().contains("class=\"video-title")) {
//                    name = entry.select(".video-title").text().trim();
                    url_entry = this.url;
                }
                if (entry.html().contains("class=\"image-poster")) {
                    name = entry.select(".image-poster img").attr("alt").trim();
                    img = entry.select(".image-poster img").attr("src").trim();
                }

                if (entry.html().contains("class=\"view-caption-title")) {
                    subname = entry.select(".view-caption-title").text().trim();
                }
                Elements allLines = data.select(".info-table tr");
                for (Element line : allLines) {
                    if (line.text().contains("Год:"))
                        year = line.text().replace("Год:", "").trim();
                    if (line.text().contains("Страна:"))
                        country += line.text().replace("Страна:", "") + " ";
                    if (line.text().contains("Жанр:"))
                        genre += line.text().replace("Жанр:", "") + " ";
                    if (line.text().contains("Перевод:"))
                        translator = line.text().replace("Перевод:", "").trim();
                    if (line.text().contains("Серия:"))
                        season = line.text().replace("Серия:", "").trim();
                }
                if (!genre.trim().equals("error"))
                    genre = genre.replace("error","").trim();
                if (!country.trim().equals("error"))
                    country = country.replace("error","").trim();

                Elements allLines2 = data.select(".tab-pane.info-tab-list tr");
                for (Element line : allLines2) {
                    if (line.text().contains("Актеры:"))
                        actors += line.text().replace("Актеры:", "") + " ";
                    if (line.text().contains("Режиссер:"))
                        director += line.text().replace("Страна:", "") + " ";
                }
                if (!actors.trim().equals("error"))
                    actors = actors.replace("error","").trim();
                if (!director.trim().equals("error"))
                    director = director.replace("error","").trim();

//                if (data.html().contains("data-kpid=\"")){
//                    kpId = data.html().split("data-kpid=\"")[1].split("\"")[0];
//                }
                if (entry.html().contains("class=\"vendor")){
                    Elements vendor = entry.select(".vendor iframe");
                    iframe = "";
                    for (Element ifrm : vendor){
                        iframe += "||http:" + ifrm.attr("src");
                    }
                    if (iframe.trim().equals("||http:") || iframe.trim().equals("||http:||http:") ||
                            iframe.isEmpty()) iframe = "error";
                    if (iframe.trim().startsWith("||")) iframe = iframe.substring(2);
                }
                if (entry.html().contains("class=\"description"))
                    description_t = entry.select(".description").first().text().trim();
                description_t = description_t.replace("<b>","").replace("</b>","")
                        .replace("Cмотреть "+name.trim()+" " + year, "")
                        .replace("Фильм "+name.trim()+" смотреть онлайн", "")
                        .replace("Сериал "+name.trim()+" смотреть онлайн", "")
                        .replace("Мультфильм "+name.trim()+" смотреть онлайн", "")
                        .replace(name.trim()+" смотреть онлайн", "")
                        .replace("смотреть онлайн", "")
                        .replace("&nbsp;","");
                description_t = description_t.substring(0, 1).toUpperCase() + description_t.substring(1);

                if (!season.contains("сезон") && !season.contains("серия") && !season.trim().equals("0")) {
                    description_t = "<br>" + season.replace("-", " - ") + "\n<br>" + description_t;
                }

                if (entry.html().contains("class=\"rating-list-single"))
                    rating = entry.select(".rating-list-single").text().trim();

                if (entry.html().contains("class=\"label-quality"))
                    quality = entry.select(".label-quality").text().trim();


                if (name.contains("сезон") || name.contains("выпуск") || name.contains("серия") || !season.contains("error")) {
                    if (!season.contains("error")){
                        if (season.contains("выпуск")) {
                            series = season.split("выпуск")[0].trim();
                            if (series.contains("сезон "))
                                series = series.split("сезон ")[1].trim();
                        }
                        if (season.contains("серия")) {
                            series = season.split("серия")[0].trim();
                            if (series.contains("сезон "))
                                series = series.split("сезон ")[1].trim();
                        }
                        if (season.contains(" сезон")) {
                            season = season.split(" сезон")[0].trim();
                        } else if (!series.contains("0") && !series.trim().isEmpty())
                            season = "1";
                    } else {
                        if (name.contains(" выпуск")){
                            series = name.split(" выпуск")[0].trim();
                            series = series.split(" ")[season.split(" ").length-1];
                        }
                        if (name.contains(" серия")){
                            series = name.split(" серия")[0].trim();
                            series = series.split(" ")[season.split(" ").length-1];
                        }
                        if (name.contains(" сезон")){
                            season = name.split(" сезон")[0].trim();
                            season = season.split(" ")[season.split(" ").length-1];
                        } else if (!series.contains("0") && !series.trim().isEmpty())
                            season = "1";
                    }
                }
                if (season.contains("-")) {
                    season = season.split("-")[1].trim();
                } else if (season.contains(",")) {
                    season = season.split(",")[1].trim();
                }
                if (series.contains("-")) {
                    series = series.split("-")[1].trim();
                } else if (series.contains(",")) {
                    series = series.split(",")[1].trim();
                }
                if (this.url.contains("serials") || this.url.contains("tv-show")) {
                    type = "serial";
                    if (season.trim().equals("0") || season.contains(".") || season.length() > 5)
                        season = "1";
                    if (series.trim().equals("0") || series.contains(".") || series.length() > 5)
                        series = "1";
                } else {
                    type = "movie";
                    season = "0";
                    series = "0";
                }
                if (name.contains(year))
                    name = name.split(year)[0].trim();

                if (!name.contains(year))
                    name += " (" + year + ")";

                if (!name.contains("error"))
                    itemSet();
            }
        }

    }

    private Document Getdata(String url) {
        try {
            if (Statics.ProxyUse.contains("rufilmtv") && Statics.ProxyCur.contains(":") && !Statics.ProxyCur.contains("адрес:порт")){
                System.setProperty("http.proxyHost", Statics.ProxyCur.split(":")[0].trim());
                System.setProperty("http.proxyPort", Statics.ProxyCur.split(":")[1].trim());
            } else {
                System.clearProperty("http.proxyHost");
                System.clearProperty("http.proxyPort");
            }
            return Jsoup.connect(url)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(30000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
