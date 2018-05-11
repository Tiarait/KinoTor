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
        getSeries(GetData(url));
        return null;
    }

    private void getSeries(Document doc) {
        if (doc != null) {
            final ArrayList<String> q = new ArrayList<>();
            final ArrayList<String> u = new ArrayList<>();
            if (doc.body().text().contains("недоступен")) {
                Log.d(TAG, "ParseMoonwalkMp4: видео недоступно");
                q.add("видео недоступно");
                u.add("error");
            }else {
//                //token
//                String token = doc.select("title").first().text().split(" ")[1];
//                Log.d("mydebug", "token :" + token);
//                //mw_pid
//                String mw_pid = doc.html().split("partner_id: ")[1].split(",")[0];
//                Log.d("post manifests", "mw_pid :" + mw_pid);
//                //p_domain_id
//                String p_domain_id = doc.html().split("domain_id: ")[1].split(",")[0];
//                Log.d("post manifests", "p_domain_id :" + p_domain_id);
//
//                //parse js
//                Document js_data = GetData("http://moonwalk.cc" + doc.html().split("script src=\"")[1].split("\"")[0]);
//                //mw_key
//                String mw_key = "error";
//                if (js_data.html().contains("mw_key:\""))
//                    mw_key = js_data.html().split("mw_key:\"")[1].split("\"")[0];
//                Log.d("post manifests", "mw_key :" + mw_key);
//                //iframe_version
//                String iframe_version = js_data.html().split("iframe_version:\"")[1].split("\"")[0];
//                Log.d("post manifests", "iframe_version :" + iframe_version);
//                //x params
//                String x1 = js_data.html().split("iframe_version:\"")[1].split("\",")[1].split(":")[0];
//                String x2 = doc.html().split("window\\[")[1].split("= '")[1].split("';")[0];
//                Log.d("post manifests", x1 + ":" + x2);
//
//                //post on http://moonwalk.cc/manifests/video/[token]/all
//                Document manifests = PostData("http://moonwalk.cc/manifests/video/"+token+"/all",
//                        mw_key, mw_pid, p_domain_id, x1, x2, iframe_version);
//                if (manifests.text().contains("manifest_mp4") && !manifests.text().contains("manifest_mp4\":null")){
//                    String mp4_url = manifests.text().split("manifest_mp4\":\"")[1].split("\"")[0].replace("\\u0026", "&");
//                    Document video = GetData(mp4_url);
//                    if (video.text().contains("360")) {
//                        q.add("360 (mp4)");
//                        u.add(video.text().split("360\":\"")[1].split("\"")[0]);
//                    } if (video.text().contains("480")) {
//                        q.add("480 (mp4)");
//                        u.add(video.text().split("480\":\"")[1].split("\"")[0]);
//                    } if (video.text().contains("720")) {
//                        q.add("720 (mp4)");
//                        u.add(video.text().split("720\":\"")[1].split("\"")[0]);
//                    } if (video.text().contains("1080")) {
//                        q.add("1080 (mp4)");
//                        u.add(video.text().split("1080\":\"")[1].split("\"")[0]);
//                    }
//                }
//                if (manifests.text().contains("manifest_m3u8") && !manifests.text().contains("manifest_m3u8\":null")) {
//                    String m3u8_url = manifests.text().split("manifest_m3u8\":\"")[1].split("\"")[0].replace("\\u0026", "&");
//                    q.add("Авто (m3u8)");
//                    u.add(m3u8_url);
//                }
//                if ((!manifests.text().contains("manifest_m3u8") && !manifests.text().contains("manifest_mp4")) ||
//                        (manifests.text().contains("manifest_m3u8\":null") && manifests.text().contains("manifest_mp4\":null"))){
//                    q.add("видео недоступно");
//                    u.add("error");
//                }
                if (doc.text().contains("#EXT-X-STREAM-INF")){
                    String all = doc.text().replace("#EXTM3U", "").trim();
                    String[] path = all.split("#EXT-X-STREAM-INF");
                    for (String aPath : path) {
                        if (aPath.contains("RESOLUTION=") && aPath.contains(","))
                            q.add(aPath.split("RESOLUTION=")[1].split(",")[0] + " (m3u8)");
                        if (aPath.contains("http:") && aPath.contains(".m3u8"))
                            u.add("http:" + aPath.split("http:")[1].split(".m3u8")[0] + ".m3u8");
                    }
                } else {
                    q.add("error");
                    u.add("error");
                }
                if (!doc.text().contains("No VideoBalancer info")) {
                    q.add("auto (m3u8)");
                    u.add("http://smartportaltv.ru/20/4.php?url=" + checkUrl(url));
                }
                quality_arr = q.toArray(new String[q.size()]);
                url_arr = u.toArray(new String[u.size()]);
            }
        }
    }

    private Document GetData(String url){
        try {
            Document htmlDoc = Jsoup.connect("http://smartportaltv.ru/20/4.php?url=" + checkUrl(url))
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .referrer("http://moonwalk.cc")
                    .timeout(5000).ignoreContentType(true).get();
//            Log.d(TAG, "GetdataMoonwalkIframe: connected to " + checkUrl(url));
            Log.d(TAG, "smartportaltv.ru: connected to " + checkUrl(url));
            return htmlDoc;
        } catch (Exception e) {
//            Log.d(TAG, "GetdataMoonwalkIframe: connected false to " + checkUrl(url));
            Log.d(TAG, "smartportaltv.ru: connected false to " + checkUrl(url));
            e.printStackTrace();
            return null;
        }
    }

    private Document PostData(String url, String mw_key, String mw_pid,
                              String p_domain_id, String x1, String x2, String iframe_version){
        try {
            Document htmlDoc = Jsoup.connect(checkUrl(url))
                    .data("mw_key", mw_key)
                    .data("mw_pid", mw_pid)
                    .data("p_domain_id", p_domain_id)
                    .data("ad_attr", "0")
                    .data("iframe_version", iframe_version)
                    .data(x1, x2)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("Accept-Encoding", "gzip, deflate")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36")
                    .referrer("http://moonwalk.cc/").timeout(5000).ignoreContentType(true).post();
            Log.d(TAG, "PostDataMoonwlk: post to " + checkUrl(url));
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "PostDataMoonwlk: post false to " + checkUrl(url));
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