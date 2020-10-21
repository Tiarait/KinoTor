package com.kinotor.tiar.kinotor.ui;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.parser.GetQualBluRay;
import com.kinotor.tiar.kinotor.parser.catalog.ParserAnidub;
import com.kinotor.tiar.kinotor.parser.catalog.ParserAnimevost;
import com.kinotor.tiar.kinotor.parser.catalog.ParserColdfilm;
import com.kinotor.tiar.kinotor.parser.catalog.ParserFanserials;
import com.kinotor.tiar.kinotor.parser.catalog.filmix.ParserFilmix;
import com.kinotor.tiar.kinotor.parser.catalog.filmix.ParserFilmixFavCheck;
import com.kinotor.tiar.kinotor.parser.catalog.ParserKinoFS;
import com.kinotor.tiar.kinotor.parser.catalog.ParserKinodom;
import com.kinotor.tiar.kinotor.parser.catalog.ParserKinolive;
import com.kinotor.tiar.kinotor.parser.catalog.ParserKinopoisk;
import com.kinotor.tiar.kinotor.parser.catalog.ParserKinopub;
import com.kinotor.tiar.kinotor.parser.catalog.ParserKinoxa;
import com.kinotor.tiar.kinotor.parser.catalog.ParserKoshara;
import com.kinotor.tiar.kinotor.parser.catalog.ParserMyhit;
import com.kinotor.tiar.kinotor.parser.catalog.ParserRufilmtv;
import com.kinotor.tiar.kinotor.parser.catalog.ParserTopkino;
import com.kinotor.tiar.kinotor.parser.catalog.filmix.ParserFilmixHistUpd;
import com.kinotor.tiar.kinotor.parser.video.filmix.FilmixDB;
import com.kinotor.tiar.kinotor.ui.fragments.DetailInfo;
import com.kinotor.tiar.kinotor.ui.fragments.DetailTorrents;
import com.kinotor.tiar.kinotor.ui.fragments.DetailVideo;
import com.kinotor.tiar.kinotor.utils.DBHelper;
import com.kinotor.tiar.kinotor.utils.Utils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DetailActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private String url_poster = "error", title = "error",
            quality = "error", rating = "error", season = "error", serie = "error";
    public static String url = "error";
    private ItemHtml itempath;
    private LinearLayout pb, leftSide;
    private Fragment fVid, fTor, fInf;
    private TabLayout tabLayout;
    int countLoad = 0;
    SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Statics.adbWached = false;
        Statics.MOON_ID = "error";
        Statics.KP_ID = "error";
        ItemMain.xs_value = "";
        preference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (preference.getBoolean("fullscreen", false)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        countLoad = 0;
        setContentView(R.layout.activity_detail);

        Bundle bundle = getIntent().getExtras();

        Toolbar toolbar = findViewById(R.id.toolbar_d);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        pb = findViewById(R.id.detail_pb);
        pb.setVisibility(View.VISIBLE);

        if (preference.getBoolean("sync_filmix_watch", false) &&
                !Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted"))
            new ParserFilmixHistUpd().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        if (bundle != null) {
            if (bundle.getString("Url") != null) {
                url = Utils.urlDecode(bundle.getString("Url"));

                if (url.contains("anidub")) {
                    toolbar.setSubtitle("anidub");
                } else if (url.contains("kinopub")) {
                    toolbar.setSubtitle("kinopub");
                } else if (url.contains("http://www.")) {
                    toolbar.setSubtitle(url.split("http://www\\.")[1].split("\\.")[0]);
                } else if (url.contains("http://")) {
                    toolbar.setSubtitle(url.split("http://")[1].split("\\.")[0]);
                } else if (url.contains("https://"))
                    toolbar.setSubtitle(url.split("https://")[1].split("\\.")[0]);
            }
            if (bundle.getString("Img") != null) {
                url_poster = bundle.getString("Img");
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ImageView poster = findViewById(R.id.imgPoster_d);
                    if (poster != null) {
                        poster.setTransitionName("poster");
                        Picasso.get()
                                .load(bundle.getString("Img"))
                                .into(poster);
                    }
                }
            }
            if (bundle.getString("Title") != null)
                title = bundle.getString("Title");
            if (bundle.getString("Season") != null)
                season = bundle.getString("Season");
            if (bundle.getString("Serie") != null)
                serie = bundle.getString("Serie");
            SharedPreferences.Editor editor = preference.edit();
            editor.putString("last_url", url);
            editor.apply();
            if (url.contains(Statics.FILMIX_URL) || url.toLowerCase().contains("filmix.")) {
                ParserFilmixFavCheck filmixFavCheck = new ParserFilmixFavCheck(url, location -> {
                    if (location.contains("favor")) {
                        addToDB("favor");
                    }
                    if (location.contains("later")) {
                        addToDB("later");
                    }
                    invalidateOptionsMenu();
                    onAttachedToWindow();
                });
                filmixFavCheck.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            setInfo();
        }

        dbHelper = new DBHelper(this);

        final ViewPager mViewPager = findViewById(R.id.container);
        final SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setNextFocusUpId(R.id.tabs);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        if (preference.getBoolean("db_cache", true) && !Statics.refreshMain) {
            if (dbHelper.getRepeatCache(url)) {
                if (dbHelper.getDbItemsCache(url).getSeason(0) == 0)
                    taskDone(mViewPager, mSectionsPagerAdapter, dbHelper.getDbItemsCache(url));
                else getItem(mViewPager, mSectionsPagerAdapter);
            } else getItem(mViewPager, mSectionsPagerAdapter);
        } else {
            if (dbHelper.getRepeatCache(url))
                dbHelper.deleteCache(url);
            Statics.refreshMain = false;
            getItem(mViewPager, mSectionsPagerAdapter);
        }
    }

    public void getItem(final ViewPager mViewPager, final SectionsPagerAdapter mSectionsPagerAdapter) {
        countLoad ++;
        if (url.toLowerCase().contains("animevost.") || url.contains(Statics.ANIMEVOST_URL)) {
            ParserAnimevost parserAnimevost = new ParserAnimevost(url, null, new ItemHtml(),
                    (items, itempath) -> taskDone(mViewPager, mSectionsPagerAdapter, itempath));
            parserAnimevost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("anidub.") || url.contains(Statics.ANIDUB_URL)) {
            ParserAnidub parserAnidub = new ParserAnidub(url, null, new ItemHtml(),
                    (items, itempath) -> taskDone(mViewPager, mSectionsPagerAdapter, itempath));
            parserAnidub.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("kino-fs.") || url.contains(Statics.KINOFS_URL)) {
            ParserKinoFS parserKinoFS = new ParserKinoFS(url, null, new ItemHtml(),
                    (items, itempath) -> taskDone(mViewPager, mSectionsPagerAdapter, itempath));
            parserKinoFS.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("filmix.") || url.contains(Statics.FILMIX_URL)) {
            ParserFilmix parserFilmix = new ParserFilmix(url, null, new ItemHtml(),
                    (items, itempath) -> taskDone(mViewPager, mSectionsPagerAdapter, itempath));
            parserFilmix.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("kinoxa.") || url.contains(Statics.KINOXA_URL)) {
            ParserKinoxa parser = new ParserKinoxa(url, null, new ItemHtml(),
                    (items, itempath) -> taskDone(mViewPager, mSectionsPagerAdapter, itempath));
            parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("rufilmtv.") || url.contains(Statics.RUFILMTV_URL)) {
            ParserRufilmtv parser = new ParserRufilmtv(url, null, new ItemHtml(),
                    (items, itempath) -> taskDone(mViewPager, mSectionsPagerAdapter, itempath));
            parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("topkino.") || url.toLowerCase().contains("infilms.")
                || url.contains(Statics.TOPKINO_URL)) {
            ParserTopkino parser = new ParserTopkino(url, null, new ItemHtml(),
                    (items, itempath) -> taskDone(mViewPager, mSectionsPagerAdapter, itempath));
            parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("fanserials.") || url.contains(Statics.FANSERIALS_URL)) {
            ParserFanserials parser = new ParserFanserials(url, null, new ItemHtml(),
                    (items, itempath) -> taskDone(mViewPager, mSectionsPagerAdapter, itempath));
            parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("my-hit.") || url.contains(Statics.MYHIT_URL)) {
            ParserMyhit parser = new ParserMyhit(url, null, new ItemHtml(),
                    (items, itempath) -> taskDone(mViewPager, mSectionsPagerAdapter, itempath));
            parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("octopushome.") || url.contains(Statics.KOSHARA_URL)) {
            ParserKoshara parserHtml = new ParserKoshara(url, null, new ItemHtml(),
                    (items, itempath) -> taskDone(mViewPager, mSectionsPagerAdapter, itempath));
            parserHtml.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("coldfilm.") || url.contains(Statics.COLDFILM_URL)) {
            ParserColdfilm coldfilm = new ParserColdfilm(url, null, new ItemHtml(),
                    (items, itempath) -> taskDone(mViewPager, mSectionsPagerAdapter, itempath));
            coldfilm.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("kinodom.") || url.contains(Statics.KINODOM_URL)) {
            ParserKinodom kinodom = new ParserKinodom(url, null, new ItemHtml(),
                    (items, itempath) -> taskDone(mViewPager, mSectionsPagerAdapter, itempath));
            kinodom.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("kinopoisk.")) {
            ParserKinopoisk kinopoisk = new ParserKinopoisk(url, null, new ItemHtml(),
                    (items, itempath) -> taskDone(mViewPager, mSectionsPagerAdapter, itempath));
            kinopoisk.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("kino-live2.") || url.contains(Statics.KINOLIVE_URL)) {
            ParserKinolive kinolive = new ParserKinolive(url, null, new ItemHtml(),
                    (items, itempath) -> taskDone(mViewPager, mSectionsPagerAdapter, itempath));
            kinolive.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("kino.pub") || url.contains(Statics.KINOPUB_URL)) {
            ParserKinopub kinopub = new ParserKinopub(url, null, new ItemHtml(),
                    (items, itempath) -> taskDone(mViewPager, mSectionsPagerAdapter, itempath));
            kinopub.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setMessage("Ошибка url адресса. Ожидайте исправлений.")
                    .setPositiveButton("Ок", (dialog, id) -> finish());
            builder.create().show();
        }
    }

    private void taskDone(ViewPager mViewPager, SectionsPagerAdapter mSectionsPagerAdapter, ItemHtml itempath) {
        if (itempath.title.size() > 0) {
            title = itempath.getTitle(0).trim();
            setTitle(title);
            DBHelper dbHelper = new DBHelper(this);

            if (dbHelper.getRepeatCache(url))
                dbHelper.deleteCache(url);
            dbHelper.Write();
            dbHelper.insertCacheWatch(itempath);

            try {
                itempath.setSeason(Integer.parseInt(season));
                itempath.getSeries(Integer.parseInt(serie));
            } catch (Exception ignored) {
            }
            this.itempath = itempath;
            addToDB("history");


            fInf = new DetailInfo().newInstance(itempath);
            mSectionsPagerAdapter.addFragment(fInf, "Информация");
            if (url.contains("person") && url.contains(Statics.FILMIX_URL) &&
                    itempath.getQuality(0).contains("error")) {
                Log.d("Detail", "taskDone: its actor");
            } else {
                if (preference.getBoolean("tab_video", true)) {
                    fVid = new DetailVideo().newInstance(itempath);
                    mSectionsPagerAdapter.addFragment(fVid, "Видео");
                }
                if (preference.getBoolean("tab_torrent", true)) {
                    fTor = new DetailTorrents().newInstance(itempath, "");
                    mSectionsPagerAdapter.addFragment(fTor, "Торренты");
                }
            }
            mViewPager.getAdapter().notifyDataSetChanged();
            //переход на вкладку видео
//            mViewPager.setCurrentItem(1);
            mViewPager.getRootView().requestFocus();
            setInfo();
        } else if (countLoad != 2) {
            Toast.makeText(this, "Ошибка загрузки, пробуемеще раз", Toast.LENGTH_LONG).show();
            getItem(mViewPager, mSectionsPagerAdapter);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setMessage("Ошибка загрузки. Попробуйте поискать данное видео в другом каталоге.\n" +
                    preference.getString("last_url", "null"))
                    .setCancelable(false)
                    .setNegativeButton("Повторить", (dialog, id) -> {
                        url = preference.getString("last_url", "null");
                        getItem(mViewPager, mSectionsPagerAdapter);
                    })
                    .setPositiveButton("Ок", (dialog, id) -> finish());
            builder.create().show();
        }
    }

    private void setInfo() {
        invalidateOptionsMenu();
        onAttachedToWindow();

        ImageView posterBg = findViewById(R.id.imgPoster_bg);
        ImageView poster = findViewById(R.id.imgPoster_d);
        leftSide = findViewById(R.id.leftSide);
        Utils utils = new Utils();
        TextView t_rating = findViewById(R.id.rating);
        TextView t_year = findViewById(R.id.year);
        TextView t_country = findViewById(R.id.country);
        TextView t_genre = findViewById(R.id.genre);
        TextView t_time = findViewById(R.id.time);
        TextView t_translator = findViewById(R.id.translator);
        TextView t_quality = findViewById(R.id.quality);
        LinearLayout l_year = findViewById(R.id.l_year);
        LinearLayout l_country = findViewById(R.id.l_country);
        LinearLayout l_genre = findViewById(R.id.l_genre);
        LinearLayout l_time = findViewById(R.id.l_time);
        LinearLayout l_quality = findViewById(R.id.l_quality);
        LinearLayout l_rating = findViewById(R.id.l_rating);
        LinearLayout l_translator = findViewById(R.id.l_translator);
        LinearLayout l_btn = findViewById(R.id.l_btn);
        Button play = findViewById(R.id.btn_play);
        Button trailer = findViewById(R.id.btn_trailer);

        if (utils.isTablet(this) && poster != null) {
            Picasso.get()
                    .load(url_poster)
                    .into(poster);
            Picasso.get()
                    .load(url_poster)
                    .into(posterBg);
            t_quality.setVisibility(View.GONE);
            poster.setNextFocusRightId(R.id.tabs);


            String tab = "";
            if ((utils.isTablet(this) &&
                    getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ||
                    itempath == null)
                tab = "\t";
            if (itempath != null) {
                Picasso.get()
                        .load(itempath.getImg(0))
                        .into(poster, new Callback() {
                            @Override
                            public void onSuccess() {
                                posterBg.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                poster.setFocusable(true);
                poster.setOnFocusChangeListener((view, b) -> {
                    if (!view.isSelected()) {
                        findViewById(R.id.img_fg_poster).setVisibility(View.VISIBLE);
                    }
                    else findViewById(R.id.img_fg_poster).setVisibility(View.GONE);
                    view.setSelected(b);
                });
                poster.setOnClickListener(view -> {
                    String imgs;
                    if (itempath.preimg.toString().equals("[]"))
                        imgs = itempath.getImg(0) + ",http://leeford.in/wp-content/uploads/2017/09/image-not-found.jpg";
                    else imgs = itempath.getImg(0) + "," + itempath.preimg.toString().replace("[","")
                            .replace("]","");
                    Intent intent = new Intent(this, ImgActivity.class);
                    intent.putExtra("Img", imgs);
                    intent.putExtra("Position", 0);
                    this.startActivity(intent);
                });

                l_year.setVisibility(View.VISIBLE);
                l_country.setVisibility(View.VISIBLE);
                l_genre.setVisibility(View.VISIBLE);
                l_time.setVisibility(View.VISIBLE);
                l_quality.setVisibility(View.VISIBLE);
                l_rating.setVisibility(View.VISIBLE);
                if (itempath.getDate(0).contains("error"))
                    l_year.setVisibility(View.GONE);
                else if (utils.isTablet(this) && getResources().getConfiguration().orientation == 2)
                    t_year.setText(tab + itempath.getDate(0).trim().replace("/", " - "));
                else t_year.setText(tab + itempath.getDate(0).trim().replace("/", "\n"));
                if (itempath.getCountry(0).contains("error"))
                    l_country.setVisibility(View.GONE);
                else t_country.setText(tab + itempath.getCountry(0).trim());
                if (itempath.getGenre(0).contains("error"))
                    l_genre.setVisibility(View.GONE);
                else t_genre.setText(tab + itempath.getGenre(0).trim());
                if (itempath.getTime(0).contains("error"))
                    l_time.setVisibility(View.GONE);
                else t_time.setText(tab + itempath.getTime(0).trim());
                if (itempath.getQuality(0).contains("error"))
                    l_quality.setVisibility(View.GONE);
                else quality = tab + itempath.getQuality(0);
                if (itempath.getRating(0).contains("error"))
                    l_rating.setVisibility(View.GONE);
                else {
                    try {
                        t_rating.setText(tab + itempath.getRating(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (itempath.getVoice(0).contains("error"))
                    l_translator.setVisibility(View.GONE);
                else t_translator.setText(tab + itempath.getVoice(0).trim());
                if (quality.contains("error"))
                    l_quality.setVisibility(View.GONE);
                else {
                    t_quality.setText(tab + quality.trim());
                    t_quality.setVisibility(View.VISIBLE);
                    l_quality.setVisibility(View.VISIBLE);
                }
                if (preference.getBoolean("check_qual", true) && itempath.getSubTitle(0) != null) {
                    String finalTab = tab;
                    GetQualBluRay getQualBluRay = new GetQualBluRay(itempath.getSubTitle(0), location -> {
                        if (!location.equals("null")) t_quality.setText(finalTab + location);
                        else t_quality.setText(finalTab + itempath.getQuality(0) + "!");
                    });
                    getQualBluRay.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                pb.setVisibility(View.GONE);
            }
        }
    }

    private void addToDBbtn(MenuItem item) {
        if (!dbHelper.getRepeat("favor", title)) {
            addToDB("favor");
            if (DetailActivity.url.contains(Statics.FILMIX_URL) &&
                    !Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted")) {
                FilmixDB filmixDB = new FilmixDB(true, "favor");
                filmixDB.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            item.setIcon(R.drawable.ic_menu_fav);
            Toast.makeText(this,
                    "Добавленно в избранное", Toast.LENGTH_LONG).show();
        } else if (dbHelper.getRepeat("favor", title)) {
            dbHelper.delete("favor", title);
            if (DetailActivity.url.contains(Statics.FILMIX_URL) &&
                    !Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted")) {
                FilmixDB filmixDB = new FilmixDB(false, "favor");
                filmixDB.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            item.setIcon(R.drawable.ic_menu_fav_add);
            Toast.makeText(this,
                    "Удалено из избранного", Toast.LENGTH_LONG).show();
        }
        invalidateOptionsMenu();
        onAttachedToWindow();
    }

    private void addToDB(String db) {
        DBHelper dbHelper = new DBHelper(this);
        if (dbHelper.getRepeat(db, title))
            dbHelper.delete(db, title);
        dbHelper.Write();
        try {
            dbHelper.insert(db, title, itempath.getImg(0), url, itempath.getVoice(0), itempath.getQuality(0),
                    itempath.getSeason(0), itempath.getSeries(0));
        } catch (Exception o){
            dbHelper.insert(db, title, itempath.getImg(0), url, itempath.getVoice(0), itempath.getQuality(0),
                    0, 0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem fav = menu.findItem(R.id.fav);

        MenuItem sortTorrent = menu.findItem(R.id.set_sortTor);
        MenuItem sortVid = menu.findItem(R.id.set_sortVid);
        MenuItem dbVid = menu.findItem(R.id.menuDBVideo);
        MenuItem dbTor = menu.findItem(R.id.menuDBTorrent);
        MenuItem actorSearch = menu.findItem(R.id.menuSearchActor);
        menu.findItem(R.id.action_no_hist).setVisible(false);
        actorSearch.setVisible(itempath!=null && !itempath.title.isEmpty());


        MenuItem inFavor = menu.findItem(R.id.menuAddFavor);
        MenuItem inLater = menu.findItem(R.id.menuAddLater);
        MenuItem inHistory = menu.findItem(R.id.menuAddHistory);
        inFavor.setChecked(dbHelper.getRepeat("favor", title));
        inLater.setChecked(dbHelper.getRepeat("later", title));
        inHistory.setChecked(dbHelper.getRepeat("history", title));

        if (tabLayout != null){
            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
                @Override
                public void onTabSelected(TabLayout.Tab tab){
                    if (tab.getText() != null){
                        sortTorrent.setVisible(tab.getText().equals("Торренты"));
                        sortVid.setVisible(tab.getText().equals("Видео"));
                        dbTor.setVisible(tab.getText().equals("Торренты"));
                        dbVid.setVisible(tab.getText().equals("Видео"));
                    } else {
                        sortTorrent.setVisible(false);
                        sortVid.setVisible(false);
                        dbTor.setVisible(false);
                        dbVid.setVisible(false);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
        }

        try {
            actorSearch.setVisible(!itempath.getGenre(0).contains("error"));
        } catch (Exception ignored){}
        if (url.contains("person") && url.contains(Statics.FILMIX_URL))
            actorSearch.setVisible(false);
        if (dbHelper.getRepeat("favor", title))
            fav.setIcon(R.drawable.ic_menu_fav);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.fav:
                if (itempath != null && itempath.title.size() > 0) {
                    addToDBbtn(item);
                }
                break;
            case R.id.action_refresh:
                Statics.refreshMain = true;
                Intent intent = getIntent();
                overridePendingTransition(0, 0);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                finish();
                overridePendingTransition(0, 0);
                startActivity(intent);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActionCopy(MenuItem item) {
        if (itempath != null && itempath.title.size() > 0) {
            ArrayList<String> ctg = new ArrayList<>();
            if (!itempath.getTitle(0).contains("error"))
                ctg.add("Название");
            if (!itempath.getDirector(0).contains("error"))
                ctg.add("Режисер");
            if (!itempath.getActors(0).contains("error"))
                ctg.add("Актеры");
            if (!itempath.getGenre(0).contains("error"))
                ctg.add("Жанр");
            final String[] ctg_list = ctg.toArray(new String[ctg.size()]);
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this, R.style.MyAlertDialogStyle);
            builder.setTitle("Выберите категорию").setItems(ctg_list, (dialogInterface, i) -> {
                String copiedText = "";
                switch (ctg_list[i]) {
                    case "Название":
                        copiedText = title.split("\\(")[0];
                        break;
                    case "Режисер":
                        copiedText = itempath.getDirector(0);
                        break;
                    case "Актеры":
                        copiedText = itempath.getActors(0);
                        break;
                    case "Жанр":
                        copiedText = itempath.getGenre(0);
                        break;
                }
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    @SuppressWarnings("deprecation")
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) DetailActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard != null) clipboard.setText(copiedText);
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) DetailActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("TAG", copiedText);
                    if (clipboard != null) clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(DetailActivity.this, "Сделано", Toast.LENGTH_SHORT).show();
            });
            builder.create().show();
        }
    }

    public void onAddDB(MenuItem item) {
        if (itempath != null){
            if (item.getTitle().equals("Избранное")) {
                if (!dbHelper.getRepeat("favor", itempath.getTitle(0))) {
                    addToDB("favor");
                    if (DetailActivity.url.contains(Statics.FILMIX_URL) &&
                            !Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted")) {
                        FilmixDB filmixDB = new FilmixDB(true, "favor");
                        filmixDB.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    Toast.makeText(this,
                            "Добавленно в избранное", Toast.LENGTH_LONG).show();
                } else {
                    dbHelper.delete("favor", itempath.getTitle(0));
                    if (DetailActivity.url.contains(Statics.FILMIX_URL) &&
                            !Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted")) {
                        FilmixDB filmixDB = new FilmixDB(false, "favor");
                        filmixDB.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    Toast.makeText(this,
                            "Удалено из избранного", Toast.LENGTH_LONG).show();
                }
            } else if (item.getTitle().equals("Посмотреть позже")) {
                if (!dbHelper.getRepeat("later", itempath.getTitle(0))) {
                    addToDB("later");
                    if (DetailActivity.url.contains(Statics.FILMIX_URL) &&
                            !Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted")) {
                        FilmixDB filmixDB = new FilmixDB(true, "later");
                        filmixDB.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    Toast.makeText(this,
                            "Добавленно в посмотреть позже", Toast.LENGTH_LONG).show();
                } else {
                    dbHelper.delete("later", itempath.getTitle(0));
                    if (DetailActivity.url.contains(Statics.FILMIX_URL) &&
                            !Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted")) {
                        FilmixDB filmixDB = new FilmixDB(false, "later");
                        filmixDB.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    Toast.makeText(this,
                            "Удалено из посмотреть позже", Toast.LENGTH_LONG).show();
                }
            } else if (item.getTitle().equals("История")) {
                if (!dbHelper.getRepeat("history", itempath.getTitle(0))) {
                    addToDB("history");
                    Toast.makeText(this,
                            "Добавленно в историю", Toast.LENGTH_LONG).show();
                } else {
                    dbHelper.delete("history", itempath.getTitle(0));
                    Toast.makeText(this,
                            "Удалено из истории", Toast.LENGTH_LONG).show();
                }
            }
            invalidateOptionsMenu();
            onAttachedToWindow();
        } else Toast.makeText(this, "Дождитесь загрузки", Toast.LENGTH_SHORT).show();
    }
    public void onActionWeb(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
    public void onSearchActor(MenuItem item) {
        if (itempath != null && itempath.title.size() > 0) {
            final String[] actor = itempath.getActors(0).trim().replace(", ", ",")
                    .split(",");
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setTitle("Выберите актера").setItems(actor, (dialogInterface, i) -> {
                Statics.refreshMain = true;
                Intent intent = new Intent(DetailActivity.this, MainCatalogActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Type", "actor");
                intent.putExtra("Query", actor[i].trim());
                DetailActivity.this.startActivity(intent);
            });
            builder.create().show();
        } else Toast.makeText(this, "Дождитесь загрузки", Toast.LENGTH_SHORT).show();
    }
    public void onSearchGoogle(MenuItem item) {
        String url = "https://www.google.com.ua/search?q=" +
                getTitle().toString().replace(" ", "+") + " смотреть онлайн";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
    public void onSearchKinopoisk(MenuItem item) {
        if (itempath != null){
            String url;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if (!Statics.KP_ID.contains("error")){
                try {
                    url = "kp://filmDetail/" + Statics.KP_ID + "/";
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (Exception e) {
                    url = "https://www.kinopoisk.ru/film/" + Statics.KP_ID + "/";
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }

            } else {
                url = "https://www.kinopoisk.ru/index.php?kp_query=" + itempath.getTitle(0) + "&what=";
                intent.setData(Uri.parse(url));
                startActivity(intent);
            }
        } else Toast.makeText(this, "Дождитесь загрузки", Toast.LENGTH_SHORT).show();
    }
    public void onSearchYoutube(MenuItem item) {
        String url =  "https://www.youtube.com/results?search_query=" +
                getTitle().toString().replace(" ", "+");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (tabLayout != null) {
            if (Statics.itemsVidVoice != null) {
                if (tabLayout.getSelectedTabPosition() == 1 & fVid != null) {
                    Statics.backClick = true;
                    fVid.onResume();
                } else onInfoTab();
            } else onInfoTab();
        } else super.onBackPressed();
    }

    private void onInfoTab (){
        if (tabLayout.getSelectedTabPosition() != 0) {
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            if (tab != null)
                tab.select();
            else super.onBackPressed();
        } else
            super.onBackPressed();
    }

    public void onSortTor(MenuItem item) {
        String[] allList = getBaseContext().getResources().getStringArray(R.array.pref_list_filtr_tor);
        String[] allListVal = getBaseContext().getResources().getStringArray(R.array.pref_val_filtr_tor);
        final String[] cur = {preference.getString("filter_tor", "none")};
        int index = new ArrayList<>(Arrays.asList(allListVal)).indexOf(cur[0].trim());

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setSingleChoiceItems(allList, index, (dialog, i) -> {
            cur[0] = allListVal[i] ;
        }).setPositiveButton("Применить", (dialog, id) -> {
            SharedPreferences.Editor editor = preference.edit();
            if (allListVal[id].equals("words")){
                AlertDialog.Builder w = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
                final EditText input = new EditText(this);
                input.setText(preference.getString("filter_tor_word", ""));
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                lp.setMargins(16,0,16,0);
                input.setTextColor(getResources().getColor(R.color.colorWhite));
                input.setLines(1);
                input.setLayoutParams(lp);
                w.setView(input);
                w.setTitle("Введите текст для фильтра");
                w.setPositiveButton("Применить", (dialogInterface, i1) -> {
                    editor.putString("filter_tor_word", input.getText().toString());
                    editor.putString("filter_tor", allListVal[id]);
                    editor.apply();
                    if (fTor != null) {
                        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.detach(fTor);
                        ft.attach(fTor);
                        ft.commit();
                    }
                });
                w.setNegativeButton("Отмена", (dialogInterface, i1) -> {
                    dialog.dismiss();
                });
                w.show();
            } else {
                editor.putString("filter_tor", cur[0]);
                editor.apply();
                if (fTor != null) {
                    final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    ft.detach(fTor);
                    ft.attach(fTor);
                    ft.commit();
                }
                dialog.dismiss();
            }
        }).setNegativeButton("Отмена", (dialog, id) -> {
            dialog.dismiss();
        }).create().show();
    }
    public void onSortVid(MenuItem item) {
        String[] allList = getBaseContext().getResources().getStringArray(R.array.pref_list_filtr_vid);
        String[] allListVal = getBaseContext().getResources().getStringArray(R.array.pref_val_filtr_vid);
        final String[] cur = {preference.getString("filter_vid", "none")};
        int index = new ArrayList<>(Arrays.asList(allListVal)).indexOf(cur[0].trim());

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setSingleChoiceItems(allList, index, (dialog, i) -> {
            SharedPreferences.Editor editor = preference.edit();
            editor.putString("filter_vid", allListVal[i]);
            editor.apply();
            if (fVid != null) {
                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.detach(fVid);
                ft.attach(fVid);
                ft.commit();
            }
            dialog.dismiss();
        }).create().show();
    }

    public void onDBTorrent(MenuItem item) {
        Set<String> pref_base;
        String[] vidBaseArr = {"tparser", "freerutor", "nnm", "bitru", "yohoho", "rutor(orig)", "megapeer",
                "piratbit", "kinozal", "hurtom", "torlook", "greentea", "rutracker"};

        HashSet<String> def = new HashSet<>(Arrays.asList(vidBaseArr));
        pref_base = preference.getStringSet("base_tparser", def);
        String vidBase = pref_base.toString();


        final String[] availableTypes = {"anidub", "tparser", "freerutor", "nnm", "bitru", "yohoho", "rutor(orig)", "megapeer",
                "piratbit", "kinozal", "hurtom", "torlook", "greentea", "rutracker"};
        final boolean[] selectedTypes = {
                boolTF(vidBase, "tparser"),
                boolTF(vidBase, "anidub"),
                boolTF(vidBase, "freerutor"),
                boolTF(vidBase, "nnm"),
                boolTF(vidBase, "bitru"),
                boolTF(vidBase, "yohoho"),
                boolTF(vidBase, "rutor(orig)"),
                boolTF(vidBase, "megapeer"),
                boolTF(vidBase, "piratbit"),
                boolTF(vidBase, "kinozal"),
                boolTF(vidBase, "hurtom"),
                boolTF(vidBase, "torlook"),
                boolTF(vidBase, "greentea"),
                boolTF(vidBase, "rutracker")};

        Statics.torrentBase =
                stringTF("anidub",boolTF(vidBase, "anidub")) +
                stringTF("tparser",boolTF(vidBase, "tparser")) +
                stringTF("freerutor",boolTF(vidBase, "freerutor")) +
                stringTF("nnm",boolTF(vidBase, "nnm")) +
                stringTF("bitru",boolTF(vidBase, "bitru")) +
                stringTF("yohoho",boolTF(vidBase, "yohoho")) +
                stringTF("rutor(orig)",boolTF(vidBase, "rutor(orig)")) +
                stringTF("megapeer",boolTF(vidBase, "megapeer")) +
                        stringTF("piratbit",boolTF(vidBase, "piratbit")) +
                        stringTF("kinozal",boolTF(vidBase, "kinozal")) +
                        stringTF("hurtom",boolTF(vidBase, "hurtom")) +
                        stringTF("torlook",boolTF(vidBase, "torlook")) +
                        stringTF("greentea",boolTF(vidBase, "greentea")) +
                        stringTF("rutracker",boolTF(vidBase, "rutracker"));

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setMultiChoiceItems(availableTypes, selectedTypes,
                (dialog, i, isChecked) -> {
                    if (boolTF(Statics.torrentBase, availableTypes[i])){
                        Statics.torrentBase = Statics.torrentBase.replace(availableTypes[i], "");
                    } else Statics.torrentBase += " " + availableTypes[i] + " ";
                }
        ).setPositiveButton("Применить", (dialog, id) -> {
            Set<String> set = new HashSet<>(Arrays. asList(Statics.torrentBase.trim().split(" ")));
            SharedPreferences.Editor editor = preference.edit();
            editor.putStringSet("base_tparser", set);
            editor.apply();
            if (fTor!=null) {
                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.detach(fTor);
                ft.attach(fTor);
                ft.commit();
            }
        }).setNegativeButton("Отмена", (dialog, id) -> {
            dialog.dismiss();
        }).create().show();
    }

    public void onDBVideo(MenuItem item) {
        Set<String> pref_base;
        String[] vidBaseArr = {"hdgo", "moonwalk", "filmix", "hdbaza", "kinolive", "kinodom", "zombiefilm"};

        HashSet<String> def = new HashSet<>(Arrays.asList(vidBaseArr));
        pref_base = preference.getStringSet("base_video", def);
        String vidBase = pref_base.toString();

        final String[] availableTypes = new String[]{"kinosha", "filmix", "moonwalk", "hdgo",
                "hdbaza", "kinohd", "kinolive","kinopub", "kinodom", "zombiefilm", "anidub", "animedia", "animevost"};
        final boolean[] selectedTypes = new boolean[]{boolTF(vidBase, "kinosha"),
                boolTF(vidBase, "filmix"), boolTF(vidBase, "moonwalk"),
                boolTF(vidBase, "hdgo"), boolTF(vidBase, "hdbaza"),
                boolTF(vidBase, "kinohd"), boolTF(vidBase, "kinolive"), boolTF(vidBase, "kinopub"),
                boolTF(vidBase, "kinodom"), boolTF(vidBase, "zombiefilm"), boolTF(vidBase, "anidub"),
                boolTF(vidBase, "animedia"), boolTF(vidBase, "animevost")};
        Statics.videoBase =
                stringTF("kinosha",boolTF(vidBase, "kinosha")) +
                        stringTF("filmix",boolTF(vidBase, "filmix")) +
                        stringTF("moonwalk",boolTF(vidBase, "moonwalk")) +
                        stringTF("hdgo",boolTF(vidBase, "hdgo")) +
                        stringTF("hdbaza",boolTF(vidBase, "hdbaza")) +
                        stringTF("kinohd",boolTF(vidBase, "kinohd")) +
                        stringTF("kinolive",boolTF(vidBase, "kinolive")) +
                        stringTF("kinopub",boolTF(vidBase, "kinopub")) +
                        stringTF("kinodom",boolTF(vidBase, "kinodom")) +
                        stringTF("zombiefilm",boolTF(vidBase, "zombiefilm")) +
                        stringTF("anidub",boolTF(vidBase, "anidub")) +
                        stringTF("animedia",boolTF(vidBase, "animedia")) +
                        stringTF("animevost",boolTF(vidBase, "animevost"));
        Statics.videoBase = Statics.videoBase.replace("  ", " ").trim();

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        builder.setMultiChoiceItems(availableTypes, selectedTypes,
                (dialog, i, isChecked) -> {
                    if (boolTF(Statics.videoBase, availableTypes[i])){
                        Statics.videoBase = Statics.videoBase.replace(availableTypes[i], "").trim();
                    } else Statics.videoBase += " " + availableTypes[i] +" ";
                }
        ).setPositiveButton("Применить", (dialog, id) -> {
            Set<String> set = new HashSet<>(Arrays. asList(Statics.videoBase.trim()
                    .replace("  "," ").split(" ")));
            SharedPreferences.Editor editor = preference.edit();
            editor.putStringSet("base_video", set);
            editor.apply();

            if (fVid!=null) {
                Statics.refreshMain = true;
                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.detach(fVid);
                ft.attach(fVid);
                ft.commit();
            }
        }).setNegativeButton("Отмена", (dialog, id) -> {
            dialog.dismiss();
        }).create().show();
    }

    public boolean boolTF(String sours, String val) {
        return sours.contains(val);
    }
    public String stringTF(String val, boolean t) {
        if (t) return val + " ";
        else return "";
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (fInf!=null && new Utils().isTablet(this)) {
            if (leftSide!= null) {
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                    leftSide.setVisibility(View.GONE);
                else leftSide.setVisibility(View.VISIBLE);
            }
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.detach(fInf);
            ft.attach(fInf);
            ft.commit();
        }
    }

    /**
     * that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
