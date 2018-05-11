package com.kinotor.tiar.kinotor.parser.hdgo;

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
                if (iframe.attr("src").contains("/1/")) {
                    q.add("360 (mp4)");
                    u.add(iframe.attr("src") + "[hdgo]");
                } else if (iframe.attr("src").contains("/2/")) {
                    q.add("480 (mp4)");
                    u.add(iframe.attr("src") + "[hdgo]");
                } else if (iframe.attr("src").contains("/3/")) {
                    q.add("720 (mp4)");
                    u.add(iframe.attr("src") + "[hdgo]");
                } else if (iframe.attr("src").contains("/4/")) {
                    q.add("1080 (mp4)");
                    u.add(iframe.attr("src") + "[hdgo]");
                }
            }
        } else if (doc.body().html().contains("media: [")) {
            String video_arr = doc.body().html().split("media: \\[\\{")[1]
                    .split("\\}]")[0];
            if (video_arr.contains("\\},\\{")) {
                String[] video_url = video_arr.split("\\},\\{");
                for (int i = 0; i < video_url.length; i++) {
                    video_url[i] = video_url[i].replace("url: '", "")
                            .replace("', type: 'video/mp4'", "")
                            .replace("'", "");
                    if (video_url[i].contains("/1/")) {
                        q.add("360 (mp4)");
                        u.add(video_url[i] + "[hdgo]");
                    } else if (video_url[i].contains("/2/")) {
                        q.add("480 (mp4)");
                        u.add(video_url[i] + "[hdgo]");
                    } else if (video_url[i].contains("/3/")) {
                        q.add("720 (mp4)");
                        u.add(video_url[i] + "[hdgo]");
                    } else if (video_url[i].contains("/4/")) {
                        q.add("1080 (mp4)");
                        u.add(video_url[i] + "[hdgo]");
                    }
                }
            } else {
                String[] video_url = video_arr.split("'");
                for (String aVideo_url : video_url) {
                    if (aVideo_url.contains("http://")) {
                        if (aVideo_url.contains("/1/")) {
                            q.add("360 (mp4)");
                            u.add(aVideo_url + "[hdgo]");
                        } else if (aVideo_url.contains("/2/")) {
                            q.add("480 (mp4)");
                            u.add(aVideo_url  + "[hdgo]");
                        } else if (aVideo_url.contains("/3/")) {
                            q.add("720 (mp4)");
                            u.add(aVideo_url + "[hdgo]");
                        } else if (aVideo_url.contains("/4/")) {
                            q.add("1080 (mp4)");
                            u.add(aVideo_url + "[hdgo]");
                        }
                    }
                }
            }
        } else {
            Log.d(TAG, "ParseHdgoMp4: видео недоступно");
            q.add("видео недоступно");
            u.add("error");
        }
        quality_arr = q.toArray(new String[q.size()]);
        url_arr = u.toArray(new String[u.size()]);
    }

    private Document GetData(String url){
        try {
            Document htmlDoc = Jsoup.connect(checkUrl(url))
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .referrer("http://hdgo.cc")
                    .timeout(5000).ignoreContentType(true).get();
            Log.d(TAG, "GetdataHdgoUrl: connected to " + checkUrl(url));
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "GetdataHdgoUrl: connected false to " + checkUrl(url));
            e.printStackTrace();
            return null;
        }
    }

//    private String getLocation (String url) {
//        try {
//            Document htmlDoc = Jsoup.connect(checkUrl(url))
//                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
//                    .referrer("http://hdgo.cc")
//                    .timeout(5000).ignoreContentType(true).get();
//            Log.d(TAG, "GetHdgoLocation: connected to " + checkUrl(url));
//            return htmlDoc.location();
//        } catch (Exception e) {
//            Log.d(TAG, "GetHdgoLocation: connected false to " + checkUrl(url));
//            e.printStackTrace();
//            return null;
//        }
//    }

    private String checkUrl(String url) {
        url = url.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", "");
        url = url.replaceAll("\"", "");
        if (!url.contains("http://")) url = url.contains("//") ? "http:" + url : "http://" + url;
        return url;
    }
}