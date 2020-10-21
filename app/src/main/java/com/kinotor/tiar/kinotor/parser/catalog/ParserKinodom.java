package com.kinotor.tiar.kinotor.parser.catalog;

import android.os.AsyncTask;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemMain;
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
public class ParserKinodom extends AsyncTask<Void, Void, Void> {
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


    public ParserKinodom(String url, ArrayList<ItemHtml> items, ItemHtml itempath, OnTaskCallback callback) {
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
        if (url.contains("index.php?do=search"))
            ParseHtml(GetdataSearch(url));
        else if (!url.contains("/news-kino-serials/"))
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
        boolean hide = quality.toLowerCase().contains("ts") && Statics.hideTs;

        if (!hide) {
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
            String dataHtml = data.html();
            if (url.endsWith(".html")) {
                defVal();
                if (dataHtml.contains("class=\"post-title\"")) {
                    name = data.select(".post-title").text().trim();
                    url_entry = this.url;
                }
                if (dataHtml.contains("class=\"post-title-eng\"")) {
                    subname = data.select(".post-title-eng").text().trim();
                }
                if (dataHtml.contains("class=\"b-img-radius\"")) {
                    img = data.select(".b-img-radius").attr("src").trim();
                }
                if (dataHtml.contains("class=\"post-text\""))
                    description_t = data.select(".post-text").text().trim();

                if (dataHtml.contains("post-properties")) {
                    Elements allLines = data.select(".post-properties table tr");
                    for (Element line : allLines) {
                        if (line.text().contains("Год выпуска:"))
                            year = line.text().replace("Год выпуска:", "").trim();
                        if (line.text().contains("Жанр:"))
                            genre = line.text().trim().replace("Жанр:","").trim();
                        if (line.text().contains("Режиссер:"))
                            director = line.text().trim().replace("Режиссер:","").trim();
                        if (line.text().contains("В главных ролях:"))
                            actors = line.text().trim().replace("В главных ролях:","").trim();
                        if (line.text().contains("Перевод:"))
                            translator = line.text().trim().replace("Перевод:","").trim();
                    }
                }
                if (year.contains("-"))
                    year = year.split("-")[0];

                if (translator.toLowerCase().contains("серия"))
                    series = translator.toLowerCase().split("серия")[0].trim();
                if (series.contains("-"))
                    series = series.split("-")[1];
                if (translator.contains("(") && translator.toLowerCase().contains(" сезон"))
                    season = translator.toLowerCase().split("\\(")[1].split(" сезон")[0].trim();
                else season = "1";

                if (translator.toLowerCase().contains("cерия - ")) {
                    translator = translator.toLowerCase().split("cерия - ")[1].split("\\(")[0];
                }

                type = "serial";

                if (name.contains(season + " сезон"))
                    name = name.split(season + " сезон")[0].trim();
                if (name.contains(series + " серия"))
                    name = name.split(series + " серия")[0].trim();

                if (dataHtml.contains("id=\"related\"")){
                    Elements allLines = data.select("#related div");
                    for (Element more : allLines) {
                        defMore();
                        if (more.html().contains("<a")) {
                            moreurl = more.selectFirst("a").attr("href").trim();
                            moretitle = more.select("a div").last().text().trim();
                            moreimg = more.selectFirst("a div").attr("style");
                            if (moreimg.contains("url("))
                                moreimg = moreimg.split("url\\(")[1].split("\\)")[0].trim();
                        }

                        if (!moreurl.isEmpty() && !moretitle.contains("error")) {
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

                if (season.contains("error") || season.trim().isEmpty() || season.trim().equals("0"))
                    season = "1";

                if (!name.contains("error") && !name.trim().contains("Результаты поиска"))
                    itemSet();
            } else if (dataHtml.contains("post shortstory")) {
                Elements allEntries = data.select(".post.shortstory");
                for (Element entry : allEntries) {
                    String entryHtml = entry.html();
                    defVal();
                    if (entryHtml.contains("class=\"post-title\"")) {
                        name = entry.select(".post-title").text().trim();
                    }
                    if (entryHtml.contains("class=\"post info\""))
                        url_entry = entry.select(".post.info a").attr("href").trim();
                    if (name.contains("/"))
                        name = name.split("/")[0].trim();
                    if (entryHtml.contains("class=\"post-image\""))
                        img = entry.select(".post-image")
                                .attr("style").trim();
                    if (img.contains("url("))
                        img = img.split("url\\(")[1].split("\\)")[0].trim();

                    if (entryHtml.contains("class=\"post-year\""))
                        year = entry.select(".post-year").text().replace("Год выпуска:", "").trim();
                    if (year.contains("-"))
                        year = year.split("-")[0];
                    if (entryHtml.contains("class=\"post-genre\""))
                        genre = entry.select(".post-genre").text().replace("Жанр:", "").trim();
                    if (entryHtml.contains("class=\"post-perevod\"")) {
                        String alls = entry.selectFirst(".post-perevod").text().replace("Перевод:", "").trim();
                        if (alls.toLowerCase().contains("серия"))
                            series = alls.toLowerCase().split("серия")[0].trim();
                        if (series.contains("-"))
                            series = series.split("-")[1];
                        if (alls.contains("(") && alls.toLowerCase().contains(" сезон"))
                            season = alls.toLowerCase().split("\\(")[1].split(" сезон")[0].trim();
                        else season = "1";
                    }
                    if (entryHtml.contains("class=\"post-story\""))
                        description_t = entry.select(".post-story").text().trim();

                    type = "serial";

                    if (!year.contains("error"))
                        name += " (" + year + ")";

                    if (!name.contains("error") && !name.trim().contains("Результаты поиска") && !url_entry.contains("/news-kino-serials/"))
                        itemSet();
                }
            }
        }

    }

    private Document GetdataSearch(String url) {
        try {
            Document htmlDoc;
            htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .data("do","search")
                    .data("subaction","search")
                    .data("search_start",ItemMain.xs_value)
                    .data("story", ItemMain.xs_search.replace("+"," "))
                    .ignoreContentType(true).post();
            return htmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private Document Getdata(String url) {
        try {
            Document htmlDoc;
            htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .ignoreContentType(true).get();
            return htmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
