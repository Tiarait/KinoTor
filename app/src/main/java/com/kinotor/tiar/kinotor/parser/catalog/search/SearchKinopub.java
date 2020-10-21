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
public class SearchKinopub extends AsyncTask<Void, Void, Void> {
    private String query;
    private List<ItemSearch> items;
    private OnTaskSearchCallback callback;

    public SearchKinopub(String query, OnTaskSearchCallback callback) {
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
                    if (entry.contains(","))
                        url_entry = Statics.KINOPUB_URL + "/item/view/" + entry.split(",")[0].trim();

                    if (entry.contains("value\":\"")) {
                        title = entry.split("value\":\"")[1].trim();
                        if (title.contains("\"}"))
                            title = title.split("\"\\}")[0].trim();
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
            return Jsoup.connect(Statics.KINOPUB_URL + "/item/autocomplete?query=" + query.trim())
                    .header("Cookie", Statics.KINOPUB_COOCKIE
                            .replace("{","")
                            .replace("}","")
                            .replace(" , ",";")
                            .replace(",",";"))
                    .validateTLSCertificates(false).ignoreContentType(true).get();
        } catch (Exception e) {
            return null;
        }
    }
}
