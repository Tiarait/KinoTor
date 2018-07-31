package com.kinotor.tiar.kinotor.parser.animevost;

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
 * Created by Tiar on 02.2018.
 */

public class ParserAnimevost extends AsyncTask<Void, Void, Void> {
    private String url;
    private ArrayList<ItemHtml> items;
    private ItemHtml itempath;
    private OnTaskCallback callback;

    public ParserAnimevost(String url, ArrayList<ItemHtml> items, ItemHtml itempath, OnTaskCallback callback){
        this.url = url;
        if (items != null) this.items = items;
        else this.items = new ArrayList<>();
        if (itempath != null) this.itempath = itempath;
        else this.itempath = new ItemHtml();
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        callback.OnCompleted(items, itempath);
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
            String subname = "error", year = "error parsing", country = "error parsing",
                    genre = "error parsing", time = "error parsing", translator = "error parsing",
                    director = "error parsing", actors = "error parsing", description = "error parsing",
                    iframe = "error", type;
            String moretitle = "error", moreurl = "error", moreimg = "error", moreseason = "0",
                    moreseries = "0", morequality = "error";
            String season = "0", series = "0";
            if (data.html().contains("class=\"shortstory\"") && !data.html().contains("id=\"comment\"")) {
                Elements allEntries = data.select(".shortstory");
                for (Element entry : allEntries) {
                    if (entry.html().contains("shortstoryHead")) {
                        title = entry.select(".shortstoryHead").first().text().trim();
                        url_entry = entry.select(".shortstoryHead a").first().attr("href");
                        if (title.contains(" сезон)")) {
                            season = convertSeason(title.split(" сезон\\)")[0].split("\\(")[1].trim());
                        } else season = "1";
                        if (title.contains("/")) {
                            subname = title.split("/")[1];
                            title = title.split("/")[0];
                            if (subname.contains("[")){
                                series = subname.split("\\[")[1].split("]")[0];
                                subname = subname.split("\\[")[0];
                                if (series.contains("-"))
                                    series = series.split("-")[1].split(" ")[0].trim();
                                else if (series.contains(" из"))
                                    series = series.split(" из")[0];
                            }
                        } else if (title.contains("[")){
                            series = subname.split("\\[")[1].split("]")[0];
                            title = subname.split("\\[")[0];
                            if (series.contains("-"))
                                series = series.split("-")[1].split(" ")[0].trim();
                            else if (series.contains(" из"))
                                series = series.split(" из")[0];
                        }
                    }
                    if (entry.html().contains("img class"))
                        img = entry.select("img").first().attr("src");
                    if (!img.contains(Statics.ANIMEVOST_URL))
                        img = Statics.ANIMEVOST_URL + img;
                    if (title.contains("сезон)"))
                        title = title.split("\\(")[0];
                    if (title.contains("(фильм")) {
                        title = title.split("\\(")[0];
                        season = "0";
                        series = "0";
                    }

                    if (entry.html().contains("current-rating"))
                        rating = entry.select(".current-rating").text().trim();

                    if (entry.html().contains("shortstoryContent")) {
                        if (entry.select(".shortstoryContent").text().contains("Год выхода:") &&
                                entry.select(".shortstoryContent").text().contains("Жанр:")) {
                            title = title.trim() + " (" + entry.select(".shortstoryContent").text()
                                    .split("Год выхода:")[1].split("Жанр")[0].trim() + ")";
                            genre = entry.select(".shortstoryContent").text()
                                    .split("Жанр:")[1].trim();
                            if (genre.contains("Тип:")) genre = genre.split("Тип:")[0].trim();
                        }
                    }
                    if (!series.trim().equals("Трейлер") && !series.trim().equals("Анонс") &&
                            !series.trim().equals("Тизер") && !series.trim().equals("Скоро")) {
                        series = series.replace("OVA", "")
                                .replace("ONA", "").replace(" ", "");
                        if (series.isEmpty()) series = "1";
                        itempath.setTitle(title);
                        itempath.setImg(img);
                        itempath.setUrl(url_entry);
                        itempath.setQuality(quality);
                        itempath.setVoice(translator);
                        itempath.setRating(rating);
                        itempath.setGenre(genre);
                        itempath.setSeason(Integer.parseInt(season.replace(" ", "")));
                        itempath.setSeries(Integer.parseInt(series));
                        items.add(itempath);
                    }
                }
            } else if (data.html().contains("id=\"comment\"")) {
                String t = "error";
                if (data.body().text().contains("Тип: ") && data.body().text().contains("Количество серий: "))
                    t = data.body().text().split("Тип: ")[1].split("Количество серий")[0];
                if (t.contains("ТВ") || data.html().contains("2 серия"))
                    type = "serial anime";
                else type = "movie anime";
                if (data.html().contains("imgRadius"))
                    img = data.select(".imgRadius").attr("src");
                if (!img.contains(Statics.ANIMEVOST_URL))
                    img = Statics.ANIMEVOST_URL + img;
                if (data.html().contains("shortstoryHead"))
                    title = data.select(".shortstoryHead").text();
                if (title.contains(" сезон)"))
                    season = convertSeason(title.split(" сезон\\)")[0].split("\\(")[1].trim());
                else season = "1";
                if (title.contains("/")) {
                    subname = title.split("/")[1];
                    if (subname.contains("[")) {
                        series = subname.split("\\[")[1].split("]")[0];
                        subname = subname.split("\\[")[0];
                        if (series.contains("-"))
                            series = series.split("-")[1].split(" ")[0].trim();
                        else if (series.contains(" из"))
                            series = series.split(" из")[0];
                    }
                    title = title.split("/")[0];
                    if (title.contains("сезон)")) title = title.split("\\(")[0];
                    if (title.contains("(фильм")) {
                        title = title.split("\\(")[0];
                        type = "movie anime";
                    }
                } else if (title.contains("[")){
                    series = subname.split("\\[")[1].split("]")[0];
                    title = subname.split("\\[")[0];
                    if (series.contains("-"))
                        series = series.split("-")[1].split(" ")[0].trim();
                    else if (series.contains(" из"))
                        series = series.split(" из")[0];
                }
                if (data.body().text().contains("Год выхода: ") && data.body().text().contains("Жанр: "))
                    year = data.body().text().split("Год выхода: ")[1].split("Жанр")[0];
                if (data.body().text().contains("Жанр: ") && data.body().text().contains("Тип: "))
                    genre = data.body().text().split("Жанр: ")[1].split("Тип")[0];
                if (data.body().text().contains("Количество серий: ")) {
                    if (data.body().text().contains("Режиссёр: "))
                        time = data.body().text().split("Количество серий: ")[1].split("Режиссёр")[0];
                    else if (data.body().text().contains("Рейтинг: "))
                        time = data.body().text().split("Количество серий: ")[1].split("Рейтинг")[0];
                }
                if (time.contains("(") && time.contains(" мин.)"))
                    time = time.split("\\(")[1].split(" мин.\\)")[0] + " мин.";
                if (data.body().text().contains("Режиссёр: ") && data.body().text().contains("Рейтинг: "))
                    director = data.body().text().split("Режиссёр: ")[1].split("Рейтинг")[0];
                if (data.body().text().contains("Описание: ") && data.html().contains("shortstoryContent"))
                    description = data.select(".shortstoryContent table").text().split("Описание: ")[1];
                description = description.replace("<br />", "").replace("\\r", "")
                        .replace("\\n", "").replace("<br>", "")
                        .replace("\\", "");

                if (data.html().contains("var data = {"))
                    iframe = data.html().split("var data = \\{")[1].split("\\}")[0] + "}"
                            .replaceAll("\"", "");
                if (iframe.endsWith(",}")) iframe = iframe.split(",\\}")[0];
                else if (iframe.endsWith("}")) iframe = iframe.split("\\}")[0];
                iframe = iframe.replace("\"", "");

                if (data.html().contains("current-rating"))
                    rating = data.select(".current-rating").text().trim();

                Elements allImg = data.select(".skrin img");
                for (Element preimg : allImg) {
                    itempath.setPreImg(Statics.ANIMEVOST_URL + preimg.attr("src"));
                }
                if (data.html().contains("miniInfo"))
                    itempath.setExtraDetail(data.select(".miniInfo").text());
                if (data.html().contains("nexttime"))
                    itempath.setExtraDetail(data.select("#nexttime").text());

                Log.d(TAG, "ParseAnimevost: "+ iframe);

                itempath.setUrl(url);
                itempath.setTitle(title);
                itempath.setSubTitle(subname);
                itempath.setImg(img);
                itempath.setQuality("HD");
                itempath.setVoice("AnimeVost");
                itempath.setRating(rating);
                itempath.setDescription(description);
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
                    itempath.setSeries(Integer.parseInt(series));
                } catch (Exception ignore){}
            }
        } else
            Log.d(TAG, "ParseHtml: data error");
    }

    private String convertSeason(String s) {
        if (s.contains("второй")) s = "2";
        else if (s.contains("третий")) s = "3";
        else if (s.contains("четвертый")) s = "4";
        else if (s.contains("пятый")) s = "5";
        else if (s.contains("шестой")) s = "6";
        else if (s.contains("седьмой")) s = "7";
        else if (s.contains("восьмой")) s = "8";
        else if (s.contains("девятый")) s = "9";
        else if (s.contains("десятый")) s = "10";
        else s = "1";
        return s;
    }

    private Document Getdata(String url) {
        try {

            Document htmlDoc;
            if (url.contains("page/") || url.contains(".html")) {
                htmlDoc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .timeout(5000).ignoreContentType(true).get();
            } else  {
                htmlDoc = Jsoup.connect(url.split( "'page'")[0])
                        .data("do", "search")
                        .data("subaction", "search")
                        .data("story", ItemMain.xs_search.replace("-", " "))
                        .data("search_start", url.split( "'page'")[1])
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .timeout(5000).ignoreContentType(true).post();
            }
            Log.d(TAG, "Getdata: get connected to " + url);
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "Getdata: connected false to " + url);
            e.printStackTrace();
            return null;
        }
    }
}
