package com.kinotor.tiar.kinotor.parser.video.kinodom;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Tiar on 02.2018.
 */

public class KinodomIframeUrl extends AsyncTask<Void, Void, Void> {
    private String url;
    private ItemHtml itempath;
    private OnTaskVideoCallback callback;
    private ItemVideo items;

    public KinodomIframeUrl(ItemHtml item, OnTaskVideoCallback callback){
        this.itempath = item;
        this.callback = callback;
        this.items = new ItemVideo();

        url = item.getUrl(0).trim();
    }

    @Override
    protected void onPostExecute(Void aVoid) {callback.OnCompleted(items); }

    @Override
    protected Void doInBackground(Void... voids) {
        ParseHtml(getData(url));
        return null;
    }

    private void ParseHtml(Document data) {
        if (data != null) {
            String d = data.html();
            String title_m = "error", season, episode;
            if (d.contains("pl=/")) {
                String pl = d.split("pl=/")[1].split("'")[0];
                if (d.contains("class=\"post-title\""))
                    title_m = data.selectFirst(".post-title").text();
                if (d.contains("<span data-link=\"")) {
                    for (String v : d.split("<span data-link=\"")) {
                        String u = v.split("\">")[0];
                        String t = v.split("\">")[1].split("</span>")[0].trim();
                        if (!title_m.contains("error") && u.contains("/play/")) {
                            Log.d("Kinodom", Statics.KINODOM_URL+"/"+pl+u+".json");
                            Document docvid = getData(Statics.KINODOM_URL+"/"+pl+u+".json");
                            if (docvid != null) {
                                String text = docvid.text().replace(" ", "");
                                if (text.contains("Сезон"))
                                    season = text.split("Сезон")[text.split("Сезон").length - 1]
                                            .split("\"")[0];
                                else season = "X";
                                if (text.contains("Серия"))
                                    episode = text.split("Серия")[text.split("Серия").length - 1]
                                            .split("\"")[0];
                                else episode = "X";
                                items.setTitle("catalog serial");
                                items.setType(title_m + "\nkinodom");
                                items.setToken("");
                                items.setId_trans("");
                                items.setId("error");
                                items.setUrl(text);
                                items.setUrlTrailer("error");
                                items.setSeason(season);
                                items.setEpisode(episode);
                                items.setTranslator(t);
                            } else Log.d("Kinodom", "ParseHtml: wtf last");
                        } else Log.d("Kinodom", "ParseHtml: wtf " + u);
                    }
                } else Log.d("Kinodom", "ParseHtml: no span");
            } else Log.d("Kinodom", "ParseHtml: no pl");
        } else Log.d("Kinodom", "ParseHtml: data error 2");
    }

    private Document getData(String url) {
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
