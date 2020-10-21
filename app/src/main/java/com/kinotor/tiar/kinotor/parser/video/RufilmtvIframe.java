package com.kinotor.tiar.kinotor.parser.video;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskUrlCallback;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class RufilmtvIframe extends AsyncTask<Void, Void, Void> {
    private String url;
    private ItemVideo items;
    private ItemHtml item;
    private OnTaskVideoCallback callback;
    private OnTaskUrlCallback callbackUrl;
    private String[] quality_arr, url_arr;

    public RufilmtvIframe(ItemHtml item, OnTaskVideoCallback callback) {
        this.item = item;
        this.callback = callback;

        this.url = item.getIframe(0);
        this.items = new ItemVideo();
    }

    public RufilmtvIframe(String url, OnTaskUrlCallback callback) {
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
        Log.d(TAG, "doInBackground: " + url);
        iframe(url);
        return null;
    }

    private void iframe(String data) {
        if (callbackUrl != null){
            final ArrayList<String> q = new ArrayList<>();
            final ArrayList<String> u = new ArrayList<>();
            if (data.contains("allmovie") && data.contains("video/embed/")){
                String url = data.replace("video/embed/", "video/show_player/") +
                        "?autopay=1&skip_ads=0";
                Document video = getData(url, data);
                if (video != null) {
                    if (video.html().contains("source src=")){
                        q.add("... (mp4)");
                        u.add(video.html().split("source src=\"")[1].split("\"")[0]);
                    } else {
                        q.add("Видео недоступно");
                        u.add("error");
                    }
                } else {
                    q.add("Видео недоступно");
                    u.add("error");
                }
            } else if (data.contains("youtube")){
                q.add("Youtube (ссылка)");
                u.add(data);
            } else if (data.contains("rutube")){
                q.add("Rutube (ссылка)");
                u.add(data);
            } else if (data.contains("out.pladform")){
                q.add("Pladform (ссылка)");
                u.add(data);
            } else {
                q.add("Видео недоступно");
                u.add(data);
            }
            if (!q.isEmpty())
                add(q, u);
        } else {
            if (data.contains("||")){
                String[] itms = data.split("\\|\\|");
                for (int i = 0; i < itms.length; i++){
                    add("ч." + (i+1), itms[i]);
                }
            } else add("", data);
        }
    }

    private void add(String n, String u) {
        Log.d(TAG, "iframe: " + item.getUrl(0));
        items.setTitle("catalog site");
        if (!url.contains("allmovie"))
            items.setType("rufilmtv [анонс] " + n);
        else items.setType("rufilmtv " + n);
        items.setToken(u);
        items.setId_trans("null");
        items.setId("site");
        items.setUrl(u);
//        items.setUrlSite(u);
        items.setUrlTrailer("error");
        items.setSeason("error");
        items.setEpisode("error");
        items.setTranslator(item.getVoice(0).contains("error") ?
                item.getTitle(0).trim() : item.getVoice(0).trim());
    }

    private Document getData(String url, String ref) {
        try {
            if (Statics.ProxyUse.contains("rufilmtv") && Statics.ProxyCur.contains(":") && !Statics.ProxyCur.contains("адрес:порт")){
                System.setProperty("http.proxyHost", Statics.ProxyCur.split(":")[0].trim());
                System.setProperty("http.proxyPort", Statics.ProxyCur.split(":")[1].trim());
            } else {
                System.clearProperty("http.proxyHost");
                System.clearProperty("http.proxyPort");
            }
            return Jsoup.connect(url)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("ExternalPlay", "true")
                    .header("Referer", ref)
//                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false)
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void add(ArrayList<String> q,  ArrayList<String> u) {
        quality_arr = q.toArray(new String[q.size()]);
        url_arr = u.toArray(new String[u.size()]);
    }
}
