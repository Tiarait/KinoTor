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
public class SearchFanserials extends AsyncTask<Void, Void, Void> {
    private String query;
    private List<ItemSearch> items;
    private OnTaskSearchCallback callback;

    public SearchFanserials(String query, OnTaskSearchCallback callback) {
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
            if (data.html().contains("item-serial")) {
                Elements allEntries = data.select(".item-serial");
                for (Element entry : allEntries) {
                    String title = "error parsing", url_entry = "error parsing", img = "error parsing",
                            description_t = "error parsing";
                    if (entry.html().contains("field-title")) {
                        url_entry = entry.select(".field-title a").attr("href");
                        title = entry.select(".field-title a").text().trim();
                    }
                    if (entry.html().contains("field-description")) {
                        description_t = entry.select(".field-description a").text();
                    }
                    if (entry.html().contains("field-img")) {
                        img = entry.select(".field-img").first().attr("style");
                        if (img.contains("url("))
                            img = img.split("url\\(")[1].split("\\)")[0];
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
            return Jsoup.connect(Statics.FANSERIALS_URL + "/a_search/?query="+query)
                    .data("query", query)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("Content-Type", "text/html; charset=windows-1251")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false).ignoreContentType(true).post();
        } catch (Exception e) {
            return null;
        }
    }
}
