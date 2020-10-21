package com.kinotor.tiar.kinotor.parser.video.kinohd;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskUrlCallback;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class KinohdIframe extends AsyncTask<Void, Void, Void> {
    private String url;
    private ItemVideo items;
    private ItemHtml item;
    private OnTaskVideoCallback callback;
    private OnTaskUrlCallback callbackUrl;
    private String[] quality_arr, url_arr;

    public KinohdIframe(ItemHtml item, OnTaskVideoCallback callback) {
        this.item = item;
        this.callback = callback;

        this.url = item.getIframe(0);
        this.items = new ItemVideo();
    }

    public KinohdIframe(String url, OnTaskUrlCallback callback) {
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
        if (!url.contains("http"))
            url = "http://"+url;
//        Log.e("KinohdIframe", "doInBackground: " + url);
        iframe(url);
        return null;
    }

    private void iframe(String data) {
        if (callbackUrl != null){
            if (data.contains(Statics.KINOHD_URL)) {
                Document doc = getData(data);
                if (doc != null) {
                    if (doc.html().contains("var player = new Playerjs(")){
                        iframe(doc.html().split("var player = new Playerjs\\(")[1].split("\\);")[0]);
                    }
                }
            } else {
                final ArrayList<String> q = new ArrayList<>();
                final ArrayList<String> u = new ArrayList<>();
                if (data.contains("file:\"")) {
                    String file = data.split("file:\"")[1].split("\"")[0].trim();
                    if (file.contains("/sz.txt?")) {
                        q.add("serial");
                        u.add("error");
                    } else {
                        if (file.contains("[4k]")) {
                            String curQ = file.split("\\[4k\\]")[1];
                            if (curQ.contains(",")) curQ = curQ.split(",")[0];
                            q.add("4K (mp4)");
                            u.add(curQ);
                        } else if (file.contains("[4K UHD]")) {
                            String curQ = file.split("\\[4K UHD\\]")[1];
                            if (curQ.contains(",")) curQ = curQ.split(",")[0];
                            q.add("4K UHD (mp4)");
                            u.add(curQ);
                        }
                        if (file.contains("[1080]")) {
                            String curQ = file.split("\\[1080\\]")[1];
                            if (curQ.contains(",")) curQ = curQ.split(",")[0];
                            q.add("1080 (mp4)");
                            u.add(curQ);
                        }
                        if (file.contains("[720]")) {
                            String curQ = file.split("\\[720\\]")[1];
                            if (curQ.contains(",")) curQ = curQ.split(",")[0];
                            q.add("720 (mp4)");
                            u.add(curQ);
                        }
                        if (file.contains("[480]")) {
                            String curQ = file.split("\\[480\\]")[1];
                            if (curQ.contains(",")) curQ = curQ.split(",")[0];
                            q.add("480 (mp4)");
                            u.add(curQ);
                        }
                        if (file.contains("[360]")) {
                            String curQ = file.split("\\[360\\]")[1];
                            if (curQ.contains(",")) curQ = curQ.split(",")[0];
                            q.add("360 (mp4)");
                            u.add(curQ);
                        }
                    }
                    if (q.isEmpty() && file.contains("video.php?name=")){
                        q.add("... (mp4)");
                        u.add(file);
                    }
                    if (q.isEmpty() && file.contains(".m3u8")){
                        q.add("HLS (m3u8)");
                        u.add(file);
                    }
                } else if (data.endsWith("mp4")) {
                    q.add("... (mp4)");
                    u.add(data.trim());
                } else {
                    q.add("Видео недоступно");
                    u.add("error");
                }
                if (!q.isEmpty())
                    add(q, u);
                else {
                    Log.e(TAG, "iframe: empty");
                    q.add("Видео недоступно");
                    u.add("error");
                    add(q, u);
                }
            }
        } else if (data.contains("http")) {
            Document doc = getData(Statics.KINOHD_URL);
            if (doc != null) {
//                Log.e("kinohd", "iframe: 0");
                if (doc.html().contains("class=\"conte\"")){
                    Elements allEntries = doc.select(".conte");
                    for (Element entry : allEntries) {
                        String t = "Неизвестный";
                        String year = "error";
                        String name = "error";
                        String u = "error";
                        String trailer = "error";
                        String q = "";
                        String allLines = entry.select("table").last().text();
//                        Log.e("kinohd", "iframe conte: "+allLines);

                        if (allLines.contains("Год") && allLines.contains("Качество")){
                            year = allLines.split("Год")[1].split("Качество")[0].trim();
                            if (allLines.contains("Оригинал")){
                                q = " (" + allLines.split("Качество")[1].split("Оригинал")[0].trim() + ")";
                            }
                        }


//                        Log.e("kinohd", "iframe: "+entry.select(".wins").html());

                        if (entry.html().contains("class=\"wins")){
                            name = entry.select(".wins a").text().trim();
                            u = Statics.KINOHD_URL + entry.select(".wins a").attr("href").trim();
                        }
                        if (entry.html().contains("var player = new Playerjs(")){
                            u = doc.html().split("var player = new Playerjs\\(")[1].split("\\);")[0];
                        }
                        if (name.contains("сезон") && name.contains(",")){
                            name = name.split(",")[0].trim();
                        }

//                        Log.e("kinohd", "iframe: "+name +" "+year);
                        String s = item.getTitle(0).contains("(") ?
                                item.getTitle(0).split("\\(")[0].trim() :
                                item.getTitle(0).trim();
                        if (s.contains("[")) s = s.split("\\[")[0].trim();
                        if (!name.contains("error") && name.toLowerCase().equals(s.trim().toLowerCase())) {
                            name = name + " " + year;
                            if (allEntries.text().contains("Сериалы"))
                                series(getData(Statics.KINOHD_URL + u));
                            else {
                                Document dd = getData(u);
                                if (dd != null){
                                    if (dd.html().contains("trailer")){
                                        trailer = getLocation(dd.select(".trailer").first().attr("onclick")
                                                .replace("trailer('","").replace("')",""));
                                        Log.e(TAG, "iframe: "+trailer);
                                        if (trailer == null)
                                            trailer = "error";
                                    }
                                    if (dd.html().contains("var player = new Playerjs(")){
                                        u = dd.html().split("var player = new Playerjs\\(")[1].split("\\);")[0];
                                    }
                                }
                                String tt = "";
                                if (trailer != null)
                                if (!trailer.contains("error"))
                                    tt = " [+trailer]";
                                items.setTitle("catalog site");
                                items.setType(name + q + tt + "\nkinohd");
                                items.setToken(u);
                                items.setId_trans("null");
                                items.setId("site");
                                items.setUrl(u);
//                                items.setUrlSite(u);
                                items.setSeason("error");
                                items.setEpisode("error");
                                if (trailer != null)
                                    items.setUrlTrailer(trailer);
                                items.setTranslator(t.trim());
                            }
                        }
                    }
                } else if (doc.html().contains("var player = new Playerjs(")){
                    String t = "Неизвестный";
                    String year = "error";
                    String name = "error";
                    String u = doc.html().split("var player = new Playerjs\\(")[1].split("\\);")[0];
                    String q = "";
                    Element allLines = doc.selectFirst(".winfull");
                    name = allLines.selectFirst(".tr").text();
                    if (allLines.text().contains("Год") && allLines.text().contains("Качество")){
                        year = " " + allLines.text().split("Год")[1].split("Качество")[0].trim();
                        if (allLines.text().contains("Оригинал")){
                            String fk = "";
                            if (allLines.html().contains("title=\"4К\""))
                                fk = " 4k";
                            q = " (" + allLines.text().split("Качество")[1].split("Оригинал")[0].trim() + fk + ")";
                        }
                    }
                    Log.e("KinohdIframe", "iframe var player : "+name +" "+year);
                    name = name + year;
                    items.setTitle("catalog video");
                    items.setType(name + q + "\nkinohd");
                    items.setToken(u);
                    items.setId_trans("null");
                    items.setId("error");
                    items.setUrl(u);
//                    items.setUrlSite(u);
                    items.setSeason("error");
                    items.setEpisode("error");
                    items.setTranslator(t.trim());
                } else if (doc.html().contains("var playerse = new Playerjs(")) {
                    series(doc);
                }
            }
        }
        else if (data.contains("var player = new Playerjs(")){
            Log.e("KinohdIframe", "iframe var player 22: "+data);
            items.setTitle("catalog site");
            items.setType("kinohd");
            items.setToken(data);
            items.setId_trans("null");
            items.setId("site");
            items.setUrl(data);
//            items.setUrlSite(data);
            items.setSeason("error");
            items.setEpisode("error");
            items.setTranslator(item.getVoice(0).contains("error") ?
                    item.getTitle(0).trim() : item.getVoice(0).trim());
        }
    }

    private void series (Document doc) {
        if (doc != null) {
            String t = "Неизвестный";
            String year = "error";
            String name = "error";
            String u = doc.html().split("var playerse = new Playerjs\\(")[1].split("\\);")[0]
                    .replace("/sz.txt?", Statics.KINOHD_URL + "/sz.txt?");
            String q = "";
            Element allLines = doc.selectFirst(".winfull");
            name = doc.selectFirst(".content h1").text();
            if (allLines.text().contains("Год") && allLines.text().contains("Качество")) {
                year = " " + allLines.text().split("Год")[1].split("Качество")[0].trim();
                if (allLines.text().contains("Оригинал")) {
                    String fk = "";
                    if (allLines.html().contains("title=\"4К\""))
                        fk = " 4k";
                    q = " (" + allLines.text().split("Качество")[1].split("Оригинал")[0].trim() + fk + ")";
                }
            }

            Log.e("KinohdIframe", "iframe var playerse : " + name + " " + year);
            if (name.contains("сезон") && name.contains(",")) {
                String s = name.split(",")[1].split("сезон")[0].trim();
                if (s.contains("-")) s = s.split("-")[1].trim();
                name = name.split(",")[0].trim();

                name = name + year;
                items.setTitle("catalog serial");
                items.setType(name + q + "\nkinohd");
                items.setToken(u);
                items.setId_trans("null");
                items.setId("error");
                items.setUrl(u);
//                items.setUrlSite(u);
                items.setSeason(s);
                items.setEpisode("ALL");
                items.setTranslator(t.trim());
            }
        }
    }

    private void add(ArrayList<String> q,  ArrayList<String> u) {
        quality_arr = q.toArray(new String[q.size()]);
        url_arr = u.toArray(new String[u.size()]);
    }

    private Document getData(String url) {
        try {
            if (url.equals(Statics.KINOHD_URL)) {
                String s = item.getTitle(0).contains("(") ?
                        item.getTitle(0).split("\\(")[0].trim() :
                        item.getTitle(0).trim();
                if (s.contains("[")) s = s.split("\\[")[0].trim();
                Log.e("KinohdIframe", "iframe: "+s);

                return Jsoup.connect(Statics.KINOHD_URL)
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .data("story", s)
                        .data("do", "search")
                        .data("subaction", "search")
                        .header("X-Requested-With", "XMLHttpRequest")
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .ignoreContentType(true).followRedirects(false).post();
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
        String ref = Statics.KINOHD_URL;
        try {
            URL uri = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Referer", ref);
            connection.setRequestProperty("Host", "kino-v.online");
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            Log.d("KinohdIframe", "GetLocation: " + url);
            Log.d("KinohdIframe", "Location: " + connection.getHeaderField("Location"));
            String loc = connection.getHeaderField("Location");
            return loc != null ? loc : url;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
