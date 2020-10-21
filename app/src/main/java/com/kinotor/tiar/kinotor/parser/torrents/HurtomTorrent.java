package com.kinotor.tiar.kinotor.parser.torrents;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskLocationCallback;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class HurtomTorrent extends AsyncTask<Void, Void, Void> {
    private String url, location;
    private OnTaskLocationCallback callback;

    public HurtomTorrent(String url, OnTaskLocationCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(location);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Connection.Response res = Getdata();
        if (res != null) {
            Statics.HURTOM_COOCKIE = res.cookies().toString().replace("{","").replace("}","");
        }
        location = Statics.HURTOM_URL + "/" + torrent(get(url));

        return null;
    }

    private String torrent(Document data) {
        if (data != null) {
            if (data.html().contains("download.php")) {
                return data.select("a[href^='download.php']").attr("href");
            } else return "error";
        } else
            return "error";
    }

    private Connection.Response Getdata() {
        try {
            return Jsoup
                    .connect(Statics.HURTOM_URL + "/login.php")
                    .method(Connection.Method.POST)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .data("username", Statics.HURTOM_ACC)
                    .data("password", Statics.HURTOM_PASS)
                    .data("autologin", "on")
                    .data("redirect", "")
                    .data("login", "Вхід")
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Document get(String url) {
        try {
            Log.e(TAG, "get: "+Statics.HURTOM_COOCKIE.replace(",",";")+";");
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .header("Cookie", Statics.HURTOM_COOCKIE.replace(",",";")+";")
                    .validateTLSCertificates(false)
                    .timeout(5000).ignoreContentType(true).get();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Location: error");
            return null;
        }
    }
}
