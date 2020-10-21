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

public class FanserialsIframe extends AsyncTask<Void, Void, Void> {
    private String url;
    private ItemVideo items;
    private ItemHtml item;
    private OnTaskVideoCallback callback;
    private OnTaskUrlCallback callbackUrl;
    private String[] quality_arr, url_arr;

    public FanserialsIframe(ItemHtml item, OnTaskVideoCallback callback) {
        this.item = item;
        this.callback = callback;

        this.url = item.getIframe(0);
        this.items = new ItemVideo();
    }

    public FanserialsIframe(String url, OnTaskUrlCallback callback) {
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
            Document d = getData(data);
            if (d != null) {
                String ds = d.body().html().replace("&quot;", "\"").replace("\\","");
                if (ds.contains("hls\":\"")) {
                    String subs = "";
                    if (ds.contains("data-ru_subtitle=\"")){
                        subs = ds.split("data-ru_subtitle=\"")[1].split("\"")[0].trim();
                    }
                    if (ds.contains("data-en_subtitle=\""))
                        subs += "," + ds.split("data-en_subtitle=\"")[1].split("\"")[0].trim();
                    if (!subs.isEmpty())
                        subs = "[subs]" + subs;

                    if (subs.startsWith(","))
                        subs = subs.substring(1);

                    String url = ds.split("hls\":\"")[1].split("\"")[0];
                    Document qq = getData(url);
                    if (qq != null) {
                        if (qq.html().contains("/1080/")){
                            q.add("1080 (m3u8)");
                            u.add(url.replace("index.m3u8", "1080/index.m3u8")+ subs);
                        }
                        if (qq.html().contains("/720/")){
                            q.add("720 (m3u8)");
                            u.add(url.replace("index.m3u8", "720/index.m3u8")+ subs);
                        }
                        if (qq.html().contains("/480/")){
                            q.add("480 (m3u8)");
                            u.add(url.replace("index.m3u8", "480/index.m3u8")+ subs);
                        }
                        if (qq.html().contains("/360/")){
                            q.add("360 (m3u8)");
                            u.add(url.replace("index.m3u8", "360/index.m3u8")+ subs);
                        }
                    } else if (url.contains("index.m3u8")){
                        q.add("720 (m3u8)");
                        u.add(url.replace("index.m3u8", "720/index.m3u8")+ subs);
                        q.add("480 (m3u8)");
                        u.add(url.replace("index.m3u8", "480/index.m3u8")+ subs);
                        q.add("360 (m3u8)");
                        u.add(url.replace("index.m3u8", "360/index.m3u8")+ subs);
                    } else {
                        Log.e(TAG, "iframe: wtf url " + qq.html() );
                        q.add("Iframe (ссылка)");
                        u.add(data);
                    }
                } else {
                    Log.e(TAG, "iframe: havnt hls \n wtf " + ds );
                    q.add("Iframe (ссылка)");
                    u.add(data);
                }
            } else {
                Log.e(TAG, "iframe: data null" );
                q.add("Iframe (ссылка)");
                u.add(data);
            }
            if (!q.isEmpty())
                add(q, u);
            else {
                q.add("Iframe (ссылка)");
                u.add(data);
                add(q, u);
            }
        } else {
            data = data.replace("\\", "");
            if (data.contains("},{")) {
                for (String n : data.split("\\},\\{")){
                    if (n.contains("name\":\"") && n.contains("player\":\"")) {
                        if (!n.contains("Альтернативный плеер")) {
                            items.setTitle("catalog site");
                            items.setType("fanserials");
                            items.setToken(data);
                            items.setId_trans("null");
                            items.setId("site");
                            items.setUrl(n.split("player\":\"")[1].split("\"")[0]);
//                            items.setUrlSite("error");
                            items.setUrlTrailer("error");
                            items.setSeason("error");
                            items.setEpisode("error");
                            items.setTranslator(n.split("name\":\"")[1].split("\"")[0]);
                        }
                    }
                }
            } else if (data.contains("name\":\"") && data.contains("player\":\"")) {
                if (!data.contains("Альтернативный плеер")) {
                    items.setTitle("catalog site");
                    items.setType("fanserials");
                    items.setToken(data);
                    items.setId_trans("null");
                    items.setId("site");
                    items.setUrl(data.split("player\":\"")[1].split("\"")[0]);
//                    items.setUrlSite("error");
                    items.setUrlTrailer("error");
                    items.setSeason("error");
                    items.setEpisode("error");
                    items.setTranslator(data.split("name\":\"")[1].split("\"")[0]);
                }
            }
            Log.d(TAG, "iframe: " + item.getUrl(0));
        }
    }

    private void add(ArrayList<String> q,  ArrayList<String> u) {
        quality_arr = q.toArray(new String[q.size()]);
        url_arr = u.toArray(new String[u.size()]);
    }

    private Document getData(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .referrer(Statics.FANSERIALS_URL)
                    .ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
