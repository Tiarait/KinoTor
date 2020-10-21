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
public class KinozalLogin extends AsyncTask<Void, Void, Void> {
    private String login, pass, result = "null";
    private OnTaskLocationCallback callback;


    public KinozalLogin(String login, String pass, OnTaskLocationCallback callback) {
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
            Log.e("test", "doInBackground: "+cookies);
            if (cookies.contains("uid=deleted"))
                result = "error";
            else if (cookies.contains("uid")) {
                result = cookies.replace("{","").replace("}","");
            } else result = "null";
        } else result = "null";
        return null;
    }

    private Connection.Response Getdata() {
        try {
            return Jsoup
                    .connect(Statics.KINOZAL_URL + "/takelogin.php")
                    .method(Connection.Method.POST)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .data("username", login)
                    .data("password", pass)
                    .data("returnto", "")
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
