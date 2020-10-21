package com.kinotor.tiar.kinotor.parser;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskLocationCallback;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class GetLocation extends AsyncTask<Void, Void, Void> {
    private String url, location;
    private OnTaskLocationCallback callback;

    public GetLocation(String url, OnTaskLocationCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(location);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        location = getLocation(url);
        return null;
    }

    private String getLocation(String url) {
        String ref;
        if (url.contains("drek")) ref = "http://animevost.org/";
        else if (url.contains(Statics.NNM_URL)) ref = Statics.NNM_URL;
        else ref = "http://hdgo.cc/";
        try {
            URL uri = new URL(checkUrl(url.trim()));
            HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Referer", ref);
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            Log.d(TAG, "GetLocation: " + url);
            Log.d(TAG, "Location: " + connection.getHeaderField("Location"));
            String loc = connection.getHeaderField("Location");
            return loc != null ? loc : url;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "GetLocation: " + url);
            Log.d(TAG, "Location: error");
            return url;
        }
    }

    private String checkUrl(String url) {
        url = url.replace(" ", "").replace("\n", "").replaceAll("\r", "");
        url = url.replace("\"", "");
        if (!url.contains("http://") && !url.contains("https://")) url = url.contains("//") ? "http:" + url : "http://" + url;
        return url;
    }
}
