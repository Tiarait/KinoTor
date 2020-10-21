package com.kinotor.tiar.kinotor.parser.video.moonwalk;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskUrlCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class MoonwalkUrl extends AsyncTask<Void, Void, Void> {
    private String url;
    private String[] quality_arr, url_arr;
    private String wokytest = "http://wonky.lostcut.net/moontest.php?url=";
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
        if (Statics.MOONWALK_URL.contains("movies.qwedl.com")) {
            getQwedlUrl(GetDataQwedl(checkUrl(url)));
        } else getHtml(GetData(url, "mp4"));
        return null;
    }

    private void getQwedlUrl(Document doc) {
        if (doc != null) {
            final ArrayList<String> q = new ArrayList<>();
            final ArrayList<String> u = new ArrayList<>();
            if (doc.text().contains("\"mp4\":")){
                String all = doc.text().replace(" ", "").trim();
                all = all.split("\"mp4\":\\{")[1].split("\\}")[0].trim();
                if (all.contains(",")) {
                    String[] path = all.split(",");
                    for (String aPath : path) {
                        if (aPath.contains(":")) {
                            q.add(aPath.split(":")[0].replace("\"","") + " (mp4)");
                            u.add(aPath.split("\":\"")[1].replace("\\","")
                                    .replace("\"",""));
                        }
                    }
                } else if (all.contains(":")) {
                    q.add(all.split(":")[0].replace("\"","") + " (mp4)");
                    u.add(all.split("\":\"")[1].replace("\\","")
                            .replace("\"",""));
                }
            }
            if (doc.text().contains("\"m3u8\":")){
                String all = doc.text().replace(" ", "").trim();
                all = all.split("\"m3u8\":\\{")[1].split("\\}")[0].trim();
                if (all.contains("auto\":\"")) {
                    String auto = all.split("auto\":\"")[1].split("\"")[0].replace("\\","");
                    q.add("auto (m3u8)");
                    u.add(auto);
//                    String other = getHtml(auto);
//                    if (other.contains("RESOLUTION=")) {
//                        for (String path : other.split("RESOLUTION=")) {
//                            if (path.contains(":")) {
//                                q.add(aPath.split(":")[0].replace("\"","") + " (mp4)");
//                                u.add(aPath.split("\":\"")[1].replace("\\","")
//                                        .replace("\"",""));
//                            }
//                        }
//                    }
                }
            }

            if (q.isEmpty()) {
                q.add("упс (ссылка)");
                u.add(wokytest + url.trim());
            }
            add(q, u);
        }
    }

    private void getHtml(Document doc) {
        if (doc != null) {
            final ArrayList<String> q = new ArrayList<>();
            final ArrayList<String> u = new ArrayList<>();
            Log.e(TAG, "getHtml: "+doc.html() );
            if (doc.body().text().contains("No VideoBalancer") || doc.body().text().contains("ot found")) {
                Log.d(TAG, "ParseMoonwalkIframe: видео недоступно");
                if (!m3u8) {
                    m3u8 = true;
                    getHtml(GetData(url, "m3u8"));
                } else {
                    q.add("упс (ссылка)");
                    u.add(wokytest + url.trim());
                    add(q, u);
                }
            } else {
                if (!doc.text().startsWith("{\"") && !doc.text().contains("#EXT-X-STREAM-INF") && !m3u8){
                    m3u8 = true;
                    getHtml(GetData(url, "m3u8"));
                } else if (doc.text().startsWith("{\"")){
                    String all = doc.text().replace("{", "")
                            .replace("}", "")
                            .replace("\"", "")
                            .replace("http:", "").trim();
                    if (all.contains(",")) {
                        String[] path = all.split(",");
                        for (String aPath : path) {
                            if (aPath.contains(":")) {
                                q.add(aPath.split(":")[0] + " (mp4)");
                                u.add("http:" + aPath.split(":")[1]);
                            }
                        }
                    } else if (all.contains(":")) {
                        q.add(all.split(":")[0] + " (mp4)");
                        u.add("http:" + all.split(":")[1]);
                    } else {
                        q.add("упс (ссылка)");
                        u.add(wokytest + url.trim());
                        add(q, u);
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
                } else if (doc.html().contains("<hr>") && doc.html().contains("http")){
                    Log.e(TAG, "moonwalk: 11");
                    if (doc.html().contains("<br>1080")) {
                        q.add("1080 (mp4)");
                        u.add(doc.html().split("<br>1080")[1].split("\\.mp4")[0]
                                .replace("<br>", "").trim() + ".mp4");
                    } else if (doc.html().contains("x1080</b>")) {
                        q.add("1080 (m3u8)");
                        u.add(doc.html().split("x1080</b>")[1].split("\\.m3u8")[0]
                                .replace("<br>", "").trim() + ".m3u8");
                    } else if (doc.html().contains("1920x")) {
                        q.add("1080 (m3u8)");
                        u.add(doc.html().split("1920x")[1].split("\\.m3u8")[0]
                                .replace("<br>", "").split("</b>")[1].trim() + ".m3u8");
                    }

                    if (doc.html().contains("<br>720")) {
                        q.add("720 (mp4)");
                        u.add(doc.html().split("<br>720")[1].split("\\.mp4")[0]
                                .replace("<br>", "").trim() + ".mp4");
                    } else if (doc.html().contains("x720</b>")) {
                        q.add("720 (m3u8)");
                        u.add(doc.html().split("x720</b>")[1].split("\\.m3u8")[0]
                                .replace("<br>", "").trim() + ".m3u8");
                    } else if (doc.html().contains("1280x")) {
                        q.add("720 (m3u8)");
                        u.add(doc.html().split("1280x")[1].split("\\.m3u8")[0]
                                .replace("<br>", "").split("</b>")[1].trim() + ".m3u8");
                    }

                    if (doc.html().contains("<br>480")) {
                        q.add("480 (mp4)");
                        u.add(doc.html().split("<br>480")[1].split("\\.mp4")[0]
                                .replace("<br>", "") + ".mp4");
                    } else if (doc.html().contains("x480</b>")) {
                        q.add("480 (m3u8)");
                        u.add(doc.html().split("x480</b>")[1].split("\\.m3u8")[0]
                                .replace("<br>", "").trim() + ".m3u8");
                    }

                    if (doc.html().contains("<br>360")) {
                        q.add("360 (mp4)");
                        u.add(doc.html().split("<br>360")[1].split("\\.mp4")[0]
                                .replace("<br>", "").trim() + ".mp4");
                    } else if (doc.html().contains("x360</b>")) {
                        q.add("360 (m3u8)");
                        u.add(doc.html().split("x360</b>")[1].split("\\.m3u8")[0]
                                .replace("<br>", "").trim() + ".m3u8");
                    } else if (doc.html().contains("640x")) {
                        q.add("360 (m3u8)");
                        u.add(doc.html().split("640x")[1].split("\\.m3u8")[0]
                                .replace("<br>", "").split("</b>")[1].trim() + ".m3u8");
                    }
                    Log.d(TAG, "getHtml: "+u.toString());
                    add(q, u);
                } else {
                    q.add("упс (ссылка)");
                    u.add(wokytest + url.trim());
                    add(q, u);
                }
            }
        }
    }

//    private String getQulity(String q) {
//        if (q.contains("360.mp4") || q.contains("tracks-4,5") || q.contains("_360_")) {
//            return "360";
//        } else if (q.contains("480.mp4") || q.contains("tracks-3,5") || q.contains("_400_")) {
//            return "480 ";
//        } else if (q.contains("720.mp4") || q.contains("tracks-2,5") || q.contains("_720_")) {
//            return "720";
//        } else if (q.contains("1080.mp4") || q.contains("tracks-1,5") || q.contains("_1080_")) {
//            return "1080";
//        } else return "... (mp4)";
//    }

    private void add(ArrayList<String> q,  ArrayList<String> u) {
        quality_arr = q.toArray(new String[q.size()]);
        url_arr = u.toArray(new String[u.size()]);
    }

    private Document GetDataQwedl(String url){
        try {
            Document htmlDoc = Jsoup.connect(Statics.MOONWALK_URL + url + "%2F0")
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36 OPR/60.0.3255.170")
                    .header("Upgrade-Insecure-Requests", "1")
                    .ignoreContentType(true).get();
            Log.d(TAG, "GetdataMoonwalkIframe: connected to " + Statics.MOONWALK_URL + url + "%2F0");
            Log.d(TAG, "GetdataMoonwalkIframe:" + htmlDoc.html());
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "GetdataMoonwalkIframe: connected false to " + url);
            e.printStackTrace();
            return null;
        }
    }

    private String getHtml(String url){
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36 OPR/60.0.3255.170")
                    .ignoreContentType(true).get().html();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private Document GetData(String url, String type){
        try {
            //http://wonky.lostcut.net/moonwalk.php?url=
            //http://smartportaltv.ru/20/4.php?url=
            //https://movies.qwedl.com/api/movies.php?method=files&q=
            Document htmlDoc = Jsoup.connect(Statics.MOONWALK_URL + checkUrl(url) + "&type=" + type)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .referrer("http://moonwalk.cc")
                    .ignoreContentType(true).get();
            Log.d(TAG, "GetdataMoonwalkIframe:" + htmlDoc.html());
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "GetdataMoonwalkIframe: connected false to " + checkUrl(url));
            e.printStackTrace();
            return null;
        }
    }

    private String checkUrl(String url) {
        url = url.replaceAll("\"", "");
        if (!url.contains("http://") && !url.contains("https://")) url = url.contains("//")?"http:" + url:"http://" + url;
        try {
            return URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return url.replace(":", "%3A").replace(":", "%2F")
                    .replace("?", "%3F").replace("=", "%3D")
                    .replace("&", "%26");
        }
    }
}