package com.kinotor.tiar.kinotor.parser.torrents;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.utils.OnTaskLocationCallback;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class FreerutorLocation extends AsyncTask<Void, Void, Void> {
    private String url, location, result;
    private OnTaskLocationCallback callback;

    public FreerutorLocation (String url, String location, OnTaskLocationCallback callback) {
        this.url = url;
        this.location = location;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (result != null)
            callback.OnCompleted(result);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        result = getLocation();
        return null;
    }

    private String getLocation() {
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
                    .connect(location)
                    .method(Connection.Method.GET)
                    .referrer(url)
                    .ignoreContentType(true)
                    .validateTLSCertificates(false)
                    .execute();
            Log.d(TAG, "freerutorLocation: " + url);
            Log.d(TAG, "getLocation: " + location);
            Log.d(TAG, "Location: " + response.header("Location"));
            Log.d(TAG, "parse: " + response.parse().html());
            return response.header("Location");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "freerutorLocation: " + url);
            Log.d(TAG, "Location: error");
            return url;
        }
    }
}
