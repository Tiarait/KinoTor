package com.kinotor.tiar.kinotor.parser.video.kinosha;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskUrlCallback;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class KinoshaList extends AsyncTask<Void, Void, Void> {
    private String url, trans;
    private String[] quality_arr, url_arr;
    private String se, ep;
    private OnTaskUrlCallback callbackUrl;
    private OnTaskVideoCallback callbackVideo;
    private ItemVideo items;
    private boolean file = false, season = false, series = false;

    private ArrayList<String> videoList = new ArrayList<>();
    private ArrayList<String> videoListName = new ArrayList<>();

    public KinoshaList(String url, String se, String ep, OnTaskUrlCallback callback, boolean file) {
        this.url = url;
        this.se = se;
        this.ep = ep;
        this.file = file;
        this.callbackUrl = callback;
    }

    public KinoshaList(String url, OnTaskVideoCallback callback, boolean season, String trans) {
        this.url = url;
        this.season = season;
        this.callbackVideo = callback;
        this.trans = trans;

        items = new ItemVideo();
    }

    public KinoshaList(String url, OnTaskVideoCallback callback, boolean series, String trans, String s) {
        this.url = url;
        this.series = series;
        this.callbackVideo = callback;
        this.trans = trans;
        this.se = s.trim();

        items = new ItemVideo();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (file) callbackUrl.OnCompleted(quality_arr, url_arr);
        if (season || series) {
            if (videoList.size() > 1) {
                Statics.videoList = videoList.toArray(new String[videoList.size()]);
                Statics.videoListName = videoListName.toArray(new String[videoListName.size()]);
            }
            callbackVideo.OnCompleted(items);
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (url.contains("status\":\"ok\""))
            parse(url);
        else getHtml(GetData(url));
        return null;
    }

    private void getHtml(Document doc) {
        if (doc != null) {
            parse(doc.text());
        } else Log.e("KinoshaList", "doc null");
    }
    
    private void parse (String url) {
        if (file)
            parseFile(url);
        else if (season)
            parseSeason(url);
        else if (series)
            parseSeries(url);
    }

    private String getQulity(String q) {
        if (q.contains("360.mp4") || q.contains(".360.") || q.contains("_360_")) {
            return "360 (mp4)";
        } else if (q.contains("480.mp4") || q.contains(".480.") || q.contains("_400_")) {
            return "480 (mp4)";
        } else if (q.contains("720.mp4") || q.contains(".720.") || q.contains("_720_")) {
            return "720 (mp4)";
        } else if (q.contains("1080.mp4") || q.contains(".1080.") || q.contains("_1080_")) {
            return "1080 (mp4)";
        } else return "... (mp4)";
    }
    
    private void parseFile(String doc) {
        final ArrayList<String> q = new ArrayList<>();
        final ArrayList<String> u = new ArrayList<>();

        if (doc.contains("sources\":{") && se.contains("error")) {
            String all = doc.split("sources\":\\{")[1].split("\\},")[0];
            all = all.replace("\"", "").replace("\\", "")
                    .replace("mp4:", "");
            String[] path = all.split(",");
            for (String aPath : path) {
                if (aPath.contains(":http"))
                    aPath = "http" + aPath.split(":http")[1];
                if (aPath.contains(".mp4")) {
                    q.add(getQulity(aPath));
                    u.add(aPath);
                }
            }
            add(q, u);
        } else if (doc.contains("{\"id\":\"" + se) || doc.contains("{\"id\":" + se)) {
            String all = doc;
            //se < 1
            if (doc.contains("{\"id\":" + se))
                all = doc.split("\\{\"id\":" + se)[1].replace("\\", "");
            else if (doc.contains("{\"id\":\"" + se))
                all = doc.split("\\{\"id\":\"" + se)[1].replace("\\", "");

            Log.e(TAG, "parseFile: "+all);

            if (all.contains("]}"))
                all = all.split("\\]\\}")[0];
            if (all.contains("playlist\":["))
                all = all.split("playlist\":\\[")[1];
            //ep < 1
            if (all.contains("},{")) {
                String[] path = all.split("\\},\\{");
                for (String aPath : path) {
                    if (aPath.contains("comment\":\"" + ep)) {
                        if (aPath.contains("file\":\"")) {
                            q.add(getQulity(aPath));
                            u.add(aPath.split("file\":\"")[1].split("\"")[0]);
                        } else {
                            q.add("Error not mp4 found");
                            u.add("error");
                        }
                    }
                }
                add(q, u);
            } else {
                if (all.contains("file\":\"")) {
                    q.add(getQulity(all.split("file\":\"")[1]));
                    u.add(all.split("file\":\"")[1].split("\"")[0]);
                } else {
                    q.add("Error not mp4 found");
                    u.add("error");
                }
                add(q, u);
            }
        } else {
            q.add("видео недоступно");
            u.add("error");
            add(q, u);
        }
    }
    
    private void parseSeason(String doc) {
        items.setTitle("season back");
        items.setType("kinosha");
        items.setUrl(url);
        items.setId("error");
        items.setId_trans("error");
        items.setSeason("error");
        items.setEpisode("error");
        items.setTranslator(trans);

        String html = doc;


        String season, episode = "0";
        if (doc.contains(":{\"playlist\":[")) {
            doc = doc.split(":\\{\"playlist\":\\[")[1];
            if (doc.contains("]}},")) {
                doc = doc.split("\\]\\}\\},")[0];
                //если сезонов больше 1
                if (doc.contains("]},{\"id\":")) {
                    String[] array = doc.split("\\]\\},\\{\"id\":");
                    for (int i = 1; i < array.length; i++) {
                        season = array[i].split(",")[0].trim();
                        if (array[i].contains("playlist\":[{")) {
                            episode = array[i].split("playlist\":\\[\\{")[1].trim();
                            if (episode.contains("},{"))
                                episode = String.valueOf(episode.split("\\},\\{").length);
                            else episode = "1";
                        }

                        items.setTitle("season");
                        items.setType("kinosha");
                        items.setUrl(html);
                        items.setId(url);
                        items.setId_trans("error");
                        items.setSeason(season);
                        items.setEpisode(episode);
                        items.setTranslator(trans);
                    }
                } else {
                    if (doc.contains("},{"))
                        episode = String.valueOf(doc.split("\\},\\{").length);
                    else episode = "1";
                    items.setTitle("season");
                    items.setType("kinosha");
                    items.setUrl(html);
                    items.setId(url);
                    items.setId_trans("error");
                    items.setSeason("1");
                    items.setEpisode(episode);
                    items.setTranslator(trans);
                }
            }
        }
    }

    private void parseSeries(String doc) {
        items.setTitle("series back");
        items.setType("kinosha");
        items.setUrl(doc);
        items.setId("error");
        items.setId_trans("error");
        items.setSeason(se);
        items.setEpisode("error");
        items.setTranslator(trans);

        String season, episode;
        if (doc.contains(":{\"playlist\":[")) {
            doc = doc.split(":\\{\"playlist\":\\[")[1];
            if (doc.contains("]}},")) {
                doc = doc.split("\\]\\}\\},")[0];
                //если сезонов больше 1
                if (doc.contains("]},{\"id\":")) {
                    String[] array = doc.split("\\]\\},\\{\"id\":");
                    for (int i = 1; i < array.length; i++) {
                        season = array[i].split(",")[0].trim();
                        if (season.equals(se) && array[i].contains("playlist\":[{")) {
                            episode = array[i].split("playlist\":\\[\\{")[1].trim();
                            //если серий больше 1
                            if (episode.contains("},{")) {
                                for (int j = 0; j < episode.split("\\},\\{").length; j ++){
                                    addSeries(parseSUrl(episode.split("\\},\\{")[j]), String.valueOf(j + 1));
                                }
                            } else addSeries(parseSUrl(episode), "1");
                        }
                    }
                } else {
                    if (doc.contains("},{")) {
                        for (int j = 0; j < doc.split("\\},\\{").length; j++) {
                            addSeries(parseSUrl(doc.split("\\},\\{")[j]), String.valueOf(j + 1));
                        }
                    } else addSeries(parseSUrl(doc), "1");
                }
            }
        }
    }

    private String parseSUrl(String doc) {
        String ur ="error";
        if (doc.contains("file\":\"")) {
            ur = doc.split("file\":\"")[1].split("\"")[0].replace("\\", "");
        }
        return ur;
    }

    private void addSeries(String url, String s) {
        videoList.add(url);
        videoListName.add("s"+se+"e"+s);

        items.setTitle("series");
        items.setType("kinosha");
        items.setUrl(this.url);
        items.setToken(this.url);
        items.setId("error");
        items.setId_trans("error");
        items.setSeason(se);
        items.setEpisode(s);
        items.setTranslator(trans);
    }

    private void add(ArrayList<String> q,  ArrayList<String> u) {
        quality_arr = q.toArray(new String[q.size()]);
        url_arr = u.toArray(new String[u.size()]);
    }

    private Document GetData(String s){
        String u = s;
        if (u.contains("/") && u.contains("-"))
            u = s.split("/")[s.split("/").length-1].split("-")[0];
        try {
            Document htmlDoc = Jsoup.connect("http://api.kinosha.se/getplay")
                    .header("Host", "api.kinosha.se")
                    .data("key[id]", u.trim())
                    .data("pl_type", "movie")
                    .data("is_mobile", "0")
                    .data("dle_group", "5")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(5000).ignoreContentType(true).post();
            return htmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}