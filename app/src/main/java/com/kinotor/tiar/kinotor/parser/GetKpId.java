package com.kinotor.tiar.kinotor.parser;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URLEncoder;

/**
 * Created by Tiar on 02.2018.
 */

public class GetKpId extends AsyncTask<Void, Void, Void> {
    private String TAG ="GetKpId";
    private String title, subtitle, year, kpId;
    private OnTaskCallback callback;

    public GetKpId(ItemHtml item, OnTaskCallback callback) {
        this.title = item.getTitle(0).replace(" ","+");
        this.subtitle = item.getSubTitle(0).replace(" ","+");
        this.year = item.getDate(0);
        this.callback = callback;

        if (subtitle.toLowerCase().contains("error"))
            this.subtitle = "";
        if (year.toLowerCase().contains("error"))
            this.year = "";
        if (this.year.contains("."))
            this.year = this.year.split("\\.")[this.year.split("\\.").length-1];
        this.title = this.title.replace("("+this.year+")","").trim();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        callback.OnCompleted(null,null);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (Statics.KP_ID.contains("error") || Statics.KP_ID.isEmpty()) {
//            Log.e(TAG, "google kpIdold: " + Statics.KP_ID);
            kpId = getGoogleKp(getData());
            if (kpId.contains("-")) {
                kpId = kpId.split("-")[kpId.split("-").length - 1];
            }
            if (!kpId.contains("error"))
                Statics.KP_ID = kpId;
        }
        Log.e(TAG, "google kpId: " + kpId);
        return null;
    }

    private Document getData() {
        try {
            String t = URLEncoder.encode(title, "UTF-8");
            String s = URLEncoder.encode(subtitle, "UTF-8");

//            Log.e(TAG, "getData: https://www.google.com/search?q="+t+"+"+s+"+"+year+"+site%3A"+Statics.KP_URL);
            return Jsoup.connect("https://www.google.com/search?q="+t+"+"+s+"+"+year+"+site%3A"+Statics.KP_URL)
                    .header("accept-encoding","gzip, deflate, br")
                    .header("accept-language","ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                    .header("accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8")
                    .header("upgrade-insecure-requests","1")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false)
                    .timeout(5000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getGoogleKp(Document doc) {
        if (doc != null) {
//            Log.e("test", "getGoogleKp0: "+doc.body().html().split(Statics.KP_URL + "/film/")[0]);
//            Log.e("test", "getGoogleKp1: "+doc.body().html().split(Statics.KP_URL + "/film/")[1]);
            if (doc.html().contains(Statics.KP_URL + "/film/")) {
                String k = doc.html().split(Statics.KP_URL + "/film/")[1].split(">")[0].trim();
//                Log.e("test", "getGoogleKp0: "+k);
//                Log.e("test", "getGoogleKp1: "+doc.html().split(k)[1]);
                if (!doc.html().split(k)[1].startsWith("><img "))
                    return k.split("/")[0].trim();
                else return "error";
            } else return "error";
        } else return "error";
    }
}
