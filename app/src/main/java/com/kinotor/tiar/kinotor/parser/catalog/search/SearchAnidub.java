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
public class SearchAnidub extends AsyncTask<Void, Void, Void> {
    private String query;
    private List<ItemSearch> items;
    private OnTaskSearchCallback callback;

    public SearchAnidub(String query, OnTaskSearchCallback callback) {
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
                        items.add(new ItemSearch(title, description_t, img, url_entry));
                    }
                }
            }
        } else
            Log.d(TAG, "ParseHtml: data error");
    }

    private Document Getdata(String query) {
        try {
            return Jsoup.connect(Statics.ANIDUB_URL + "/engine/ajax/search.php")
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
