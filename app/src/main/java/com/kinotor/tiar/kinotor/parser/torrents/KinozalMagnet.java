package com.kinotor.tiar.kinotor.parser.torrents;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskLocationCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class KinozalMagnet extends AsyncTask<Void, Void, Void> {
    private String url, location;
    private OnTaskLocationCallback callback;

    public KinozalMagnet(String url, OnTaskLocationCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(location);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        location = "magnet:?xt=urn:btih:" + magnet(get(url));
        Log.d("KinozalMagnet", "magnet: " + location);
        return null;
    }

    private String magnet(Document data) {
        if (data != null) {
            if (data.html().contains("Инфо хеш: ")) {
                String m = data.html().split("Инфо хеш: ")[1].trim();
                if (m.contains("</li>"))
                    return m.split("</li>")[0].trim();
                else return "error";
            } else return "error";
        } else
            return "error";
    }

    private Document get(String url) {
        try {
            if (url.contains("?id="))
                url = url.split("\\?id=")[1].trim();
            Log.e(TAG, "get: "+Statics.KINOZAL_COOCKIE.replace(",",";")+";");
            return Jsoup.connect(Statics.KINOZAL_URL + "/get_srv_details.php?id=" + url + "&action=2")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .header("Cookie", Statics.KINOZAL_COOCKIE.replace(",",";")+";")
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
