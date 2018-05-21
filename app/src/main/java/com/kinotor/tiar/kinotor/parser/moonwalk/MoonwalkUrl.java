package com.kinotor.tiar.kinotor.parser.moonwalk;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.utils.OnTaskUrlCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class MoonwalkUrl extends AsyncTask<Void, Void, Void> {
    private String url;
    private String[] quality_arr, url_arr;
    private OnTaskUrlCallback callback;
    private boolean m3u8 = false;

    public MoonwalkUrl(String url, OnTaskUrlCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(quality_arr, url_arr);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        getHtml(GetData(url + "&type=mp4"));
        return null;
    }

    private void getHtml(Document doc) {
        if (doc != null) {
            final ArrayList<String> q = new ArrayList<>();
            final ArrayList<String> u = new ArrayList<>();
            if (doc.body().text().contains("No VideoBalancer") || doc.body().text().contains("ot found")) {
                Log.d(TAG, "ParseMoonwalkIframe: видео недоступно");
                if (!m3u8) {
                    m3u8 = true;
                    getHtml(GetData(url));
                } else {
                    q.add(doc.body().text());
                    u.add("error");
                    add(q, u);
                }
            }else {
                if (!doc.text().startsWith("{\"") && !doc.text().contains("#EXT-X-STREAM-INF") && !m3u8){
                    m3u8 = true;
                    getHtml(GetData(url));
                } else if (doc.text().startsWith("{\"")){
                    String all = doc.text().replace("{", "")
                            .replace("}", "")
                            .replace("\"", "")
                            .replace("http:", "").trim();
                    String[] path = all.split(",");
                    for (String aPath : path) {
                        if (aPath.contains(":")) {
                            q.add(aPath.split(":")[0] + " (mp4)");
                            u.add("http:" + aPath.split(":")[1]);
                        }
                    }
                    add(q, u);
                } else if (doc.text().contains("#EXT-X-STREAM-INF")){
                    String all = doc.text().replace("#EXTM3U", "").trim();
                    String[] path = all.split("#EXT-X-STREAM-INF");
                    for (String aPath : path) {
                        if (aPath.contains("RESOLUTION=") && aPath.contains(","))
                            q.add(aPath.split("RESOLUTION=")[1].split(",")[0] + " (m3u8)");
                        if (aPath.contains("http:") && aPath.contains(".m3u8"))
                            u.add("http:" + aPath.split("http:")[1].split(".m3u8")[0] + ".m3u8");
                    }
                    add(q, u);
                } else {
                    q.add("видео недоступно");
                    u.add("error");
                    add(q, u);
                }
            }
        }
    }

    private void add(ArrayList<String> q,  ArrayList<String> u) {
        quality_arr = q.toArray(new String[q.size()]);
        url_arr = u.toArray(new String[u.size()]);
    }

    private Document GetData(String url){
        try {
            Document htmlDoc = Jsoup.connect("http://smartportaltv.ru/20/4.php?url=" + checkUrl(url))
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .referrer("http://moonwalk.cc")
                    .timeout(5000).ignoreContentType(true).get();
            Log.d(TAG, "GetdataMoonwalkIframe: connected to " + checkUrl(url));
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "GetdataMoonwalkIframe: connected false to " + checkUrl(url));
            e.printStackTrace();
            return null;
        }
    }

    private String checkUrl(String url) {
        url = url.replaceAll("\"", "");
        if (!url.contains("http://")) url = url.contains("//")?"http:" + url:"http://" + url;
        return url;
    }
}