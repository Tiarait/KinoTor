package com.kinotor.tiar.kinotor.parser.catalog.filmix;

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
public class ParserFilmixFav extends AsyncTask<Void, Void, Void> {
    private String url;
    private ArrayList<ItemHtml> items;
    private ItemHtml itempath;
    private OnTaskCallback callback;
    private boolean noblock = true;

    private String url_entry = "error ", img = "error ", kpId = "error",
            quality = "error ", rating = "error ";
    private String name = "error", subname = "error", year = "error ", country = "error ",
            genre = "error ", time = "error ", translator = "error ",
            director = "error ", actors = "error ", description_t = "error ",
            iframe = "error", type = "error";
    private String season = "0", series = "0";


    public ParserFilmixFav(String url, OnTaskCallback callback) {
        this.url = url;
        items = new ArrayList<>();
        itempath = new ItemHtml();
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

    private void itemSet(){
        if (url_entry.trim().isEmpty())
            url_entry = this.url;

        if (subname.trim().isEmpty())
            subname = "error";
        if (!name.contains("error") && !url_entry.contains("error")) {
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
            } catch (Exception e) {
                itempath.setSeason(0);
                itempath.setSeries(0);
            }
            items.add(itempath);
        }
    }

    private void ParseHtml(Document data) {
        if (data != null) {
            String dd = data.body().html();

            Log.e("test", "Getdata 1: "+url );
            Elements allEntries = data.select("article");
            for (Element entry : allEntries) {
                String ee = entry.html();
                defVal();
                
                if (ee.contains("class=\"name\"")) {
                    name = entry.select(".name").text().trim();
                    url_entry = entry.select(".name a").attr("href").trim();
                }
                if (dd.contains("trailerVideoLink = '")){
                    String tr = dd.split("trailerVideoLink = '")[1].split("'")[0];
                    if (!tr.trim().isEmpty())
                        iframe = iframe + "[trailer]" + Utils.decodeUppod(tr);
                }
//                Log.e("test", "Getdata id1: "+iframe+"|"+url );

                if (ee.contains("class=\"origin-name\""))
                    subname = entry.select(".origin-name").text().trim();
                if (ee.contains("class=\"item year\""))
                    year = entry.select(".year > .item-content a").first().text().trim();
                if (ee.contains("class=\"item contry\""))
                    country = entry.select(".contry > .item-content").text().trim();

                if (ee.contains("class=\"poster poster-tooltip\""))
                    img = entry.select(".poster.poster-tooltip").attr("src").trim();
                else if (ee.contains("class=\"poster\""))
                    img = entry.select(".poster").attr("src").trim();
                if (ee.contains("class=\"quality\""))
                    quality = entry.select(".quality").text().trim();
                if (ee.contains("class=\"like\"")) {
                    rating = "SITE["+entry.select(".like span").first().text().trim() + "] ";
                }
                if (ee.contains("class=\"imdb")) {
                    if (rating.contains("error"))
                        rating =  "IMDB["+entry.select(".imdb p").first().text().trim()+"] ";
                    else rating += "IMDB["+entry.select(".imdb p").first().text().trim()+"] ";
                }
                if (ee.contains("class=\"kinopoisk")) {
                    if (rating.contains("error"))
                        rating = "KP["+entry.select(".kinopoisk p").first().text().trim()+"]";
                    else rating += "KP["+entry.select(".kinopoisk p").first().text().trim()+"]";
                }
                if (ee.contains("class=\"item category\""))
                    genre = entry.select(".category > .item-content").first().text().trim();
                if (ee.contains("itemprop=\"genre\"")) {
                    Elements genres = entry.select("a[itemprop='genre']");
                    genre = "";
                    for (Element g : genres){
                        genre += g.text().trim() + " ";
                    }
                } else if (ee.contains("Жанры:")){
                    String gnr = ee.split("Жанры:")[1];
                    if (gnr.contains("text-item\">")) {
                        gnr = gnr.split("text-item\">")[1];
                        if (gnr.contains("<span")) genre = gnr.split("<span")[0]
                                .replace("\"", "").trim();
                    }

                }

                if (ee.contains("class=\"item translate\""))
                    translator = entry.select(".translate > .item-content").first().text().trim();
                else if (ee.contains("item count-movies"))
                    translator = entry.select(".item.count-movies").text().trim();
                translator = translator.replace(",", " ")
                        .replace("Всего фильмов:", "").trim();
                if (ee.contains("class=\"item durarion\""))
                    time = entry.select(".durarion > .item-content").text().trim();
                type = "movie";
                if (ee.contains("class=\"added-info\"")) {
                    String inf = entry.select(".added-info").text().trim();
                    if (inf.contains(" сезон)")) {
                        type = "serial";
                        season = inf.split(" сезон\\)")[0].split("\\(")[1].trim();
                    }
                    if (season.contains("-")) {
                        season = season.split("-")[1].trim();
                    }
                    if (inf.contains(" серия")) {
                        type = "serial";
                        series = inf.split(" серия")[0].trim();
                    }
                    if (series.contains("-"))
                        series = series.split("-")[1].trim();
                }

                if (dd.contains("class=\"slider-item\"") && ee.contains("class=\"fancybox\"")){
                    img = entry.select(".fancybox").attr("href").trim();
                }

                if (url.contains("/anime/"))
                    type += " anime";
                if (!year.contains("error"))
                    name += " (" + year + ")";

                Log.e("test", "ParseHtml: "+name );
                if (!name.contains("error"))
                    itemSet();
            }
            if (dd.contains("class=\"navigation") && noblock) {
                noblock = false;
                for (int i = 0; i < data.select(".navigation").select("a").size() - 1; i++) {
//                    Log.e("test", "ParseHtml: "+data.select(".navigation").select("a").get(i).attr("href"));
                    ParseHtml(Getdata(data.select(".navigation").select("a").get(i).attr("href")));
                }
            }  else {
//                Log.e("test", "ParseHtml: one page");
            }
        }

    }

    private Document Getdata(String url) {
        try {
            Log.e("test", "Getdata 0: "+Statics.FILMIX_COOCKIE.replace(",",";"));
            return Jsoup.connect(url)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("Cookie", Statics.FILMIX_COOCKIE.replace(",",";") +";")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false)
                    .timeout(30000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
