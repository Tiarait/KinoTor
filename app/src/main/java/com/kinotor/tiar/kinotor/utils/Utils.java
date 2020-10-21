package com.kinotor.tiar.kinotor.utils;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.kinotor.tiar.kinotor.items.Statics;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.UI_MODE_SERVICE;

/**
 * Created by Tiar on 04.2018.
 */
public class Utils {

    public boolean isPackage(String name, PackageManager packageManager) {
        boolean f = true;
        try {
            packageManager.getPackageInfo(name, 0);
        } catch (PackageManager.NameNotFoundException e) {
            f = false;
        }
        return f;
    }

    public static String getUrl(String source) {
        switch (source) {
            case "filmix":
                return Statics.FILMIX_URL;
            case "kinolive":
                return Statics.KINOLIVE_URL;
            case "hdgo":
                return "http://hdgo.cc";
            case "moonwalk":
                return "http://moonwalk.cc";
            case "hdbaza":
                return "https://hdbaza.com";
            case "anidub":
                return Statics.ANIDUB_URL;
            case "animevost":
                return Statics.ANIMEVOST_URL;
            case "animedia":
                return Statics.ANIMEDIA_URL;
            case "kinohd":
                return Statics.KINOHD_URL;
            case "kinosha":
                return Statics.KINOSHA_URL;
            case "kinodom":
                return Statics.KINODOM_URL;
            case "zombiefilm":
                return "https://api.delivembed.cc";
            default:
                return Statics.KINOLIVE_URL;
        }
    }

    public boolean isOnline(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return nInfo != null && nInfo.isConnected();
    }

    public boolean isTablet(Context context) {
        if (context != null) {
            UiModeManager uiModeManager = (UiModeManager) context.getSystemService(UI_MODE_SERVICE);
            if (uiModeManager != null)
                return (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION ||
                        ((context.getResources().getConfiguration().screenLayout
                                & Configuration.SCREENLAYOUT_SIZE_MASK)
                                >= Configuration.SCREENLAYOUT_SIZE_LARGE));
            else
                return ((context.getResources().getConfiguration().screenLayout
                        & Configuration.SCREENLAYOUT_SIZE_MASK)
                        >= Configuration.SCREENLAYOUT_SIZE_LARGE)
                        || context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEVISION)
                        || context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK);
        } return false;
    }

    public static String renGenre(String s){
        s = s.replace("Аниме,", "").replace("Аниме", "")
                .replace("аниме,", "").replace("аниме", "")
                .replace("Фильмы,", "").replace("Фильмы", "")
                .replace("Сериалы,", "").replace("Сериалы", "")
                .replace("Мультфильмы,", "").replace("Мультфильмы", "")
                .replace("мультфильм,", "").replace("мультфильм", "")
                .replace("Мультсериалы,", "").replace("Мультсериалы", "")
                .replace("Мультсериал,", "").replace("Мультсериал", "")
                .replace("Мультики,", "").replace("Мультики", "")
                .replace("Телепередачи,", "").replace("Телепередачи", "").trim();
        if (s.isEmpty())
            return "error";
        else return s;
    }

    private int calcGrid(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("side_menu", true)
                && context.getResources().getConfiguration().orientation == 2 && isTablet(context))
            dpWidth -= 230;
        return  (int)dpWidth / Statics.CATALOG_W;
    }

    public int calculateGrid(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int r = calcGrid(context);
        if (preferences.getString("grid_catalog", "2").equals("2") &&
                preferences.getString("grid_count", "0").equals("0")) {
            if (r == 1)
                r = 2;
            return r;
        } else if (preferences.getString("grid_catalog", "2").equals("1")) {
            return 1;
        }
        else return Integer.parseInt(preferences.getString("grid_count", "0"));
    }

    public float calculateScale(Context context, int count) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getString("grid_count", "0").equals("0") ||
                count == calcGrid(context)) {
            return 1;
        } else {
//            &&
//                    !PreferenceManager.getDefaultSharedPreferences(context).getBoolean("tv_activity_main", true)
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("side_menu", true)
                    && context.getResources().getConfiguration().orientation == 2 && isTablet(context))
                dpWidth -= 230;
            if (PreferenceManager.getDefaultSharedPreferences(context).getString("grid_catalog", "2").equals("2")) {
                int newWidth = (int) (dpWidth / count);
                return ((float) newWidth / (float) Statics.CATALOG_W);
            } else return 1;
        }
    }

    public float pixelToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public float dpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public static String unicodeToString(String uni){
        // "%u" followed by 4 hex digits, capture the digits
        uni = uni.replace("\\","%");
        Pattern p = Pattern.compile("%u([0-9a-f]{4})", Pattern.CASE_INSENSITIVE);

        Matcher m = p.matcher(uni);
        StringBuffer decoded = new StringBuffer(uni.length());

        // replace every occurrences (and copy the parts between)
        while (m.find()) {
            m.appendReplacement(decoded, Character.toString((char)Integer.parseInt(m.group(1), 16)));
        }

        m.appendTail(decoded);
        if (decoded.toString().contains("ў") ||
                decoded.toString().contains("њ") ||
                decoded.toString().contains("µ") ||
                decoded.toString().contains("Ѓ") ||
                decoded.toString().contains("°") ||
                decoded.toString().contains("Њ") ||
                decoded.toString().contains("Ђ")) {
            Log.e("Utils", "unicodeToString Њ");
            try {
                byte[] sourceBytes = decoded.toString().getBytes("Windows-1251");
                String data = new String(sourceBytes , "utf-8");
                Log.e("Utils", "unicodeToString "+data);
                return data;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return decoded.toString();
            }
        } else
            return decoded.toString();
    }

    public static String decodeUppod(String data){
//        String[] a = new String[] {"0", "1", "2", "3", "4", "5", "6", "7", "=", "B", "D", "H", "I", "J", "L", "M", "N", "U", "V", "Z", "c", "f", "i", "k", "n", "p"};
//        String[] b = new String[] {"d", "9", "b", "e", "R", "X", "8", "T", "r", "Y", "G", "W", "s", "u", "Q", "y", "a", "w", "o", "g", "z", "v", "m", "l", "x", "t"};
//
//        for (int i=0; i < a.length; i++)
//        {
//            data = data.replace(b[i], "__");
//            data = data.replace(a[i], b[i]);
//            data = data.replace("__", a[i]);
//        }
//
//        byte[] valueDecoded = Base64.decodeBase64(data.getBytes());

//        Log.e("test", "decodeUppod: " + data);
        data = data
                .replace("#2","")
                .replace(":<:","")
                .replace("\\/\\/","")
                .replace("//","")
                .replace("ZjlTMnFFUWFabjV5MERDMjdq","")
                .replace("TWFtNlFaZGRTUVhuUmJOS0Q1","")
                .replace("aUhOZENrYW1rV3J2UXhjd0pV","")
                .replace("c0RIVjdBWWtHWUJHbGJzMzYw","")

                .replace("aEZaQkFUN2cxRGU4ZUxQbFBL","");

        //java.lang.NoClassDefFoundError: Failed resolution of: Lorg/apache/commons/codec/binary/Base64;
//        byte[] valueDecoded = Base64.decodeBase64(data
//                .replace("#2","")
//                .replace(":<:","")
//                .replace("\\/\\/","")
//                .replace("//","")
////                .replace("Y2VyY2EudHJvdmEuc2FnZ2V6emE=","")
////                .replace("a2lub2NvdmVyLnc5OC5uamJo","")
////                .replace("c2ljYXJpby4yMi5tb3ZpZXM=","")
////                .replace("a2lub2NvdmVyLnc5OC5uamJo","")
//                .replace("ZjlTMnFFUWFabjV5MERDMjdq","")
//                .replace("TWFtNlFaZGRTUVhuUmJOS0Q1","")
//                .replace("aUhOZENrYW1rV3J2UXhjd0pV","")
//                .replace("c0RIVjdBWWtHWUJHbGJzMzYw","")
//
//                .replace("aEZaQkFUN2cxRGU4ZUxQbFBL","")
//                .getBytes());

//        Log.e("test", "decodeUppod done: " + new String(valueDecoded));
//        return new String(valueDecoded);
        return "";
    }

    public static String decodeFilmix(String data){
        if (data.startsWith("#")){
            data = data.substring(1);
            data = data.replaceAll("(.{3})", "%u0$1");
            return data;
        } else return "error";
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    public static void showKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    public static String urlDecode(String url) {
        return url.replace("(koshara)", Statics.KOSHARA_URL)
                .replace("(filmix)", Statics.FILMIX_URL)
                .replace("(kinofs)", Statics.KINOFS_URL)
                .replace("(rufilmtv)", Statics.RUFILMTV_URL)
                .replace("(topkino)", Statics.TOPKINO_URL)
                .replace("(myhit)", Statics.MYHIT_URL)
                .replace("(kinopub)", Statics.KINOPUB_URL)
                .replace("(kinolive)", Statics.KINOLIVE_URL)
                .replace("(kinodom)", Statics.KINODOM_URL)
                .replace("(fanserials)", Statics.FANSERIALS_URL)
                .replace("(animevost)", Statics.ANIMEVOST_URL)
                .replace("(anidub)", Statics.ANIDUB_URL)
                .replace("(coldfilm)", Statics.COLDFILM_URL)
                .replace("http://octopushome.tv", Statics.KOSHARA_URL)
                .replace("http://octopushome.su", Statics.KOSHARA_URL)
                .replace("http://koshara.net", Statics.KOSHARA_URL)
                .replace("http://koshara777.net", Statics.KOSHARA_URL)
                .replace("http://koshara777.org", Statics.KOSHARA_URL)
                .replace("https://filmix.pub", Statics.FILMIX_URL)
                .replace("https://filmix.vip", Statics.FILMIX_URL)
                .replace("https://filmix.site", Statics.FILMIX_URL)
                .replace("https://filmix.zone", Statics.FILMIX_URL);
    }
    public static String urlEncode(String url) {
        return url.replace(Statics.KOSHARA_URL, "(koshara)")
                .replace(Statics.FILMIX_URL, "(filmix)")
                .replace(Statics.KINOFS_URL, "(kinofs)")
                .replace(Statics.RUFILMTV_URL, "(rufilmtv)")
                .replace(Statics.TOPKINO_URL, "(topkino)")
                .replace(Statics.MYHIT_URL, "(myhit)")
                .replace(Statics.KINOPUB_URL, "(kinopub)")
                .replace(Statics.KINOLIVE_URL, "(kinolive)")
                .replace(Statics.KINODOM_URL, "(kinodom)")
                .replace(Statics.FANSERIALS_URL, "(fanserials)")
                .replace(Statics.ANIMEVOST_URL, "(animevost)")
                .replace(Statics.ANIDUB_URL, "(anidub)")
                .replace(Statics.COLDFILM_URL, "(coldfilm)")
                .replace(Statics.KOSHARA_URL, "http://octopushome.tv")
                .replace(Statics.KOSHARA_URL, "http://octopushome.su")
                .replace(Statics.KOSHARA_URL, "http://koshara.net")
                .replace(Statics.KOSHARA_URL, "http://koshara777.net")
                .replace(Statics.KOSHARA_URL, "http://koshara777.org")
                .replace(Statics.FILMIX_URL, "https://filmix.pub")
                .replace(Statics.FILMIX_URL, "https://filmix.vip")
                .replace(Statics.FILMIX_URL, "https://filmix.site")
                .replace(Statics.FILMIX_URL, "https://filmix.zone");
    }

    public static boolean checkFilmixHist(String url, String trans, String s, String e) {
        if (Statics.FILMIX_HIST.contains("{\"id\":") &&
                Statics.FILMIX_HIST.contains(url.replace("/","\\/"))) {
            for (String r : Statics.FILMIX_HIST.split("\\{\"id\":")) {
                if (r.contains(url.replace("/","\\/"))) {
                    r = unicodeToString(r);
                    if (s.isEmpty() && e.isEmpty()) {
                        if (r.contains(trans))
                            return true;
                    } else if (e.isEmpty()) {
                        if (r.contains(trans) && r.contains("\"season\":" + s))
                            return true;
                    } else {
                        if (r.contains(trans) && r.contains("\"season\":" + s) && r.contains("\"episode\":" + e))
                            return true;
                    }
                }
            }
        }
        return false;
    }

    public static int boolToVisible(boolean v) {
        return v? View.VISIBLE : View.GONE;
    }

    public static void setDomen(String domen, Context context){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        if (domen.contains("koshara"))
            editor.putString("koshara_furl", domen.split("koshara=\\[")[1].split("\\]")[0]);
        if (domen.contains("amcet"))
            editor.putString("amcet_furl", domen.split("amcet=\\[")[1].split("\\]")[0]);
        if (domen.contains("kinofs"))
            editor.putString("kinofs_furl", domen.split("kinofs=\\[")[1].split("\\]")[0]);
        if (domen.contains("kinoxa"))
            editor.putString("kinoxa_furl", domen.split("kinoxa=\\[")[1].split("\\]")[0]);
        if (domen.contains("rufilmtv"))
            editor.putString("rufilmtv_furl", domen.split("rufilmtv=\\[")[1].split("\\]")[0]);
        if (domen.contains("topkino"))
            editor.putString("topkino_furl", domen.split("topkino=\\[")[1].split("\\]")[0]);
        if (domen.contains("my-hit"))
            editor.putString("myhit_furl", domen.split("my-hit=\\[")[1].split("\\]")[0]);
        if (domen.contains("kinopub"))
            editor.putString("kinopub_furl", domen.split("kinopub=\\[")[1].split("\\]")[0]);

        if (domen.contains("animevost"))
            editor.putString("animevost_furl", domen.split("animevost=\\[")[1].split("\\]")[0]);
        if (domen.contains("coldfilm"))
            editor.putString("coldfilm_furl", domen.split("coldfilm=\\[")[1].split("\\]")[0]);
        if (domen.contains("fanserials"))
            editor.putString("fanserials_furl", domen.split("fanserials=\\[")[1].split("\\]")[0]);

        if (domen.contains("anidub"))
            editor.putString("anidub_furl", domen.split("anidub=\\[")[1].split("\\]")[0]);
        if (domen.contains("anidb_tr"))
            editor.putString("anidub_tr_furl", domen.split("anidb_tr=\\[")[1].split("\\]")[0]);
        if (domen.contains("kinosha"))
            editor.putString("kinosha_furl", domen.split("kinosha=\\[")[1].split("\\]")[0]);
        if (domen.contains("filmix"))
            editor.putString("filmix_furl", domen.split("filmix=\\[")[1].split("\\]")[0]);
        if (domen.contains("movieshd"))
            editor.putString("movieshd_furl", domen.split("movieshd=\\[")[1].split("\\]")[0]);
        if (domen.contains("kinohd"))
            editor.putString("kinohd_furl", domen.split("kinohd=\\[")[1].split("\\]")[0]);
        if (domen.contains("kinolive"))
            editor.putString("kinolive_furl", domen.split("kinolive=\\[")[1].split("\\]")[0]);
        if (domen.contains("moonwalk"))
            editor.putString("moonwalk_furl", domen.split("moonwalk=\\[")[1].split("\\]")[0]);
        if (domen.contains("kinodom"))
            editor.putString("kinodom_furl", domen.split("kinodom=\\[")[1].split("\\]")[0]);
        if (domen.contains("zombiefilm"))
            editor.putString("zombiefilm_furl", domen.split("zombiefilm=\\[")[1].split("\\]")[0]);

        if (domen.contains("freerutor"))
            editor.putString("freerutor_furl", domen.split("freerutor=\\[")[1].split("\\]")[0]);
        if (domen.contains("trurutor"))
            editor.putString("rutor_furl", domen.split("trurutor=\\[")[1].split("\\]")[0]);
        if (domen.contains("nnm"))
            editor.putString("nnm_furl", domen.split("nnm=\\[")[1].split("\\]")[0]);
        if (domen.contains("zooqle"))
            editor.putString("zooqle_furl", domen.split("zooqle=\\[")[1].split("\\]")[0]);
        if (domen.contains("bitru"))
            editor.putString("bitru_furl", domen.split("bitru=\\[")[1].split("\\]")[0]);
        if (domen.contains("ba3a"))
            editor.putString("ba3a_furl", domen.split("ba3a=\\[")[1].split("\\]")[0]);
        if (domen.contains("tparser"))
            editor.putString("tparser_furl", domen.split("tparser=\\[")[1].split("\\]")[0]);
        if (domen.contains("megapeer"))
            editor.putString("megapeer_furl", domen.split("megapeer=\\[")[1].split("\\]")[0]);
        if (domen.contains("piratbit"))
            editor.putString("piratbit_furl", domen.split("piratbit=\\[")[1].split("\\]")[0]);
        if (domen.contains("kinozal"))
            editor.putString("kinozal_furl", domen.split("kinozal=\\[")[1].split("\\]")[0]);
        if (domen.contains("hurtom"))
            editor.putString("hurtom_furl", domen.split("hurtom=\\[")[1].split("\\]")[0]);
        if (domen.contains("torlook"))
            editor.putString("torlook_furl", domen.split("torlook=\\[")[1].split("\\]")[0]);
        if (domen.contains("rutracker"))
            editor.putString("rutracker_furl", domen.split("rutracker=\\[")[1].split("\\]")[0]);
        if (domen.contains("greentea_tr"))
            editor.putString("greentea_tr_furl", domen.split("greentea_tr=\\[")[1].split("\\]")[0]);
        editor.apply();

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        //catalog
        Statics.KOSHARA_URL = preference.getString("koshara_furl", Statics.KOSHARA_URL);
        Statics.AMCET_URL = preference.getString("amcet_furl", Statics.AMCET_URL);
        Statics.KINOFS_URL = preference.getString("kinofs_furl", Statics.KINOFS_URL);
        Statics.KINOXA_URL = preference.getString("kinoxa_furl", Statics.KINOXA_URL);
        Statics.RUFILMTV_URL = preference.getString("rufilmtv_furl", Statics.RUFILMTV_URL);
        Statics.TOPKINO_URL = preference.getString("topki_furl", Statics.TOPKINO_URL);
        Statics.MYHIT_URL = preference.getString("myhit_furl", Statics.MYHIT_URL);
        Statics.KINOPUB_URL = preference.getString("kinopub_furl", Statics.KINOPUB_URL);
        //+catalog
        Statics.ANIMEVOST_URL = preference.getString("animevost_furl", Statics.ANIMEVOST_URL);
        Statics.COLDFILM_URL = preference.getString("coldfilm_furl", Statics.COLDFILM_URL);
        Statics.FANSERIALS_URL = preference.getString("fanserials_furl", Statics.FANSERIALS_URL);
        //video
        Statics.KINOSHA_URL = preference.getString("kinosha_furl", Statics.KINOSHA_URL);
        Statics.ANIDUB_URL = preference.getString("anidub_furl", Statics.ANIDUB_URL);
        Statics.MOONWALK_URL = preference.getString("moonwalk_furl", Statics.MOONWALK_URL);
        Statics.FILMIX_URL = preference.getString("filmix_furl", Statics.FILMIX_URL);
        Statics.MOVIESHD_URL = preference.getString("movieshd_furl", Statics.MOVIESHD_URL);
        Statics.KINOHD_URL = preference.getString("kinohd_furl", Statics.KINOHD_URL);
        Statics.KINOLIVE_URL = preference.getString("kinolive_furl", Statics.KINOLIVE_URL);
        Statics.KINODOM_URL = preference.getString("kinodom_furl", Statics.KINODOM_URL);
        Statics.ZOMBIEFILM_URL = preference.getString("zombiefilm_furl", Statics.ZOMBIEFILM_URL);
        //torrent
        Statics.ANIDUB_TR_URL = preference.getString("anidub_tr_furl", Statics.ANIDUB_TR_URL);
        Statics.GREENTEA_TR_URL = preference.getString("greentea_tr_furl", Statics.GREENTEA_TR_URL);
        Statics.RUTRACKER_URL = preference.getString("rutracker_furl", Statics.RUTRACKER_URL);
        Statics.TPARSER_URL = preference.getString("tparser_furl", Statics.TPARSER_URL);
        Statics.RUTOR_URL = preference.getString("rutor_furl", Statics.RUTOR_URL);
        Statics.NNM_URL = preference.getString("nnm_furl", Statics.NNM_URL);
        Statics.FREERUTOR_URL = preference.getString("freerutor_furl", Statics.FREERUTOR_URL);
        Statics.BITRU_URL = preference.getString("bitru_furl", Statics.BITRU_URL);
        Statics.BA3A_URL = preference.getString("ba3a_furl", Statics.BA3A_URL);
        Statics.TPARSER_URL = preference.getString("tparser_furl", Statics.TPARSER_URL);
        Statics.MEGAPEER_URL = preference.getString("megapeer_furl", Statics.MEGAPEER_URL);
        Statics.KINOZAL_URL = preference.getString("kinozal_furl", Statics.KINOZAL_URL);
        Statics.HURTOM_URL = preference.getString("hurtom_furl", Statics.HURTOM_URL);
        Statics.TORLOOK_URL = preference.getString("torlook_furl", Statics.TORLOOK_URL);
        Statics.PIRATBIT_URL = preference.getString("piratbit_furl", Statics.PIRATBIT_URL);
    }

    public boolean trueTitle(String t_m, String t_n) {
        boolean tit = ("." + t_m + ".").contains("." + t_n + ".") ||
                ("." + t_m + ",").contains("." + t_n + ",") ||
                ("." + t_m + " ").contains("." + t_n + " ") ||
                ("." + t_m + ":").contains("." + t_n + ":") ||
                ("." + t_m + ";").contains("." + t_n + ";") ||
                ("(" + t_m + ")").contains("(" + t_n + ")") ||
                ("." + t_m + " /").contains("." + t_n + " /") ||
                ("/ " + t_m + ".").contains("/ " + t_n + ".") ||
                ("." + t_n + ".").contains("." + t_m + ".") ||
                ("." + t_n + ",").contains("." + t_m + ",") ||
                ("." + t_n + " ").contains("." + t_m + " ") ||
                ("." + t_n + ":").contains("." + t_m + ":") ||
                ("." + t_n + ";").contains("." + t_m + ";") ||
                ("(" + t_n + ")").contains("(" + t_m + ")") ||
                ("." + t_n + " /").contains("." + t_m + " /") ||
                ("/ " + t_n + ".").contains("/ " + t_m + ".");
        return tit;
    }

    public String replaceTitle(String t) {
        if (t.contains("Гавайи 5.0"))
            return t.replace("5.0", "5-0");
        else if (t.contains("Агенты Щ.И.Т."))
            return t.replace("Агенты Щ.И.Т.", "Агенты «Щ.И.Т.»");
        else if (t.contains("Агенты ЩИТ"))
            return t.replace("Агенты ЩИТ", "Агенты «Щ.И.Т.»");
        else if (t.contains("Боруто"))
            return "Боруто";
        else if (t.contains("Древние") && t.contains("/"))
            return "Древние";
        else if (t.contains("Банановая рыба"))
            return "Рыбка-бананка";
        else return t;
    }
}
