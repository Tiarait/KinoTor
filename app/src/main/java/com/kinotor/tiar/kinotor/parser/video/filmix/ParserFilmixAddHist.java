package com.kinotor.tiar.kinotor.parser.video.filmix;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.ui.DetailActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Tiar on 08.2018.
 */
public class ParserFilmixAddHist extends AsyncTask<Void, Void, Void> {
    private String id = "error", translator = "error", s = "0", e = "0";

    public ParserFilmixAddHist(String translator, String id, String s, String e) {
        this.translator = translator;
        this.id = id;
        this.s = s;
        this.e = e;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (id.contains("/")) {
            id = id.split("/")[id.split("/").length-1];
            if (id.contains("-"))
                id = id.split("-")[0].trim();
        }


        Document d = addData();
        if (d != null) Log.d("ParserFilmixAddHist", d.text());
        else Log.d("ParserFilmixAddHist", "null");

        Document u = updData();
        if (u != null) {
            Statics.FILMIX_HIST = u.text();
        } else {
            Statics.FILMIX_HIST = "null";
        }

        return null;
    }

    private Document updData() {
        try {
            return Jsoup.connect(Statics.FILMIX_URL+"/api/movies/list_watched")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("Cookie", Statics.FILMIX_COOCKIE.replace(",",";") +";")
                    .referrer(Statics.FILMIX_URL+"/last_seen")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false)
                    .timeout(30000).ignoreContentType(true).post();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private Document addData() {
        try {
            if (s.contains("error") || e.contains("error"))
                return Jsoup.connect(Statics.FILMIX_URL+"/api/movies/add_watched")
                        .data("id", id)
                        .data("translation", translator)
                        .header("X-Requested-With","XMLHttpRequest")
                        .header("Cookie", Statics.FILMIX_COOCKIE.replace(",",";") +";")
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .validateTLSCertificates(false)
                        .timeout(30000).ignoreContentType(true).post();
            else
                return Jsoup.connect(Statics.FILMIX_URL+"/api/movies/add_watched")
                        .data("id", id)
                        .data("translation", translator)
                        .data("season", s)
                        .data("episode", e)
                        .header("X-Requested-With","XMLHttpRequest")
                        .header("Cookie", Statics.FILMIX_COOCKIE.replace(",",";") +";")
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .validateTLSCertificates(false)
                        .timeout(30000).ignoreContentType(true).post();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
