package com.kinotor.tiar.kinotor.updater;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.kinotor.tiar.kinotor.BuildConfig;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by Tiar on 01.07.2017.
 */

public class Update extends AsyncTask<Void, Void, Void> {
    Activity activity;
    DialogFragment update_d;
    private final String GITHUB_RELEASES_URL = "https://github.com/Tiarait/KinoTor/releases/latest";
    private final String PDA_RELEASES_URL = "https://4pda.ru/forum/index.php?showtopic=887739";
    static float curr_ver = Float.parseFloat(BuildConfig.VERSION_NAME);
    private boolean new_ver = true;

    public Update(Activity a) {
        this.activity = a;
    }

    @Override
    protected Void doInBackground(Void... params) {
//        UpdaterGit(Getdata(GITHUB_RELEASES_URL));
        Updater4pda(Getdata(PDA_RELEASES_URL));
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (!new_ver)
            Toast.makeText(activity.getApplicationContext(), "Обновлений нет", Toast.LENGTH_SHORT).show();
    }

    private void UpdaterGit(Document getdata) {
        if (getdata != null) {
            float latest_ver = Float.parseFloat(getdata.select("span.css-truncate-target").first().text());
            String download_url = "";
            if (curr_ver < latest_ver && getdata.html().contains(".apk")) {
                download_url ="https://github.com" + getdata.select("a[href$='.apk']").first().attr("href");
                update_d = new UpdateDialog();
                update_d.show(activity.getFragmentManager(), download_url);
            } else {
                new_ver = false;
            }
            Log.d("mydebug", "version: " + curr_ver + " git: " + latest_ver + "|" + download_url);
        }
    }

    private void Updater4pda(Document getdata) {
        if (getdata != null) {
            Element l = getdata.select("#post-70095054").first();
            float latest_ver = Float.parseFloat(l.text().split("версия: ")[1].split(" ")[0]);
            if (curr_ver < latest_ver) {
                update_d = new UpdateDialog();
                update_d.show(activity.getFragmentManager(), PDA_RELEASES_URL);
            } else {
                new_ver = false;
            }
            Log.d("mydebug", "version: " + curr_ver + " 4pda: " + latest_ver);
        }
    }

    public Document Getdata(String url){
        try {
            Document htmlDoc = Jsoup.connect(url).validateTLSCertificates(false)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(15000).ignoreContentType(true).get();
            Log.d("mydebug","connected to " + url);
            return htmlDoc;
        } catch (Exception e) {
            Log.d("mydebug","connected false to " + url);
            e.printStackTrace();
            return null;
        }
    }
}

