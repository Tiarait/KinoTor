package com.kinotor.tiar.kinotor.parser.catalog;

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
public class ParserKinopubLogin extends AsyncTask<Void, Void, Void> {
    private String login, pass, result = "null";
    private OnTaskLocationCallback callback;


    public ParserKinopubLogin(String login, String pass, OnTaskLocationCallback callback) {
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
        Connection.Response res = GetdataLogin();
        if (res != null) {
            String cookies = res.cookies().toString();
            Log.e("test", "doInBackground coockies: "+cookies);
            Document d = resToDoc(res);
            if (d != null) {
                String content = "error";
                if (d.html().contains("name=\"csrf-token\" content=\""))
                    content = d.html().split("name=\"csrf-token\" content=\"")[1].split("\"")[0].trim();
                Log.e("test", "doInBackground content: "+content);
                if (!content.contains("error") && !content.isEmpty()) {
                    Connection.Response last = PostdataLogin(res, content);
                    if (last != null) {
                        String cookiesLast = last.cookies().toString();
                        Log.e("test", "doInBackground cookiesLast: "+cookiesLast);
                        result = cookiesLast;
                    } else {
                        Log.e("test", "Connection.Response last error ");
                        result = "null";
                    }
                } else {
                    Log.e("test", "!content.contains(error) && !content.isEmpty()");
                    result = "null";
                }
            } else result = "null";
        } else result = "null";
        return null;
    }

    private Document resToDoc(Connection.Response res) {
        try {
            return res.parse();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Connection.Response GetdataLogin() {
        try {
            return Jsoup
                    .connect(Statics.KINOPUB_URL + "/user/login")
                    .method(Connection.Method.GET)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Connection.Response PostdataLogin(Connection.Response res, String csrf) {
        try {
            return Jsoup
                    .connect(Statics.KINOPUB_URL + "/user/login")
                    .cookies(res.cookies())
                    .data("login-form[login]", login)
                    .data("login-form[password]", pass)
                    .data("login-form[rememberMe]", "1")
                    .data("_csrf", csrf)
                    .method(Connection.Method.POST)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
