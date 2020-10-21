package com.kinotor.tiar.kinotor.parser;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.utils.OnTaskLocationCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class GetQualBluRay extends AsyncTask<Void, Void, Void> {
    private String title, location = "null";
    private OnTaskLocationCallback callback;

    public GetQualBluRay(String title, OnTaskLocationCallback callback) {
        this.title = title;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Log.d("GetQualBluRay", "Qual: "+location);
        callback.OnCompleted(location);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (!title.equals("error"))
            checkQual(get(title));
        return null;
    }

    private void checkQual (Document document){
        if (document != null) {
            if (document.html().contains("<li")){
                for (Element li : document.select("li")){
                    if (li.text().contains(title)) {
                        if (li.text().contains(title + " 4K")) {
                            location = "4K";
                            break;
                        } else location = "1080p";
                    }
                }
            }
        }
    }

    private Document get(String url) {
        try {
            return Jsoup.connect("https://www.blu-ray.com/search/quicksearch.php")
                    .data("section","bluraymovies")
                    .data("userid","-1")
                    .data("country","all")
                    .data("keyword",url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false)
                    .timeout(5000).ignoreContentType(true).post();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "GetLocation: " + url);
            Log.d(TAG, "Location: error");
            return null;
        }
    }
}
