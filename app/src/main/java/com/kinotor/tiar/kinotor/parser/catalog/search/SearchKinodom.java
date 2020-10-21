package com.kinotor.tiar.kinotor.parser.catalog.search;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemSearch;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskSearchCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 10.2018.
 */
public class SearchKinodom extends AsyncTask<Void, Void, Void> {
    private String query;
    private List<ItemSearch> items;
//    private ItemSearch item;
    private OnTaskSearchCallback callback;

    public SearchKinodom(String query, OnTaskSearchCallback callback) {
        items = new ArrayList<>();
//        item = new ItemSearch();
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
                for (String e : data.html().split("</a>")) {
                    e = e + "</a>";
                    Document entry = Jsoup.parse(e);
                    String title = "error", url_entry = "error", img = "error",
                            description_t = "error";
                    if (entry.html().contains("href=\""))
                        url_entry = entry.selectFirst("a").attr("href");
                    if (entry.html().contains("class=\"searchheading\""))
                        title = entry.selectFirst(".searchheading").text().trim();
                    if (entry.html().contains("<span>")) {
                        description_t = entry.select("span").last().text().trim();
                    }

                    Log.e(TAG, "ParseHtml: "+url_entry);

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
            return Jsoup.connect(Statics.KINODOM_URL + "/engine/ajax/search.php")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .data("query", query.trim())
                    .validateTLSCertificates(false).ignoreContentType(true).post();
        } catch (Exception e) {
            return null;
        }
    }
}
