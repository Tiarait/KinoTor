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
public class SearchMyhit extends AsyncTask<Void, Void, Void> {
    private String query;
    private List<ItemSearch> items;
//    private ItemSearch item;
    private OnTaskSearchCallback callback;

    public SearchMyhit(String query, OnTaskSearchCallback callback) {
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
            if (data.html().contains("\"value\":")) {
                String[] allEntries = data.text().split("\"value\":");
                for (String entry : allEntries) {
                    String title = "error", url_entry = "error", img = "error",
                            description_t = "error";
                    if (entry.contains("\"url\":\""))
                        url_entry = Statics.MYHIT_URL + entry.split("\"url\":\"")[1].split("\"")[0].replace("\\","");
                    Log.e(TAG, "ParseHtml: "+entry );
                    Log.e(TAG, "ParseHtml: "+url_entry );

                    if (entry.contains("\",\"search_url"))
                        title = Utils.unicodeToString(entry.split("\",\"search_url")[0].replace("\"","")).trim();
                    if (title.contains("(")) {
                        description_t = title.split("\\(")[1].split("\\)")[0].trim();
                        title = title.split("\\(")[0].trim();
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
            return Jsoup.connect(Statics.MYHIT_URL + "/search/live/?query=" + query.trim())
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false).ignoreContentType(true).get();
        } catch (Exception e) {
            return null;
        }
    }
}
