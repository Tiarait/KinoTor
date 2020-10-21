package com.kinotor.tiar.kinotor.parser.catalog.filmix;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskLocationCallback;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Tiar on 08.2018.
 */
public class ParserFilmixLogin extends AsyncTask<Void, Void, Void> {
    private String login, pass, result = "null";
    private OnTaskLocationCallback callback;


    public ParserFilmixLogin(String login, String pass, OnTaskLocationCallback callback) {
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
            if (cookies.contains("dle_user_id=deleted"))
                result = "error";
            else if (cookies.contains("dle_user_id")) {
                result = "dle_user_id"+cookies.split("dle_user_id")[1].replace("}","");
                Document doc = Getdata(res);
                if (doc != null){
                    if (doc.body().html().contains("pro-status")) {
                        if (!doc.select(".pro-status").text().contains("неактивен"))
                            result = result + "[!pro!]";
                    }
                }
            } else result = "null";
        } else result = "null";
        return null;
    }

    private Document Getdata(Connection.Response res) {
        try {
            return Jsoup
                    .connect(Statics.FILMIX_URL + "/my_settings")
                    .header("X-Requested-With", "XMLHttpRequest")
                    .cookies(res.cookies())
                    .get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Connection.Response Getdata() {
        try {
            return Jsoup
                    .connect(Statics.FILMIX_URL + "/engine/ajax/user_auth.php")
                    .method(Connection.Method.POST)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .data("login_name", login)
                    .data("login_password", pass)
                    .data("login_not_save", "1")
                    .data("login", "submit")
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
