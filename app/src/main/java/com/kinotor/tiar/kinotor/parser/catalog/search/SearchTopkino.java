package com.kinotor.tiar.kinotor.parser.catalog.search;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemSearch;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskSearchCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 10.2018.
 */
public class SearchTopkino extends AsyncTask<Void, Void, Void> {
    private String query;
    private List<ItemSearch> items;
    private OnTaskSearchCallback callback;

    public SearchTopkino(String query, OnTaskSearchCallback callback) {
        items = new ArrayList<>();
        this.query = query;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(items);
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ParseHtml(Getdata(query));
        return null;
    }

    private void ParseHtml(Document data) {
        if (data != null) {
            if (data.html().contains("movie-list__item")) {
                Elements allEntries = data.select(".movie-list__item");
                for (Element entry : allEntries) {
                    String title = "error parsing", url_entry = "error parsing", img = "error parsing",
                            description_t = "error parsing";
                    if (entry.html().contains("movie-list__title")) {
                        url_entry = entry.select(".movie-list__title a").attr("href");
                        title = entry.select(".movie-list__title a").first().text().trim();
                    }
                    if (entry.html().contains("movie-list__params")) {
                        description_t = entry.select(".movie-list__params").text();
                        if (description_t.contains("Рейтинг:")){
                            String f = description_t.split("Рейтинг:")[0].replace("Качество:", "").trim() + ", ";
                            if (description_t.contains("Год:")){
                                String l = description_t.split("Год:")[1].replace(" Жанр:", ",")
                                        .replace(" Страна:", ",").trim();
                                description_t = f + l;
                            }
                        }

                    }
                    if (entry.html().contains("<img")) {
                        img = Statics.TOPKINO_URL + entry.select("img").first().attr("src");
                    }

                    if (!title.contains("error")) {
                        items.add(new ItemSearch(title, description_t, img, url_entry));
                    }
                }
            }
        } else
            Log.d(TAG, "ParseHtml: data error");
    }

    private Document Getdata(String query) {
        try {
            return Jsoup.connect(Statics.TOPKINO_URL + "/engine/mod_gameer/search/frontend/ajax_search.php")
                    .data("query", query)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false).ignoreContentType(true).post();
        } catch (Exception e) {
            return null;
        }
    }
}
