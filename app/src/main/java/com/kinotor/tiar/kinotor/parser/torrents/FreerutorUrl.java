package com.kinotor.tiar.kinotor.parser.torrents;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskLocationCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class FreerutorUrl  extends AsyncTask<Void, Void, Void> {
    private String url, file, magnet, torrent;
    private OnTaskLocationCallback callback;

    public FreerutorUrl(String url, String file, OnTaskLocationCallback callback) {
        this.url = url.trim();
        this.file = file;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (file.equals("magnet")) {
            if (!magnet.startsWith("magnet")) {
                FreerutorLocation getLocation = new FreerutorLocation(magnet, new OnTaskLocationCallback() {
                    @Override
                    public void OnCompleted(String location) {
                        callback.OnCompleted(location);
                    }
                });
                getLocation.execute();
            } else callback.OnCompleted(magnet);
        } else if (file.equals("play")) callback.OnCompleted(torrent);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (file.equals("magnet"))
            getMagnet(Getdata(url));
        else getTorrent(Getdata(url));
        return null;
    }

    private void getMagnet(Document data) {
        if (data != null) {
            magnet = data.select("a[href^='magnet']").attr("href");
        }
    }

    private void getTorrent(Document data) {
        if (data != null) {
            torrent = data.select("a[href^='/engine/download.php']").attr("href");
            torrent = torrent.startsWith("http://") ? torrent : Statics.FREERUTOR_URL + torrent;
        }
    }

    private Document Getdata(String url) {
        try {
            Document htmlDoc = Jsoup.connect(url).followRedirects(false).referrer(Statics.FREERUTOR_URL)
                    .validateTLSCertificates(false)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .ignoreContentType(true)
                    .get();
            Log.d(TAG, "GetUrl: FreeRutorMe " + url);
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "GetUrl: FreeRutorMe error " + url);
            e.printStackTrace();
            return null;
        }
    }
}