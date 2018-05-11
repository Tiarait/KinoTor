package com.kinotor.tiar.kinotor.parser.torrents;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.utils.OnTaskLocationCallback;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

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
            URL uri = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Referer", "http://freerutor.me/");
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            Log.d(TAG, "freerutorLocation: " + url);
            Log.d(TAG, "Location: " + connection.getHeaderField("Location"));
            return connection.getHeaderField("Location");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "freerutorLocation: " + url);
            Log.d(TAG, "Location: error");
            return url;
        }
    }
}
