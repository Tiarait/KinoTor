package com.kinotor.tiar.kinotor.utils.voice;

import android.app.SearchManager;
import android.media.Rating;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.items.movie.Movie;
import com.kinotor.tiar.kinotor.items.movie.MovieBuilder;
import com.kinotor.tiar.kinotor.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Tiar on 12.2018.
 */
public class VideoDatabase  {


    // The columns we'll include in the video database table
    public static final String KEY_NAME = SearchManager.SUGGEST_COLUMN_TEXT_1;
    public static final String KEY_DESCRIPTION = SearchManager.SUGGEST_COLUMN_TEXT_2;
    public static final String KEY_ICON = SearchManager.SUGGEST_COLUMN_RESULT_CARD_IMAGE;
    public static final String KEY_DATA_TYPE = SearchManager.SUGGEST_COLUMN_CONTENT_TYPE;
    public static final String KEY_IS_LIVE = SearchManager.SUGGEST_COLUMN_IS_LIVE;
    public static final String KEY_VIDEO_WIDTH = SearchManager.SUGGEST_COLUMN_VIDEO_WIDTH;
    public static final String KEY_VIDEO_HEIGHT = SearchManager.SUGGEST_COLUMN_VIDEO_HEIGHT;
    public static final String KEY_AUDIO_CHANNEL_CONFIG =
            SearchManager.SUGGEST_COLUMN_AUDIO_CHANNEL_CONFIG;
    public static final String KEY_PURCHASE_PRICE = SearchManager.SUGGEST_COLUMN_PURCHASE_PRICE;
    public static final String KEY_RENTAL_PRICE = SearchManager.SUGGEST_COLUMN_RENTAL_PRICE;
    public static final String KEY_RATING_STYLE = SearchManager.SUGGEST_COLUMN_RATING_STYLE;
    public static final String KEY_RATING_SCORE = SearchManager.SUGGEST_COLUMN_RATING_SCORE;
    public static final String KEY_PRODUCTION_YEAR = SearchManager.SUGGEST_COLUMN_PRODUCTION_YEAR;
    public static final String KEY_COLUMN_DURATION = SearchManager.SUGGEST_COLUMN_DURATION;
    public static final String KEY_ACTION = SearchManager.SUGGEST_COLUMN_INTENT_ACTION;

    private static List<Movie> movies;


    /**
     * Searches for a movie whose title or description can match against the query parameter.
     *
     * @param query Search string.
     * @return A list of movies that match the query string.
     */
    public List<Movie> search(String query, String catalog) throws IOException {
        movies = new ArrayList<>();
        searchFilmix(query);
        if (movies.size() < 3) {
            searchMyhit(query);
        }
        if (movies.size() < 3) {
            searchAnimevost(query);
        }
        if (movies.size() < 3) {
            searchAnidub(query);
        }
        return movies;
    }

    private void searchFilmix(String query) throws IOException {
        Document data = Jsoup.connect(Statics.FILMIX_URL + "/api/v2/suggestions?search_word="+query.trim())
                .data("search_word", query.trim())
                .header("X-Requested-With", "XMLHttpRequest")
                .referrer(Statics.FILMIX_URL)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                .validateTLSCertificates(false).ignoreContentType(true).get();
        if (data != null) {
            if (data.html().contains("\"id\":")) {
                String[] allEntries = data.text().split("\"id\":");
                for (String entry : allEntries) {

                    entry = entry.replace("\\\"", "'");
                    String title = "error", url_entry = "error", img = "error",
                            description_t = "error";
                    if (entry.contains("link\":\""))
                        url_entry = entry.split("link\":\"")[1].split("\"")[0].replace("\\","");

                    if (entry.contains("title\":\"")) {
                        title = Utils.unicodeToString(entry.split("title\":\"")[1].split("\"")[0]);
                    }
                    if (entry.contains("poster\":\""))
                        img = entry.split("poster\":\"")[1].split("\"")[0].replace("\\","").replace("/w40/", "/w220/");
                    if (entry.contains("year\":") && entry.contains("categories\":\""))
                        description_t = entry.split("year\":")[1].split(",")[0] + " "+
                                Utils.unicodeToString(entry.split("categories\":\"")[1].split("\"")[0]);
                    description_t = description_t.replace("<%/span>","");
                    title = title.replace("%/","");



                    if (!title.contains("error")) {
                        Statics.list.add(url_entry);
                        MovieBuilder builder = new MovieBuilder();
                        builder.setId(Statics.list.size() - 1)
                                .setTitle(title)
                                .setDescription(description_t)
                                .setCardImage(img)
                                .setBackgroundImage(img)
                                .setVideoUrl(url_entry)
                                .setContentType("video/*")
                                .setWidth(460)
                                .setHeight(720)
                                .setAudioChannelConfig("2.0")
                                .setPurchasePrice("$0")
                                .setRentalPrice("$0")
                                .setRatingStyle(Rating.RATING_5_STARS)
                                .setRatingScore(5f)
                                .setDuration((int) TimeUnit.HOURS.toMillis(1));
                        movies.add(builder.createMovie());
                    }
                }
            }
        }
    }

    private void searchAnidub(String query) throws IOException {
        Document data = Jsoup.connect(Statics.ANIDUB_URL + "/engine/ajax/search.php")
                .data("query", query)
                .header("X-Requested-With", "XMLHttpRequest")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                .validateTLSCertificates(false).ignoreContentType(true).post();
        if (data != null) {
            if (data.html().contains("</a>")) {
                Elements allEntries = data.select("a");
                for (Element entry : allEntries) {
                    String title = "error parsing", url_entry = "error parsing", img = "error parsing",
                            description_t = "error parsing";
                    url_entry = entry.attr("href");
                    if (entry.html().contains("searchheading")) {
                        title = entry.select(".searchheading").first().text().trim();
                    }
                    if (entry.html().contains("span")) {
                        description_t = entry.select("span").last().text();
                    }
                    if (entry.html().contains("<img ")) {
                        img = entry.select("img").first().attr("src");
                    }

                    if (!title.contains("error")) {
                        Statics.list.add(url_entry);
                        MovieBuilder builder = new MovieBuilder();
                        if (Statics.list.size() != 0) {
                            builder.setId(Statics.list.size() - 1)
                                    .setTitle(title)
                                    .setDescription(description_t)
                                    .setCardImage(img)
                                    .setBackgroundImage(img)
                                    .setVideoUrl(url_entry)
                                    .setContentType("video/*")
                                    .setWidth(460)
                                    .setHeight(720)
                                    .setAudioChannelConfig("2.0")
                                    .setPurchasePrice("$0")
                                    .setRentalPrice("$0")
                                    .setRatingStyle(Rating.RATING_5_STARS)
                                    .setRatingScore(5f);
                        } else {
                            builder.setId(Statics.list.size() - 1)
                                    .setTitle(title)
                                    .setDescription(description_t)
                                    .setCardImage(img)
                                    .setBackgroundImage(img)
                                    .setVideoUrl(url_entry)
                                    .setContentType("video/*")
                                    .setWidth(460)
                                    .setHeight(720)
                                    .setAudioChannelConfig("2.0")
                                    .setPurchasePrice("$0")
                                    .setRentalPrice("$0")
                                    .setRatingStyle(Rating.RATING_5_STARS)
                                    .setRatingScore(5f)
                                    .setDuration((int) TimeUnit.HOURS.toMillis(1));
                        }
                        movies.add(builder.createMovie());
                    }
                }
            }
        }
    }

    private void searchAnimevost(String query) throws IOException {
        Document data = Jsoup.connect(Statics.ANIMEVOST_URL + "/engine/ajax/search.php")
                .data("query", query)
                .header("X-Requested-With", "XMLHttpRequest")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                .validateTLSCertificates(false).ignoreContentType(true).post();
        if (data != null) {
            if (data.html().contains("</a>")) {
                Elements allEntries = data.select("a");
                for (Element entry : allEntries) {
                    String title = "error parsing", url_entry = "error parsing", img = "error parsing",
                            description_t = "error parsing";
                    url_entry = entry.attr("href");
                    if (entry.html().contains("searchheading")) {
                        title = entry.select(".searchheading").first().text().trim();
                    }
                    if (entry.html().contains("span")) {
                        description_t = entry.select("span").last().text();
                    }
                    if (entry.html().contains("<img ")) {
                        img = entry.select("img").first().attr("src");
                    }

                    if (!title.contains("error")) {
                        Statics.list.add(url_entry);
                        MovieBuilder builder = new MovieBuilder();
                        if (Statics.list.size() == 0 && !url_entry.contains("/actor/")) {
                            builder.setId(Statics.list.size() - 1)
                                    .setTitle(title)
                                    .setDescription(description_t)
                                    .setCardImage(img)
                                    .setBackgroundImage(img)
                                    .setVideoUrl(url_entry)
                                    .setContentType("video/*")
                                    .setWidth(460)
                                    .setHeight(720)
                                    .setAudioChannelConfig("2.0")
                                    .setPurchasePrice("$0")
                                    .setRentalPrice("$0")
                                    .setRatingStyle(Rating.RATING_5_STARS)
                                    .setRatingScore(5f)
                                    .setDuration((int) TimeUnit.HOURS.toMillis(1));
                        } else {
                            builder.setId(Statics.list.size() - 1)
                                    .setTitle(title)
                                    .setDescription(description_t)
                                    .setCardImage(img)
                                    .setBackgroundImage(img)
                                    .setVideoUrl(url_entry)
                                    .setContentType("video/*")
                                    .setWidth(460)
                                    .setHeight(720)
                                    .setAudioChannelConfig("2.0")
                                    .setPurchasePrice("$0")
                                    .setRentalPrice("$0")
                                    .setRatingStyle(Rating.RATING_5_STARS)
                                    .setRatingScore(5f);
                        }
                        movies.add(builder.createMovie());
                    }
                }
            }
        }
    }


    private void searchMyhit(String query) throws IOException {
        Document myhit = Jsoup.connect(Statics.MYHIT_URL + "/search/?q=" + query.trim())
                .header("X-Requested-With", "XMLHttpRequest")
                .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                .validateTLSCertificates(false)
                .timeout(30000).ignoreContentType(true).get();
        if (myhit != null) {
            Elements allEntries = myhit.select(".row");
            for (Element entry : allEntries) {
                String title = "error", url_entry = "error", img = "error",
                        description_t = "", year = "error", genre = "error";
                String ee = entry.html();
                if (ee.contains("class=\"col-xs-9\"")) {
                    title = entry.select(".col-xs-9 b a").text().trim();
                    url_entry = Statics.MYHIT_URL + entry.select(".col-xs-9 b a").attr("href").trim();
                }
                if (ee.contains("col-xs-9")) {
                    if (entry.select(".col-xs-9").html().contains("list-unstyled")) {
                        Element allL = entry.select(".list-unstyled").last();
                        if (allL.html().contains("<li")) {
                            Elements allLines = allL.select("li");
                            for (Element line : allLines) {
                                if (line.text().contains("Год:"))
                                    year = line.text().replace("Год:", "").trim().replace(".","");
                                else if (line.text().contains("Жанр:"))
                                    genre = line.text().replace("Жанр:", "").trim();
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
                if (!year.contains("error"))
                    description_t = year;
                if (!genre.contains("error"))
                    description_t += " " + genre;

                if (ee.contains("class=\"img-rounded img-responsive\""))
                    img = Statics.MYHIT_URL + entry.select(".img-rounded.img-responsive").attr("src").trim();


                if (title.contains("/")) {
                    if (title.split("/").length > 2) title = "error";
                    else title = title.split("/")[0].trim();
                }

                if (!title.contains("error")) {
                    Statics.list.add(url_entry);
                    MovieBuilder builder = new MovieBuilder();
                    if (Statics.list.size() == 0 && !url_entry.contains("/actor/")) {
                        builder.setId(Statics.list.size() - 1)
                                .setTitle(title)
                                .setDescription(description_t)
                                .setCardImage(img)
                                .setBackgroundImage(img)
                                .setVideoUrl(url_entry)
                                .setContentType("video/*")
                                .setWidth(460)
                                .setHeight(720)
                                .setAudioChannelConfig("2.0")
                                .setPurchasePrice("$0")
                                .setRentalPrice("$0")
                                .setRatingStyle(Rating.RATING_5_STARS)
                                .setRatingScore(5f)
                                .setDuration((int) TimeUnit.HOURS.toMillis(1));
                    } else {
                        builder.setId(Statics.list.size() - 1)
                                .setTitle(title)
                                .setDescription(description_t)
                                .setCardImage(img)
                                .setBackgroundImage(img)
                                .setVideoUrl(url_entry)
                                .setContentType("video/*")
                                .setWidth(460)
                                .setHeight(720)
                                .setAudioChannelConfig("2.0")
                                .setPurchasePrice("$0")
                                .setRentalPrice("$0")
                                .setRatingStyle(Rating.RATING_5_STARS)
                                .setRatingScore(5f);
                    }
                    movies.add(builder.createMovie());
                }
            }
        }
    }
}
