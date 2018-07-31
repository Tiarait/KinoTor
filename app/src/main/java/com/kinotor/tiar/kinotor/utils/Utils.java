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

import com.kinotor.tiar.kinotor.items.Statics;

import static android.content.Context.UI_MODE_SERVICE;

/**
 * Created by Tiar on 04.2018.
 */
public class Utils {

    public boolean isOnline(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return nInfo != null && nInfo.isConnected();
    }

    public boolean isTablet(Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(UI_MODE_SERVICE);
        if (uiModeManager != null)
            return  (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION ||
                    ((context.getResources().getConfiguration().screenLayout
                            & Configuration.SCREENLAYOUT_SIZE_MASK)
                            >= Configuration.SCREENLAYOUT_SIZE_LARGE));
        else
            return ((context.getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_LARGE)
                    || context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEVISION)
                    || context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK);
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
                preferences.getString("grid_count", "0").equals("0"))
            return r;
        else if (preferences.getString("grid_catalog", "2").equals("1")) {
            return 1;
        }
        else return Integer.parseInt(preferences.getString("grid_count", "0"));
    }

    public float calculateScale(Context context, int count) {
        if (PreferenceManager.getDefaultSharedPreferences(context).getString("grid_count", "0").equals("0") ||
                count == calcGrid(context)) {
            return 1;
        } else {
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

    public String unicodeToString(String uni){
        StringBuilder tt = new StringBuilder();
        String[] parts = uni.replace("\\", "/").split("/");
        for(String x:parts){
            if (x.startsWith("u")) {
                String k = "";
                if (x.trim().contains(" ")){
                    k = x.split(" ")[1];
                    x = x.split(" ")[0].trim();
                } else if (x.trim().contains("-")){
                    k = "-";
                    x = x.replace("-", "");
                } else if (x.trim().contains(":")){
                    k = ":";
                    x = x.replace(":", "");
                } else if (x.trim().contains(".")){
                    k = ".";
                    x = x.replace(".", "");
                } else if (x.trim().contains(",")){
                    k = ":";
                    x = x.replace(",", "");
                }  else if (x.trim().contains("'")){
                    k = "'";
                    x = x.replace("'", "");
                }
                String s = x.replace("u", "").trim();
                try {
                    int hexVal = Integer.parseInt(s, 16);
                    tt.append((char) hexVal);
                } catch (Exception e){
                    tt.append(s);
                }

                if (!k.isEmpty()) {
                    tt.append(" ");
                    tt.append(k);
                }
            } else tt.append(" ");
            if (x.contains(" "))
                tt.append(" ");
        }
        return tt.toString().replace(" -", "-").trim();
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
        else return t;
    }
}
