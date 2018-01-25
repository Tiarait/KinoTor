package com.kinotor.tiar.kinotor.parser;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemDetail;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.ui.DetailActivity;
import com.kinotor.tiar.kinotor.utils.AdapterCatalog;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import static com.kinotor.tiar.kinotor.items.ItemMain.isLoading;

/**
 * Created by Tiar on 24.09.2017.
 */

public class ParserHtml extends AsyncTask<Void, Void, Void> {
    private String url;
    private RecyclerView rv;
    private RelativeLayout pb = null;
    private LinearLayout lpb = null;
    public static ArrayList<ItemHtml> items;
    public static ItemHtml itemHtml;
    public static ItemDetail itemDetail;
    private String page;
    private File file;

    public ParserHtml(String url, String page, RecyclerView rv, RelativeLayout pb){
        this.url = url;
        this.page = page;
        this.rv = rv;
        this.pb = pb;
    }

    public ParserHtml(String url, String page, RecyclerView rv, LinearLayout pb) {
        this.url = url;
        this.page = page;
        this.rv = rv;
        this.lpb = pb;
    }

    public ParserHtml(String url, String page) {
        this.url = url;
        this.page = page;
    }

    @Override
    protected void onPreExecute() {
        isLoading = true;
        if (pb != null) pb.setVisibility(View.VISIBLE);
        if (lpb != null) lpb.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (items != null && rv != null) {
            ((AdapterCatalog) rv.getAdapter()).setHtmlItems(items);
            rv.getAdapter().notifyDataSetChanged();
        }
        if (page.equals("detail")) {
            DetailActivity.setInfo();
        }
        if (page.contains("torrent")){
            if (page.contains("play")) {
                playTor(file);
                ItemMain.status = "Запуск bittorrent клиента...";
            } else
                ItemMain.status = "Загружено в " + file;

            Toast.makeText(DetailActivity.fragm_tor.getContext(),
                    ItemMain.status, Toast.LENGTH_LONG).show();
        }
        isLoading = false;
        if (pb != null) pb.setVisibility(View.GONE);
        if (lpb != null) lpb.setVisibility(View.GONE);
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if(page.contains("torrent")) {
            Torrent(url);
        }  else ParseHtml(Getdata(url));
        return null;
    }

    private void ParseHtml(Document data) {
        if (data != null) {
            if (items == null) {
                items = new ArrayList<>();
                itemHtml = new ItemHtml();
            }
            if (itemDetail == null) itemDetail = new ItemDetail();
            String title = "error parsing", url_entry = "error parsing", img = "error parsing",
                    date = "error parsing", description = "error parsing", voice = "error parsing",
                    quality = "error parsing";
            String name = "error", year = "error parsing", country = "error parsing", genre = "error parsing",
                    time = "error parsing", quality_t = "error parsing", translator = "error parsing",
                    director = "error parsing", actors = "error parsing", description_t = "error parsing",
                    img_t = "error parsing", torrents = "error parsing", tor_name = "error parsing",
                    tor_magnet = "error parsing", tor_size = "error parsing", tor_takes = "error parsing",
                    tor_content = "error parsing", iframe = "error";
            String season = "0", series = "0";
            if (url.contains("koshara.co")) {
                //main page
                if (page.equals("catalog")) {
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
                                img = "http://koshara.co" + img;
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

                        items.add(AddItem(itemHtml, title, url_entry, img, date,
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
                                img = "http://koshara.co" + img;
                        }
                        if (search.html().contains("sres-date"))
                            date = search.select(".sres-date").first().text();
                        if (search.html().contains("sres-desc"))
                            description = search.select(".sres-desc").first().text();


                        date = date.split(",")[0];
                        description = description.split("1400Mb")[0].split("2100Mb")[0];

                        items.add(AddItem(itemHtml, title, url_entry, img, date,
                                description + "...", voice, quality, season, series));
                    }
                }
                //video page
                if (page.equals("detail") && data.html().contains("full-article")) {
                    Elements details = data.select(".full-article");
                    if (details.html().contains("side-title"))
                        name = details.select(".side-title").first().text();
                    else
                        name = details.html().split("смотреть онлайн ")[1].split(" в хорошем качестве")[0];
                    if (details.html().contains("в хорошем качестве")) {
                        String n = details.html().split("смотреть онлайн ")[1].split(" в хорошем качестве")[0];
                        if (n.contains("сезон)")) {
                            DetailActivity.type = "serial";
                            season = n.split(" сезон\\)")[0];
                            if (season.contains(")"))
                                if (season.contains(") ("))
                                    season = season.split("\\) \\(")[1];
                                else season = season.split("\\)\\(")[1];
                            else season = season.split("\\(")[1];
                            DetailActivity.season = season.trim();
                        } else if (n.contains("мини-сериал")){
                            DetailActivity.type = "serial";
                            if (n.contains("сезон"))
                                DetailActivity.season = n.split(" сезон\\)")[0].split("\\(")[1].trim();
                            else
                                DetailActivity.season = "1";
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

                    Elements allTorrents = data.select(".torrent");
                    for (Element torrent : allTorrents) {
                        torrents = "http://koshara.co" + torrent.select(".title a").first().attr("href");
                        tor_name = torrent.select(".info_d").first().text();
                        if (tor_name.contains("_KOSHARA"))
                            tor_name = tor_name.split("_KOSHARA")[0];
                        tor_size = torrent.select(".cont").first().text().split("Размер: ")[1].split("Последняя")[0];
                        tor_magnet = torrent.select("a[href^='magnet']").first().attr("href");
                        if (torrent.select(".li_list_a3").html().contains("class=\"folder"))
                            tor_content = torrent.select(".folder").first().text();
                        else tor_content = "koshara.co";
                        if (tor_content.contains("файлов)") || tor_content.contains("файла)"))
                            tor_content = "koshara.co (" + tor_content.split("\\(")[1].split("\\)")[0] + ")";

                        if (!itemDetail.torrents.contains(torrents)) {
                            itemDetail.setTorrents(torrents);
                            itemDetail.setTor_name(tor_name);
                            itemDetail.setTor_size(tor_size);
                            itemDetail.setTor_magnet(tor_magnet);
                            itemDetail.setTor_content(tor_content);
                            itemDetail.setTor_lich("x");
                            itemDetail.setTor_sid("x");
                        }
                    }

                    Elements allMore = data.select(".owl-item");
                    for (Element more : allMore) {
                        String more_img = more.select(".rel-movie img").attr("src");
                        String more_url = more.select(".rel-movie").attr("href");
                        itemDetail.setMore_url(more_url.contains("://") ? more_url : "http://koshara.co" + more_url);
                        itemDetail.setMore_img(more_img.contains("://") ? more_img : "http://koshara.co" + more_img);
                        itemDetail.setMore_title(more.select(".rel-movie-title").text());
                    }

                    itemDetail.setName(name);
                    itemDetail.setYear(year);
                    itemDetail.setCountry(country);
                    itemDetail.setTranslator(translator);
                    itemDetail.setGenre(genre);
                    itemDetail.setQuality(quality_t);
                    itemDetail.setDirector(director);
                    itemDetail.setActors(actors);
                    itemDetail.setTime(time);
                    itemDetail.setDescription(description_t);

                    itemDetail.setSeries(0);
                    itemDetail.setSeason(Integer.parseInt(season));
                    itemDetail.setIframe(iframe);

                    DetailActivity.iframe = iframe;
                }
                //torrent file
                if (page.equals("torrent")) {
                }
            } else if (url.contains("coldfilm.ru")){
                voice = "Coldfilm";
                quality = "HD";
                if (page.equals("catalog")) {
                    //search
                    if (url.contains("/search/")){
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
                                    url_entry = "http://coldfilm.ru" + url_entry;
                                if (entry.html().contains("sres-img") && entry.html().contains("img src")) {
                                    img = entry.select(".sres-img img").first().attr("src");
                                    if (!img.contains("://"))
                                        img = "http://coldfilm.ru" + img;
                                }
                                if (entry.html().contains("sres-desc"))
                                    description = entry.select(".sres-desc").first().text();
                                if (description.contains("Обзор]") || description.contains("Трейлер]") ||
                                        description.contains("Удалено по просьбе правообладателя"))
                                    title = "error";

                                if (!title.contains("error"))
                                    items.add(AddItem(itemHtml, title, url_entry, img, date,
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
                                    url_entry = "http://coldfilm.ru" + url_entry;
                            }
                            if (entry.html().contains("kino-img")) {
                                img = entry.select(".kino-img img").first().attr("src");
                                if (!img.contains("://"))
                                    img = "http://coldfilm.ru" + img;
                            }
                            if (entry.html().contains("kino-lines"))
                                quality = entry.select(".kino-lines").first().text()
                                        .split("Качество:")[1].split("Просмотров")[0];
                            if (entry.html().contains("kino-date"))
                                date = entry.select(".kino-date").first().text();
                            if (entry.html().contains("kino-desc"))
                                description = entry.select(".kino-desc").first().text();
                            if (description.contains("Обзор]") || description.contains("Трейлер]"))
                                title = "error";

                            if (!title.contains("error"))
                                items.add(AddItem(itemHtml, title, url_entry, img, date,
                                        description + "...", voice, quality, season, series));
                        }
                    }
                }
                //video page
                if (page.equals("detail") && data.html().contains("kino-inner-full")) {
                    DetailActivity.type = "serial";
                    if (data.html().contains("kino-h")) {
                        name = data.select(".kino-h").first().text();
                        season = name.split("сезон")[0];
                        String[] arr = season.split(" ");
                        season = arr[arr.length - 1].replaceAll("\u00a0", "").trim();
                        if (title.contains("серия")) {
                            series = title.split("сезон")[1].split("серия")[0].replaceAll("\u00a0", "").trim();
                            if (series.contains("-"))
                                series = series.split("-")[1];
                        }
                    }
                    if (name.contains("[Смотреть")) name = name.split("\\[Смотреть")[0];

                    country = "...";
                    translator = "ColdFilm";
                    genre = "...";
                    quality_t = "HD";
                    director = "...";
                    actors = "...";
                    time = "...";
                    description_t = "Перевод: Профессиональный многоголосый закадровый - ColdFilm";

                    if (data.html().contains("player-box visible full-text"))
                        iframe = data.select(".player-box.visible.full-text iframe").first().attr("src");
                    if (data.html().contains("kino-date icon-left"))
                        year = data.select(".kino-date").first().text();
                    year = year.contains("\"") ? year.replaceAll("\"", "") : year;

                    Elements allTorrents = data.select("a[href$=\".torrent\"]");
                    for (Element torrent : allTorrents) {
                        torrents = torrent.attr("href");
                        String[] arr = torrents.split("/");
                        tor_name = arr[arr.length - 1].split(".torrent")[0];

                        if (!itemDetail.torrents.contains(torrents)) {
                            itemDetail.setTorrents(torrents);
                            itemDetail.setTor_name(tor_name);
                            itemDetail.setTor_size(tor_size);
                            itemDetail.setTor_magnet(tor_magnet);
                            itemDetail.setTor_content("coldfilm.ru");
                            itemDetail.setTor_lich("x");
                            itemDetail.setTor_sid("x");
                        }
                    }

                    Elements allMore = data.select(".owl-item");
                    for (Element more : allMore) {
                        String more_img = more.select(".rel-kino-img img").attr("src");
                        String more_url = more.select(".rel-kino").attr("href");
                        itemDetail.setMore_url(more_url.contains("://") ? more_url : "http://coldfilm.ru" + more_url);
                        itemDetail.setMore_img(more_img.contains("://") ? more_img : "http://coldfilm.ru" + more_img);
                        itemDetail.setMore_title(more.select(".rel-kino-title").text());
                    }

                    itemDetail.setName(name);
                    itemDetail.setYear(year);
                    itemDetail.setCountry(country);
                    itemDetail.setTranslator(translator);
                    itemDetail.setGenre(genre);
                    itemDetail.setQuality(quality_t);
                    itemDetail.setDirector(director);
                    itemDetail.setActors(actors);
                    itemDetail.setTime(time);
                    itemDetail.setDescription(description_t);

                    itemDetail.setSeries(Integer.parseInt(series));
                    itemDetail.setSeason(Integer.parseInt(season));
                    itemDetail.setIframe(iframe);

                    DetailActivity.iframe = iframe;
                }
            } else {
                items.add(AddItem(itemHtml, title, url_entry, img, date, description,
                        voice, quality, season, series));
            }
            Log.d("mydebug","parse items done");
        }
    }

    private ItemHtml AddItem (ItemHtml htmlItem, String title, String url, String img, String date, String description,
                              String voice, String quality, String season, String series){
        htmlItem.setTitle(title);
        htmlItem.setUrl(url);
        htmlItem.setImg(img);
        htmlItem.setDate(date);
        htmlItem.setDescription(description);
        htmlItem.setVoice(voice);
        htmlItem.setQuality(quality);
        if (TextUtils.isDigitsOnly(season)) htmlItem.setSeason(Integer.parseInt(season));
        else htmlItem.setSeason(99);
        if (TextUtils.isDigitsOnly(series)) htmlItem.setSeries(Integer.parseInt(series));
        else htmlItem.setSeries(99);

        return htmlItem;
    }

    public void Torrent(String url){
        if (page.contains("down")) {
            try {
                String ref = url.contains("koshara.co") ? "http://koshara.co" : "http://coldfilm.ru";
                Connection.Response res = Jsoup.connect(url).referrer(ref)
                        .timeout(5000).ignoreContentType(true).postDataCharset("CP1251").execute();
                String path = Environment.getExternalStorageDirectory() + "/" +
                        DetailActivity.activity.getString(R.string.app_name) + "/";
                String name = itemDetail.getTor_name(itemDetail.getCur())
                        .replace("/", ",").replace(":", " -")
                        .replace("|", ".");
                file = new File(path);
                if (!file.exists()) {
                    file.mkdirs();
                }
                if (!name.contains(".torrent"))
                    file = new File(path + name + ".torrent");
                else file = new File(path + name);

                try {
                    file.createNewFile();
                    OutputStream fo = new FileOutputStream(file);
                    fo.write(res.bodyAsBytes());
                    fo.close();
                    Log.d("mydebug", "file : " + file);

                } catch (IOException e) {
                    ItemMain.status = "Ошибка торрент файла ):";
                    e.printStackTrace();
                }
            } catch (Exception e) {
                ItemMain.status = "Ошибка загрузки торрент файла ):";
                Log.d("mydebug", "connected false to " + url);
                e.printStackTrace();
            }
        }
    }

    private void playTor (File file) {
        Uri uri = file != null ? Uri.fromFile(file) : Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW);

        try {
            intent.setDataAndType(uri, "application/x-bittorrent");
            DetailActivity.fragm_tor.getContext().startActivity(intent);
        } catch (Exception e) {
            intent.setDataAndType(uri, "application/*");
            DetailActivity.fragm_tor.getContext().startActivity(intent);
            e.printStackTrace();
        }
    }


    private Document Getdata(String url) {
        try {
            Document htmlDoc;
            String ref = "";
            if (ItemMain.cur_url.contains("koshara.co") || url.contains("koshara.co"))
                ref = "http://koshara.co";
            if (ItemMain.cur_url.contains("coldfilm.ru") || url.contains("coldfilm.ru"))
                ref = "http://coldfilm.ru";
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
