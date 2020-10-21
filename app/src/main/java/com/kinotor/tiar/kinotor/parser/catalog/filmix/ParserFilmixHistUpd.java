package com.kinotor.tiar.kinotor.parser.catalog.filmix;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskLocationCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Tiar on 08.2018.
 */
public class ParserFilmixHistUpd extends AsyncTask<Void, Void, Void> {


    public ParserFilmixHistUpd() {
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Document d = getData();
        if (d != null) {
            Statics.FILMIX_HIST = d.text();
        } else {
            Statics.FILMIX_HIST = "null";
        }
        return null;
    }


    private Document getData() {
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
}
