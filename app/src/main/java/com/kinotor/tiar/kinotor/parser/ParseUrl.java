package com.kinotor.tiar.kinotor.parser;

import android.os.AsyncTask;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * Created by Tiar on 19.01.2018.
 */

public class ParseUrl extends AsyncTask <Void, Void, Connection.Response> {
    String url, ref;

    public ParseUrl(String url, String ref) {
        this.url = url;
        this.ref = ref == null ? "http://tparser.org" : ref;
    }

    @Override
    protected Connection.Response doInBackground(Void... voids) {
        Connection.Response data = null;
        try {
            data = Jsoup.connect(url).followRedirects(false).referrer("http://tparser.org")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .ignoreContentType(true)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }
}
