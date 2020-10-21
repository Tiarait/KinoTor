package com.kinotor.tiar.kinotor.parser.video.kinopub;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class KinopubList extends AsyncTask<Void, Void, Void> {
    private String url, trans;
    private String se;
    private OnTaskVideoCallback callbackVideo;
    private ItemVideo items;
    private boolean season = false, series = false;
    private ArrayList<String> videoList = new ArrayList<>();
    private ArrayList<String> videoListName = new ArrayList<>();

    public KinopubList(String url, OnTaskVideoCallback callback, boolean season, String trans) {
        this.url = url;
        this.season = season;
        this.callbackVideo = callback;
        this.trans = trans;

        items = new ItemVideo();
    }

    public KinopubList(String url, OnTaskVideoCallback callback, boolean series, String trans, String s) {
        this.url = url;
        this.series = series;
        this.callbackVideo = callback;
        this.trans = trans;
        this.se = s.trim();

        items = new ItemVideo();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        Statics.videoList = videoList.toArray(new String[videoList.size()]);
        Statics.videoListName = videoListName.toArray(new String[videoListName.size()]);
        callbackVideo.OnCompleted(items);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (season)
            parseSeason(url);
        else if (series)
            parseSeries(url);
        return null;
    }

    private void parseSeason(String doc) {
        items.setTitle("season back");
        items.setType("kinopub");
        items.setUrl(url);
        items.setId("error");
        items.setId_trans("error");
        items.setSeason("error");
        items.setEpisode("error");
        items.setTranslator(trans);


        if (url.contains("/s"))
            url = url.split("/s")[0].trim();
        String sss = url.split("/s")[url.split("/s").length-1].trim();
        sss = sss.split("e")[0].trim();
        if (url.contains("view/")) {
            if (url.split("view/")[1].contains("/")) {
                url = url.split("view/")[0] + "view/" +
                        url.split("view/")[1].split("/")[0];
            }
        }
        String curl = url + "/s1e1";
        Log.e("qwe",curl);
        Document data = getData(curl);
        if (data != null) {
            String d = data.html();
            String ls = "error", le = "error";
            if (d.contains("table table-striped") && d.contains("#season_slide")) {
                Elements allLines = data.select(".table.table-striped tr");
                for (Element line : allLines) {
                    if (line.text().contains("Добавлен")) {
                        String l = line.text().replace("Добавлен", "").trim();
                        if (l.contains(" сезон")) {
                            ls = l.split(" сезон")[0].trim();
                        }
                        if (l.contains(" эпизод")) {
                            le = l.split(" эпизод")[0].trim();
                            if (le.contains("сезон ")) {
                                le = le.split("сезон ")[1].trim();
                            }
                        }
                        if (!ls.contains("error") || !le.contains("error")) {
                            addSeason(ls, le);
                            break;
                        }
                    }
                }
            } else if (d.contains("class=\"item episode-thumbnail\"")) {
                Document datav = getData(url);
                if (datav != null) {
                    String dv = datav.html();
                    if (dv.contains("var playlist = [")) {
                        String playlist = dv.split("var playlist = \\[")[1].split("\\]")[0];
                        se = sss;
                        if (playlist.contains("},{")) {
                            for (int i = 0; i < playlist.split("\\},\\{").length; i++) {
                                addSeries(playlist.split("\\},\\{")[i], i);
                            }
                        } else addSeries(playlist, 0);
                    }
                }
            }
        }
    }

    private void addSeason(String s, String e){
        try {
            int ls = Integer.parseInt(s);
            for (int i = 1; i <= ls; i++) {
                if (i != ls) {
                    items.setTitle("season");
                    items.setType("kinopub");
                    items.setUrl(url + "/s"+i+"e1");
                    items.setId("error");
                    items.setId_trans("error");
                    items.setSeason(String.valueOf(i));
                    items.setEpisode("все");
                    items.setTranslator(trans);
                } else {
                    items.setTitle("season");
                    items.setType("kinopub");
                    items.setUrl(url + "/s"+i+"e1");
                    items.setId("error");
                    items.setId_trans("error");
                    items.setSeason(String.valueOf(i));
                    items.setEpisode(e);
                    items.setTranslator(trans);
                }
            }
        } catch (Exception r) {
            r.printStackTrace();
        }
    }

    private void parseSeries(String doc) {
        items.setTitle("series back");
        items.setType("kinopub");
        items.setUrl(doc);
        items.setId("error");
        items.setId_trans("error");
        items.setSeason(se);
        items.setEpisode("error");
        items.setTranslator(trans);


        Document data = getData(url);
        if (data != null) {
            String d = data.html();
            if (d.contains("var playlist = [")) {
                String playlist = d.split("var playlist = \\[")[1].split("\\]")[0];
                if (playlist.contains("},{")) {
                    for (int i = 0; i < playlist.split("\\},\\{").length; i++) {
                        addSeries(playlist.split("\\},\\{")[i], i);
                    }
                } else addSeries(playlist, 0);
            }
        }
    }

    private void addSeries(String s, int ep) {
        String epis = String.valueOf(ep + 1);
        if (s.contains("vnumber\":")) {
            epis = s.split("vnumber\":")[1].split(",")[0];
        }
        if (url.contains("/s"))
            url = url.split("/s")[0].trim();
        if (url.contains("view/")) {
            if (url.split("view/")[1].contains("/")) {
                url = url.split("view/")[0] + "view/" +
                        url.split("view/")[1].split("/")[0];
            }
        }
        if (s.contains("file\":\"")) {
            videoList.add(s.split("file\":\"")[1].split("\"")[0].replace("\\",""));
            videoListName.add("s"+se+"e"+epis);
        } else Log.e("Kinopub", "file nf");
        String curl = url + "/s"+se+"e"+epis;

        items.setTitle("series");
        items.setType("kinopub");
        items.setUrl(curl);
        items.setToken("error");
        items.setId("error");
        items.setId_trans("error");
        items.setSeason(se);
        items.setEpisode(epis);
        items.setTranslator(trans);
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