package com.kinotor.tiar.kinotor.parser.video.kinodom;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskLocationCallback;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

/**
 * Created by Tiar on 08.2018.
 */
public class KinodomLogin extends AsyncTask<Void, Void, Void> {
    private String login, pass, result = "null";
    private OnTaskLocationCallback callback;


    public KinodomLogin(String login, String pass, OnTaskLocationCallback callback) {
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
            Log.e("test", "doInBackground kinodom: "+cookies);
            if (cookies.contains("dle_user_id=deleted"))
                result = "error";
            else if (cookies.contains("dle_user_id")) {
                result = "dle_user_id"+cookies.split("dle_user_id")[1].replace("}","");
            } else result = "null";
        } else result = "null";
        return null;
    }

    private Connection.Response Getdata() {
        try {
            return Jsoup
                    .connect(Statics.KINODOM_URL)
                    .method(Connection.Method.POST)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .data("login_name", login)
                    .data("login_password", pass)
                    .data("login_not_save", "0")
                    .data("login", "submit")
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
