package com.kinotor.tiar.kinotor.ui;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kinotor.tiar.kinotor.BuildConfig;
import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemCatalogUrls;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.ui.dialogs.DialogSearch;
import com.kinotor.tiar.kinotor.ui.fragments.MainFragmentTv;
import com.kinotor.tiar.kinotor.updater.Update;
import com.kinotor.tiar.kinotor.utils.DBHelper;
import com.kinotor.tiar.kinotor.utils.Utils;

import java.util.ArrayList;

public class MainActivityTv extends AppCompatActivity {
    private ItemCatalogUrls catalogUrls;
    private SharedPreferences preference;
    private static String subtitle = "Фильмы", catalog = "filmix";
    private boolean coldfilm, animevost, anidub, kinodom, fanserials, exit, side_menu, pro;

    private static final String TAG = "MainActivity";

    private int colorStatus = R.color.colorPrimaryLight;
    private int colorBg = R.color.colorPrimaryLight;
    private int colorBgImg = R.drawable.gradient_darkgone_dark;
    private int colorText = R.color.colorWhite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        switch (preference.getString("theme_list", "gray")) {
            case "black":
                colorBg = R.color.colorBlack;
                colorStatus = R.color.colorBlack;
                colorBgImg = R.drawable.gradient_gone_black;
                colorText = R.color.colorWhite;
                break;
            case "white":
                colorBg = R.color.colorWhite;
                colorStatus = R.color.colorBlack;
                colorBgImg = R.drawable.gradient_gone_white;
                colorText = R.color.colorBlack;
                break;
        }

        catalogUrls = new ItemCatalogUrls();

        String curr_ver = BuildConfig.VERSION_NAME;
        if (curr_ver.contains("b") && preference.getBoolean("first_b", true)) {
            SharedPreferences.Editor editor = preference.edit();
            editor.putBoolean("first_b", false);
            editor.apply();

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setMessage("Это бетта версия приложения.\n" +
                    "Она обновляеться отдельно и может быть крайне нестабильной.\n" +
                    "Скачать стабильную версию вы можете на сайте приложения или 4pda.")
                    .setPositiveButton("Ok", (dialog, id) -> dialog.dismiss())
                    .create().show();
        }
        if (preference.getBoolean("fullscreen", false)) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_main_tv);

        if (Statics.firsStart){
            boolean upd_in_start = preference.getBoolean("auto_update", true);
            boolean dom_in_start = preference.getBoolean("domen_auto_check", true);
            if (dom_in_start && upd_in_start) {
                Update update = new Update(this, "version domen", location -> {
                    Utils.setDomen(location, this);
                });
                update.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (dom_in_start) {
                Update update = new Update(this, "domen", location -> {
                    Utils.setDomen(location, this);
                });
                update.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (upd_in_start) {
                Update updator = new Update(this, "version", null);
                updator.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            Statics.firsStart = false;
        }
        ConstraintLayout content = findViewById(R.id.content);
        if (preference.getString("theme_list", "gray").equals("gray"))
            content.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_angle_light));
        else content.setBackgroundColor(getResources().getColor(colorBg));


        ImageView search = findViewById(R.id.img_action_search);
        ImageView settings = findViewById(R.id.img_action_settings);
        ImageView more = findViewById(R.id.img_action_more);
        ScrollView scroll = findViewById(R.id.scrol);

        search.setFocusable(true);
        settings.setFocusable(true);

        search.setOnClickListener(view -> {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        });
        search.setOnFocusChangeListener((view, b) -> {
            scroll.scrollTo(0,0);
            if (b) {
                search.setScaleX((float) 1.3);
                search.setScaleY((float) 1.3);
            } else {
                search.setScaleX((float) 1);
                search.setScaleY((float) 1);
            }
        });
        settings.setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
        settings.setOnFocusChangeListener((view, b) -> {
            scroll.scrollTo(0,0);
            if (b) {
                settings.setScaleX((float) 1.3);
                settings.setScaleY((float) 1.3);
            } else {
                settings.setScaleX((float) 1);
                settings.setScaleY((float) 1);
            }
        });
        more.setOnClickListener(view -> {
            ArrayList<String> ctg = new ArrayList<>();
            ctg.add("Каталог");
            if (pro)
                ctg.add("Поиск видео");
            ctg.add("Поиск по актерам");
            final String[] list = ctg.toArray(new String[ctg.size()]);

            AlertDialog.Builder b = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            b.setItems(list, (dialogInterface, k) -> {
                switch (list[k]) {
                    case "Каталог":
                        String[] foo_array = this.getResources().getStringArray(R.array.pref_list_base);
                        AlertDialog.Builder c = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
                        c.setTitle("Выберите каталог").setItems(foo_array, (dialog, i) ->
                                onSelectCatalog(foo_array[i])).create().show();
                        break;
                    case "Поиск видео":
                        Statics.itemLast = null;
                        Intent intent = new Intent(this, DetailActivityVid.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("search", true);
                        startActivity(intent);
                        break;
                    case "Поиск по актерам":
                        DialogFragment searchA = new DialogSearch();
                        searchA.show(this.getFragmentManager(), subtitle);
                        break;
                    default:
                        dialogInterface.dismiss();
                        break;
                }
            }).create().show();
        });
        more.setOnFocusChangeListener((view, b) -> {
            scroll.scrollTo(0,0);
            if (b) {
                more.setScaleX((float) 1.3);
                more.setScaleY((float) 1.3);
            } else {
                more.setScaleX((float) 1);
                more.setScaleY((float) 1);
            }
        });
        search.setNextFocusDownId(R.id.category_films);
        settings.setNextFocusDownId(R.id.category_films);
        search.setNextFocusLeftId(R.id.category_films);
        more.setNextFocusRightId(R.id.category_films);
        more.setNextFocusDownId(R.id.category_films);

        setCurURL();
        updPref();
        setupFrames();

        TextView subtitle = findViewById(R.id.linear_toolbar_subtitle);
        subtitle.setText(Statics.CATALOG);
    }

    public void onSelectCatalog(String item) {
        SharedPreferences.Editor editor = preference.edit();
        switch (item.toLowerCase()) {
            case "amcet": editor.putString("catalog", "amcet");
                break;
            case "koshara": editor.putString("catalog", "koshara");
                break;
            case "filmix": editor.putString("catalog", "filmix");
                break;
            case "kinoxa": editor.putString("catalog", "kinoxa");
                break;
            case "kinofs": editor.putString("catalog", "kinofs");
                break;
            case "rufilmtv": editor.putString("catalog", "rufilmtv");
                break;
            case "topkino": editor.putString("catalog", "topkino");
                break;
            case "my-hit": editor.putString("catalog", "my-hit");
                break;
            default: editor.putString("catalog", "my-hit");
                break;
        }
        editor.apply();
        catalog = preference.getString("catalog", "filmix");

        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    private void updPref(){
        coldfilm = preference.getBoolean("coldfilm_menu", true);
        animevost = preference.getBoolean("animevost_menu", true);
        anidub = preference.getBoolean("anidub_menu", true);
        fanserials = preference.getBoolean("fanserials_menu", true);
        kinodom = preference.getBoolean("kinodom_menu", true);
        exit = preference.getBoolean("exit", true);
        side_menu = preference.getBoolean("side_menu", true);
        catalog = preference.getString("catalog", "filmix");
        pro = preference.getBoolean("pro_version", false);

        //catalog
        Statics.KOSHARA_URL = preference.getString("koshara_furl", Statics.KOSHARA_URL);
        Statics.AMCET_URL = preference.getString("amcet_furl", Statics.AMCET_URL);
        Statics.KINOFS_URL = preference.getString("kinofs_furl", Statics.KINOFS_URL);
        Statics.KINOXA_URL = preference.getString("kinoxa_furl", Statics.KINOXA_URL);
        Statics.RUFILMTV_URL = preference.getString("rufilmtv_furl", Statics.RUFILMTV_URL);
        Statics.MYHIT_URL = preference.getString("myhit_furl", Statics.MYHIT_URL);
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
        //torrent
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
        //other
        Statics.hideTs = preference.getBoolean("hide_ts", false);
        Statics.FILMIX_COOCKIE = preference.getString("filmix_coockie", Statics.FILMIX_COOCKIE);
        Statics.FILMIX_ACC = preference.getString("filmix_acc", Statics.FILMIX_ACC);
        Statics.FILMIX_PRO = preference.getBoolean("filmix_pro", false);
//        Statics.KINODOM_COOCKIE = preference.getString("kinodom_coockie", Statics.KINODOM_COOCKIE);
        Statics.KINODOM_ACC = preference.getString("kinodom_acc", Statics.KINODOM_ACC);
        Statics.KINOZAL_COOCKIE = preference.getString("kinozal_coockie", Statics.KINOZAL_COOCKIE);
        Statics.KINOZAL_ACC = preference.getString("kinozal_acc", Statics.KINOZAL_ACC);
        Statics.HURTOM_COOCKIE = preference.getString("hurtom_coockie", Statics.HURTOM_COOCKIE);
        Statics.HURTOM_PASS = preference.getString("hurtom_pass", Statics.HURTOM_PASS);
        Statics.HURTOM_ACC = preference.getString("hurtom_acc", Statics.HURTOM_ACC);
    }

    private void setCurURL(){
        switch (subtitle) {
            case "Coldfilm":
                ItemMain.cur_url = Statics.COLDFILM_URL + "/news/";
                Statics.CATALOG = "coldfilm";
                break;
            case "AnimeVost":
                ItemMain.cur_url = Statics.ANIMEVOST_URL + "/";
                Statics.CATALOG = "animevost";
                break;
            case "Anidub":
                ItemMain.cur_url = Statics.ANIDUB_URL + "/";
                Statics.CATALOG = "anidub";
                break;
            case "FanSerials":
                ItemMain.cur_url = Statics.FANSERIALS_URL + "/new/";
                Statics.CATALOG = "fanserials";
                break;
            default:
                Statics.CATALOG = catalog;
                if (subtitle.equals("Фильмы") || getTitle().equals("Фильмы"))
                    ItemMain.cur_url = catalogUrls.film(catalog);
                if (subtitle.equals("Сериалы") || getTitle().equals("Сериалы"))
                    ItemMain.cur_url = catalogUrls.serial(catalog);
                if (subtitle.equals("Мультфильмы") || getTitle().equals("Мультфильмы"))
                    ItemMain.cur_url = catalogUrls.mult(catalog);
                if (subtitle.equals("Мультсериалы") || getTitle().equals("Мультсериалы"))
                    ItemMain.cur_url = catalogUrls.multserial(catalog);
                if (subtitle.equals("Аниме") || getTitle().equals("Аниме"))
                    ItemMain.cur_url = catalogUrls.anime(catalog);
                if (subtitle.equals("ТВ Передачи") || getTitle().equals("ТВ Передачи"))
                    ItemMain.cur_url = catalogUrls.tv(catalog);
                break;
        }
    }


    private void setupFrames() {
        LinearLayout contentLinear = findViewById(R.id.content_linear);
        DBHelper dbHelper = new DBHelper(this);
        //------------------------------------------------------------------------------------------
        FrameLayout frameFilm = new FrameLayout(this);
        frameFilm.setId(R.id.frame_films);
        frameFilm.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        contentLinear.addView(frameFilm);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_films, new MainFragmentTv().newInstance(catalogUrls.film(catalog), "Фильмы", Statics.CATALOG))
                .commit();
        //------------------------------------------------------------------------------------------
        FrameLayout frameSerial = new FrameLayout(this);
        frameSerial.setId(R.id.frame_serials);
        frameSerial.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        contentLinear.addView(frameSerial);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_serials, new MainFragmentTv().newInstance(catalogUrls.serial(catalog), "Сериалы", Statics.CATALOG))
                .commit();
        //------------------------------------------------------------------------------------------
        FrameLayout frameMult = new FrameLayout(this);
        frameMult.setId(R.id.frame_mults);
        frameMult.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        contentLinear.addView(frameMult);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frame_mults, new MainFragmentTv().newInstance(catalogUrls.mult(catalog), "Мультфильмы", Statics.CATALOG))
                .commit();
        //------------------------------------------------------------------------------------------
        if (catalog.equals("filmix") || catalog.equals("my-hit") || catalog.equals("kinofs") || catalog.equals("topkino")) {
            FrameLayout frameAnime = new FrameLayout(this);
            frameAnime.setId(R.id.frame_anime);
            frameAnime.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            contentLinear.addView(frameAnime);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_anime, new MainFragmentTv().newInstance(catalogUrls.multserial(catalog), "Аниме", Statics.CATALOG))
                    .commit();
        }
        //------------------------------------------------------------------------------------------
        if (catalog.equals("kinofs") || catalog.equals("filmix") || catalog.equals("rufilmtv")) {
            FrameLayout frameTv = new FrameLayout(this);
            frameTv.setId(R.id.frame_tv);
            frameTv.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            contentLinear.addView(frameTv);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_tv, new MainFragmentTv().newInstance(catalogUrls.tv(catalog), "ТВ Передачи", Statics.CATALOG))
                    .commit();
        }
        //------------------------------------------------------------------------------------------
        if (dbHelper.getDbItems("favor") != null)
        if (dbHelper.getDbItems("favor").size() > 0) {
            FrameLayout frameFavor = new FrameLayout(this);
            frameFavor.setId(R.id.frame_favor);
            frameFavor.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            contentLinear.addView(frameFavor);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_favor, new MainFragmentTv().newInstance("http:// /", "Избранное", "favor"))
                    .commit();
        }
        //------------------------------------------------------------------------------------------
        if (dbHelper.getDbItems("history") != null)
        if (dbHelper.getDbItems("history").size() > 0) {
            FrameLayout frameHistory = new FrameLayout(this);
            frameHistory.setId(R.id.frame_history);
            frameHistory.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            contentLinear.addView(frameHistory);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_history, new MainFragmentTv().newInstance("http:// /", "История", "history"))
                    .commit();
        }
        //------------------------------------------------------------------------------------------
        if (animevost) {
            FrameLayout frameAnimevost = new FrameLayout(this);
            frameAnimevost.setId(R.id.frame_animevost);
            frameAnimevost.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            contentLinear.addView(frameAnimevost);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_animevost, new MainFragmentTv().newInstance(Statics.ANIMEVOST_URL + "/", "ANIMEVOST", "animevost"))
                    .commit();
        }
        //------------------------------------------------------------------------------------------
        if (anidub) {
            FrameLayout frameAnidub = new FrameLayout(this);
            frameAnidub.setId(R.id.frame_anidub);
            frameAnidub.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            contentLinear.addView(frameAnidub);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_anidub, new MainFragmentTv().newInstance(Statics.ANIDUB_URL + "/", "ANIDUB", "anidub"))
                    .commit();
        }
        //------------------------------------------------------------------------------------------
        if (coldfilm) {
            FrameLayout frameColdfilm = new FrameLayout(this);
            frameColdfilm.setId(R.id.frame_coldfim);
            frameColdfilm.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            contentLinear.addView(frameColdfilm);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_coldfim, new MainFragmentTv().newInstance(Statics.COLDFILM_URL + "/news/", "COLDFILM", "coldfilm"))
                    .commit();
        }
        //------------------------------------------------------------------------------------------
        if (fanserials) {
            FrameLayout frameFanserials = new FrameLayout(this);
            frameFanserials.setId(R.id.frame_fanserials);
            frameFanserials.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            contentLinear.addView(frameFanserials);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_fanserials, new MainFragmentTv().newInstance(Statics.FANSERIALS_URL + "/news/", "FANSERIALS", "fanserials"))
                    .commit();
        }
        //------------------------------------------------------------------------------------------
        if (kinodom) {
            FrameLayout frameColdfilm = new FrameLayout(this);
            frameColdfilm.setId(R.id.frame_kinodom);
            frameColdfilm.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            contentLinear.addView(frameColdfilm);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_kinodom, new MainFragmentTv().newInstance(Statics.KINODOM_URL + "/", "KINODOM", "kinodom"))
                    .commit();
        }
        //------------------------------------------------------------------------------------------
    }
}
