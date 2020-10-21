package com.kinotor.tiar.kinotor.parser.catalog.search;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemSearch;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskSearchCallback;
import com.kinotor.tiar.kinotor.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 10.2018.
 */
public class SearchFilmix extends AsyncTask<Void, Void, Void> {
    private String query;
    private List<ItemSearch> items;
//    private ItemSearch item;
    private OnTaskSearchCallback callback;

    public SearchFilmix(String query, OnTaskSearchCallback callback) {
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
                        String tn = "";
                        if (entry.contains("original_name\":\"")) {
                            tn = Utils.unicodeToString(entry.split("original_name\":\"")[1].split("\"")[0]);
                        }
                        if (!tn.trim().isEmpty())
                            title += " / " + tn;
                    }
                    if (entry.contains("poster\":\""))
                        img = entry.split("poster\":\"")[1].split("\"")[0].replace("\\","");
                    if (entry.contains("year\":") && entry.contains("categories\":\""))
                        description_t = entry.split("year\":")[1].split(",")[0] + " "+
                                Utils.unicodeToString(entry.split("categories\":\"")[1].split("\"")[0]);
                    description_t = description_t.replace("<%/span>","");
                    title = title.replace("%/","");



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
            return Jsoup.connect(Statics.FILMIX_URL + "/api/v2/suggestions?search_word="+query.trim())
                    .data("search_word", query.trim())
                    .header("X-Requested-With", "XMLHttpRequest")
                    .referrer(Statics.FILMIX_URL)
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false).ignoreContentType(true).get();
        } catch (Exception e) {
            return null;
        }
    }
}
