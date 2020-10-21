package com.kinotor.tiar.kinotor.parser.catalog.filmix;

import android.os.AsyncTask;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskLocationCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Tiar on 08.2018.
 */
public class ParserFilmixFavCheck extends AsyncTask<Void, Void, Void> {
    private String url, result = "error";
    private OnTaskLocationCallback callback;


    public ParserFilmixFavCheck(String url, OnTaskLocationCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(result.trim());

        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        ParseHtml(Getdata(url));
        return null;
    }

    private void ParseHtml(Document data) {
        if (data != null) {
            if (data.html().contains("favorite active"))
                result = "favor";
            if (data.html().contains("future active")) {
                if (result.contains("error"))
                    result = "later";
                else result += " later";
            }
        }
    }

    private Document Getdata(String url) {
        try {
            return Jsoup.connect(url)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("Cookie", Statics.FILMIX_COOCKIE.replace(",",";") +";")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false)
                    .timeout(30000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
