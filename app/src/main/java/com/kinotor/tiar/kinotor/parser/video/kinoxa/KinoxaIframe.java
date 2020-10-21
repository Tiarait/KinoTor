package com.kinotor.tiar.kinotor.parser.video.kinoxa;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.ui.DetailActivity;
import com.kinotor.tiar.kinotor.utils.OnTaskUrlCallback;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class KinoxaIframe extends AsyncTask<Void, Void, Void> {
    private String url;
    private ItemVideo items;
    private ItemHtml item;
    private OnTaskVideoCallback callback;
    private OnTaskUrlCallback callbackUrl;
    private String[] quality_arr, url_arr;

    public KinoxaIframe(ItemHtml item, OnTaskVideoCallback callback) {
        this.item = item;
        this.callback = callback;

        if (DetailActivity.url.contains(Statics.KINOXA_URL))
            this.url = item.getIframe(0);
        else this.url = Statics.KINOXA_URL;
        this.items = new ItemVideo();
    }

    public KinoxaIframe(String url, OnTaskUrlCallback callback) {
        this.url = url;
        this.callbackUrl = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (callbackUrl != null)
            callbackUrl.OnCompleted(quality_arr, url_arr);
        else callback.OnCompleted(items);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.d("kinoxa", "doInBackground: " + url);
        iframe(url);
        return null;
    }

    private void iframe(String data) {
        if (callbackUrl != null){
            if (data.contains("trailer-cdn")){
                final ArrayList<String> q = new ArrayList<>();
                final ArrayList<String> u = new ArrayList<>();

                q.add("(ссылка)");
                u.add(data);
                add(q, u);
            } else if (data.contains(Statics.KINOXA_URL)) {
                final ArrayList<String> q = new ArrayList<>();
                final ArrayList<String> u = new ArrayList<>();
//                Log.e("test", "iframe: 0");
                Document doc = getData(data);
                if (doc != null) {
                    if (doc.html().contains("var player = new Playerjs(")){
                        iframe(doc.html().split("var player = new Playerjs\\(")[1].split("\\);")[0]);
                    } else {
                        Log.e("test", "iframe: null");
                        q.add("Видео недоступно");
                        u.add("error");
                        add(q, u);
                    }
                } else {
                    Log.e("test", "iframe: null 2");
                    q.add("Видео недоступно");
                    u.add("error");
                    add(q, u);
                }
            } else {
                final ArrayList<String> q = new ArrayList<>();
                final ArrayList<String> u = new ArrayList<>();
                if (data.contains("qualities\":\"") && data.contains("file\":\"")) {
                    String qualits = data.split("qualities\":\"")[1].split("\"")[0]
                            .replace("p", "").trim();
//                    Log.e("test", "iframe:"+qualits);
                    String file = data.split("file\":\"")[1].split("\"")[0].trim();
                    String newFile = getLocation(file.replace("hls.m3u8", "360.mp4:hls:manifest.mp4"));

//                    Log.e("test", "file: " + file);
//                    Log.e("test", "newfile: " + newFile);
                    if (qualits.contains("1080")) {
                        String curQ = "1080";
                        q.add(curQ + " (mp4)");
                        u.add(newFile.replace("360.mp4", curQ + ".mp4"));
                    }
                    if (qualits.contains("720")) {
                        String curQ = "720";
                        q.add(curQ + " (mp4)");
                        u.add(newFile.replace("360.mp4", curQ + ".mp4"));
                    }
                    if (qualits.contains("480")) {
                        String curQ = "480";
                        q.add(curQ + " (mp4)");
                        u.add(newFile.replace("360.mp4", curQ + ".mp4"));
                    }
                    if (qualits.contains("360")) {
                        String curQ = "360";
                        q.add(curQ + " (mp4)");
                        u.add(newFile.replace("360.mp4", curQ + ".mp4"));
                    }
                } else {
                    Log.e("test", "iframe: null 3:" + data);
                    q.add("Видео недоступно");
                    u.add("error");
                }
                if (!q.isEmpty())
                    add(q, u);
            }
        } else if (data.contains("http")) {
            Document doc = getData(Statics.KINOXA_URL);
            if (doc != null) {
                if (doc.html().contains("short")){
                    Elements allEntries = doc.select(".short");
                    for (Element entry : allEntries) {
                        String t = "Неизвестный";
                        String year = "error";
                        String name = "error";
                        String u = "error";
                        String trailer = "error";
                        String q = "";
                        Elements allLines = entry.select(".short-info");
                        for (Element line : allLines) {
                            if (line.text().contains("Год:"))
                                year = " " + line.text().replace("Год:", "").trim();
                            if (line.text().contains("Перевод:"))
                                t = line.text().replace("Перевод:", "").trim();
                        }

//                        Log.e(TAG, "iframe: "+entry.html());
                        if (entry.html().contains("short-top-left fx-1")){
                            name = entry.select(".short-top-left.fx-1 a").text().trim();
                            u = entry.select(".short-top-left.fx-1 a").attr("href").trim();
//                            Log.e(TAG, "iframe: "+entry.select(".short-title").html());
                        }
                        if (name.contains("("))
                            name = name.split("\\(")[0].trim();
                        if (year.contains("error"))
                            year = "";
                        if (entry.html().contains("m-qual")){
                            q = " (" + entry.select(".m-qual").text().trim() + ")";
                        }
                        if (t.equals("-"))
                            t = "error";

                        if (u.contains(Statics.KINOXA_URL + "/") && u.contains("-"))
                            trailer = Statics.KINOXA_URL + "/trailer-cdn/" +
                                    u.split(Statics.KINOXA_URL + "/")[1].split("-")[0] + "/";

//                        Log.e("test", "iframe: "+trailer +"|"+u);
                        String s = item.getTitle(0).contains("(") ?
                                item.getTitle(0).split("\\(")[0].trim() :
                                item.getTitle(0).trim();
                        if (s.contains("[")) s = s.split("\\[")[0].trim();
                        if (!name.contains("error") && name.toLowerCase().equals(s.trim().toLowerCase())
                                && item.getType(0).contains("movie")) {
                            String tt = "";
                            if (!trailer.contains("error"))
                                tt = " [+trailer]";
                            items.setTitle("catalog site");
                            items.setType(name + year + q + tt + "\nkinoxa");
                            items.setToken(u);
                            items.setId_trans("null");
                            items.setId("site");
                            items.setUrl(u);
//                            items.setUrlSite(u);
                            items.setUrlTrailer(trailer);
                            items.setSeason("error");
                            items.setEpisode("error");
                            items.setTranslator(t.trim());
                        }
                    }
                }
            }
        } else if (item.getType(0).contains("movie") && !data.equals("error")){
            String trailer = "error";
            if (data.contains("[trailer]")) {
                trailer = data.split("\\[trailer\\]")[1].trim();
                data = data.split("\\[trailer\\]")[0].trim();
            }
//            Log.d(TAG, "iframe data: " + data);
            items.setTitle("catalog site");
            if (trailer.equals("error"))
                items.setType("kinoxa");
            else items.setType("[+trailer] \nkinoxa");
            items.setToken(data);
            items.setId_trans("null");
            items.setId("site");
            items.setUrl(data);
            items.setUrlTrailer(trailer);
//            items.setUrlSite(data);
            items.setSeason("error");
            items.setEpisode("error");
            items.setTranslator(item.getVoice(0).contains("error") ?
                    item.getTitle(0).trim() : item.getVoice(0).trim());
        }
    }

    private void add(ArrayList<String> q,  ArrayList<String> u) {
        quality_arr = q.toArray(new String[q.size()]);
        url_arr = u.toArray(new String[u.size()]);
    }

    private Document getData(String url) {
        try {
            if (url.equals(Statics.KINOXA_URL)) {
                String s;
                if (item.getSubTitle(0).contains("error")) {
                    s = item.getTitle(0).contains("(") ?
                            item.getTitle(0).split("\\(")[0].trim() :
                            item.getTitle(0).trim();
                    if (s.contains("[")) s = s.split("\\[")[0].trim();
                } else {
                    s = item.getSubTitle(0).trim();
                }

                return Jsoup.connect(Statics.KINOXA_URL)
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .data("story", s)
                        .data("do", "search")
                        .data("subaction", "search")
                        .ignoreContentType(true).post();
            } else {
                return Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .ignoreContentType(true).get();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getLocation(String url) {
        try {
            if (Statics.ProxyUse.contains("kinoxa") && Statics.ProxyCur.contains(":") && !Statics.ProxyCur.contains("адрес:порт")){
                System.setProperty("http.proxyHost", Statics.ProxyCur.split(":")[0].trim());
                System.setProperty("http.proxyPort", Statics.ProxyCur.split(":")[1].trim());
            } else {
                System.clearProperty("http.proxyHost");
                System.clearProperty("http.proxyPort");
            }
            Connection.Response response = Jsoup
                    .connect(url)
                    .method(Connection.Method.GET)
                    .referrer(DetailActivity.url)
                    .header("host","cdn2.kinogid.me")
                    .ignoreContentType(true)
                    .followRedirects(false)
                    .execute();
            return response.header("Location");
        } catch (IOException e) {
            e.printStackTrace();
            return url;
        }
    }
}
