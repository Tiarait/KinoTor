package com.kinotor.tiar.kinotor.parser.torrents;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskLocationCallback;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 * Created by Tiar on 08.2018.
 */
public class RutrackerLogin extends AsyncTask<Void, Void, Void> {
    private String login, pass, result = "null";
    private OnTaskLocationCallback callback;


    public RutrackerLogin(String login, String pass, OnTaskLocationCallback callback) {
        this.login = login;
        this.pass = pass;
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(result);

        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Connection.Response res = Getdata();
        if (res != null) {
            String cookies = res.cookies().toString();
            Log.e("RutrackerLogin", cookies);
            if (cookies.contains("bb_session=deleted"))
                result = "error";
            else if (cookies.contains("bb_session")) {
                result = cookies.replace("{","").replace("}","");
            } else result = "null";
        } else {
            Log.e("RutrackerLogin", "res null");
            result = "null";
        }
        return null;
    }

    private Connection.Response Getdata() {
        try {
            return Jsoup
                    .connect(Statics.RUTRACKER_URL + "/forum/login.php")
                    .method(Connection.Method.POST)
                    .referrer(Statics.RUTRACKER_URL + "/forum/index.php")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .data("login_username", login)
                    .data("login_password", pass)
                    .data("login", "%E2%F5%EE%E4")
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
