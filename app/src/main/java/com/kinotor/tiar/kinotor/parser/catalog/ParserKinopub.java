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

import java.util.ArrayList;

/**
 * Created by Tiar on 08.2018.
 */
public class ParserKinopub extends AsyncTask<Void, Void, Void> {
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


    public ParserKinopub(String url, ArrayList<ItemHtml> items, ItemHtml itempath, OnTaskCallback callback) {
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
            if (url.contains("item/view")) {
                defVal();
                url_entry = this.url;
                if (dataHtml.contains("og:title\" content=\"")){
                    name = dataHtml.split("og:title\" content=\"")[1].split("\"")[0];
                    if (name.contains("/")) {
                        subname = name.split("/")[1].trim();
                        name = name.split("/")[0].trim();
                    }
                } else if (dataHtml.contains("<h3>")) {
                    name = data.select("h3").text().trim();
                    if (data.select("h3").html().contains("class=\"text-muted\"")) {
                        subname = data.select("h3 .text-muted").text().trim();
                    }
                    name = name.replace(subname, "").trim();
                }
                if (dataHtml.contains("class=\"img-responsive item-poster-relative\"")) {
                    img = data.select(".img-responsive.item-poster-relative").attr("src").trim();
                }
                if (dataHtml.contains("id=\"plot\""))
                    description_t = data.select("#plot").text().trim();


                type = "movie";
                if (dataHtml.contains("table table-striped")) {
                    Elements allLines = data.select(".table.table-striped tr");
                    for (Element line : allLines) {
                        if (line.text().contains("Год выхода"))
                            year = line.text().replace("Год выхода", "").trim();
                        if (line.text().contains("Жанр"))
                            genre = line.text().trim().replace("Жанр","").trim();
                        if (line.text().contains("Страна"))
                            country = line.text().trim().replace("Страна","").trim();
                        if (line.text().contains("Режиссёр"))
                            director = line.text().trim().replace("Режиссёр","").trim();
                        if (line.text().contains("В ролях"))
                            actors = line.text().trim().replace("В ролях","").trim();
                        if (line.text().contains("Длительность"))
                            time = line.text().trim().replace("Длительность","").trim();
                        if (line.text().contains("Добавлен")) {
                            String l = line.text().replace("Добавлен","").trim();
                            if (l.contains(" сезон")) {
                                type = "serial";
                                season = l.split(" сезон")[0].trim();
                            }
                            if (l.contains(" эпизод")) {
                                type = "serial";
                                series = l.split(" эпизод")[0].trim();
                                if (series.contains("сезон ")) {
                                    series = series.split("сезон ")[1].trim();
                                }
                            }
                        }
                    }
                }

                if (dataHtml.contains("href=\"https://www.imdb.com/title")) {
                    if (rating.contains("error"))
                        rating =  "IMDB["+data.select("a[href^='https://www.imdb.com/title']").text()+"] ";
                    else rating += "IMDB["+data.select("a[href^='https://www.imdb.com/title']").text()+"] ";
                }
                if (dataHtml.contains("href=\"http://www.kinopoisk.ru")) {
                    Element kp = data.selectFirst("a[href^='http://www.kinopoisk.ru']");
                    if (kp.text().contains("film/")) {
                        Statics.KP_ID = kp.text().split("film/")[1].trim();
                        kpId = kp.text().split("film/")[1].trim();
                    }
                    if (rating.contains("error"))
                        rating = "KP["+ kp.text().trim()+"]";
                    else rating += "KP["+kp.text().trim()+"]";
                }
                if (dataHtml.contains("class=\"btn-group")) {
                    String btnHtml = data.selectFirst(".btn-group").html();
                    if (btnHtml.contains(">4K")) {
                        quality = "4K";
                    } else if (btnHtml.contains(">1080p")) {
                        quality = "1080p";
                    } else if (btnHtml.contains(">720p")) {
                        quality = "720p";
                    } else if (btnHtml.contains(">480p")) {
                        quality = "480p";
                    }
                }
                if (year.contains("-"))
                    year = year.split("-")[0];

                if (dataHtml.contains("class=\"util-item\"")){
                    Elements allLines = data.select(".util-item");
                    for (Element more : allLines) {
                        defMore();
                        if (more.html().contains("<a"))
                            moreurl = Statics.KINOPUB_URL + more.selectFirst("a").attr("href").trim();
                        if (more.html().contains("<img"))
                            moreimg = more.selectFirst("img").attr("src");
                        if (more.html().contains("class=\"text-xs"))
                            moretitle = more.select(".text-xs").text().trim();

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

                if (!name.contains("error") && !name.trim().contains("Результаты поиска"))
                    itemSet();
            } else if (dataHtml.contains("col-xs-4 col-sm-3 col-md-2 col-lg-2 col-xl-2")) {
                Elements allEntries = data.select(".col-xs-4.col-sm-3.col-md-2.col-lg-2.col-xl-2");
                for (Element entry : allEntries) {
                    String entryHtml = entry.html();
                    defVal();
                    if (entryHtml.contains("class=\"item-title text-ellipsis\"")) {
                        name = entry.select(".item-title.text-ellipsis a").text().trim();
                        url_entry = Statics.KINOPUB_URL +
                                entry.select(".item-title.text-ellipsis a").attr("href").trim();
                    }
                    if (entryHtml.contains("class=\"img-responsive img-rounded\""))
                        img = entry.select(".img-responsive.img-rounded")
                                .attr("src").trim();
                    if (entryHtml.contains("class=\"item-author text-ellipsis text-sm text-muted\"")) {
                        genre = entry.select(".item-author.text-ellipsis.text-sm.text-muted").text();
                        subname = entry.select(".item-author.text-ellipsis.text-sm.text-muted").text();
                    }
                    if (entryHtml.contains("class=\"glyphicon glyphicon-hd-video")) {
                        quality = "HD";
                    } else if (entryHtml.contains("class=\"material-icons poster4k")) {
                        quality = "4K";
                    }

                    if (entryHtml.contains("href=\"https://www.imdb.com/title")) {
                        rating = entry.select("a[href^='https://www.imdb.com/title']").text();
                    }
                    if (entryHtml.contains("href=\"https://www.kinopoisk.ru") &&
                            (rating.contains("error") || rating.trim().equals("0") || rating.trim().isEmpty())) {
                        rating = entry.select("a[href^='https://www.kinopoisk.ru']").text();
                    }
                    if (entryHtml.contains("class=\"m-r-xs") &&
                            (rating.contains("error") || rating.trim().equals("0") || rating.trim().isEmpty())) {
                        rating = entry.select(".m-r-xs.hidden-lg-down").text().trim();
                    }
                    if (rating.isEmpty())
                        rating = "error";
                    if (!rating.isEmpty() && !rating.contains(",") && !rating.contains(".") && !rating.contains("-") && !rating.equals("0"))
                        rating = "+" + rating;

                    if (!name.contains("error") && !name.trim().contains("Результаты поиска") && !url_entry.contains("/news-kino-serials/"))
                        itemSet();
                }
            } else if (dataHtml.contains("class=\"item r")) {
                Elements allEntries = data.select(".item.r");
                for (Element entry : allEntries) {
                    String entryHtml = entry.html();
                    defVal();
                    if (entryHtml.contains("class=\"item-title text-ellipsis\"")) {
                        name = entry.select(".item-title.text-ellipsis a").text().trim();
                        url_entry = Statics.KINOPUB_URL +
                                entry.select(".item-title.text-ellipsis a").attr("href").trim();
                    }
                    if (entryHtml.contains("class=\"item-author text-ellipsis text-muted\"")) {
                        subname = entry.selectFirst(".item-author.text-ellipsis.text-muted a").text().trim();
                    }
                    if (entryHtml.contains("class=\"item-author text-ellipsis text-sm\"")) {
                        genre = entry.selectFirst(".item-author.text-ellipsis.text-sm").text().trim();
                    }

                    if (entryHtml.contains("<img"))
                        img = entry.selectFirst("img").attr("src").trim();
                    if (entryHtml.contains("class=\"item-author text-ellipsis text-sm text-muted\"")) {
                        genre = entry.select(".item-author.text-ellipsis.text-sm.text-muted").text();
                        subname = entry.select(".item-author.text-ellipsis.text-sm.text-muted").text();
                    }
                    if (entryHtml.contains("class=\"list-inline-item")) {
                        rating = entry.select(".list-inline-item").last().text();
                    }
                    if (!rating.contains(",") && !rating.contains(".") && !rating.contains("-") && !rating.equals("0"))
                        rating = "+" + rating;

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
            Log.e("test", "Getdata: "+url);
            return Jsoup.connect(url)
                    .header("Cookie", Statics.KINOPUB_COOCKIE
                            .replace("{","")
                            .replace("}","")
                            .replace(" , ",";")
                            .replace(",",";"))
                    .ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
