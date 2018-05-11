package com.kinotor.tiar.kinotor.utils;

import android.app.Activity;
import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

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

        public int calculateGrid(Context context, int width) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("side_menu", true)
                && context.getResources().getConfiguration().orientation == 2 && isTablet(context))
            dpWidth -= 230;
        if (PreferenceManager.getDefaultSharedPreferences(context).getString("grid_catalog", "2").equals("2"))
            return (int) (dpWidth / width);
        else return 1;
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
        else return t;
    }
}
