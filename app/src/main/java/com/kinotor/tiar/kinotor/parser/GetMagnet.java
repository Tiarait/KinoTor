package com.kinotor.tiar.kinotor.parser;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.utils.OnTaskLocationCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class GetMagnet extends AsyncTask<Void, Void, Void> {
    private String url, location;
    private OnTaskLocationCallback callback;

    public GetMagnet(String url, OnTaskLocationCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(location);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        location = magnet(get(url));
        return null;
    }

    private String magnet(Document data) {
        if (data != null) {
            if (data.html().contains("magnet:?"))
                return data.select("a[href^='magnet:?']").attr("href");
            else return "error";
        } else
            return "error";
    }

    private Document get(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false)
                    .timeout(5000).ignoreContentType(true).get();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "GetLocation: " + url);
            Log.d(TAG, "Location: error");
            return null;
        }
    }
}
