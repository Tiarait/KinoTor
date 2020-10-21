package com.kinotor.tiar.kinotor.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.parser.GetKpId;
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
import com.kinotor.tiar.kinotor.parser.video.filmix.FilmixTrailerUrl;
import com.kinotor.tiar.kinotor.parser.video.filmix.FilmixUrl;
import com.kinotor.tiar.kinotor.parser.video.kinoxa.KinoxaTrailerUrl;
import com.kinotor.tiar.kinotor.utils.DBHelper;
import com.kinotor.tiar.kinotor.utils.RoundedTransformation;
import com.kinotor.tiar.kinotor.utils.Trailer;
import com.kinotor.tiar.kinotor.utils.Utils;
import com.kinotor.tiar.kinotor.utils.adapters.AdapterImages;
import com.kinotor.tiar.kinotor.utils.adapters.AdapterMore;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class DetailActivityTv extends AppCompatActivity {
    private static final String TAG = "DetailActivityTv";
    private SharedPreferences preference;
    private String url = "error";
    private DBHelper dbHelper;
    private ItemHtml itempath = null;
    private ScrollView sv;

    private int colorStatus = R.color.colorPrimaryLight;
    private int colorBg = R.color.colorPrimaryLight;
    private int colorBgImg = R.drawable.gradient_darkgone_dark;
    private int colorText = R.color.colorWhite;

    private static final int REQUEST_CODE_SIGN_IN = 0;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        preference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        ItemMain.xs_value = "";
        Statics.adbWached = false;
        Statics.MOON_ID = "error";
        Statics.KP_ID = "error";
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
        if (preference.getBoolean("fullscreen", false)) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.activity_detail_tv);
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        dbHelper = new DBHelper(this);


        final ConstraintLayout all = findViewById(R.id.main_content);
        final ImageView imageBg = findViewById(R.id.image_bg);
        final ImageView imagePoster = findViewById(R.id.image_poster);
        final TextView rateKp = findViewById(R.id.rate_kp);
        final TextView rateImdb = findViewById(R.id.rate_imdb);
        final TextView rateSite = findViewById(R.id.rate_site);
        final TextView title = findViewById(R.id.title);
        final TextView subtitle = findViewById(R.id.subtitle);
        final TextView genre = findViewById(R.id.genre);
        final TextView sound = findViewById(R.id.sound);
        final TextView director = findViewById(R.id.director);
        final TextView actor = findViewById(R.id.actor);
        final TextView titledesc = findViewById(R.id.title_desc);
        final TextView desc = findViewById(R.id.desc);
        final TextView quality = findViewById(R.id.quality);
        final TextView status = findViewById(R.id.status);
        final TextView other = findViewById(R.id.other);
        final TextView titlemore = findViewById(R.id.title_more);
        final TextView titleimg = findViewById(R.id.title_image);
        final Button play = findViewById(R.id.btn_play);
        final Button trailer = findViewById(R.id.btn_trailer);
        final ImageButton fav = findViewById(R.id.btn_fav);
        final ImageButton later = findViewById(R.id.btn_later);
        final View imgBg = findViewById(R.id.bg);

        if (preference.getString("theme_list", "gray").equals("white")){
            fav.setColorFilter(ContextCompat.getColor(this, colorText), android.graphics.PorterDuff.Mode.SRC_IN);
            later.setColorFilter(ContextCompat.getColor(this, colorText), android.graphics.PorterDuff.Mode.SRC_IN);
            fav.setColorFilter(colorText);
            later.setColorFilter(colorText);
        }

        imgBg.setBackgroundDrawable(getResources().getDrawable(colorBgImg));

        all.setBackgroundColor(getResources().getColor(colorBg));
        rateKp.setTextColor(getResources().getColor(colorText));
        rateImdb.setTextColor(getResources().getColor(colorText));
        rateSite.setTextColor(getResources().getColor(colorText));
        title.setTextColor(getResources().getColor(colorText));
        subtitle.setTextColor(getResources().getColor(colorText));
        genre.setTextColor(getResources().getColor(colorText));
        sound.setTextColor(getResources().getColor(colorText));
        director.setTextColor(getResources().getColor(colorText));
        actor.setTextColor(getResources().getColor(colorText));
        titledesc.setTextColor(getResources().getColor(colorText));
        desc.setTextColor(getResources().getColor(colorText));
        status.setTextColor(getResources().getColor(colorText));
        other.setTextColor(getResources().getColor(colorText));
        titlemore.setTextColor(getResources().getColor(colorText));
        titleimg.setTextColor(getResources().getColor(colorText));
        int sizetext = Integer.parseInt(preference.getString("text_size_detail", "16"));
        rateKp.setTextSize(sizetext);
        rateImdb.setTextSize(sizetext);
        rateSite.setTextSize(sizetext);
        title.setTextSize(sizetext + 2);
        subtitle.setTextSize(sizetext + 2);
        genre.setTextSize(sizetext);
        sound.setTextSize(sizetext);
        director.setTextSize(sizetext);
        actor.setTextSize(sizetext);
        titledesc.setTextSize(sizetext);
        desc.setTextSize(sizetext);
        quality.setTextSize(sizetext);
        status.setTextSize(sizetext);
        other.setTextSize(sizetext);
        titlemore.setTextSize(sizetext);
        titleimg.setTextSize(sizetext);
        play.setTextSize(sizetext);
        trailer.setTextSize(sizetext);


        if (preference.getBoolean("sync_filmix_watch", false) &&
                !Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted"))
            new ParserFilmixHistUpd().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = Utils.urlDecode(bundle.getString("Url"));
            DetailActivity.url = url;

            if (bundle.getString("Url") != null) {
                if (bundle.getString("Url").contains("anidub")) {
                    toolbar.setSubtitle("anidub");
                } else if (bundle.getString("Url").contains("kinopub")) {
                    toolbar.setSubtitle("kinopub");
                } else if (bundle.getString("Url").contains("http://www.")) {
                    toolbar.setSubtitle(bundle.getString("Url").split("http://www\\.")[1].split("\\.")[0]);
                } else if (bundle.getString("Url").contains("http://")) {
                    toolbar.setSubtitle(bundle.getString("Url").split("http://")[1].split("\\.")[0]);
                } else if (bundle.getString("Url").contains("https://"))
                    toolbar.setSubtitle(bundle.getString("Url").split("https://")[1].split("\\.")[0]);
            }

            if (preference.getBoolean("tv_activity_detail_bg", true)) {
                Picasso.get()
                        .load(bundle.getString("Img"))
                        .into(imageBg);
            } else imageBg.setVisibility(View.GONE);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                imagePoster.setTransitionName("poster");
                Picasso.get()
                        .load(bundle.getString("Img"))
                        .transform(new RoundedTransformation(15, 4))
                        .into(imagePoster);
            }

            title.setText(bundle.getString("Title"));

            SharedPreferences.Editor editor = preference.edit();
            editor.putString("last_url", url);
            editor.apply();

            if (url != null) {
                if (url.contains(Statics.FILMIX_URL) || url.toLowerCase().contains("filmix.")) {
                    ParserFilmixFavCheck filmixFavCheck = new ParserFilmixFavCheck(url, location -> {
                        if (location.contains("favor")) {
                            addToDB("favor");
                            fav.setImageResource(R.drawable.ic_menu_fav);
                        }
                        if (location.contains("later")) {
                            addToDB("later");
                            later.setImageResource(R.drawable.ic_menu_flag);
                        }
                    });
                    filmixFavCheck.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        }


        sv = findViewById(R.id.scrollview);
        sv.setEnabled(false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sv.setNestedScrollingEnabled(false);
        }

        scale(play, 0.85f);
        scale(findViewById(R.id.btn_trailer), 0.85f);
        scale(findViewById(R.id.btn_fav), 0.85f);
        scale(findViewById(R.id.btn_later), 0.85f);
        play.setOnFocusChangeListener((view, b) -> scaleFocus(view, b, 0.85f, 1f));
        findViewById(R.id.btn_trailer).setOnFocusChangeListener((view, b) -> scaleFocus(view, b, 0.85f, 1f));
        findViewById(R.id.btn_fav).setOnFocusChangeListener((view, b) -> scaleFocus(view, b, 0.85f, 1f));
        findViewById(R.id.btn_later).setOnFocusChangeListener((view, b) -> scaleFocus(view, b, 0.85f, 1f));

        LinearLayout top = findViewById(R.id.top);
        top.setOnFocusChangeListener((view, b) -> {
            Log.e(TAG, "test: "+play.isSelected() + view.isSelected());
            play.requestFocus();
        });

        play.requestFocus();

        final RelativeLayout imagePosterR = findViewById(R.id.image_poster_r);
        scale(imagePosterR, 0.85f);
        imagePoster.setOnFocusChangeListener((view, b) -> scaleFocus(imagePosterR, b, 0.85f, 1f));
        if (preference.getBoolean("db_cache", true)) {
            if (dbHelper.getRepeatCache(url)) {
                ItemHtml itemHtml = dbHelper.getDbItemsCache(url);
                if (itemHtml != null) {
                    if (!itemHtml.season.isEmpty()) {
                        if (itemHtml.getSeason(0) == 0)
                            loadDone(dbHelper.getDbItemsCache(url));
                        else load();
                    } else load();
                } else load();
            } else load();
        } else {
            if (dbHelper.getRepeatCache(url))
                dbHelper.deleteCache(url);
            load();
        }
    }

    private void scaleFocus(View view, boolean b, float out, float zoom) {
        if (view.isSelected()) {
            view.setScaleX(out);
            view.setScaleY(out);
        } else {
            view.setScaleX(zoom);
            view.setScaleY(zoom);
        }
        view.setSelected(b);
    }
    private void scale(View view, float zoom) {
        view.setScaleX(zoom);
        view.setScaleY(zoom);
    }

    private void reload(){
        findViewById(R.id.detail_pb).setVisibility(View.VISIBLE);
        if (dbHelper.getRepeatCache(url))
            dbHelper.deleteCache(url);
        load();
    }
    
    private void load(){
        if (url.toLowerCase().contains("animevost.") || url.contains(Statics.ANIMEVOST_URL)) {
            Log.e("DetailActivity", "loadDone 2");
            ParserAnimevost parserAnimevost = new ParserAnimevost(url, null, new ItemHtml(),
                    (items, itempath) -> loadDone(itempath));
            parserAnimevost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("anidub.") || url.contains(Statics.ANIDUB_URL)) {
            Log.e("DetailActivity", "loadDone 3");
            ParserAnidub parserAnidub = new ParserAnidub(url, null, new ItemHtml(),
                    (items, itempath) -> loadDone(itempath));
            parserAnidub.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("kino-fs.") || url.contains(Statics.KINOFS_URL)) {
            Log.e("DetailActivity", "loadDone 5");
            ParserKinoFS parserKinoFS = new ParserKinoFS(url, null, new ItemHtml(),
                    (items, itempath) -> loadDone(itempath));
            parserKinoFS.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("filmix.") || url.contains(Statics.FILMIX_URL)) {
            Log.e("DetailActivity", "loadDone 6 " + url);
            ParserFilmix parserFilmix = new ParserFilmix(url, null, new ItemHtml(),
                    (items, itempath) -> loadDone(itempath));
            parserFilmix.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("kinoxa.") || url.contains(Statics.KINOXA_URL)) {
            Log.e("DetailActivity", "loadDone 7");
            ParserKinoxa parser = new ParserKinoxa(url, null, new ItemHtml(),
                    (items, itempath) -> loadDone(itempath));
            parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("rufilmtv.") || url.contains(Statics.RUFILMTV_URL)) {
            Log.e("DetailActivity", "loadDone 8");
            ParserRufilmtv parser = new ParserRufilmtv(url, null, new ItemHtml(),
                    (items, itempath) -> loadDone(itempath));
            parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("topkino.")  || url.toLowerCase().contains("infilms.") || url.contains(Statics.TOPKINO_URL)) {
            Log.e("DetailActivity", "loadDone 9");
            ParserTopkino parser = new ParserTopkino(url, null, new ItemHtml(),
                    (items, itempath) -> loadDone(itempath));
            parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("fanserials.") || url.contains(Statics.FANSERIALS_URL)) {
            Log.e("DetailActivity", "loadDone 10");
            ParserFanserials parser = new ParserFanserials(url, null, new ItemHtml(),
                    (items, itempath) -> loadDone(itempath));
            parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("my-hit.") || url.contains(Statics.MYHIT_URL)) {
            Log.e("DetailActivity", "loadDone 11");
            ParserMyhit parser = new ParserMyhit(url, null, new ItemHtml(),
                    (items, itempath) -> loadDone(itempath));
            parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("octopushome.") || url.contains(Statics.KOSHARA_URL)) {
            Log.e("DetailActivity", "loadDone 12");
            ParserKoshara parserHtml = new ParserKoshara(url, null, new ItemHtml(),
                    (items, itempath) -> loadDone(itempath));
            parserHtml.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("coldfilm.") || url.contains(Statics.COLDFILM_URL)) {
            Log.e("DetailActivity", "loadDone 13");
            ParserColdfilm coldfilm = new ParserColdfilm(url, null, new ItemHtml(),
                    (items, itempath) -> loadDone(itempath));
            coldfilm.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("kino-dom.") || url.contains(Statics.KINODOM_URL)) {
            Log.e("DetailActivity", "loadDone 14");
            ParserKinodom kinodom = new ParserKinodom(url, null, new ItemHtml(),
                    (items, itempath) -> loadDone(itempath));
            kinodom.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("kinopoisk.")) {
            Log.e("DetailActivity", "loadDone 15");
            ParserKinopoisk kinopoisk = new ParserKinopoisk(url, null, new ItemHtml(),
                    (items, itempath) -> loadDone(itempath));
            kinopoisk.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("kino-live2.") || url.contains(Statics.KINOLIVE_URL)) {
            ParserKinolive kinolive = new ParserKinolive(url, null, new ItemHtml(),
                    (items, itempath) -> loadDone(itempath));
            kinolive.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.toLowerCase().contains("kino.pub") || url.contains(Statics.KINOPUB_URL)) {
            ParserKinopub kinopub = new ParserKinopub(url, null, new ItemHtml(),
                    (items, itempath) -> loadDone(itempath));
            kinopub.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Log.e("DetailActivity", "loadDone error url:"+url);
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setMessage("Ошибка url адресса. Ожидайте исправлений.")
                    .setPositiveButton("Ок", (dialog, id) -> finish());
            builder.create().show();
        }
    }

    private void loadDone(ItemHtml itempath) {
        if (itempath.title.size() > 0) {
            if (dbHelper.getRepeatCache(url))
                dbHelper.deleteCache(url);
            dbHelper.Write();
            dbHelper.insertCacheWatch(itempath);

            findViewById(R.id.detail_pb).setVisibility(View.GONE);
            sv.setEnabled(true);
//            try {
//                itempath.setSeason(Integer.parseInt(season));
//                itempath.getSeries(Integer.parseInt(serie));
//            } catch (Exception ignored) {
//            }

            this.itempath = itempath;
            addToDB("history");
            setInfo();
        } else {
            Log.e("DetailActivity", "loadDone error: "+itempath.title.size()+" url:"+url);
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setMessage("Ошибка загрузки. Попробуйте поискать данное видео в другом каталоге.\n" +
                    preference.getString("last_url", "null"))
                    .setNegativeButton("Повторить", (dialog, id) -> {
                        url = preference.getString("last_url", "null");
                        load();
                    })
                    .setPositiveButton("Ок", (dialog, id) -> finish());
            builder.create().show();
        }
    }

    private void setInfo() {
        invalidateOptionsMenu();
        onAttachedToWindow();
        Statics.KP_ID = itempath.getKpId();
        if ((Statics.KP_ID.contains("error") || Statics.KP_ID.isEmpty()) && Statics.MOON_ID.contains("error")) {
            GetKpId getList = new GetKpId(itempath, (n, m) -> { });
            getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

//        Log.d(TAG, "setInfo: start set");
        //--------------set---------------------------
        ImageView posterBg = findViewById(R.id.image_bg);
        ImageView poster = findViewById(R.id.image_poster);
        Picasso.get()
                .load(itempath.getImg(0))
                .transform(new RoundedTransformation(15, 4))
                .into(poster);
        if (preference.getBoolean("tv_activity_detail_bg", true))
            Picasso.get()
                    .load(itempath.getImg(0))
                    .into(posterBg);
        else posterBg.setVisibility(View.GONE);
        poster.setOnClickListener(view -> {
            String imgs;
            if (itempath.preimg.toString().equals("[]"))
                imgs = itempath.getImg(0);
            else imgs = itempath.getImg(0) + ", " + itempath.preimg.toString().replace("[","")
                    .replace("]","");
            Intent intent = new Intent(this, ImgActivity.class);
            intent.putExtra("Img", imgs);
            intent.putExtra("Position", 0);
            this.startActivity(intent);
        });
        TextView rateKp = findViewById(R.id.rate_kp);
        TextView rateImdb = findViewById(R.id.rate_imdb);
        TextView rateSite = findViewById(R.id.rate_site);
        TextView title = findViewById(R.id.title);
        TextView subtitle = findViewById(R.id.subtitle);
        TextView genre = findViewById(R.id.genre);
        TextView sound = findViewById(R.id.sound);
        TextView director = findViewById(R.id.director);
        TextView actor = findViewById(R.id.actor);
        TextView desc = findViewById(R.id.desc);
        TextView quality = findViewById(R.id.quality);
        TextView status = findViewById(R.id.status);
        TextView other = findViewById(R.id.other);
        Button play = findViewById(R.id.btn_play);
        Button trailer = findViewById(R.id.btn_trailer);

        //--------------rating------------------------

        if (itempath.getRating(0).contains("KP["))
            rateKp.setText(itempath.getRating(0).split("KP\\[")[1].split("\\]")[0].trim());
        if (itempath.getRating(0).contains("IMDB["))
            rateImdb.setText(itempath.getRating(0).split("IMDB\\[")[1].split("\\]")[0].trim());
        if (itempath.getRating(0).contains("SITE["))
            rateSite.setText(itempath.getRating(0).split("SITE\\[")[1].split("\\]")[0].trim());
        findViewById(R.id.rate_kp_l).setVisibility(Utils.boolToVisible(!rateKp.getText().toString().contains("error")));
        findViewById(R.id.rate_imdb_l).setVisibility(Utils.boolToVisible(!rateImdb.getText().toString().contains("error")));
        findViewById(R.id.rate_site_l).setVisibility(Utils.boolToVisible(!rateSite.getText().toString().contains("error")));
        //----------------TextView--------------------
        title.setText(itempath.getTitle(0).split("\\(")[0]);
        subtitle.setVisibility(Utils.boolToVisible(!itempath.getSubTitle(0).contains("error")));
        subtitle.setText(itempath.getSubTitle(0));
        genre.setVisibility(Utils.boolToVisible(!itempath.getGenre(0).contains("error")));
        genre.setText(itempath.getGenre(0));
        sound.setVisibility(Utils.boolToVisible(!itempath.getVoice(0).contains("error")));
        sound.setText(Html.fromHtml("<b>ЗВУК:</b> " + itempath.getVoice(0)));
        director.setVisibility(Utils.boolToVisible(!itempath.getDirector(0).contains("error")));
        director.setText(Html.fromHtml("<b>РЕЖИССЕР:</b> " + itempath.getDirector(0)));
        actor.setVisibility(Utils.boolToVisible(!itempath.getActors(0).contains("error")));
        actor.setText(Html.fromHtml("<b>В РОЛЯХ:</b> " + itempath.getActors(0)));
        desc.setText(Html.fromHtml(itempath.getDescription(0)));
        status.setVisibility(Utils.boolToVisible(true));
        status.setText("");
        if (itempath.getSeason(0) != 0) {
            if (itempath.getSeries(0) != 0) {
                status.append("s"+itempath.getSeason(0)+"e"+itempath.getSeries(0));
            } else status.append("сезон "+itempath.getSeason(0));
        } else if (itempath.getSeries(0) != 0) {
            status.append("серия "+itempath.getSeries(0));
        } else status.setVisibility(Utils.boolToVisible(false));
        String oth = "";
        if (!itempath.getDate(0).contains("error"))
            oth = itempath.getDate(0) + ", ";
        if (!itempath.getTime(0).contains("error"))
            oth += itempath.getTime(0) + ", ";
        if (!itempath.getCountry(0).contains("error"))
            oth += itempath.getCountry(0) + ", ";
        oth = oth.replace("  ","").replace(", ,", "")
                .replace(",,", "").trim();
        if (oth.startsWith(",")) oth = oth.substring(1).trim();
        if (oth.endsWith(",")) oth = oth.trim().substring(0, oth.length() - 1).trim();
        other.setText(oth);
        //----------------Quality------------------
        quality.setText(itempath.getQuality(0));
        quality.setVisibility(Utils.boolToVisible(!itempath.getQuality(0).contains("error")));
        if (preference.getBoolean("check_qual", true) && itempath.getSubTitle(0) != null) {
            GetQualBluRay getQualBluRay = new GetQualBluRay(itempath.getSubTitle(0), location -> {
                if (!location.equals("null")) {
                    quality.setText(location);
                    quality.setVisibility(View.VISIBLE);
                } else quality.setText(itempath.getQuality(0) + "!");
            });
            getQualBluRay.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        //----------------RecyclerView------------------
        RecyclerView more = findViewById(R.id.rv_more);
        if (itempath.moretitle.size() == 0){
            more.setVisibility(Utils.boolToVisible(false));
            findViewById(R.id.title_more).setVisibility(Utils.boolToVisible(false));
        } else {
            more.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            AdapterMore adapter = new AdapterMore(this);
            adapter.setHtmlItems(itempath);
            more.setNestedScrollingEnabled(false);
            more.setAdapter(adapter);
        }

        play.requestFocus();
        play.setOnClickListener(view -> {
            if (itempath.getUrl(0).contains("/person/") || itempath.getUrl(0).contains("/star/")) {
                more.requestFocus();
            } else {
                Statics.itemLast = itempath;
                Intent intent = new Intent(this, DetailActivityVid.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Full", false);
                intent.putExtra("Title", itempath.getTitle(0));
                intent.putExtra("Subtitle", itempath.getSubTitle(0));
                intent.putExtra("Year", itempath.getDate(0));
                intent.putExtra("Id", itempath.getKpId());
                intent.putExtra("Type", itempath.getType(0));
                intent.putExtra("Season", itempath.getSeason(0));
                intent.putExtra("Episode", itempath.getSeries(0));
                intent.putExtra("Url", itempath.getUrl(0));
                intent.putExtra("Iframe", itempath.getIframe(0));
                intent.putExtra("Translator", itempath.getVoice(0));
                startActivity(intent);
                overridePendingTransition(R.anim.left_to_right, R.anim.left_to_right_2);
            }
        });
        Context c = this;
        trailer.setVisibility(Utils.boolToVisible(!itempath.getTrailer(0).contains("error")));
        trailer.setOnClickListener(view -> {
            findViewById(R.id.detail_pb).setVisibility(View.VISIBLE);
            if (url.toLowerCase().contains("filmix.") || url.contains(Statics.FILMIX_URL)) {
                new FilmixTrailerUrl(itempath.getTrailer(0), (q, url) -> {
                    findViewById(R.id.detail_pb).setVisibility(View.GONE);
                    new Trailer().play(q, url, itempath.getTitle(0), "filmix", c);
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (url.toLowerCase().contains("kinoxa.") || url.contains(Statics.KINOXA_URL)) {
                new KinoxaTrailerUrl(itempath.getTrailer(0), (q, url) -> {
                    findViewById(R.id.detail_pb).setVisibility(View.GONE);
                    new Trailer().play(q, url, itempath.getTitle(0), "kinoxa", c);
                }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                findViewById(R.id.detail_pb).setVisibility(View.GONE);
            }
        });


        ImageButton fav = findViewById(R.id.btn_fav);
        if (dbHelper.getRepeat("favor", itempath.getTitle(0)))
            fav.setImageResource(R.drawable.ic_menu_fav );
        else fav.setImageResource(R.drawable.ic_menu_fav_add );
        fav.setOnClickListener(view -> {
            if (dbHelper.getRepeat("favor", itempath.getTitle(0))) {
                Toast.makeText(this, "Удалено из <<Избранного>>", Toast.LENGTH_SHORT).show();
                dbHelper.delete("favor", itempath.getTitle(0));
                fav.setImageResource(R.drawable.ic_menu_fav_add);
                if (url.contains(Statics.FILMIX_URL)) {
//                    Log.e(TAG, "setInfo: " );
                    FilmixDB filmixDB = new FilmixDB(false, "favor");
                    filmixDB.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            } else {
                addToDB("favor");
                Toast.makeText(this, "Добавлено в <<Избранное>>", Toast.LENGTH_SHORT).show();
                fav.setImageResource(R.drawable.ic_menu_fav );
                if (url.contains(Statics.FILMIX_URL)) {
                    FilmixDB filmixDB = new FilmixDB(true, "favor");
                    filmixDB.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

        ImageButton late = findViewById(R.id.btn_later);
        if (dbHelper.getRepeat("later", itempath.getTitle(0)))
            late.setImageResource(R.drawable.ic_menu_flag);
        else late.setImageResource(R.drawable.ic_menu_flag_add);
        late.setOnClickListener(view -> {
            if (dbHelper.getRepeat("later", itempath.getTitle(0))) {
                dbHelper.delete("later", itempath.getTitle(0));
                Toast.makeText(this, "Удалено из <<Посмотреть позже>>", Toast.LENGTH_SHORT).show();
                late.setImageResource(R.drawable.ic_menu_flag_add);
                if (url.contains(Statics.FILMIX_URL)) {
                    FilmixDB filmixDB = new FilmixDB(false, "later");
                    filmixDB.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            } else {
                addToDB("later");
                Toast.makeText(this, "Добавлено в <<Посмотреть позже>>", Toast.LENGTH_SHORT).show();
                late.setImageResource(R.drawable.ic_menu_flag);
                if (url.contains(Statics.FILMIX_URL)) {
                    FilmixDB filmixDB = new FilmixDB(true, "later");
                    filmixDB.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });
        RecyclerView images = findViewById(R.id.rv_image);
//        Log.e(TAG, "setInfo: "+itempath.preimg.toString() );
        if (itempath.preimg.size() == 0 ){
            play.setNextFocusDownId(R.id.rv_more);
            late.setNextFocusDownId(R.id.rv_more);
            fav.setNextFocusDownId(R.id.rv_more);
            poster.setNextFocusDownId(R.id.rv_more);

            images.setVisibility(Utils.boolToVisible(false));
            findViewById(R.id.title_image).setVisibility(Utils.boolToVisible(false));
        } else {
            images.setVisibility(Utils.boolToVisible(true));
            images.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            AdapterImages adapter = new AdapterImages(this);
            adapter.setItems(itempath.preimg);
            images.setNestedScrollingEnabled(false);
            images.setAdapter(adapter);
        }



//        RelativeLayout r = findViewById(R.id.all);
//        View b = findViewById(R.id.bg);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(r.getWidth(), r.getHeight());
//        lp.height = r.getHeight();
//        b.setLayoutParams(lp);
//        Log.d(TAG, "setInfo: end set");
        //-----------------end----------------------
    }

//    private void onPreImg(String img, int pos){
//        if (getContext() != null) {
//            Intent intent = new Intent(getContext(), ImgActivity.class);
//            intent.putExtra("Img", img);
//            intent.putExtra("Position", pos);
//            getContext().startActivity(intent);
//        }
//    }

    private void addToDB (String db) {
        try {
            if (dbHelper.getRepeat(db, itempath.getTitle(0)))
                dbHelper.delete(db, itempath.getTitle(0));
            dbHelper.Write();
            try {
                dbHelper.insert(db, itempath.getTitle(0), itempath.getImg(0), url, itempath.getVoice(0), itempath.getQuality(0),
                        itempath.getSeason(0), itempath.getSeries(0));
            } catch (Exception o){
                dbHelper.insert(db, itempath.getTitle(0), itempath.getImg(0), url, itempath.getVoice(0), itempath.getQuality(0),
                        0, 0);
            }
            if (preference.getString("save_db_s", "default").equals("google")) {
                signIn();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void onSearchActor(MenuItem item) {
        if (itempath != null && itempath.title.size() > 0) {
            final String[] actor = itempath.getActors(0).trim().replace(", ", ",")
                    .split(",");
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
            builder.setTitle("Выберите актера").setItems(actor, (dialogInterface, i) -> {
                Statics.refreshMain = true;
                Intent intent = new Intent(DetailActivityTv.this, MainCatalogActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("Type", "actor");
                intent.putExtra("Query", actor[i].trim());
                DetailActivityTv.this.startActivity(intent);
            });
            builder.create().show();
        } else Toast.makeText(this, "Дождитесь загрузки", Toast.LENGTH_SHORT).show();
    }
    public boolean boolTF(String sours, String val) {
        return sours.contains(val);
    }
    public String stringTF(String val, boolean t) {
        if (t) return val + " ";
        else return "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_tv, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_web:
                intent.setData(Uri.parse(url));
                startActivity(intent);
                break;
            case R.id.menuSearchGoogle:
                intent.setData(Uri.parse("https://www.google.com.ua/search?q=" +
                        itempath.getTitle(0).replace(" ", "+") + " смотреть онлайн"));
                startActivity(intent);
                break;
            case R.id.menuSearchYoutube:
                intent.setData(Uri.parse("https://www.youtube.com/results?search_query=" +
                        itempath.getTitle(0).replace(" ", "+")));
                startActivity(intent);
                break;
            case R.id.menuSearchKinopoisk:
                if (itempath != null){
                    String url;
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
                break;
            case R.id.action_refresh:
                reload();
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
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivityTv.this, R.style.MyAlertDialogStyle);
            builder.setTitle("Выберите категорию").setItems(ctg_list, (dialogInterface, i) -> {
                String copiedText = "";
                switch (ctg_list[i]) {
                    case "Название":
                        copiedText = itempath.getTitle(0).split("\\(")[0];
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
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) DetailActivityTv.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard != null) clipboard.setText(copiedText);
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) DetailActivityTv.this.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("TAG", copiedText);
                    if (clipboard != null) clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(DetailActivityTv.this, "Сделано", Toast.LENGTH_SHORT).show();
            });
            builder.create().show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("test", "onActivityResult: "+requestCode);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode != RESULT_OK) {
                    Log.e(TAG, "Sign-in failed.");
                    return;
                }

                Task<GoogleSignInAccount> getAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) initializeDriveClient(getAccountTask.getResult());
                else {
                    Log.e(TAG, "Sign-in failed.");
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    protected void signIn() {
        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount);
        } else {
            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(Drive.SCOPE_FILE)
                            .requestScopes(Drive.SCOPE_APPFOLDER)
                            .requestEmail()
                            .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
            startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }
    }
    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
        Log.d(TAG, "initializeDriveClient: " + signInAccount.getEmail());
        createDbInDrive();
    }
    private void createDbInDrive() {
        final Task<DriveFolder> appFolderTask = mDriveResourceClient.getRootFolder();
        final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
        Tasks.whenAll(appFolderTask, createContentsTask)
                .continueWithTask(task -> {
                    DriveFolder parent = appFolderTask.getResult();
                    DriveContents contents = createContentsTask.getResult();
                    OutputStream outputStream = contents.getOutputStream();

                    String db_name = "DB";
                    String db_path = this.getDatabasePath(db_name).getPath();
                    Log.d(TAG, "kk-"+db_path);

                    deleteFile();

                    File dbFile = new File(db_path);
                    final FileInputStream fileInputStream;
                    try {
                        fileInputStream = new FileInputStream(dbFile);
                        byte[] buffer = new byte[4096];
                        int c;

                        while ((c = fileInputStream.read(buffer, 0, buffer.length)) > 0){
                            outputStream.write(buffer, 0, c);
                        }
                        outputStream.flush();
                        outputStream.close();
                        fileInputStream.close();
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "Could not get input stream from local file\n");
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("KinotorDB.sqlite")
                            .setMimeType("application/x-sqlite3")
                            .setStarred(true)
                            .build();

                    return mDriveResourceClient.createFile(parent, changeSet, contents);
                })
                .addOnSuccessListener(this,
                        driveFile -> {
                            Log.d(TAG, "create file");
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to create file", e);
                });
    }
    private void deleteFile() {
        final String sFilename = "KinotorDB.sqlite";

        Query query = new Query.Builder()
                .addFilter( Filters.eq( SearchableField.TITLE, sFilename ) )
                .build();

        Task<MetadataBuffer> queryTask = mDriveResourceClient.query(query);

        queryTask.addOnSuccessListener( this,
                metadataBuffer -> {
                    for( Metadata m : metadataBuffer )
                    {
                        DriveResource driveResource = m.getDriveId().asDriveResource();
                        Log.i( TAG, "Deleting file: " + sFilename + "  DriveId:(" + m.getDriveId() + ")" );
                        mDriveResourceClient.delete( driveResource );
                    }

                })
                .addOnFailureListener( this, e -> Log.i( TAG, "ERROR: File not found: " + sFilename ));
    }
}
