package com.kinotor.tiar.kinotor.parser.trailer;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.utils.OnTaskUrlCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class TrailerUrl extends AsyncTask<Void, Void, Void> {
    private String url;
    private String[] quality_arr, url_arr;
    private OnTaskUrlCallback callback;

    public TrailerUrl(String url, OnTaskUrlCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(quality_arr, url_arr);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        getUrl(getData(url));
        return null;
    }

    private void getUrl(Document data) {
        final ArrayList<String> q = new ArrayList<>();
        final ArrayList<String> u = new ArrayList<>();
        if (data != null) {
            if (data.body().html().contains("dop-download")) {
                Elements allEntries = data.select(".dop-download-item");
                for (Element entry : allEntries) {
                    Element a = entry.select("a").last();
                    String url = "http://www.kinomania.ru" + a.attr("href");
                    q.add(a.text().replace("HD", "").trim() + " (mp4)");
                    u.add(url);
                }
            }
        }
        if (q.size() == 0) {
            q.add("видео недоступно");
            u.add("error");
        }
        quality_arr = q.toArray(new String[q.size()]);
        url_arr = u.toArray(new String[u.size()]);
    }

    private Document getData(String url){
        try {
            Document htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(5000).ignoreContentType(true).get();
            Log.d(TAG, "Getdata: connected to " + url);
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "Getdata: connected false to " + url);
            e.printStackTrace();
            return null;
        }
    }

}
