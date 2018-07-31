package com.kinotor.tiar.kinotor.parser.torrents;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskLocationCallback;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class FreerutorLocation extends AsyncTask<Void, Void, Void> {
    private String url, location;
    private OnTaskLocationCallback callback;

    public FreerutorLocation (String url, OnTaskLocationCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (location != null)
            callback.OnCompleted(location);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        location = getLocation(url);
        return null;
    }

    private String getLocation(String url) {
        try {
//            URL uri = new URL(url);
//            HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
//            connection.setRequestMethod("GET");
//            connection.setRequestProperty("Referer", Statics.FREERUTOR_URL);
//            connection.setInstanceFollowRedirects(false);
//            connection.connect();
//            Log.d(TAG, "freerutorLocation: " + url);
//            Log.d(TAG, "Location: " + connection.getHeaderField("Location"));
//            return connection.getHeaderField("Location");
            Connection.Response response = Jsoup
                    .connect(url)
                    .method(Connection.Method.POST)
                    .referrer(Statics.FREERUTOR_URL)
                    .followRedirects(false)
                    .validateTLSCertificates(false)
                    .execute();
            Log.d(TAG, "freerutorLocation: " + url);
            Log.d(TAG, "Location: " + response.header("Location"));
            return response.header("Location");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "freerutorLocation: " + url);
            Log.d(TAG, "Location: error");
            return url;
        }
    }
}
