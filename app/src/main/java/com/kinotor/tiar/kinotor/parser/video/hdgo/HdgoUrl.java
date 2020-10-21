package com.kinotor.tiar.kinotor.parser.video.hdgo;

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

public class HdgoUrl extends AsyncTask<Void, Void, Void> {
    private String url;
    private String[] quality_arr, url_arr;
    private OnTaskUrlCallback callback;

    public HdgoUrl (String url, OnTaskUrlCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(quality_arr, url_arr);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        getSeries(GetData(url));
        Log.e(TAG, "HdgoUrl: parse");
        return null;
    }

    private void getSeries(Document doc) {
        if (doc != null) {
            if (doc.body().html().contains("<iframe ") && !doc.html().contains("embed: '<iframe src")) {
                String s_iframe = doc.select("iframe").first().attr("src");
                ParseHdgoMp42(GetData(s_iframe));
            } else {
                ParseHdgoMp42(GetData(url));
            }
        }
    }

    private void ParseHdgoMp42(Document doc) {
        final ArrayList<String> q = new ArrayList<>();
        final ArrayList<String> u = new ArrayList<>();
        if (doc == null) {
            Log.d(TAG, "ParseHdgoMp4: некорректная ссылка");
            q.add("видео недоступно");
            u.add("error");
        } else if (doc.body().html().contains("<video ")) {
            //film
            Elements video = doc.select("video source");
            for (Element iframe : video) {
                if (iframe.attr("src").contains("/4/")) {
                    q.add("1080 (mp4)");
                    u.add(iframe.attr("src") + "[hdgo]");
                } else if (iframe.attr("src").contains("/3/")) {
                    q.add("720 (mp4)");
                    u.add(iframe.attr("src") + "[hdgo]");
                } else if (iframe.attr("src").contains("/2/")) {
                    q.add("480 (mp4)");
                    u.add(iframe.attr("src") + "[hdgo]");
                } else if (iframe.attr("src").contains("/1/")) {
                    q.add("360 (mp4)");
                    u.add(iframe.attr("src") + "[hdgo]");
                }
            }
            Log.d(TAG, "ParseHdgoMp4 0: "+q.toString());
            if (q.isEmpty()){
                Log.d(TAG, "ParseHdgoMp4: "+doc.html());
                q.add("видео недоступно");
                u.add("error");
            }
        } else if (doc.body().html().contains("media: [") || doc.body().html().contains("\"media\": [")) {
            String video_arr = doc.body().html().replace("\"","").split("media: \\[\\{")[1]
                    .split("\\}]")[0];
            if (video_arr.contains("},{")) {
                String[] video_url = video_arr.split("\\},\\{");
                for (int i = 0; i < video_url.length; i++) {
                    video_url[i] = video_url[i].replace("url: '", "")
                            .replace("', type: 'video/mp4'", "")
                            .replace("'", "");
                    if (video_url[i].contains("/4/")) {
                        q.add("1080 (mp4)");
                        u.add(video_url[i] + "[hdgo]");
                    } else if (video_url[i].contains("/3/")) {
                        q.add("720 (mp4)");
                        u.add(video_url[i] + "[hdgo]");
                    } else if (video_url[i].contains("/2/")) {
                        q.add("480 (mp4)");
                        u.add(video_url[i] + "[hdgo]");
                    } else if (video_url[i].contains("/1/")) {
                        q.add("360 (mp4)");
                        u.add(video_url[i] + "[hdgo]");
                    }
                }

                Log.d(TAG, "ParseHdgoMp4 1: "+q.toString());
                if (q.isEmpty()){
                    Log.d(TAG, "ParseHdgoMp4: "+doc.html());
                    q.add("видео недоступно");
                    u.add("error");
                }
            } else {
                String[] video_url = video_arr.split("'");
                for (String aVideo_url : video_url) {
                    if (aVideo_url.contains("//")) {
                        if (aVideo_url.contains("/4/")) {
                            q.add("1080 (mp4)");
                            u.add(aVideo_url + "[hdgo]");
                        } else if (aVideo_url.contains("/3/")) {
                            q.add("720 (mp4)");
                            u.add(aVideo_url + "[hdgo]");
                        } else if (aVideo_url.contains("/2/")) {
                            q.add("480 (mp4)");
                            u.add(aVideo_url  + "[hdgo]");
                        } else if (aVideo_url.contains("/1/")) {
                            q.add("360 (mp4)");
                            u.add(aVideo_url + "[hdgo]");
                        }
                    }
                }

                Log.d(TAG, "ParseHdgoMp4 2: "+q.toString());
                if (q.isEmpty()){
                    Log.e(TAG, "ParseHdgoMp4: "+doc.html());
                    q.add("видео недоступно");
                    u.add("error");
                }
            }
        } else {
            Log.d(TAG, "ParseHdgoMp4: видео недоступно" + doc.body().html());
            q.add("видео недоступно");
            u.add("error");
        }
        quality_arr = q.toArray(new String[q.size()]);
        url_arr = u.toArray(new String[u.size()]);
    }

    private Document GetData(String url){
        try {
            Log.d(TAG, "ParseHdgo: " + url.trim());
            String ref = "http://hdgo.cc";
            if (url.contains("vio.to/video/playlist") && url.contains("&e=")) {
                url = "https://vio.to/api/videoget.json?token=test&id=" + url.split("&e=")[1].trim();
                ref = "https://vio.to/";
            }
            Log.d(TAG, "ParseHdgo2: " + url.trim());
            return Jsoup.connect(checkUrl(url.trim()))
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .referrer(ref)
                    .ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String checkUrl(String url) {
        url = url.replace(" ", "").replace("\n", "").replaceAll("\r", "");
        url = url.replace("\"", "");
        if (!url.contains("http://") && !url.contains("https://")) url = url.contains("//") ? "http:" + url : "http://" + url;
        return url;
    }
}