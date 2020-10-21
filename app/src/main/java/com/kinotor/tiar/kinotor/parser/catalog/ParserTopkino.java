package com.kinotor.tiar.kinotor.parser.catalog;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskCallback;
import com.kinotor.tiar.kinotor.utils.Utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Tiar on 08.2018.
 */
public class ParserTopkino extends AsyncTask<Void, Void, Void> {
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


    public ParserTopkino(String url, ArrayList<ItemHtml> items, ItemHtml itempath, OnTaskCallback callback) {
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
        Log.e("google", "onPostExecute: "+Statics.KP_ID );
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (url.contains("sphinx_search.php"))
            ParseHtml(GetDataSearch());
        else ParseHtml(Getdata(url));
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
            if (data.html().contains("movie-list__item")) {
                Elements allEntries = data.select("article");
                for (Element entry : allEntries) {
                    defVal();
                    if (entry.html().contains("class=\"movie-list__title\"")) {
                        name = entry.select(".movie-list__title a").first().text().trim();
                        url_entry = entry.select(".movie-list__title a").attr("href").trim();
                    }
                    if (name.contains("/"))
                        name = name.split("/")[0].trim();
                    if (entry.html().contains("class=\"movie-list__subtitle\""))
                        subname = entry.select(".movie-list__subtitle h4").text().trim();
                    if (entry.html().contains("class=\"movie-list__img-inner\""))
                        img = Statics.TOPKINO_URL + entry.select(".movie-list__img-inner img")
                                .attr("src").trim();
                    if (entry.html().contains("class=\"text-primary js-quality\""))
                        quality = entry.select(".text-primary.js-quality").text().trim();

                    Elements allLines = entry.select(".movie-list__params tr");
                    for (Element line : allLines) {
                        if (line.text().contains("Год:"))
                            year = line.text().replace("Год:", "").trim();
                        if (line.text().contains("Страна:"))
                            country = line.text().replace("Страна:", "").trim();
                        if (line.text().contains("Жанр:"))
                            genre = line.text().replace("Жанр:", "").trim();
                        if (line.text().contains("Рейтинг:"))
                            rating = line.text().replace("Рейтинг:", "").trim();
                    }
                    if (url_entry.contains("film/")) {
                        kpId = url_entry.split("film/")[1].split("/")[0].trim();
                        Statics.KP_ID = kpId;
                    } if (rating.contains("Кинопоиск "))
                        rating = rating.split("Кинопоиск ")[1].trim();
                    if (rating.contains("IMDb "))
                        rating = rating.split("IMDb ")[0].trim();
                    rating = rating.replace("&nbsp;", "");

                    type = "movie";
                    if (entry.attr("data-movie").contains("\"season\" : \"")) {
                        type = "serial";
                        season = entry.attr("data-movie").split("season\" : \"")[1].split(" сезон")[0].trim();
                        if (entry.attr("data-movie").contains("series\" : \"")) {
                            series = entry.attr("data-movie").split("series\" : \"")[1].split(" серия")[0].trim();
                        }
                    }

                    if (!year.contains("error"))
                        name += " (" + year + ")";
                    boolean hide = quality.toLowerCase().contains("ts") && Statics.hideTs;

                    if (!name.contains("error") && !hide)
                        itemSet();
                }
            } else {
                defVal();
                if (data.html().contains("class=\"action-header__title\"")) {
                    name = data.select(".action-header__title h1").text().trim();
                    subname = data.select(".action-header__title h2").text().trim();
                    url_entry = this.url;
                    img = Statics.TOPKINO_URL + data.select(".action-header__title img")
                            .attr("src").trim();
                }
                if (name.contains("/"))
                    name = name.split("/")[0].trim();
                if (data.html().contains("class=\"text-primary js-quality\""))
                    quality = data.select(".text-primary.js-quality").text().trim();

                if (data.html().contains("id=\"progress-count\"")) {
                    if (!data.select("#progress-count").first().text().trim().isEmpty())
                        rating = "SITE["+data.select("#progress-count").first().text().trim() + "] ";
                }
                if (data.html().contains("class=\"imdb rki\"")) {
                    if (rating.contains("error"))
                        rating =  "IMDB["+data.select(".imdb.rki").first().text().trim()+"] ";
                    else rating += "IMDB["+data.select(".imdb.rki").first().text().trim()+"] ";
                }
                if (data.html().contains("class=\"kinopoisk rki\">")) {
                    if (rating.contains("error"))
                        rating = "KP["+data.html().split("kinopoisk rki\">")[1].split("<")[0].trim()+"]";
                    else rating += "KP["+data.html().split("kinopoisk rki\">")[1].split("<")[0].trim()+"]";
                }

//                if (data.html().contains("class=\"kinopoisk rki\"")) {
//                    rating = data.select(".kinopoisk.rki").text().trim();
//                } else if (data.html().contains("class=\"imdb rki\"")) {
//                    rating = data.select(".imdb.rki").text().trim();
//                } else if (data.html().contains("id=\"progress-count\""))
//                    rating = data.select("#progress-count").text().trim();

                Elements allLines = data.select(".movie-info tr");
                for (Element line : allLines) {
                    if (line.text().contains("Год:"))
                        year = line.text().replace("Год:", "").trim();
                    if (line.text().contains("Страна:"))
                        country = line.text().replace("Страна:", "").trim();
                    if (line.text().contains("Жанр:"))
                        genre = line.text().replace("Жанр:", "").trim();
                    if (line.text().contains("Время:"))
                        time = line.text().replace("Время:", "").trim();
                    if (line.text().contains("Звук:"))
                        translator = line.text().replace("Звук:", "").trim();
                    if (line.text().contains("Режиссер:"))
                        director = line.text().replace("Режиссер:", "").trim();
                    if (line.text().contains("Актеры:"))
                        actors = line.text().replace("Актеры:", "").trim();
                }
                if (data.html().contains("class=\"movie-desc__inner\""))
                    description_t = data.select(".movie-desc__inner").text().trim();
                description_t = description_t.replace("<b>","").replace("</b>","")
                        .replace("<strong>", "")
                        .replace("</strong>", "")
                        .replace("&nbsp;","");
                if (description_t.contains("Смотрите онлайн"))
                    description_t = description_t.split("Смотрите онлайн")[0].trim();
                if (description_t.contains("Хорошее качество и озвучка у нас появляются очень"))
                    description_t = description_t.split("Хорошее качество и озвучка у нас появляются очень")[0];
                if (description_t.contains("Желаем Вам приятного онлайн просмотра на нашем сайте"))
                    description_t = description_t.split("Желаем Вам приятного онлайн просмотра на нашем сайте")[0];

                if (img.contains("/") && img.contains(".")) {
                    kpId = img.split("/")[img.split("/").length - 1].split("\\.")[0].trim();
                    Statics.KP_ID = kpId;
                }

                Log.d("t", "ParseHtml: "+kpId);

                type = "movie";
                if (data.html().contains("season = '")) {
                    type = "serial";
                    season = data.html().split("season = '")[1].split(" сезон")[0].trim();
                    if (data.html().contains("episod = '")) {
                        series = data.html().split("episod = '")[1].split(" серия")[0].trim();
                    }
                }

                if (!year.contains("error"))
                    name += " (" + year + ")";

                if (!name.contains("error"))
                    itemSet();
            }
        }

    }

    private Document GetDataSearch(){
        String url = Statics.FILMIX_URL + "/engine/ajax/sphinx_search.php";
        String search;
        if (url.startsWith("http://cameleo.xyz/r?url=")) {
            url = "http://cameleo.xyz/r?url=" +
                    url.split("r\\?url=")[1].replace("/", "%2F");
            search = "%2Fsearch%2F";
        } else search = "/search/";
        try {
            if (Statics.ProxyUse.contains("topkino") && Statics.ProxyCur.contains(":") && !Statics.ProxyCur.contains("адрес:порт")){
                System.setProperty("http.proxyHost", Statics.ProxyCur.split(":")[0].trim());
                System.setProperty("http.proxyPort", Statics.ProxyCur.split(":")[1].trim());
            } else {
                System.clearProperty("http.proxyHost");
                System.clearProperty("http.proxyPort");
            }
            Connection.Response res = Jsoup
                    .connect(Statics.FILMIX_URL + search)
                    .execute();
            String loginCookies = res.cookies().toString().replace("{","")
                    .replace("}","").replace(", ",";").trim()+";";
            Document htmlDoc = Jsoup.connect(url)
                    .data("scf", "fx")
                    .data("story", ItemMain.xs_search)
                    .data("search_start", ItemMain.xs_value)
                    .header("Cookie", loginCookies)
//                    .header("Cookie", loginCookies.replace("per_page_news=15", "per_page_news=60"))
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .ignoreContentType(true).post();
            return htmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Document Getdata(String url) {
        try {
//            if (Statics.ProxyUse.contains("topkino") && Statics.ProxyCur.contains(":") && !Statics.ProxyCur.contains("адрес:порт")){
//                System.setProperty("http.proxyHost", Statics.ProxyCur.split(":")[0].trim());
//                System.setProperty("http.proxyPort", Statics.ProxyCur.split(":")[1].trim());
//                Log.e("test", "proxy +");Log.e("test", "proxy +");
//            } else {
//                System.clearProperty("http.proxyHost");
//                System.clearProperty("http.proxyPort");
//            }
            //++++++++++++++++++++++++++++++++++++
//            Document htmlDoc1 = Jsoup.connect("https://www.kinopoisk.ru/")
//                    .proxy("109.72.229.77",53281)
//                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
//                    .timeout(10000).ignoreContentType(true).get();
//            Log.e("kp", "Getdata: "+ htmlDoc1.body().html());
            Document htmlDoc;
            if (url.startsWith("http://cameleo.xyz/r?url="))
                url = "http://cameleo.xyz/r?url=" +
                        url.split("r\\?url=")[1].replace("/","%2F");
            htmlDoc = Jsoup.connect(url)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .ignoreContentType(true).get();
            return htmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
