package com.kinotor.tiar.kinotor.parser.video.zombiefilm;

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

public class ParserZombiefilm extends AsyncTask<Void, Void, Void> {
    private String search_title;
    private ItemHtml itempath;
    private OnTaskVideoCallback callback;
    private ItemVideo items;

    public ParserZombiefilm(ItemHtml item, OnTaskVideoCallback callback){
        this.itempath = item;
        this.callback = callback;
        this.items = new ItemVideo();

        search_title = item.getTitle(0).trim().replace("\u00a0", " ");
        search_title = search_title.replace("("+item.getDate(0)+")", "").trim();
    }

    @Override
    protected void onPostExecute(Void aVoid) {callback.OnCompleted(items); }

    @Override
    protected Void doInBackground(Void... voids) {
        ParseHtml(Getdata());
        return null;
    }

    private void ParseHtml(Document data) {
        if (data != null) {
            Log.e("Zombie", data.text());
            if (data.text().contains("{\"id\":")) {
                for (String s : data.text().split("\"id\":")) {
                    String title_m = "error", title_en = "error", year = "error", season = "error", episode = "error",
                            id = "error", url = "error", type = "1", trans = "error", orig = "error", slug = "error";
                    id = s.split(",")[0].trim();
                    if (s.contains("name\":\""))
                        title_m = s.split("name\":\"")[1].split("\"")[0].trim();
                    if (s.contains("originName\":\""))
                        title_en = s.split("originName\":\"")[1].split("\"")[0].trim();
                    if (s.contains("originName\":\"") && !s.contains("originName\":\"\\n\""))
                        title_en = s.split("originName\":\"")[1].split("\"")[0].trim();
                    if (s.contains("year\":"))
                        year = s.split("year\":")[1].split(",")[0].trim();
                    if (s.contains("type\":"))
                        type = s.split("type\":")[1].split(",")[0].trim();
                    if (s.contains("slug\":\"")) {
                        slug = s.split("slug\":\"")[1].split("\"")[0].trim();
                        if (type.equals("1"))
                            url = Statics.ZOMBIEFILM_URL + "/film-" + slug;
                        else if (type.equals("2"))
                            url = Statics.ZOMBIEFILM_URL + "/multfilm-" + slug;
                        else if (type.equals("3"))
                            url = Statics.ZOMBIEFILM_URL + "/serial-" + slug;
                        else if (type.equals("4"))
                            url = Statics.ZOMBIEFILM_URL + "/tv-" + slug;
                        else if (type.equals("7"))
                            url = Statics.ZOMBIEFILM_URL + "/anime-" + slug;
                    }
                    if (title_m.contains("сезон)"))
                        title_m = title_m.split("сезон\\)")[0].split("\\(")[0].trim();
                    Log.e("Zombie", title_m +"||"+search_title+"||"+title_en+"||"+itempath.getSubTitle(0));
                    if (title_m.toLowerCase().trim().equals(search_title.toLowerCase().trim()) ||
                            (title_en.toLowerCase().equals(itempath.getSubTitle(0).toLowerCase().trim()) && !title_en.equals("error"))) {
//                        Log.d("Delivembed", "ParseHtml1: " + title_m);
                        Document doc = Getdata(url, Statics.ZOMBIEFILM_URL);
                        if (doc != null) {
                            if (doc.html().contains("video\":\"") || doc.html().contains("urlQuality\":{")) {
                                String urlslug = "https://api.videobalancer.net/v1/franchise/view/?slug=" + slug +
                                        "&ref="+Statics.ZOMBIEFILM_URL.replace("/", "%2F");
                                Document docslug = Getdata(urlslug, Statics.ZOMBIEFILM_URL);
                                if (docslug != null) {
                                    if (docslug.text().contains("dubbing\":[{\"name\":\""))
                                        trans = docslug.text()
                                                .split("dubbing\":\\[\\{\"name\":\"")[1]
                                                .split("\"")[0];
                                    else trans = "Неизвестный";
                                } else Log.d("Delivembed", "ParseHtml0: data 6 error");
                                if (doc.html().contains("video\":\""))
                                    url = doc.html().split("video\":\"")[1].split("\"")[0];
                                else if (doc.html().contains("urlQuality\":{"))
                                    url = doc.html().split("urlQuality\":\\{")[1].split("\\}")[0];
                                if (title_en.contains("\\n")) {
                                    title_en = title_m;
                                    if (trans.equals("Неизвестный"))
                                        trans = "Оригинал";
                                }
                                items.setTitle("catalog movie");
                                items.setType(title_en.trim() + " " + year  + "\nzombiefilm");
                                items.setToken("");
                                items.setId_trans("");
                                items.setId("error");
                                items.setUrl(url);
                                items.setUrlTrailer("error");
                                items.setSeason(season);
                                items.setEpisode(episode);
                                items.setTranslator(trans);
                            } else if (doc.html().contains("franchiseId\":")) {
                                String urlslug = "https://api.videobalancer.net/v1/franchise/view/?slug=" + slug +
                                        "&season=1&ref="+Statics.ZOMBIEFILM_URL.replace("/", "%2F");
                                Document docslug = Getdata(urlslug, Statics.ZOMBIEFILM_URL);
                                if (docslug != null) {
                                    if (docslug.text().contains("dubbing\":[{\"name\":\""))
                                        trans = docslug.text()
                                                .split("dubbing\":\\[\\{\"name\":\"")[1]
                                                .split("\"")[0];
                                    else trans = "Неизвестный";
                                } else Log.d("Delivembed", "ParseHtml0: data 6 error");
                                String franchise = doc.html().split("franchiseId\":")[1].split(",")[0].trim();
                                url = "https://api.videobalancer.net/v1/season/?findBy=franchise&fId="+ franchise +
                                        "&ref="+Statics.ZOMBIEFILM_URL.replace("/", "%2F");
                                Document docend = Getdata(url, Statics.ZOMBIEFILM_URL);
                                if (docend != null) {
                                    if (docend.html().contains("season\":")) {
                                        String inf = docend.html().split("season\":")
                                                [docend.html().split("season\":").length - 1];
                                        season = inf.split(",")[0].trim();

                                        String ses = docend.html().split("id\":")
                                                [docend.html().split("id\":").length - 1].split(",")[0];
                                        String urlses = "https://api.videobalancer.net/contents/video/by-season/?id=" + ses +
                                                "&host=zombie-film.com";
                                        Document docses = Getdata(urlses, Statics.ZOMBIEFILM_URL);
//                                        Log.e("qwe", docses.html());
                                        if (docses != null) {
                                            if (docses.html().contains("episode\":"))
                                                episode = docses.html().split("episode\":")
                                                        [docses.html().split("episode\":").length - 1].split(",")[0];
//                                            Log.d("Delivembed", "ParseHtml0: season " + season);
//                                            Log.d("Delivembed", "ParseHtml0: episode " + episode);
                                            items.setTitle("catalog serial");
                                            items.setType(title_en.trim() + " " + year  + "\nzombiefilm");
                                            items.setToken("");
                                            items.setId_trans("");
                                            items.setId("error");
                                            items.setUrl(docend.text());
                                            items.setUrlTrailer("error");
                                            items.setSeason(season);
                                            items.setEpisode(episode);
                                            items.setTranslator(trans);
                                        } else Log.d("Delivembed", "data 8 error");
                                    } else Log.d("Delivembed", "data season error");
                                } else Log.d("Delivembed", "ParseHtml: data 5 error");
                            } else Log.d("Delivembed", "wtf data " + url);
                        } else Log.d("Delivembed", "ParseHtml1: data 2 error");
                        break;
                    } else Log.d("Delivembed", "ParseHtml1: wrong search");
                }
            } else Log.d("Delivembed", "ParseHtml0: data search error");
        } else
            Log.d("Delivembed", "ParseHtml2: data error");
    }

    private Document Getdata(String url, String ref) {
        if (!url.startsWith("http"))
            url = "https:" + url;
        if (!ref.startsWith("http"))
            ref = "https:" + ref;
        try {
            return Jsoup.connect(url)
                    .header("Origin", ref)
                    .referrer(ref)
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Document Getdata() {
        try {
            return Jsoup.connect("https://api.videobalancer.net/v1/franchise/search/?search="+
                    search_title.replace(" ", "+") +
                    "&ref="+Statics.ZOMBIEFILM_URL.replace("/", "%2F"))
                    .referrer(Statics.ZOMBIEFILM_URL)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
