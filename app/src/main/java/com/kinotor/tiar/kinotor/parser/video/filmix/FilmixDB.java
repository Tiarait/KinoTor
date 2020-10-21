package com.kinotor.tiar.kinotor.parser.video.filmix;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.ui.DetailActivity;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Tiar on 02.2018.
 */

public class FilmixDB extends AsyncTask<Void, Void, Void> {
    private Boolean add;
    private String id = "error", base = "favor";

    public FilmixDB(Boolean add, String base) {
        this.add = add;
        this.base = base;
        if (DetailActivity.url.contains("-")) {
            String idd = DetailActivity.url.split("-")[0];
            if (idd.contains("/")){
                id = idd.split("/")[idd.split("/").length-1];
            }
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Log.e("filmix", "doInBackground: "+id );
        if (!id.equals("error")) {
            if (base.equals("favor")) {
                if (add)
                    addDBF();
                else delDBF();
            } else if (base.equals("later")) {
                if (add)
                    addDBL();
                else delDBL();
            }
        }
        return null;
    }

    private void addDBF() {
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

            Jsoup.connect(Statics.FILMIX_URL + "/engine/ajax/favorites.php?fav_id="+id+"&action=plus&skin=Filmix&alert=0")
                    .header("Cookie", loginCookies)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .referrer(Statics.FILMIX_URL + "/favorites")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false)
                    .timeout(5000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void addDBL() {
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

            Jsoup.connect(Statics.FILMIX_URL + "/engine/ajax/watch_later.php")
                    .header("Cookie", loginCookies)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .referrer(Statics.FILMIX_URL + "/watch_later")
                    .data("action", "add")
                    .data("post_id", id)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false)
                    .timeout(5000).ignoreContentType(true).post();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void delDBF() {
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

            Document doc = Jsoup.connect(Statics.FILMIX_URL + "/engine/ajax/favorites.php?fav_id="+id+"&action=minus&skin=Filmix&alert=0")
                    .header("Cookie", loginCookies)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .referrer(Statics.FILMIX_URL + "/favorites")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false)
                    .timeout(5000).ignoreContentType(true).get();
            Log.d("test", "delDBF: " + doc.body().html());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void delDBL() {
        try {
            Connection.Response res = Jsoup
                    .connect(Statics.FILMIX_URL  + "/search/")
                    .execute();
            String loginCookies = res.cookies().toString().replace("{","")
                    .replace("}","")
                    .replace("dle_user_id=deleted","")
                    .replace("dle_password=deleted","")
                    .replace("dle_hash=deleted","")
                    .replace("remember_me=deleted","").trim()+", "+Statics.FILMIX_COOCKIE+";";
            loginCookies = loginCookies.replace(" , ","").replace(",,","")
                    .replace(",",";");

            Jsoup.connect(Statics.FILMIX_URL + "/engine/ajax/watch_later.php")
                    .header("Cookie", loginCookies)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .referrer(Statics.FILMIX_URL + "/watch_later")
                    .data("action", "rm")
                    .data("post_id", id)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false)
                    .timeout(5000).ignoreContentType(true).post();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}