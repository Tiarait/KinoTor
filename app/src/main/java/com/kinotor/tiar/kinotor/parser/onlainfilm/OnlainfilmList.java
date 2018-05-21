package com.kinotor.tiar.kinotor.parser.onlainfilm;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.utils.OnTaskUrlCallback;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class OnlainfilmList extends AsyncTask<Void, Void, Void> {
    private String url, trans;
    private String[] quality_arr, url_arr;
    private String se, ep;
    private OnTaskUrlCallback callbackUrl;
    private OnTaskVideoCallback callbackVideo;
    private ItemVideo items;
    private boolean file = false, season = false, series = false;

    public OnlainfilmList(String url, String se, String ep, OnTaskUrlCallback callback, boolean file) {
        this.url = url;
        this.se = se;
        this.ep = ep;
        this.file = file;
        this.callbackUrl = callback;
    }

    public OnlainfilmList(String url, OnTaskVideoCallback callback, boolean season, String trans) {
        this.url = url;
        this.season = season;
        this.callbackVideo = callback;
        this.trans = trans;

        items = new ItemVideo();
    }

    public OnlainfilmList(String url, OnTaskVideoCallback callback, boolean series, String trans, String s) {
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
        if (season || series) callbackVideo.OnCompleted(items);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (url.startsWith("http://onlainfilm") || url.startsWith("http://serv"))
            getHtml(GetData(url));
        else if (url.contains("var pl =") && file)
            getHtmlEp(url);
        else parse(url);
        return null;
    }

    private void getHtml(Document doc) {
        if (doc != null) {
            if (doc.html().contains("class=\"extra-player")) {
                url = doc.select(".extra-player iframe").attr("src");
                Document iframe = GetData(url);
                if (iframe != null) {
                    parse(iframe.body().html());
                }
            } else if (doc.html().contains("var pl =")) {
                getHtmlEp(doc.html());
            }
        }
    }

    private void getHtmlEp(String doc) {
        if (doc.contains("var pl =")) {
            String s = doc.split("var pl =")[1].split(";")[0].trim();
            s = s.replace("\"", "").trim();
            Document iframe = GetData(s);
            if (iframe != null) {
                parse(iframe.body().text());
            }
        }
    }
    
    private void parse (String url) {
        if (file) parseFile(url);
        else if (season) parseSeason(url);
        else if (series) parseSeries(url);
    }

    private String getQuality(String q) {
        if (q.contains("360p")) {
            return "360 (mp4)";
        } else if (q.contains("480p")) {
            return "480 (mp4)";
        } else if (q.contains("720p")) {
            return "720 (mp4)";
        } else if (q.contains("1080p")) {
            return "1080 (mp4)";
        } else if (q.contains("2160p")) {
            return "2160 (mp4)";
        } else return "unknown quality (mp4)";
    }
    
    private void parseFile(String doc) {
        final ArrayList<String> q = new ArrayList<>();
        final ArrayList<String> u = new ArrayList<>();

        if (doc.contains("var fileurl =") && se.contains("error")) {
            String all = doc.split("var fileurl =")[1].split(";")[0].trim();
            all = all.replace("\"", "").trim();
            if (all.contains(",")) {
                String[] path = all.split(",");
                for (String aPath : path) {
                    if (aPath.contains(".mp4")) {
                        q.add(getQuality(aPath));
                        u.add(aPath);
                        add(q, u);
                    }
                }
            } else {
                if (all.contains(".mp4")) {
                    q.add(getQuality(all));
                    u.add(all);
                    add(q, u);
                }
            }
        } else if (doc.contains("comment\":\"" + se +" сезон")) {
            String all = doc.split("comment\":\"" + se +" сезон")[1]
                    .replace(" ", "");
            //se < 1
            if (all.contains("]},{"))
                all = all.split("\\]\\},\\{")[0] + "]";
            if (all.contains("playlist\":["))
                all = all.split("playlist\":\\[")[1].trim();
            //ep < 1
            if (all.contains("},{")) {
                String[] path = all.split("\\},\\{");
                for (String aPath : path) {
                    if (aPath.contains("comment\":\"" + ep)) {
                        if (aPath.contains("file\":\"")) {
                            String fileM = all.split("file\":\"")[1].split("\"")[0];
                            Pattern p = Pattern.compile("\\[\\w+,,\\w+\\]");
                            if (p.matcher(fileM).find()) {
                                if (fileM.contains("360p")) {
                                    q.add("360p (mp4)");
                                    u.add(fileM.replaceAll("\\[\\w+,,\\w+\\]", "360p"));
                                }
                                if (fileM.contains("480p")) {
                                    q.add("480p (mp4)");
                                    u.add(fileM.replaceAll("\\[\\w+,,\\w+\\]", "480p"));
                                }
                                if (fileM.contains("720p")) {
                                    q.add("720p (mp4)");
                                    u.add(fileM.replaceAll("\\[\\w+,,\\w+\\]", "720p"));
                                }
                                if (fileM.contains("1080p")) {
                                    q.add("1080p (mp4)");
                                    u.add(fileM.replaceAll("\\[\\w+,,\\w+\\]", "1080p"));
                                }
                                if (fileM.contains("2160p")) {
                                    q.add("2160p (mp4)");
                                    u.add(fileM.replaceAll("\\[\\w+,,\\w+\\]", "2160p"));
                                }
                            } else {
                                q.add(getQuality(fileM));
                                u.add(fileM);
                            }
                        } else {
                            q.add("Error not found mp4");
                            u.add("error");
                        }
                        add(q, u);
                        break;
                    }
                }
            } else {
                if (all.contains("file\":\"")) {
                    String fileM = all.split("file\":\"")[1].split("\"")[0];
                    Pattern p = Pattern.compile("\\[\\w+,,\\w+\\]");
                    if (p.matcher(fileM).find()) {
                        if (fileM.contains("360p")) {
                            q.add("360p (mp4)");
                            u.add(fileM.replaceAll("\\[\\w+,,\\w+\\]", "360p"));
                        }
                        if (fileM.contains("480p")) {
                            q.add("480p (mp4)");
                            u.add(fileM.replaceAll("\\[\\w+,,\\w+\\]", "480p"));
                        }
                        if (fileM.contains("720p")) {
                            q.add("720p (mp4)");
                            u.add(fileM.replaceAll("\\[\\w+,,\\w+\\]", "720p"));
                        }
                        if (fileM.contains("1080p")) {
                            q.add("1080p (mp4)");
                            u.add(fileM.replaceAll("\\[\\w+,,\\w+\\]", "1080p"));
                        }
                        if (fileM.contains("2160p")) {
                            q.add("2160p (mp4)");
                            u.add(fileM.replaceAll("\\[\\w+,,\\w+\\]", "2160p"));
                        }
                    } else {
                        q.add(getQuality(fileM));
                        u.add(fileM);
                        add(q, u);
                    }
                } else {
                    q.add("Error not mp4 found");
                    u.add("error");
                    add(q, u);
                }
            }
        } else {
            q.add("видео недоступно");
            u.add("error");
            add(q, u);
        }
    }
    
    private void parseSeason(String doc) {
        items.setTitle("season back");
        items.setType("onlainfilm");
        items.setUrl(url);
        items.setId("error");
        items.setId_trans("error");
        items.setSeason("error");
        items.setEpisode("error");
        items.setTranslator(trans);

        String html = doc;


        String season, episode;
        if (doc.contains("window.arNumberOf =")) {
            doc = doc.split("window.arNumberOf =")[1];

            if (doc.contains("};")) {
                doc = doc.split("\\};")[0].replace("{", "").trim()
                        .replace("\"", "");

                for (int i = 0; i < 30; i++) {
                    if (doc.contains(":" + i + "_")) {
                        String[] sArr = doc.split(":" + i + "_");
                        season = String.valueOf(i);
                        episode = String.valueOf(sArr.length - 1);

                        items.setTitle("season");
                        items.setType("onlainfilm");
                        items.setUrl(html);
                        items.setId(url);
                        items.setId_trans("error");
                        items.setSeason(season);
                        items.setEpisode(episode);
                        items.setTranslator(trans);
                    }
                }
            }
        }
    }

    private void parseSeries(String doc) {
        items.setTitle("series back");
        items.setType("onlainfilm");
        items.setUrl(doc);
        items.setId("error");
        items.setId_trans("error");
        items.setSeason(se);
        items.setEpisode("error");
        items.setTranslator(trans);

        if (doc.contains("window.arNumberOf =")) {
            doc = doc.split("window.arNumberOf =")[1];
            int s = Integer.parseInt(se);
            if (doc.contains("};")) {
                doc = doc.split("\\};")[0].replace("{", "").trim()
                        .replace("\"", "");
                s = s + 1;
                if (doc.contains("," + s + "_1:")) {
                    doc = doc.split("," + s + "_1:")[0];
                } else if (doc.contains("," + se + "_1:")) {
                    doc = se + "_1:" + doc.split("," + se + "_1:")[1];
                }
                String[] array = doc.split(":" + se + "_");
                for (int i = 1; i < array.length; i ++)
                    addSeries(String.valueOf(i));
            }
        }
    }

    private void addSeries(String s) {
        items.setTitle("series");
        items.setType("onlainfilm");
        items.setUrl(url);
        items.setToken(url);
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
        String n = s.trim().replace("\u00a0", " ").replace(" ", "%20").trim();
        try {
            Document htmlDoc = Jsoup.connect(n)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(5000).ignoreContentType(true).get();
            Log.d(TAG, "GetdataKinoshaUrl: connected to " + htmlDoc.location());
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "GetdataKinoshaUrl: connected false to " + s);
            e.printStackTrace();
            return null;
        }
    }
}