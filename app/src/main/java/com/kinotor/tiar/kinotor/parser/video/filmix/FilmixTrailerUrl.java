package com.kinotor.tiar.kinotor.parser.video.filmix;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskUrlCallback;
import com.kinotor.tiar.kinotor.utils.Utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class FilmixTrailerUrl extends AsyncTask<Void, Void, Void> {
    private String url;
    private String[] quality_arr, url_arr;
    private OnTaskUrlCallback callback;
    ArrayList<String> q = new ArrayList<>();
    ArrayList<String> u = new ArrayList<>();

    public FilmixTrailerUrl(String url, OnTaskUrlCallback callback) {
        this.url = url;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(quality_arr, url_arr);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (url.contains("/"))
            url = url.split("/")[url.split("/").length-1];
        getMp4(GetDataUpod(url));
        return null;
    }

    private void getMp4(Document document) {
        if (document != null) {
            if (document.text().contains("trailers\":{")) {
                Log.e("rty1", document.text());
                String tr = document.text().split("trailers\":\\{")[1].split("\\}")[0];
                if (tr.contains("\":\"")) {
                    setUrl(Utils.decodeUppod(tr.split("\":\"")[1]));
                    Log.e("rty2", Utils.decodeUppod(tr.split("\":\"")[1]));
                } else {
                    q.add("видео не доступно");
                    u.add("error");
                    add(q, u);
                }
            } else {
                q.add("видео не доступно");
                u.add("error");
                add(q, u);
            }
        } else {
            Log.e("rty-1", url);
            q.add("видео не доступно");
            u.add("error");
            add(q, u);
        }
    }

    private void setUrl(String qual){
        if (qual.replace(" ","").contains(",http")){
            for (String qq : qual.split(",http")){
                qq = qq.replace(",","").replace(" or ","or").trim();
//                Log.e("test", "getUppod0: " + qq);
                if (qq.contains("orhttp")){
                    for (String qqq : qq.split("orhttp")){
                        if (!qqq.contains("/u3.")) {
                            if (Statics.FILMIX_PRO) {
                                if (qqq.contains("_2160")) {
                                    q.add("2160 (mp4)");
                                    u.add("http" + qqq);
                                }
                                if (qqq.contains("_1440")) {
                                    q.add("1440 (mp4)");
                                    u.add("http" + qqq);
                                }
                                if (qqq.contains("_1080")) {
                                    q.add("1080 (mp4)");
                                    u.add("http" + qqq);
                                }
                            }
                            if (qqq.contains("_720")) {
                                q.add("720 (mp4)");
                                u.add("http" + qqq);
                            }
                            if (qqq.contains("_480")) {
                                q.add("480 (mp4)");
                                u.add("http" + qqq);
                            }
                            if (qqq.contains("_360")) {
                                q.add("360 (mp4)");
                                u.add("http" + qqq);
                            }
                        }
                    }
                } else {
                    if (Statics.FILMIX_PRO) {
                        if (qq.contains("_2160")) {
                            q.add("2160 (mp4)");
                            u.add("http"+qq);
                        }
                        if (qq.contains("_1440")) {
                            q.add("1440 (mp4)");
                            u.add("http"+qq);
                        }
                        if (qq.contains("_1080")) {
                            q.add("1080 (mp4)");
                            u.add("http"+qq);
                        }
                    }
                    if (qq.contains("_720")) {
                        q.add("720 (mp4)");
                        u.add("http"+qq);
                    }
                    if (qq.contains("_480")) {
                        q.add("480 (mp4)");
                        u.add("http"+qq);
                    }
                    if (qq.contains("_360")) {
                        q.add("360 (mp4)");
                        u.add("http"+qq);
                    }
                }
            }
        } else if (qual.startsWith("[")){
            for (String qqq : qual.split("\\[")){
                if (qqq.contains("]"))
                    qqq = qqq.split("\\]")[1].replace(",","").trim();
//                Log.e("test", "getUppod01: " + qqq);
                qqq = qqq.replace(" or ","or").trim();
                if (qqq.contains("orhttp")){
                    for (String qq : qqq.split("orhttp")){
                        if (!qq.contains("/u3.")) {
                            if (Statics.FILMIX_PRO) {
                                if (qq.contains("_2160")) {
                                    q.add("2160 (mp4)");
                                    u.add("http" + qq);
                                }
                                if (qq.contains("_1440")) {
                                    q.add("1440 (mp4)");
                                    u.add("http" + qq);
                                }
                                if (qq.contains("_1080")) {
                                    q.add("1080 (mp4)");
                                    u.add("http" + qq);
                                }
                            }
                            if (qq.contains("_720")) {
                                q.add("720 (mp4)");
                                u.add("http" + qq);
                            }
                            if (qq.contains("_480")) {
                                q.add("480 (mp4)");
                                u.add("http" + qq);
                            }
                            if (qq.contains("_360")) {
                                q.add("360 (mp4)");
                                u.add("http" + qq);
                            }
                        }
                    }
                } else {
                    if (Statics.FILMIX_PRO) {
                        if (qqq.contains("_2160")) {
                            q.add("2160 (mp4)");
                            u.add(qqq);
                        }
                        if (qqq.contains("_1440")) {
                            q.add("1440 (mp4)");
                            u.add(qqq);
                        }
                        if (qqq.contains("_1080")) {
                            q.add("1080 (mp4)");
                            u.add(qqq);
                        }
                    }
                    if (qqq.contains("_720")) {
                        q.add("720 (mp4)");
                        u.add(qqq);
                    }
                    if (qqq.contains("_480")) {
                        q.add("480 (mp4)");
                        u.add(qqq);
                    }
                    if (qqq.contains("_360")) {
                        q.add("360 (mp4)");
                        u.add(qqq);
                    }
                }
            }
        } else {
            if (Statics.FILMIX_PRO) {
                if (qual.contains("2160") || qual.contains("2160p")) {
                    q.add("2160 (mp4)");
                    u.add(url + "2160.mp4");
                }
                if (qual.contains("1440") || qual.contains("1440p")) {
                    q.add("1440 (mp4)");
                    u.add(url + "1440.mp4");
                }
                if (qual.contains("1080") || qual.contains("1080p") || qual.contains("1O8Op")) {
                    q.add("1080 (mp4)");
                    u.add(url + "1080.mp4");
                }
            }
            if (qual.contains("720") || qual.contains("720p")) {
                q.add("720 (mp4)");
                u.add(url + "720.mp4");
            }
            if (qual.contains("480") || qual.contains("480p") || qual.contains("400")) {
                q.add("480 (mp4)");
                u.add(url + "480.mp4");
            }
            if (qual.contains("360") || qual.contains("360p")) {
                q.add("360 (mp4)");
                u.add(url + "360.mp4");
            }
        }
        if (!q.isEmpty())
            add(q, u);
        else {
            q.add("видео не доступно");
            u.add("error");
            add(q, u);
        }
    }

    private void add(ArrayList<String> q,  ArrayList<String> u) {
        quality_arr = q.toArray(new String[q.size()]);
        url_arr = u.toArray(new String[u.size()]);
    }

    private Document GetDataUpod(String s) {
        String url = Statics.FILMIX_URL + "/api/movies/player_data";

        try {
            Connection.Response res = Jsoup
                    .connect(Statics.FILMIX_URL + "/search/")
                    .execute();
            String loginCookies = res.cookies().toString().replace("{","")
                    .replace("}","")
                    .replace("dle_user_id=deleted","")
                    .replace("dle_password=deleted","")
                    .replace("dle_hash=deleted","")
                    .replace("remember_me=deleted","").trim()+", "+Statics.FILMIX_COOCKIE+";";
            loginCookies = loginCookies.replace(" , ","").replace(",,","")
                    .replace(",",";");

            return Jsoup.connect(url)
                    .data("post_id", s.trim())
                    .data("showfull", "true")
                    .header("Cookie", loginCookies)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false)
                    .timeout(5000).ignoreContentType(true).post();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}