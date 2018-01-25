package com.kinotor.tiar.kinotor.updater;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.kinotor.tiar.kinotor.BuildConfig;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Tiar on 01.07.2017.
 */

public class Update extends AsyncTask<Void, Void, Void> {
    Activity activity;
    DialogFragment update_d;
    private final String GITHUB_RELEASES_URL = "https://github.com/Tiarait/KinoTor/releases/latest";
    static double curr_ver = Double.parseDouble(BuildConfig.VERSION_NAME);
    private boolean new_ver = true;

    public Update(Activity a) {
        this.activity = a;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Updater(Getdata(GITHUB_RELEASES_URL));
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (!new_ver)
            Toast.makeText(activity.getApplicationContext(), "Обновлений нет", Toast.LENGTH_SHORT).show();
    }

    private void Updater(Document getdata) {
        if (getdata != null) {
            double latest_ver = Double.parseDouble(getdata.select("span.css-truncate-target").first().text());
            if (curr_ver < latest_ver && getdata.html().contains(".apk")) {
                final String download_url ="https://github.com" + getdata.select("a[href$='.apk']").first().attr("href");
                update_d = new UpdateDialog();
                update_d.show(activity.getFragmentManager(), download_url);
            } else {
                new_ver = false;
                Log.d("mydebug", "version: " + curr_ver + " git: " + latest_ver);
            }
        }
    }

    public Document Getdata(String url){
        try {
            Document htmlDoc = Jsoup.connect(url).get();
            Log.d("mydebug","connected to " + url);
            return htmlDoc;
        } catch (Exception e) {
            Log.d("mydebug","connected false to " + url);
            e.printStackTrace();
            return null;
        }
    }
}

