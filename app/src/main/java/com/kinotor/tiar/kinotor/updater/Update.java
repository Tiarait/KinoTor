package com.kinotor.tiar.kinotor.updater;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.kinotor.tiar.kinotor.BuildConfig;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskLocationCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;

/**
 * Created by Tiar on 01.07.2017.
 */

public class Update extends AsyncTask<Void, Void, Void> {
    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    @SuppressLint("StaticFieldLeak")
    private DialogFragment update_d;
    private static String curr_ver = BuildConfig.VERSION_NAME;
//    private static double curr_ver = 0.44;
    private boolean new_ver = true;
    private boolean error = false;
    private String emails, upd, dom;
    private OnTaskLocationCallback callback;

    public Update(Activity a, String upd, OnTaskLocationCallback callback) {
        this.activity = a;
        this.upd = upd;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        UpdaterSite(Getdata(BuildConfig.SITE_UPD + "/app.php"));
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (!new_ver && upd.contains("version"))
            Toast.makeText(activity.getApplicationContext(), "Обновлений нет", Toast.LENGTH_SHORT).show();
        if (error) {
            Toast.makeText(activity.getApplicationContext(), "Ошибка сервера", Toast.LENGTH_SHORT).show();
            if (callback != null)
                callback.OnCompleted("error");
        }

        if (!error) {
            String status = " noacc ";
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
            String curEm = pref.getString("pro_acc", "Вход не выполнен").toLowerCase();
            String curAcc = pref.getString("filmix_acc", Statics.FILMIX_ACC);
            if (curEm.contains("@"))
                curEm = curEm.trim().split("@")[0];
            if (upd.contains("acc")) {
                if (emails != null) {
                    if (emails.toLowerCase().contains(curEm.toLowerCase()) || (emails.contains(curAcc) && !curAcc.isEmpty())) {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("pro_version", true);
                        editor.putBoolean("side_exist", true);
                        editor.apply();
                        String st = "-";
                        try {
                            st = new String(Base64.decode("UHJvINCy0LXRgNGB0LjRjyDQsNC60YLQuNCy0LjRgNC+0LLQsNC90LA=", Base64.DEFAULT),
                                    "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(activity.getApplicationContext(), st, Toast.LENGTH_SHORT).show();
                        status = " yesacc ";
                    } else {
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putBoolean("pro_version", false);
                        editor.putBoolean("side_left", false);
                        editor.putBoolean("side_exist", false);
                        editor.putBoolean("side_video", false);
                        editor.putString("pro_acc", "Вход не выполнен");
                        editor.putString("cur_google_account", "Вход не выполнен");
                        editor.apply();
                        String st = "-";
                        try {
                            st = new String(Base64.decode("0JDQutC60LDRg9C90YIg0L3QtSDQv9GA0LjQstGP0LfQsNC9", Base64.DEFAULT),
                                    "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(activity.getApplicationContext(), st, Toast.LENGTH_SHORT).show();
                    }
                }
            }
            if (callback != null) {
                if (upd.contains("domen")) {
                    callback.OnCompleted("true " + status + dom);
                } else callback.OnCompleted("true" + status);
            }
        }
    }

    private void UpdaterSite(Document getdata) {
        if (getdata != null) {

            if (curr_ver.contains("b")){
                String latest_ver = getdata.select(".download-b").attr("id").trim();
                Log.d("test", "UpdaterSite: " + getdata.select(".download-b").attr("id"));
                String download_url = getdata.select(".download-b").first().attr("href");
                Statics.newVerLog = getdata.select(".download-b").first().attr("name");
                if (!curr_ver.equals(latest_ver) && upd.contains("version")) {
                    update_d = new UpdateDialog();
                    if (activity.getFragmentManager() != null & download_url != null) {
                        try {
                            update_d.show(activity.getFragmentManager(), download_url);
                        } catch (Exception e){

                        }

                    }
                } else {
                    new_ver = false;
                }
                if (getdata.html().contains("id=\"domens\"")) {
                    dom = getdata.select("#domens").text();
                    if (dom.contains("fancookie=[")) {
                        Statics.FANSERIALS_COOKIE = dom.split("fancookie=\\[")[1].split("\\]")[0].trim();
                    }
                }
                Log.d("mydebug", "version: " + curr_ver + " site: " + latest_ver + "|" + download_url);
            } else {
                String latest_ver = getdata.select(".download").attr("id").trim();
                Log.d("test", "UpdaterSite: " + getdata.select(".download").attr("id"));
                String download_url = getdata.select(".download").first().attr("href");
                Statics.newVerLog = getdata.select(".download").first().attr("name");
                if (!curr_ver.equals(latest_ver) && upd.contains("version")) {
                    update_d = new UpdateDialog();
                    if (activity.getFragmentManager() != null & download_url != null)
                        update_d.show(activity.getFragmentManager(), download_url);
                } else {
                    new_ver = false;
                }
                if (getdata.html().contains("id=\"domens\"")) {
                    dom = getdata.select("#domens").text();
                }
                Log.d("mydebug", "version: " + curr_ver + " site: " + latest_ver + "|" + download_url);
            }
            Document doc = Getdata(BuildConfig.SITE_UPD + "/acc/list_email.php");
            if (doc != null) {
                emails = doc.html();
            }
        } else error = true;
    }

    public Document Getdata(String url){
        try {
            Document htmlDoc = Jsoup.connect(url).validateTLSCertificates(false)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(10000).ignoreContentType(true).get();
            Log.d("mydebug","connected to " + url);
            return htmlDoc;
        } catch (Exception e) {
            error = true;
            e.printStackTrace();
            return null;
        }
    }
}

