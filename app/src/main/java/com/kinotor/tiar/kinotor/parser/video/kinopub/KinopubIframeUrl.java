package com.kinotor.tiar.kinotor.parser.video.kinopub;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Tiar on 02.2018.
 */

public class KinopubIframeUrl extends AsyncTask<Void, Void, Void> {
    private String url;
    private ItemHtml itempath;
    private OnTaskVideoCallback callback;
    private ItemVideo items;

    public KinopubIframeUrl(ItemHtml item, OnTaskVideoCallback callback){
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
            String season = "error", episode = "error";
            String t = "";
            if (d.contains("og:title\" content=\"")){
                t = d.split("og:title\" content=\"")[1].split("\"")[0];
                if (t.contains("/")) {
                    t = t.split("/")[0].trim();
                }
            } else if (data.html().contains("<h3"))
                t = data.selectFirst("h3").text();
            if (t.contains("/"))
                t = t.split("/")[0].trim();

            if (d.contains("table table-striped") && d.contains("#season_slide")) {
                Elements allLines = data.select(".table.table-striped tr");
                for (Element line : allLines) {
                    if (line.text().contains("Добавлен")) {
                        String l = line.text().replace("Добавлен","").trim();
                        if (l.contains(" сезон")) {
                            season = l.split(" сезон")[0].trim();
                        }
                        if (l.contains(" эпизод")) {
                            episode = l.split(" эпизод")[0].trim();
                            if (episode.contains("сезон ")) {
                                episode = episode.split("сезон ")[1].trim();
                            }
                        }
                        if (!season.contains("error") || !episode.contains("error")) {
                            items.setTitle("catalog serial");
                            items.setType(t + "\nkinopub");
                            items.setToken("");
                            items.setId_trans("");
                            items.setId("error");
                            items.setUrl(url);
                            items.setUrlTrailer("error");
                            items.setSeason(season);
                            items.setEpisode(episode);
                            items.setTranslator("Неизвестный");
                        }
                    }
                }
            } else if (d.contains("class=\"item episode-thumbnail\"")) {
                Element vv = data.selectFirst(".item.episode-thumbnail");
                String ss = vv.attr("data-url");
                if (!ss.contains("/s")) {
                    for (Element v : data.select(".item.episode-thumbnail")) {
                        if (v.html().contains("item-title text-ellipsis")) {
                            String u = Statics.KINOPUB_URL + v.select(".item-title.text-ellipsis a").attr("href");
                            t = v.select(".item-title.text-ellipsis a").text();
                            items.setTitle("catalog video");
                            items.setType(t + "\nkinopub");
                            items.setToken("");
                            items.setId_trans("");
                            items.setId("error");
                            items.setUrl(u);
                            items.setUrlTrailer("error");
                            items.setSeason(season);
                            items.setEpisode(episode);
                            items.setTranslator("Неизвестный");
                        } else Log.d("Kinopub", "ParseHtml: no item-title");
                    }
                } else {
                    Element v = data.select(".item.episode-thumbnail").last();
                    String sv = v.attr("data-url");
                    if (sv.contains("/s")) {
                        String s = sv.split("/s")[sv.split("/s").length-1];
                        String e = s.split("e")[1];
                        s = s.split("e")[0];
                        items.setTitle("catalog serial");
                        items.setType(t + "\nkinopub");
                        items.setToken("");
                        items.setId_trans("");
                        items.setId("error");
                        items.setUrl(Statics.KINOPUB_URL + sv);
                        items.setUrlTrailer("error");
                        items.setSeason(s);
                        items.setEpisode(e);
                        items.setTranslator("Неизвестный");
                    }
                }
            } else if (d.contains("class=\"dropdown-menu")) {
                for (Element v : data.select(".dropdown-menu")) {
                    if (v.html().contains("Файл mp4") || v.html().contains("HLS плейлист")) {
                        String q = "";
                        if (v.html().contains("4K"))
                            q = "4K";
                        else if (v.html().contains("1080p"))
                            q = "1080p";
                        else if (v.html().contains("720p"))
                            q = "720p";
                        else if (v.html().contains("480p"))
                            q = "480p";

                        items.setTitle("catalog video");
                        items.setType(t + " "+ q + "\nkinopub");
                        items.setToken("");
                        items.setId_trans("");
                        items.setId("error");
                        items.setUrl(v.html());
                        items.setUrlTrailer("error");
                        items.setSeason(season);
                        items.setEpisode(episode);
                        items.setTranslator("Неизвестный");
                    }  else Log.d("Kinopub", "ParseHtml: no item-title");
                }
            } else Log.d("Kinopub", "ParseHtml: no item episode-thumbnail && mp4");
        } else Log.d("Kinopub", "ParseHtml: data error 2");
    }

    private Document getData(String url) {
        try {
            return Jsoup.connect(url)
                    .header("Cookie", Statics.KINOPUB_COOCKIE
                            .replace("{","")
                            .replace("}","")
                            .replace(" , ",";")
                            .replace(",",";"))
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
