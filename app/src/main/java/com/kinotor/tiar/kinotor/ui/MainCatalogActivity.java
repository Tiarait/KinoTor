package com.kinotor.tiar.kinotor.ui;


import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinotor.tiar.kinotor.BuildConfig;
import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemCatalogUrls;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.items.ItemSearch;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.parser.catalog.ParserKinopubLogin;
import com.kinotor.tiar.kinotor.parser.catalog.filmix.ParserFilmixHistUpd;
import com.kinotor.tiar.kinotor.parser.catalog.search.SearchAnidub;
import com.kinotor.tiar.kinotor.parser.catalog.search.SearchAnimevost;
import com.kinotor.tiar.kinotor.parser.catalog.search.SearchFanserials;
import com.kinotor.tiar.kinotor.parser.catalog.search.SearchFilmix;
import com.kinotor.tiar.kinotor.parser.catalog.search.SearchKinofs;
import com.kinotor.tiar.kinotor.parser.catalog.search.SearchKinolive;
import com.kinotor.tiar.kinotor.parser.catalog.search.SearchKinopub;
import com.kinotor.tiar.kinotor.parser.catalog.search.SearchKoshara;
import com.kinotor.tiar.kinotor.parser.catalog.search.SearchMyhit;
import com.kinotor.tiar.kinotor.parser.catalog.search.SearchTopkino;
import com.kinotor.tiar.kinotor.ui.dialogs.DialogSearch;
import com.kinotor.tiar.kinotor.ui.dialogs.DialogSort;
import com.kinotor.tiar.kinotor.ui.fragments.MainCatalogFragment;
import com.kinotor.tiar.kinotor.updater.Update;
import com.kinotor.tiar.kinotor.utils.SignatureUtil;
import com.kinotor.tiar.kinotor.utils.Utils;
import com.kinotor.tiar.kinotor.utils.adapters.AdapterSearch;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.picasso.Picasso;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.kinotor.tiar.kinotor.utils.Utils.hideKeyboard;

public class MainCatalogActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private static String subtitle = "Фильмы", catalog;
    boolean doubleBackToExitPressedOnce = false;
    private Utils utils;
    private ItemCatalogUrls catalogUrls;
    private boolean coldfilm, animevost, anidub, kinodom, fanserials, exit, side_menu, pro, side_ezist, side_exist, side_ecist;
    private SharedPreferences preference;
    private RecyclerView recyclerView;
    private Set<String> prefCategory;
//    private MaterialSearchView searchView;

    private static final String TAG = "MainActivity";

    private int colorStatus = R.color.colorPrimaryLight;
    private int colorBg = R.color.colorPrimaryLight;
    private int colorBgImg = R.drawable.gradient_darkgone_dark;
    private int colorText = R.color.colorWhite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setTheme(R.style.AppWhiteTheme);
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

        prefCategory = preference.getStringSet("display_category",
                new HashSet<>(Arrays.asList(getResources().getStringArray(R.array.pref_list_cat_content))));

        catalogUrls = new ItemCatalogUrls();
        utils = new Utils();

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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.activity_main);
        updPref();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            this.getWindow().setStatusBarColor(getResources().getColor(colorStatus));
        } else {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setTintColor(getResources().getColor(colorStatus));

            tintManager.setNavigationBarTintResource(getResources().getColor(colorStatus));
        }

        if (Statics.firsStart) {
            if (preference.getString("start_category", "Фильмы").equals("Избранное")) {
                subtitle = "Фильмы";
                Intent intent = new Intent(getBaseContext(), BDActivity.class);
                intent.putExtra("Status", "favor");
                startActivity(intent);
            } else if (preference.getString("start_category", "Фильмы").equals("История")) {
                subtitle = "Фильмы";
                Intent intent = new Intent(getBaseContext(), BDActivity.class);
                intent.putExtra("Status", "history");
                startActivity(intent);
            } else subtitle = preference.getString("start_category", "Фильмы");
        } else if (preference.getString("start_category", "Фильмы").equals("Избранное") ||
                preference.getString("start_category", "Фильмы").equals("История"))
            subtitle = "Фильмы";
        else
            subtitle = preference.getString("start_category", "Фильмы");
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
        SharedPreferences.Editor editor = preference.edit();
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);

//        if (SignatureUtil.isGetHooked())
//            finish();

        if (!preference.getBoolean("flag_telega", false)) {
            editor.putBoolean("flag_telega", true);
            editor.putInt("flag_telega_day", day);
            editor.apply();
            showTelega();
        }
        switch (day) {
            case Calendar.SUNDAY:
                if (preference.getInt("flag_telega_day", Calendar.SUNDAY) != Calendar.SUNDAY) {
                    editor.putInt("flag_telega_day", day);
                    editor.apply();
                    showTelega();
                }
                break;
            case Calendar.WEDNESDAY:
                if (preference.getInt("flag_telega_day", Calendar.SUNDAY) != Calendar.WEDNESDAY) {
                    editor.putInt("flag_telega_day", day);
                    editor.apply();
                    showTelega();
                }
                break;
        }
        refreshBar();
        setCurURL();
        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            String query = getIntent().getStringExtra(SearchManager.QUERY);
            getIntent().removeExtra(SearchManager.QUERY);
            Log.e(TAG, "onCreate: "+query);
        }
        CoordinatorLayout content = findViewById(R.id.content);
        content.setBackgroundColor(getResources().getColor(colorBg));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("Type") != null && bundle.getString("Query") != null) {
                switch (Objects.requireNonNull(bundle.getString("Type"))) {
                    case "actor":
                        searchActor(bundle.getString("Query"));
                        break;
                    case "sort":
                        OnPage(bundle.getString("Query"), "Сортировка");
                        break;
                    default:
                        OnPage(ItemMain.cur_url, subtitle);
                        break;
                }
            } else OnPage(ItemMain.cur_url, subtitle);
        } else OnPage(ItemMain.cur_url, subtitle);

        if (getIntent() != null && getIntent().getData() != null) {
            Uri uri = getIntent().getData();
            int id;
            try {
                id = Integer.valueOf(uri.getLastPathSegment());
            } catch (Exception e) {
                e.printStackTrace();
                id = 0;
            }
            String url;
            if (Statics.list.size() > 0) {
                url = Statics.list.get(id);
            } else url = getIntent().getDataString();

            getIntent().setData(null);
            Intent intent;
            if (preference.getBoolean("tv_activity_detail", true)) {
                intent = new Intent(this, DetailActivityTv.class);
                intent.putExtra("Url", url);
            } else {
                intent = new Intent(this, DetailActivity.class);
                intent.putExtra("Url", url);
            }
            startActivity(intent);
            finish();
        }
    }

    private void checkFilters() {
        if (!preference.getString("last_catalog","filmix").equals(catalog)) {
            SharedPreferences.Editor editor = preference.edit();
            editor.putString("last_catalog", catalog);
            editor.putString("filter_category", "");
            editor.putString("filter_country", "");
            editor.putString("filter_genre", "");
            editor.putString("filter_year_st", "");
            editor.putString("filter_year_en", "");
            editor.putString("filter_kp_st", "");
            editor.putString("filter_kp_en", "");
            editor.putString("filter_sort", "");
            editor.apply();
        }
    }

    private void showTelega() {
//        PackageManager packageManager = getPackageManager();
//        if (utils.isPackage("org.thunderdog.challegram", packageManager) ||
//                utils.isPackage("org.telegram.messenger", packageManager)) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
//            LayoutInflater inflater = this.getLayoutInflater();
//
//            View dialog_layout = inflater.inflate(R.layout.dialog_info, null);
//            final Button channel = dialog_layout.findViewById(R.id.channel);
//            final Button chat = dialog_layout.findViewById(R.id.chat);
//            channel.setFocusable(true);
//            chat.setFocusable(true);
//            channel.setOnClickListener(view -> {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://t.me/Kino_Tor"));
//                startActivity(intent);
//            });
//            chat.setOnClickListener(view -> {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse("https://t.me/KinoTor"));
//                startActivity(intent);
//            });
//            builder.setView(dialog_layout)
//                    .setNegativeButton("Закрыть", (dialogInterface, i) -> dialogInterface.dismiss())
//                    .create().show();
//        } else Toast.makeText(this, "Канал в телеграме @KinoTor",Toast.LENGTH_SHORT).show();
    }

    private void setupRecyclerView() {
        //take our menu items
        // large-screen layouts (res/values-w900dp)
        if (recyclerView != null) {
            recyclerView.setFocusable(true);
            ItemMain menu_item = new ItemMain();
            menu_item.delItem();
            if (prefCategory.contains("4K") && (catalog.equals("filmix")  || catalog.equals("kinopub")))
                menu_item.addItem(new ItemMain.Item(0, "4K", catalogUrls.fourK(catalog)));
            if (prefCategory.contains("3D") && (catalog.equals("kinopub")))
                menu_item.addItem(new ItemMain.Item(0, "3D", catalogUrls.threeD(catalog)));
            if (prefCategory.contains("Новинки") && (catalog.equals("filmix")  || catalog.equals("kinofs") ||
                    catalog.equals("topkino") || catalog.equals("kinoxa") || catalog.equals("rufilmtv") ||
                    catalog.equals("kinolive") || catalog.equals("kinopub")))
                menu_item.addItem(new ItemMain.Item(0, "Новинки", catalogUrls.news(catalog)));
            if (prefCategory.contains("Сейчас смотрят") && catalog.equals("filmix"))
                menu_item.addItem(new ItemMain.Item(0, "Сейчас смотрят", catalogUrls.viewing(catalog)));
            if (prefCategory.contains("Фильмы"))
                menu_item.addItem(new ItemMain.Item(0, "Фильмы", catalogUrls.film(catalog)));
            if (prefCategory.contains("Сериалы"))
                menu_item.addItem(new ItemMain.Item(1, "Сериалы", catalogUrls.serial(catalog)));
            if (prefCategory.contains("Мультфильмы"))
                menu_item.addItem(new ItemMain.Item(2, "Мультфильмы", catalogUrls.mult(catalog)));
            if (prefCategory.contains("Мультсериалы") &&
                    (catalog.equals("filmix") || catalog.equals("my-hit") || catalog.equals("kinofs") ||
                            catalog.equals("topkino") || catalog.equals("kinolive") || catalog.equals("kinopub")))
                menu_item.addItem(new ItemMain.Item(3, "Мультсериалы", catalogUrls.multserial(catalog)));
            if (prefCategory.contains("Аниме") &&
                    (!catalog.equals("koshara") && !catalog.equals("kinoxa") && !catalog.contains("my-hit")))
                menu_item.addItem(new ItemMain.Item(4, "Аниме", catalogUrls.anime(catalog)));
            if (prefCategory.contains("ТВ Передачи") &&
                    (catalog.equals("kinofs") || catalog.equals("filmix") || catalog.equals("kinopub") ||
                            catalog.equals("rufilmtv") || catalog.equals("kinolive")))
                menu_item.addItem(new ItemMain.Item(5, "ТВ Передачи", catalogUrls.tv(catalog)));
            if (!Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted") && catalog.equals("filmix")) {
                menu_item.addItem(new ItemMain.Item(5, "Избранное", "filmix_fav"));
                menu_item.addItem(new ItemMain.Item(5, "Посмотреть позже","filmix_later"));
            }
            menu_item.addItem(new ItemMain.Item(6, "...", ""));
            menu_item.addItem(new ItemMain.Item(7, "Избранное", "http:// /"));
            menu_item.addItem(new ItemMain.Item(8, "История", "http:// /"));
            if (coldfilm || animevost || kinodom || fanserials || anidub)
                menu_item.addItem(new ItemMain.Item(9, "...", ""));
            if (animevost) menu_item.addItem(new ItemMain.Item(11, "AnimeVost", Statics.ANIMEVOST_URL + "/"));
            if (anidub) menu_item.addItem(new ItemMain.Item(11, "Anidub", Statics.ANIDUB_URL + "/"));
            if (coldfilm) menu_item.addItem(new ItemMain.Item(19, "Coldfilm", Statics.COLDFILM_URL + "/news/"));
            if (fanserials) menu_item.addItem(new ItemMain.Item(11, "FanSerials", Statics.FANSERIALS_URL + "/new/"));
            if (kinodom) menu_item.addItem(new ItemMain.Item(11, "KinoDom", Statics.KINODOM_URL + "/"));
            if (exit) {
                menu_item.addItem(new ItemMain.Item(12, "...", ""));
                menu_item.addItem(new ItemMain.Item(13, "Выход", "http:// /"));
            }
            recyclerView.setAdapter(new ItemRVAdapter(ItemMain.ITEMS));
            if (!preference.getBoolean("focus_on_video", true))
                recyclerView.requestFocus();
        }
    }

    private void refreshBar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle();
        //draweble
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //menu/draweble
        recyclerView = findViewById(R.id.item_list);
        setupRecyclerView();
        if (recyclerView != null) {
            if (side_menu && this.getResources().getConfiguration().orientation == 2) {
                toggle.setDrawerIndicatorEnabled(false);
                recyclerView.setVisibility(View.VISIBLE);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    getSupportActionBar().setDisplayShowHomeEnabled(false);
                }
            } else {
                toggle.setDrawerIndicatorEnabled(true);
                recyclerView.setVisibility(View.GONE);
            }
        }
    }

    private void updPref(){
        if (preference.getBoolean("sync_filmix_watch", false) &&
                !Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted"))
            new ParserFilmixHistUpd().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        String[] baseArrP = {"anidub", "animevost", "coldfilm", "fanserials", "kinodom"};
        HashSet<String> defP = new HashSet<>(Arrays.asList(baseArrP));
        Set<String> pref_baseP = preference.getStringSet("plus_catalog", defP);

        String[] baseTorSearch = {""};
        HashSet<String> defTorSearch = new HashSet<>(Arrays.asList(baseTorSearch));
        String pref_baseTorSearch = preference.getStringSet("torrent_search_list", defTorSearch).toString();

        coldfilm = pref_baseP.toString().contains("coldfilm");
        animevost = pref_baseP.toString().contains("animevost");
        anidub = pref_baseP.toString().contains("anidub");
        kinodom = pref_baseP.toString().contains("kinodom");
        fanserials = pref_baseP.toString().contains("fanserials");

        exit = preference.getBoolean("exit", true);
        side_menu = preference.getBoolean("side_menu", true);
        catalog = preference.getString("catalog", "filmix");
        pro = preference.getBoolean("pro_version", false);
        side_ecist = preference.getBoolean("side_left", false);
        side_exist = preference.getBoolean("side_exist", false) && pro;
        side_ezist = preference.getBoolean("side_video", false);

        if ((side_ecist && side_exist) || (side_ezist && side_exist) || (side_ecist && side_ezist)
                || (side_ecist && pro) || (side_ezist && pro))
            finish();


        prefCategory = preference.getStringSet("display_category",
                new HashSet<>(Arrays.asList(getResources().getStringArray(R.array.pref_list_cat_content))));

        //catalog
        Statics.KOSHARA_URL = preference.getString("koshara_furl", Statics.KOSHARA_URL);
        Statics.AMCET_URL = preference.getString("amcet_furl", Statics.AMCET_URL);
        Statics.KINOFS_URL = preference.getString("kinofs_furl", Statics.KINOFS_URL);
        Statics.KINOXA_URL = preference.getString("kinoxa_furl", Statics.KINOXA_URL);
        Statics.RUFILMTV_URL = preference.getString("rufilmtv_furl", Statics.RUFILMTV_URL);
        Statics.TOPKINO_URL = preference.getString("topkino_furl", Statics.TOPKINO_URL);
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
        Statics.ANIDUB_TR_COOCKIE = preference.getString("anidub_tr_coockie", Statics.ANIDUB_TR_COOCKIE);
        Statics.ANIDUB_TR_PASS = preference.getString("anidub_tr_pass", Statics.ANIDUB_TR_PASS);
        Statics.ANIDUB_TR_ACC = preference.getString("anidub_tr_acc", Statics.ANIDUB_TR_ACC);
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
        Statics.GREENTEA_TR_URL = preference.getString("greentea_tr_furl", Statics.GREENTEA_TR_URL);
        Statics.GREENTEA_TR_COOCKIE = preference.getString("greentea_tr_coockie", Statics.GREENTEA_TR_COOCKIE);
        Statics.GREENTEA_TR_ACC = preference.getString("greentea_tr_acc", Statics.GREENTEA_TR_ACC);
        Statics.RUTRACKER_URL = preference.getString("rutracker_furl", Statics.RUTRACKER_URL);
        Statics.RUTRACKER_COOCKIE = preference.getString("rutracker_coockie", Statics.RUTRACKER_COOCKIE);
        Statics.RUTRACKER_ACC = preference.getString("rutracker_acc", Statics.RUTRACKER_ACC);
        //other
        Statics.hideTs = preference.getBoolean("hide_ts", false);
        Statics.rateImdb = preference.getBoolean("rate_imdb", true);
        Statics.torS = pref_baseTorSearch;
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
        Statics.KINOPUB_COOCKIE = preference.getString("kinopub_coockie", Statics.KINOPUB_COOCKIE);
    }


    @Override
    protected void onResume() {
        super.onResume();
        DetailActivity.url = "error";
        updPref();
        checkFilters();
        if (getIntent() != null && getIntent().getData() != null) {
            Uri uri = getIntent().getData();
            int id;
            try {
                id = Integer.valueOf(uri.getLastPathSegment());
            } catch (Exception e) {
                e.printStackTrace();
                id = 0;
            }
            String url;
            if (Statics.list.size() > 0) {
                url = Statics.list.get(id);
            } else url = getIntent().getDataString();

            getIntent().setData(null);
            Intent intent;
            if (preference.getBoolean("tv_activity_detail", true)) {
                intent = new Intent(this, DetailActivityTv.class);
                intent.putExtra("Url", url);
            } else {
                intent = new Intent(this, DetailActivity.class);
                intent.putExtra("Url", url);
            }
            startActivity(intent);
            finish();
        }
        Log.e(TAG, "onResume: "+Statics.refreshMain);
        if (Statics.refreshMain) {
            Statics.refreshMain = false;
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                if (bundle.getString("Type") != null && bundle.getString("Query") != null) {
                    String t = bundle.getString("Type");
                    String q = bundle.getString("Query");
                    getIntent().removeExtra("Type");
                    getIntent().removeExtra("Query");
                    switch (Objects.requireNonNull(t)) {
                        case "actor":
                            searchActor(q);
                            break;
                        case "sort":
                            OnPage(q, "Сортировка");
                            break;
                        default:
                            OnPage(ItemMain.cur_url, subtitle);
                            break;
                    }
                } else refresh();
            } else refresh();
        }
        refreshBar();
    }

    private void refresh(){
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.putExtra("Type", "resume");
        intent.putExtra("Query", "none");
        intent.putExtra("Title", subtitle);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
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
            case "KinoDom":
                ItemMain.cur_url = Statics.KINODOM_URL + "/";
                Statics.CATALOG = "kinodom";
                break;
            default:
                Statics.CATALOG = catalog;
                if (subtitle.equals("3D") || getTitle().equals("3D"))
                    ItemMain.cur_url = catalogUrls.threeD(catalog);
                if (subtitle.equals("4K") || getTitle().equals("4K"))
                    ItemMain.cur_url = catalogUrls.fourK(catalog);
                if (subtitle.equals("Новинки") || getTitle().equals("Новинки"))
                    ItemMain.cur_url = catalogUrls.news(catalog);
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

    public void setTitle() {
        toolbar.setTitle(subtitle.replace("Актер", ""));
        toolbar.setSubtitle(catalog);
        if (Statics.CATALOG.contains("coldfilm")) {
            setTitle("ColdFilm");
            toolbar.setSubtitle("coldfilm");
        }
        if (Statics.CATALOG.contains("animevost")) {
            setTitle("AnimeVost");
            toolbar.setSubtitle("animevost");
        }
        if (Statics.CATALOG.contains("anidub")) {
            setTitle("Anidub");
            toolbar.setSubtitle("anidub");
        }
        if (Statics.CATALOG.contains("fanserials")) {
            setTitle("FanSerials");
            toolbar.setSubtitle("fanserials");
        }
        if (Statics.CATALOG.contains("kinodom")) {
            setTitle("KinoDom");
            toolbar.setSubtitle("kinodom");
        }
//        if (subtitle.equals("История") || subtitle.equals("Избранное")) {
//            toolbar.setSubtitle("kinotor");
//        }
    }

    private void OnPage (String url, String category) {
        if (utils.isOnline(this)) {
            ItemMain.cur_url = url;
            subtitle = category;
//            Log.e("test", "OnPage: "+category );

            setCurURL();
            invalidateOptionsMenu();
            onAttachedToWindow();
            setupRecyclerView();
//            recyclerView.getAdapter().notifyDataSetChanged();

            ItemMain.cur_items = 0;

            if (subtitle.contains("Поиск:") || subtitle.contains("ПоискАктер:")) {
                Log.d("Main", "OnPage: " + category + " " + url + " " + Statics.CATALOG);
                switch (Statics.CATALOG) {
                    case "amcet":
                        ItemMain.cur_url = Statics.AMCET_URL;
                        break;
                    case "koshara":
                        ItemMain.cur_url = Statics.KOSHARA_URL;
                        break;
                    case "kinofs":
                        ItemMain.cur_url = Statics.KINOFS_URL;
                        break;
                    case "filmix":
                        ItemMain.cur_url = Statics.FILMIX_URL;
                        break;
                    case "kinopub":
                        ItemMain.cur_url = Statics.KINOPUB_URL;
                        break;
                    case "kinoxa":
                        ItemMain.cur_url = Statics.KINOXA_URL;
                        break;
                    case "rufilmtv":
                        ItemMain.cur_url = Statics.RUFILMTV_URL;
                        break;
                    case "topkino":
                        ItemMain.cur_url = Statics.TOPKINO_URL;
                        break;
                    case "my-hit":
                        ItemMain.cur_url = Statics.MYHIT_URL;
                        break;
                    case "kinolive":
                        ItemMain.cur_url = Statics.KINOLIVE_URL;
                        break;
                    case "coldfim":
                        ItemMain.cur_url = Statics.COLDFILM_URL;
                        break;
                    case "animevost":
                        ItemMain.cur_url = Statics.ANIMEVOST_URL;
                        break;
                    case "anidub":
                        ItemMain.cur_url = Statics.ANIDUB_URL;
                        break;
                    case "kinodom":
                        ItemMain.cur_url = Statics.KINODOM_URL;
                        break;
                    case "fanserials":
                        ItemMain.cur_url = Statics.FANSERIALS_URL;
                        break;
                }
                if (subtitle.contains("Актер"))
                    searchActor(subtitle.split(": ")[1].trim());
                else searchGo(subtitle.split(": ")[1].trim());
            } else {
                if (category.equals("filmix_fav") || url.equals("filmix_fav")){
                    url = "http:// /";
                    category = "filmix_fav";
                }
                if (category.equals("filmix_later") || url.equals("filmix_later")){
                    url = "http:// /";
                    category = "filmix_later";
                }
                Log.d("Main", "OnPage: " + category + " " + url);
                MainCatalogFragment fragment = new MainCatalogFragment().newInstance(url, category, Statics.CATALOG);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.item_detail_container, fragment)
                        .commit();
                setTitle();
            }
        } else if (!utils.isOnline(this)){
            Toast.makeText(this,
                    "Ошибка интернет соединения...",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        ItemMain.xs_value = "";
        ItemMain.xs_field = "";
        if (id == R.id.films) {
            subtitle = "Фильмы";
            OnPage(catalogUrls.film(catalog), "Фильмы");
        } else if (id == R.id.news) {
            subtitle = "Новинки";
            OnPage(catalogUrls.news(catalog), "Новинки");
        } else if (id == R.id.threed) {
            subtitle = "3D";
            OnPage(catalogUrls.threeD(catalog), "3D");
        } else if (id == R.id.fourk) {
            subtitle = "4K";
            OnPage(catalogUrls.fourK(catalog), "4K");
        } else if (id == R.id.viewing) {
            subtitle = "Сейчас смотрят";
            OnPage(catalogUrls.viewing(catalog), "Сейчас смотрят");
        } else if (id == R.id.serials) {
            OnPage(catalogUrls.serial(catalog), "Сериалы");
        } else if (id == R.id.mults) {
            OnPage(catalogUrls.mult(catalog), "Мультфильмы");
        } else if (id == R.id.multserial) {
            OnPage(catalogUrls.multserial(catalog), "Мультсериалы");
        } else if (id == R.id.anime) {
            OnPage(catalogUrls.anime(catalog), "Аниме");
        } else if (id == R.id.tv) {
            OnPage(catalogUrls.tv(catalog), "ТВ Передачи");
        } else if (id == R.id.fav) {
            if (!Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted") && catalog.equals("filmix")) {
                OnPage("filmix_fav", "Избранное");
            }
        } else if (id == R.id.later) {
            if (!Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted") && catalog.equals("filmix")) {
                OnPage("filmix_later", "Посмотреть позже");
            }
        } else if (id == R.id.favor) {
            Intent intent = new Intent(this, BDActivity.class);
            intent.putExtra("Status", "favor");
            startActivity(intent);
        } else if (id == R.id.history) {
            Intent intent = new Intent(this, BDActivity.class);
            intent.putExtra("Status", "history");
            startActivity(intent);
        } else if (id == R.id.animevost) {
            OnPage(Statics.ANIMEVOST_URL + "/", "AnimeVost");
        } else if (id == R.id.anidub) {
            OnPage(Statics.ANIDUB_URL + "/", "Anidub");
        } else if (id == R.id.coldfilm) {
            OnPage(Statics.COLDFILM_URL + "/", "Coldfilm");
        } else if (id == R.id.fanserials) {
            OnPage(Statics.FANSERIALS_URL + "/new/", "FanSerials");
        } else if (id == R.id.kinodom) {
            OnPage(Statics.KINODOM_URL + "/", "KinoDom");
        } else if (id == R.id.exit) {
            finish();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setFocusable(true);

        navigationView.getMenu().findItem(R.id.coldfilm).setVisible(coldfilm);
        navigationView.getMenu().findItem(R.id.animevost).setVisible(animevost);
        navigationView.getMenu().findItem(R.id.anidub).setVisible(anidub);
        navigationView.getMenu().findItem(R.id.fanserials).setVisible(fanserials);
        navigationView.getMenu().findItem(R.id.kinodom).setVisible(kinodom);
        navigationView.getMenu().findItem(R.id.exit).setVisible(exit);



        navigationView.getMenu().findItem(R.id.fav).setVisible(
                !Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted") && catalog.equals("filmix"));
        navigationView.getMenu().findItem(R.id.later).setVisible(
                !Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted") && catalog.equals("filmix"));
        //------------------------------------------------------------------------------------------

        navigationView.getMenu().findItem(R.id.anime).setVisible(prefCategory.contains("Аниме") &&
                !catalog.equals("koshara") && !catalog.equals("kinoxa") && !catalog.contains("my-hit"));
        navigationView.getMenu().findItem(R.id.mults).setVisible(prefCategory.contains("Мультфильмы"));
        navigationView.getMenu().findItem(R.id.multserial).setVisible(prefCategory.contains("Мультсериалы") &&
                (catalog.equals("kinofs") || catalog.equals("filmix") || catalog.equals("kinopub") ||
                        catalog.equals("my-hit") || catalog.equals("topkino") || catalog.equals("kinolive")));
        navigationView.getMenu().findItem(R.id.tv).setVisible(prefCategory.contains("ТВ Передачи") &&
                (catalog.equals("kinofs") || catalog.equals("filmix") || catalog.equals("rufilmtv") ||
                        catalog.equals("topkino") || catalog.equals("kinolive") || catalog.equals("kinopub")));
        navigationView.getMenu().findItem(R.id.serials).setVisible(prefCategory.contains("Сериалы"));
        navigationView.getMenu().findItem(R.id.films).setVisible(prefCategory.contains("Фильмы"));
        navigationView.getMenu().findItem(R.id.viewing).setVisible(prefCategory.contains("Сейчас смотрят") && catalog.equals("filmix"));
        navigationView.getMenu().findItem(R.id.news).setVisible(prefCategory.contains("Новинки") && (catalog.equals("filmix")  || catalog.equals("kinofs") ||
                catalog.equals("topkino") || catalog.equals("kinoxa") || catalog.equals("rufilmtv") ||
                catalog.equals("kinolive") || catalog.equals("kinopub")));
        navigationView.getMenu().findItem(R.id.threed).setVisible(prefCategory.contains("3D") && (
                catalog.equals("kinopub")));
        navigationView.getMenu().findItem(R.id.fourk).setVisible(prefCategory.contains("4K") && (
                catalog.equals("filmix") || catalog.equals("kinopub")));

        navigationView.getMenu().findItem(R.id.coldfilm).setChecked(subtitle.equals("Coldfilm"));
        navigationView.getMenu().findItem(R.id.fanserials).setChecked(subtitle.equals("FanSerials"));
        navigationView.getMenu().findItem(R.id.kinodom).setChecked(subtitle.equals("KinoDom"));
        navigationView.getMenu().findItem(R.id.animevost).setChecked(subtitle.equals("AnimeVost"));
        navigationView.getMenu().findItem(R.id.anidub).setChecked(subtitle.equals("Anidub"));
        navigationView.getMenu().findItem(R.id.history).setChecked(subtitle.equals("История") || getTitle().equals("История"));
        navigationView.getMenu().findItem(R.id.favor).setChecked(subtitle.equals("Избранное") || getTitle().equals("Избранное"));
        navigationView.getMenu().findItem(R.id.mults).setChecked(subtitle.equals("Мультфильмы"));
        navigationView.getMenu().findItem(R.id.multserial).setChecked(subtitle.equals("Мультсериалы"));
        navigationView.getMenu().findItem(R.id.anime).setChecked(subtitle.equals("Аниме"));
        navigationView.getMenu().findItem(R.id.tv).setChecked(subtitle.equals("ТВ Передачи"));
        navigationView.getMenu().findItem(R.id.fav).setChecked(subtitle.equals("Избранное"));
        navigationView.getMenu().findItem(R.id.later).setChecked(subtitle.equals("Посмотреть позже"));
        navigationView.getMenu().findItem(R.id.serials).setChecked(subtitle.equals("Сериалы"));
        navigationView.getMenu().findItem(R.id.films).setChecked(subtitle.equals("Фильмы"));
    }

    @Override
    public void onNewIntent(Intent intent){
        setIntent(intent);
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            hideKeyboard(this);
            searchGo(intent.getStringExtra(SearchManager.QUERY));
//            Log.e(TAG, "onNewIntent: "+query );
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        MenuItem menuSort = menu.findItem(R.id.action_sort);
        MenuItem menuSortAll = menu.findItem(R.id.action_sort_all);
        MenuItem menuSearchVid = menu.findItem(R.id.action_video_search);
        MenuItem menuSortList = menu.findItem(R.id.menuSortOrderList);
        MenuItem menuSortType = menu.findItem(R.id.menuSortOrderType);
        MenuItem menuSortGenre = menu.findItem(R.id.menuSortOrderCategory);
        MenuItem menuSortCountry = menu.findItem(R.id.menuSortOrderCountry);
        MenuItem menuSortYear = menu.findItem(R.id.menuSortOrderYear);

        MenuItem menuCategory = menu.findItem(R.id.action_category);

//        MenuItem amcet = menu.findItem(R.id.menuСatalogAmcet);
        MenuItem filmix = menu.findItem(R.id.menuСatalogFilmix);
        MenuItem kinopub = menu.findItem(R.id.menuСatalogKinopub);
        MenuItem topkino = menu.findItem(R.id.menuСatalogTopkino);
        MenuItem kinofs = menu.findItem(R.id.menuСatalogKinoFS);
        MenuItem koshara = menu.findItem(R.id.menuСatalogKoshara);
        MenuItem kinoxa = menu.findItem(R.id.menuСatalogKinoxa);
        MenuItem rufilmtv = menu.findItem(R.id.menuСatalogRufilmtv);
        MenuItem myhit = menu.findItem(R.id.menuСatalogMyhit);
        MenuItem kinolive = menu.findItem(R.id.menuСatalogKinolive);

        MenuItem menuCatalog = menu.findItem(R.id.action_catalog);
        MenuItem menuCatalogAlt = menu.findItem(R.id.action_alt_cat);
        MenuItem menuSearchActor = menu.findItem(R.id.action_actor_search);

        String[] baseArr = {"my-hit", "filmix", "kinofs", "topkino", "kinolive"};
        HashSet<String> def = new HashSet<>(Arrays.asList(baseArr));
        Set<String> pref_base = preference.getStringSet("base_catalog", def);
//        amcet.setVisible(pref_base.toString().contains("amcet"));
        koshara.setVisible(pref_base.toString().contains("koshara"));
        filmix.setVisible(pref_base.toString().contains("filmix"));
        kinopub.setVisible(pref_base.toString().contains("kinopub"));
        topkino.setVisible(pref_base.toString().contains("topkino"));
        kinofs.setVisible(pref_base.toString().contains("kinofs"));
        kinoxa.setVisible(pref_base.toString().contains("kinoxa"));
        rufilmtv.setVisible(pref_base.toString().contains("rufilmtv"));
        myhit.setVisible(pref_base.toString().contains("my-hit"));
        kinolive.setVisible(pref_base.toString().contains("kinolive"));

        menuSort.setVisible(!catalog.equals("koshara") && !catalog.equals("filmix") &&
                !catalog.equals("my-hit") && !catalog.equals("topkino") && !catalog.equals("kinopub"));
        filmix.setChecked(catalog.equals("filmix"));
        filmix.setEnabled(!catalog.equals("filmix"));
        kinopub.setChecked(catalog.equals("kinopub"));
        kinopub.setEnabled(!catalog.equals("kinopub"));
        kinolive.setChecked(catalog.equals("kinolive"));
        kinolive.setEnabled(!catalog.equals("kinolive"));
        koshara.setChecked(catalog.equals("koshara"));
        koshara.setEnabled(!catalog.equals("koshara"));
        topkino.setChecked(catalog.equals("topkino"));
        topkino.setEnabled(!catalog.equals("topkino"));
        kinofs.setChecked(catalog.equals("kinofs"));
        kinofs.setEnabled(!catalog.equals("kinofs"));
        kinoxa.setChecked(catalog.equals("kinoxa"));
        kinoxa.setEnabled(!catalog.equals("kinoxa"));
        rufilmtv.setChecked(catalog.equals("rufilmtv"));
        rufilmtv.setEnabled(!catalog.equals("rufilmtv"));
        myhit.setChecked(catalog.equals("my-hit"));
        myhit.setEnabled(!catalog.equals("my-hit"));

        menuSortAll.setVisible((catalog.equals("filmix") || catalog.equals("my-hit") ||
                catalog.equals("topkino") || catalog.equals("kinopub")) &&
                !Statics.CATALOG.contains("coldfilm") && !Statics.CATALOG.contains("fanserials") &&
                !Statics.CATALOG.contains("animevost") && !Statics.CATALOG.contains("anidub") &&
                !Statics.CATALOG.contains("kinodom") && !Statics.CATALOG.contains("kinolive"));

        menuSearchVid.setVisible(side_exist);


        menuSearchActor.setVisible((Statics.CATALOG.contains("amcet") ||
                Statics.CATALOG.contains("koshara") || Statics.CATALOG.contains("kinofs") ||
                Statics.CATALOG.contains("filmix") || Statics.CATALOG.contains("topkino") ||
                Statics.CATALOG.contains("kinopub")));

        if (Statics.CATALOG.contains("coldfilm") || Statics.CATALOG.contains("fanserials") || Statics.CATALOG.contains("kinodom")) {
            menuSort.setVisible(false);
            menuCategory.setVisible(false);
            menuSortType.setVisible(false);
            menuCatalog.setVisible(false);
            menuCatalogAlt.setVisible(false);
        } else if (Statics.CATALOG.contains("animevost") || Statics.CATALOG.contains("anidub") ||
                Statics.CATALOG.contains("kinolive")) {
            menuSort.setVisible(true);
            menuSortCountry.setVisible(false);
            menuCategory.setVisible(false);
            menuSortType.setVisible(true);
            menuCatalog.setVisible(false);
            menuCatalogAlt.setVisible(false);
        } else {
            menuSortType.setVisible(false);
            menuCatalog.setVisible(!preference.getBoolean("alt_catalog_select", false));
            menuCatalogAlt.setVisible(preference.getBoolean("alt_catalog_select", false));
            Log.e(TAG, "onCreateOptionsMenu: " + preference.getBoolean("alt_catalog_select", false));

            menuSortCountry.setVisible(true);
        }
        if (Statics.CATALOG.contains("kinodom")) {
            menuCategory.setVisible(true);
        }
        if (Statics.CATALOG.contains("kinolive")) {
            menuCategory.setVisible(true);
            menuSortYear.setVisible(true);
            menuSortType.setVisible(false);
            menuCatalog.setVisible(!preference.getBoolean("alt_catalog_select", false));
            menuCatalogAlt.setVisible(preference.getBoolean("alt_catalog_select", false));
        }

        menuSortList.setVisible(Statics.CATALOG.contains("rufilmtv"));
        if (Statics.CATALOG.contains("kinofs") || Statics.CATALOG.contains("kinoxa")) {
            menuSortCountry.setVisible(false);

            menuSortYear.setVisible(subtitle.equals("Фильмы") || Statics.CATALOG.contains("kinoxa"));
            menuSortGenre.setVisible(subtitle.equals("Фильмы"));
        }


        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        AdapterSearch adapterSearch = new AdapterSearch(this, new ArrayList<>()) {
            @Override
            public void click(ItemSearch itemCur) {
                Intent intent;
                if (!preference.getBoolean("tv_activity_detail", true)) {
                    intent = new Intent(getContext(), DetailActivity.class);
                } else {
                    intent = new Intent(getContext(), DetailActivityTv.class);
                }
                intent.putExtra("Title", itemCur.getTitle());
                intent.putExtra("Url", itemCur.getUrl());
                intent.putExtra("Img", itemCur.getImg());
                startActivity(intent);
            }
        };
        SearchView.SearchAutoComplete searchSrcTextView = searchView.findViewById(R.id.search_src_text);
        searchSrcTextView.setAdapter(adapterSearch);

        searchView.setOnClickListener(view -> {
            if (preference.getBoolean("tv_activity_search", true)) {
                hideKeyboard(this);
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
            } else {
                searchView.setIconified(false);
            }
//                searchView.performClick();
//                searchItem.expandActionView();
            Log.d("qwe", "onClick: search");
        });

        searchView.setOnSearchClickListener(view -> {
            if (preference.getBoolean("tv_activity_search", true)) {
                hideKeyboard(this);
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
            }
            Log.d("test", "onClick: search");
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e("test", "onQueryTextSubmit: "+query );
                if (query.length() > 1) {
                    searchGo(query);
                    searchView.clearFocus();
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
//                Log.e("test", "onQueryTextChange: "+Statics.CATALOG );
                if (newText.length() > 1) {
                    switch (Statics.CATALOG.trim()) {
                        case "koshara":
                            SearchKoshara searchKoshara = new SearchKoshara(newText, items -> addSearchItem(items, searchSrcTextView));
                            searchKoshara.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        case "coldfilm":
                            //-
                            break;
                        case "animevost":
                            SearchAnimevost searchAnimevost = new SearchAnimevost(newText, items -> addSearchItem(items, searchSrcTextView));
                            searchAnimevost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        case "anidub":
                            SearchAnidub searchAnidub = new SearchAnidub(newText, items -> addSearchItem(items, searchSrcTextView));
                            searchAnidub.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        case "kinofs": {
                            SearchKinofs searchKinofs = new SearchKinofs(newText, items -> addSearchItem(items, searchSrcTextView));
                            searchKinofs.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        }
                        case "kinoxa": {
                            //-
                            break;
                        }
                        case "rufilmtv": {
                            //-
                            break;
                        }
                        case "topkino": {
                            SearchTopkino searchTopkino = new SearchTopkino(newText, items -> addSearchItem(items, searchSrcTextView));
                            searchTopkino.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        }
                        case "fanserials": {
                            SearchFanserials searchFanserials = new SearchFanserials(newText, items -> addSearchItem(items, searchSrcTextView));
                            searchFanserials.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        }
                        case "kinodom": {
//                            SearchKinodom searchKinodom = new SearchKinodom(newText, items -> addSearchItem(items, searchSrcTextView));
//                            searchKinodom.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        }
                        case "filmix":
                            SearchFilmix searchFilmix = new SearchFilmix(newText, items -> addSearchItem(items, searchSrcTextView));
                            searchFilmix.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        case "my-hit":
                            SearchMyhit searchMyhit = new SearchMyhit(newText, items -> addSearchItem(items, searchSrcTextView));
                            searchMyhit.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        case "kinopub":
                            SearchKinopub searchKinopub = new SearchKinopub(newText, items -> addSearchItem(items, searchSrcTextView));
                            searchKinopub.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        case "kinolive":
                            SearchKinolive searchKinolive = new SearchKinolive(newText, items -> addSearchItem(items, searchSrcTextView));
                            searchKinolive.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                    }
                }
                return false;
            }
        });
        searchSrcTextView.setFocusable(true);
        searchSrcTextView.setFocusableInTouchMode(true);

        return super.onCreateOptionsMenu(menu);
    }

    public void addSearchItem(List<ItemSearch> items, SearchView.SearchAutoComplete searchSrcTextView){
        AdapterSearch adapterSearch = new AdapterSearch(getBaseContext(), items) {
            @Override
            public void click(ItemSearch itemCur) {
                Intent intent;
                if (!preference.getBoolean("tv_activity_detail", true)) {
                    intent = new Intent(getContext(), DetailActivity.class);
                } else {
                    intent = new Intent(getContext(), DetailActivityTv.class);
                }
                intent.putExtra("Title", itemCur.getTitle());
                intent.putExtra("Url", itemCur.getUrl());
                intent.putExtra("Img", itemCur.getImg());
                startActivity(intent);
            }
        };
        adapterSearch.notifyDataSetChanged();
        searchSrcTextView.setAdapter(adapterSearch);
    }

    public void searchGo(String query){
        subtitle = "Поиск: " + query;
        onAttachedToWindow();
        ItemMain.cur_items = 0;
        ItemMain.xs_value = "";
        ItemMain.xs_search = query;

        setupRecyclerView();
        switch (Statics.CATALOG) {
            case "koshara":
                try {
                    query = URLEncoder.encode(query, "windows-1251");
                } catch (UnsupportedEncodingException e) {
                    query = "error";
                }
                ItemMain.cur_url = Statics.KOSHARA_URL + "/index.php?do=search&subaction=search&titleonly=3&story=" + query;
                break;
            case "fanserials":
//                try {
//                    query = URLEncoder.encode(query, "windows-1251");
//                } catch (UnsupportedEncodingException e) {
//                    query = "error";
//                }
                ItemMain.cur_url = Statics.FANSERIALS_URL + "/search/?query=" + query;
                break;
            case "kinodom":
                try {
                    query = URLEncoder.encode(query, "windows-1251");
                } catch (UnsupportedEncodingException e) {
                    query = "error";
                }
                ItemMain.xs_search = query;
                ItemMain.cur_url = Statics.KINODOM_URL + "/index.php?do=search";
                break;
            case "kinolive":
                try {
                    query = URLEncoder.encode(query, "windows-1251");
                } catch (UnsupportedEncodingException e) {
                    query = "error";
                }
                ItemMain.xs_search = query;
                ItemMain.cur_url = Statics.KINOLIVE_URL + "/index.php?do=search";
                break;
            case "coldfilm":
                ItemMain.cur_url = Statics.COLDFILM_URL + "/search/?q=" + query;
                break;
            case "kinopub":
                ItemMain.cur_url = Statics.KINOPUB_URL + "/item/search?query=" + query;
                break;
            case "kinofs":
                ItemMain.cur_url = Statics.KINOFS_URL + "/search/?q=" + query + ";t=0;p=";
                break;
            case "amcet":
                ItemMain.cur_url = Statics.AMCET_URL + "/?subaction=search&do=search&story=" + query;
                break;
            case "animevost":
                ItemMain.xs_search = query;
                break;
            case "anidub":
                ItemMain.xs_search = query;
                break;
            case "filmix":
                ItemMain.xs_search = query;
                ItemMain.xs_value = "0";
                ItemMain.cur_url = Statics.FILMIX_URL + "/engine/ajax/sphinx_search.php";
                break;
            case "kinoxa":
                ItemMain.cur_url = Statics.KINOXA_URL + "/index.php?do=search&subaction=search&titleonly=3&story=" + query;
                break;
            case "rufilmtv":
                ItemMain.cur_url = Statics.RUFILMTV_URL + "/?s=" + query;
                break;
            case "topkino":
                ItemMain.cur_url = Statics.TOPKINO_URL +
                        "/index.php?do=search&subaction=search&full_search=1&titleonly=3&story=" + query;
                break;
            case "my-hit":
                ItemMain.cur_url = Statics.MYHIT_URL + "/search/?q=" + query;
                break;
        }

        boolean all = preference.getBoolean("search_all", false);

        String catalog = Statics.CATALOG;
        if (all && side_exist) {
            if (!Statics.CATALOG.toLowerCase().equals("fanserials") &&
                    !Statics.CATALOG.toLowerCase().equals("animevost") &&
                    !Statics.CATALOG.toLowerCase().equals("coldfilm") &&
                    !Statics.CATALOG.toLowerCase().equals("anidub") &&
                    !Statics.CATALOG.toLowerCase().equals("kinodom"))
                catalog = "all";
        }


        MainCatalogFragment fragment = new MainCatalogFragment().newInstance(ItemMain.cur_url, "Поиск", catalog);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .commitAllowingStateLoss();
        setTitle();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_category:
                final String[] ctg_list = catalogUrls.getGenre(Statics.CATALOG);
                final String[] url_list = catalogUrls.getGenreUrl(Statics.CATALOG);

                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
                builder.setTitle("Выберите категорию").setItems(ctg_list, (dialogInterface, i) -> {
                    //ItemMain.xs_value = "";
                    //subtitle =  ctg_list[i];
                    setCurURL();
                    switch (Statics.CATALOG) {
                        case "filmix":
                            OnPage(Statics.FILMIX_URL + "/filters/" + url_list[i] +
                                    "&requested_url=filters%2F" + url_list[i], ctg_list[i]);
                            break;
                        case "topkino":
                            OnPage(Statics.TOPKINO_URL + "/f/cat=" + url_list[i] + "/" +
                                    ItemCatalogUrls.sortTopkinoID[0], ctg_list[i]);
                            break;
                        case "my-hit":
                            OnPage(Statics.MYHIT_URL + url_list[i], ctg_list[i]);
                            break;
                        case "kinodom":
                            OnPage(url_list[i], subtitle);
                            break;
                        case "kinopub":
                            OnPage(Statics.KINOPUB_URL + url_list[i], subtitle);
                            break;
                        default:
                            OnPage(url_list[i], ctg_list[i]);
                            break;
                    }
                });
                builder.create().show();
                break;
            case R.id.action_settings:
                ItemMain.xs_search = "";
                ItemMain.xs_value = "";
                Intent i = new Intent(this, SettingsActivity.class);
                this.startActivity(i);
                break;
            case R.id.action_alt_cat:
                String[] foo_array = this.getResources().getStringArray(R.array.pref_list_base);
                AlertDialog.Builder b = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
                b.setTitle("Выберите каталог").setItems(foo_array, (dialogInterface, k) -> {
                    onSelectCatalog(foo_array[k]);
                }).create().show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    public void onSortOrderList(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle("Выберите тип");
        switch (Statics.CATALOG.trim()) {
            case "animevost":
                builder.setItems(catalogUrls.getSortName("animevost"), (dialogInterface, i) ->
                        OnPage(catalogUrls.getSortUrl("animevost")[i], subtitle));
                break;
            case "anidub":
                builder.setItems(catalogUrls.getSortName("anidub"), (dialogInterface, i) ->
                        OnPage(catalogUrls.getSortUrl("anidub")[i], subtitle));
                break;
            case "kinofs":
                builder.setItems(catalogUrls.getSortName("kinofs"), (dialogInterface, i) -> {
                    ItemMain.xs_value = catalogUrls.getSortUrl("kinofs")[i];
                    OnPage(ItemMain.cur_url, subtitle);
                });
                break;
            case "amcet":
                builder.setItems(catalogUrls.getSortName("amcet"), (dialogInterface, i) -> {
                    ItemMain.xs_field = "defaultsort";
                    ItemMain.xs_value = catalogUrls.getSortUrl("amcet")[i];
                    OnPage(ItemMain.cur_url, subtitle);
                });
                break;
            case "kinoxa":
                builder.setItems(catalogUrls.getSortName("kinoxa"), (dialogInterface, i) -> {
                    ItemMain.xs_field = "defaultsort";
                    ItemMain.xs_value = catalogUrls.getSortUrl("kinoxa")[i];
                    OnPage(ItemMain.cur_url, subtitle);
                });
                break;
        }
        builder.create().show();
    }

    public void onSortOrderGenre(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle("Выберите жанр");
        switch (Statics.CATALOG) {
            case "animevost":
                builder.setItems(catalogUrls.getGenre("animevost"), (dialogInterface, i) ->
                        OnPage(catalogUrls.getGenreUrl("animevost")[i],
                                subtitle));
                break;
            case "anidub":
                builder.setItems(catalogUrls.getGenre("anidub"), (dialogInterface, i) ->
                        OnPage(Statics.ANIDUB_URL + "/xfsearch/" + catalogUrls.getGenre("anidub")[i],
                                subtitle));
                break;
            case "kinolive":
                builder.setItems(catalogUrls.getGenre("kinolive"), (dialogInterface, i) ->
                        OnPage(Statics.KINOLIVE_URL + catalogUrls.getGenreUrl("kinolive")[i],
                                catalogUrls.getGenre("kinolive")[i]));
                break;
            case "kinofs":
                if (subtitle.equals("Сериалы")) {
                    builder.setItems(catalogUrls.getGenreS("kinofs"), (dialogInterface, i) ->
                            OnPage(catalogUrls.getGenreSUrl("kinofs")[i], subtitle));
                } else if (subtitle.equals("Фильмы")) {
                    builder.setItems(catalogUrls.getGenreF("kinofs"), (dialogInterface, i) ->
                            OnPage(catalogUrls.getGenreFUrl("kinofs")[i], subtitle));
                }
                break;
            case "kinoxa":
                if (subtitle.equals("Сериалы")) {
                    builder.setItems(catalogUrls.getGenreS("kinoxa"), (dialogInterface, i) ->
                            OnPage(catalogUrls.getGenreSUrl("kinoxa")[i], subtitle));
                } else if (subtitle.equals("Фильмы")) {
                    builder.setItems(catalogUrls.getGenreF("kinoxa"), (dialogInterface, i) ->
                            OnPage(catalogUrls.getGenreFUrl("kinoxa")[i], subtitle));
                }
                break;
            case "rufilmtv":
                builder.setItems(catalogUrls.getGenreF("rufilmtv"), (dialogInterface, i) ->
                        OnPage(catalogUrls.getGenreFUrl("rufilmtv")[i],
                                catalogUrls.getGenreF("rufilmtv")[i]));
                break;
        }
        builder.create().show();
    }
    public void onSortOrderType(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle("Выберите тип");
        switch (Statics.CATALOG) {
            case "animevost":
                builder.setItems(catalogUrls.getType("animevost"), (dialogInterface, i) ->
                        OnPage(catalogUrls.getTypeUrl("animevost")[i],
                                subtitle));
                break;
            case "anidub":
                builder.setItems(catalogUrls.getType("anidub"), (dialogInterface, i) ->
                        OnPage(catalogUrls.getTypeUrl("anidub")[i],
                                subtitle));
                break;
        }
        builder.create().show();
    }

    public void onSortOrderYear(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setTitle("Выберите год");
        switch (Statics.CATALOG) {
            case "animevost":
                builder.setItems(catalogUrls.getYear("animevost"), (dialogInterface, i) ->
                        OnPage(Statics.ANIMEVOST_URL + "/god/" + catalogUrls.getYear("animevost")[i] + "/",
                                subtitle));
                break;
            case "anidub":
                builder.setItems(catalogUrls.getYear("animevost"), (dialogInterface, i) ->
                        OnPage(Statics.ANIDUB_URL + "/xfsearch/" + catalogUrls.getYear("animevost")[i] + "/",
                                subtitle));
                break;
            case "kinofs":
                builder.setItems(catalogUrls.getYear("kinofs"), (dialogInterface, i) ->
                        OnPage(catalogUrls.getYearURL("kinofs")[i], subtitle));
                break;
            case "kinolive":
                builder.setItems(catalogUrls.getYear("amcet"), (dialogInterface, i) ->
                        OnPage(Statics.KINOLIVE_URL + "/tags/" + catalogUrls.getYear("amcet")[i] + "/",
                                "Фильтр: " + catalogUrls.getYear("amcet")[i]));
                break;
            case "kinoxa":
                builder.setItems(catalogUrls.getYear("amcet"), (dialogInterface, i) -> {
                    ItemMain.xs_field = "year";
                    ItemMain.xs_value = catalogUrls.getYear("amcet")[i];
                    OnPage(ItemMain.cur_url, subtitle);
                });
                break;
            case "rufilmtv":
                builder.setItems(catalogUrls.getYear("rufilmtv"), (dialogInterface, i) -> {
                    OnPage(Statics.RUFILMTV_URL + "/god/" + catalogUrls.getYear("rufilmtv")[i],
                            "Фильтр: " + catalogUrls.getYear("rufilmtv")[i]);
                });
                break;
        }
        builder.create().show();
    }

    public void onSortOrderCountry(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        if (catalog.equals("rufilmtv")) {
            builder.setTitle("Выберите страну").setItems(catalogUrls.getSortCountry("rufilmtv"), (dialogInterface, i) -> {
                OnPage(catalogUrls.getSortCountryUrl("rufilmtv")[i] + "", subtitle);
            });
            builder.create().show();
        }
    }

    public void onSelectCatalog(MenuItem item) {
        item.setChecked(true);
        onSelectCatalog(item.getTitle().toString());
    }
    public void onSelectCatalog(String item) {
        SharedPreferences.Editor editor = preference.edit();
        ItemMain.xs_field = "";
        if (item.toLowerCase().equals("kinopub") && Statics.KINOPUB_COOCKIE.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setTitle("Каталог Kinopub")
                    .setMessage("Данный каталог доступен лишь после авторизации на сервисе kino.pub.\n" +
                            "Войти в аккаунт кинопаба?\n")
                    .setPositiveButton("Вход", (dialog, id) -> {
                        AlertDialog.Builder loginkinopub = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
                        LayoutInflater inflater = getLayoutInflater();

                        View dialog_layout = inflater.inflate(R.layout.dialog_login,null);
                        final EditText txtun = dialog_layout.findViewById(R.id.txtusername);
                        final EditText txtps = dialog_layout.findViewById(R.id.txtuserpass);
                        final TextView header = dialog_layout.findViewById(R.id.textView);
                        txtun.setText(preference.getString("kinopub_acc", Statics.HURTOM_ACC));
                        header.setText("Kinopub");
                        loginkinopub.setView(dialog_layout)
                                .setPositiveButton("Вход", (dialogInterface, i) -> {
                                    String uname = txtun.getText().toString();
                                    String pass = txtps.getText().toString();
                                    ParserKinopubLogin p = new ParserKinopubLogin(uname, pass, location -> {
                                        switch (location) {
                                            case "null":
                                                Toast.makeText(this, "Ошибка соединения.", Toast.LENGTH_SHORT).show();
                                                break;
                                            case "error":
                                                Toast.makeText(this, "Неправильный логин/пароль", Toast.LENGTH_SHORT).show();
                                                break;
                                            default:
                                                editor.putString("kinopub_acc", uname);
                                                editor.putString("kinopub_pass", pass);
                                                Log.e("kinopub", "onCreate: "+location.trim());
                                                editor.putString("kinopub_coockie", location.trim());
                                                editor.putString("catalog", "kinopub");
                                                editor.apply();
                                                catalog = preference.getString("catalog", "filmix");
                                                setCurURL();

                                                OnPage(ItemMain.cur_url, subtitle);
                                                break;
                                        }
                                    });
                                    p.execute();
                                }).setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.dismiss())
                                .create().show();
                    })
                    .setNegativeButton("Отказ", (dialog, id) -> dialog.dismiss())
                    .create().show();
        } else {
            switch (item.toLowerCase()) {
                case "koshara":
                    editor.putString("catalog", "koshara");
                    break;
                case "filmix":
                    editor.putString("catalog", "filmix");
                    break;
                case "kinoxa":
                    editor.putString("catalog", "kinoxa");
                    break;
                case "kinofs":
                    editor.putString("catalog", "kinofs");
                    break;
                case "rufilmtv":
                    editor.putString("catalog", "rufilmtv");
                    break;
                case "topkino":
                    editor.putString("catalog", "topkino");
                    break;
                case "my-hit":
                    editor.putString("catalog", "my-hit");
                    break;
                case "kinolive":
                    editor.putString("catalog", "kinolive");
                    break;
                case "kinopub":
                    editor.putString("catalog", "kinopub");
                    break;
                default:
                    editor.putString("catalog", "filmix");
                    break;
            }
            editor.apply();

            catalog = preference.getString("catalog", "filmix");
            setCurURL();
            checkFilters();

            OnPage(ItemMain.cur_url, subtitle);
        }
    }

    public void onActorSearch(MenuItem item) {
        DialogFragment search = new DialogSearch();
        search.show(this.getFragmentManager(), subtitle);
    }
    public void onVideoSearch(MenuItem item) {
        Statics.itemLast = null;
        Statics.KP_ID = "error";
        Intent intent = new Intent(this, DetailActivityVid.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("search", true);
        startActivity(intent);
    }
    public void onSortAll(MenuItem item) {
        DialogFragment sort = new DialogSort();
        sort.show(this.getFragmentManager(), subtitle);
    }

    private void searchActor(String actor) {
        subtitle = "ПоискАктер: " + actor;
        onAttachedToWindow();
        ItemMain.cur_items = 0;
        ItemMain.xs_value = "";
        ItemMain.xs_search = actor;
        switch (Statics.CATALOG.trim()) {
            case "koshara":
                try { actor = URLEncoder.encode(actor, "windows-1251"); }
                catch (UnsupportedEncodingException ignored) { }
                ItemMain.cur_url = Statics.KOSHARA_URL + "/index.php?do=search&subaction=search&story=" + actor;
                break;
            case "amcet":
                ItemMain.cur_url = Statics.AMCET_URL + "/xfsearch/actors/" +
                        actor.replace(" ", "+") + "/";
                break;
            case "kinofs":
                ItemMain.cur_url = Statics.KINOFS_URL + "/search/" +
                        actor.replace(" ", "%20") + "/";
                break;
            case "filmix":
                ItemMain.cur_url = Statics.FILMIX_URL + "/persons/search/" +
                        actor.replace(" ", "%20") + "/";
                break;
            case "topkino":
                ItemMain.cur_url = Statics.TOPKINO_URL + "/xfsearch/" +
                        actor.replace(" ", "+") + "/";
                break;
            case "kinopub":
                ItemMain.cur_url = Statics.KINOPUB_URL + "/item/search?query=" +
                        actor.replace(" ", "+") + "&mode=actor";
                break;
            case "my-hit":
                ItemMain.cur_url = Statics.MYHIT_URL + "/search/?q=" + actor;
                break;
        }
        MainCatalogFragment fragment = new MainCatalogFragment().newInstance(ItemMain.cur_url, "ПоискАктер", Statics.CATALOG);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .commit();
        setTitle();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                finish();
            } else {
                Toast.makeText(this, "Нажмите назад для выхода", Toast.LENGTH_SHORT).show();
            }
            this.doubleBackToExitPressedOnce = true;

            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }

    //Our menu/draweble
    public class ItemRVAdapter
            extends RecyclerView.Adapter<ItemRVAdapter.ViewHolder> {
        private List<ItemMain.Item> mValues;
        ItemRVAdapter(List<ItemMain.Item> items) {
            mValues = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
            final int cur = position;
            holder.mItem = mValues.get(position);
            holder.mName.setText(mValues.get(position).name);
            holder.mView.setFocusable(true);
            if (holder.mItem.getName().equals("Избранное") && !holder.mItem.getdetails().contains("filmix")) {
                Check(false, holder);
            } else if (holder.mItem.getName().equals("История") && !holder.mItem.getdetails().contains("filmix")) {
                Check(false, holder);
            } else if (mValues.get(position).name.equals(subtitle)) {
                Check(true, holder);
            } else {
                Check(false, holder);
            }

            if (mValues.get(position).name.equals("Выход")) {
                holder.mView.setNextFocusDownId(holder.mView.getId());
            }

//            holder.mView.requestFocus();

            if (mValues.get(position).name.equals("...")) {
                holder.mView.setSelected(false);
                holder.mView.setEnabled(false);
                holder.mView.setFocusable(false);
                holder.separ.setVisibility(View.VISIBLE);
                holder.mName.setVisibility(View.GONE);
            }

            holder.mView.setOnFocusChangeListener((view, b) -> {
                if (!view.isSelected()) {
                    view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    holder.mName.setTextColor(getResources().getColor(R.color.colorWhite));
                } else if (holder.mItem.getName().equals("Избранное") && !holder.mItem.getdetails().contains("filmix")) {
                    holder.mName.setTextColor(getResources().getColor(R.color.colorDarkWhite));
                    Check(false, holder);
                } else if (holder.mItem.getName().equals("История") && !holder.mItem.getdetails().contains("filmix")) {
                    holder.mName.setTextColor(getResources().getColor(R.color.colorDarkWhite));
                    Check(false, holder);
                } else if (mValues.get(cur).name.equals(subtitle)) {
                    Check(true, holder);
                } else {
                    holder.mName.setTextColor(getResources().getColor(R.color.colorDarkWhite));
                    Check(false, holder);
                }
                view.setSelected(b);
            });

            holder.mView.setOnKeyListener((view, i, keyEvent) -> {
                if (holder.mItem.getName().equals("Выход") && keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                    holder.mView.requestFocus();
                    return true;
                } else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_SEARCH){
                    Intent intent = new Intent(getBaseContext(), SearchActivity.class);
                    startActivity(intent);
                    return true;
                } else return false;
            });
            holder.mView.setOnClickListener(v -> {
                ItemMain.xs_value = "";
                ItemMain.xs_field = "";
                holder.mView.requestFocus();
                if (holder.mItem.getName().equals("Выход")) {
                    finish();
                } else if (holder.mItem.getName().equals("Избранное") && !holder.mItem.getdetails().contains("filmix")) {
                    Intent intent = new Intent(getBaseContext(), BDActivity.class);
                    intent.putExtra("Status", "favor");
                    startActivity(intent);
                } else if (holder.mItem.getName().equals("История") && !holder.mItem.getdetails().contains("filmix")) {
                    Intent intent = new Intent(getBaseContext(), BDActivity.class);
                    intent.putExtra("Status", "history");
                    startActivity(intent);
                } else {
                    OnPage(holder.mItem.getdetails(), holder.mItem.getName());
                }
            });
        }

        void Check(boolean check, final ViewHolder holder){
            if (check){
                //holder.mView.setEnabled(false);
                //holder.mView.setFocusable(false);
                holder.select.setVisibility(View.VISIBLE);
                holder.mView.setBackgroundColor(getResources().getColor(R.color.colorAccentDark));
                holder.mName.setTextColor(getResources().getColor(R.color.colorWhite));
            } else {
                //holder.mView.setFocusable(true);
                //holder.mView.setEnabled(true);
                holder.mView.setBackgroundColor(getResources().getColor(R.color.colorGone));
                holder.mName.setTextColor(getResources().getColor(R.color.colorDarkWhite));
            }
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final View mView, separ;
            final TextView mName;
            public final ImageView select;
            ItemMain.Item mItem;

            ViewHolder(View view) {
                super(view);
                mView = view;
                select = view.findViewById(R.id.select_list);
                separ = view.findViewById(R.id.separ);
                mName = view.findViewById(R.id.content);
            }
        }
    }
}
