package com.kinotor.tiar.kinotor.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.parser.ParserBase;
import com.kinotor.tiar.kinotor.parser.ParserHtml;
import com.kinotor.tiar.kinotor.parser.ParserTorrent;
import com.kinotor.tiar.kinotor.utils.AdapterTorrents;
import com.kinotor.tiar.kinotor.utils.DBHelper;
import com.squareup.picasso.Picasso;

import static com.kinotor.tiar.kinotor.items.ItemMain.isLoading;

public class DetailActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private DBHelper dbHelper;
    public static String url_poster, title, voice, quality;
    private static LinearLayout info, pb;
    private static boolean lv = true, lt = true, li = true;
    static final private String load = "...загрузка...";
    public static String type, season, serie, iframe = "error", link;

    public static Activity activity;
    public static View fragm_inf, fragm_tor, fragm_vid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (preference.getBoolean("fullscreen", false)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_detail);

        activity = this;

        lt = true;
        lv = true;
        li = true;
        fragm_inf = null;
        fragm_vid = null;
        fragm_tor = null;

        Bundle bundle = getIntent().getExtras();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_d);
        setSupportActionBar(toolbar);
        if (bundle.getString("Url").contains("http:"))
            toolbar.setSubtitle(bundle.getString("Url").split("http://")[1].split("/")[0]);
        else if (bundle.getString("Url").contains("https:"))
            toolbar.setSubtitle(bundle.getString("Url").split("https://")[1].split("/")[0]);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        info = (LinearLayout) findViewById(R.id.info);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setFocusable(true);
        mViewPager.requestFocus();

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        url_poster = bundle.getString("Img");
        title = bundle.getString("Title");
        link = bundle.getString("Url");
        voice = bundle.getString("Voice");
        quality = bundle.getString("Quality");
        season = bundle.getString("Season");
        serie = bundle.getString("Serie");
        if (!season.equals("0")) {
            type = "serial";
            if (!title.contains("сезон)"))
                if (!serie.equals("0")) title = title + " (" + serie + " серия " + season + " сезон)";
                else title = title + " (" + season + " сезон)";
        } else type = "movie";
        setTitle(title);

        if (title.contains("серия")) {
            type = "serial";
            serie = title.split("\\(")[1].split(" серия")[0].trim();
            season = title.split("серия ")[1].split(" сезон")[0].trim();
        } else if (title.contains("сезон)")){
            type = "serial";
            season = title.split("\\(")[1].split(" сезон")[0].trim();
        }

        if (info != null) {
            ImageView poster = (ImageView) findViewById(R.id.imgPoster);
            Picasso.with(this)
                    .load(url_poster)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(poster);
        }

        dbHelper = new DBHelper(this);

        ParserHtml.itemDetail = null;
    }

    public static void setInfo() {
        TextView t_year, t_country, t_genre, t_time,t_quality, t_tranlator;
        TextView t_description, t_director, t_actor;
        if (info != null) {
            t_year = (TextView) activity.findViewById(R.id.year);
            t_country = (TextView) activity.findViewById(R.id.country);
            t_genre = (TextView) activity.findViewById(R.id.genre);
            t_time = (TextView) activity.findViewById(R.id.time);
            t_quality = (TextView) activity.findViewById(R.id.quality);
            t_tranlator = (TextView) activity.findViewById(R.id.translator);
        } else {
            Log.d("mydebug", "portret/small");
            LinearLayout info =  (LinearLayout) fragm_inf.findViewById(R.id.l_info);
            info.setVisibility(View.VISIBLE);
            ImageView poster = (ImageView) fragm_inf.findViewById(R.id.imgPoster_d);
            Picasso.with(fragm_inf.getContext())
                    .load(url_poster)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(poster);
            t_year = (TextView) fragm_inf.findViewById(R.id.year);
            t_country = (TextView) fragm_inf.findViewById(R.id.country);
            t_genre = (TextView) fragm_inf.findViewById(R.id.genre);
            t_time = (TextView) fragm_inf.findViewById(R.id.time);
            t_quality = (TextView) fragm_inf.findViewById(R.id.quality);
            t_tranlator = (TextView) fragm_inf.findViewById(R.id.translator);
        }
        t_description = (TextView) fragm_inf.findViewById(R.id.desc_inf);
        t_director = (TextView) fragm_inf.findViewById(R.id.director);
        t_actor = (TextView) fragm_inf.findViewById(R.id.actor);

        t_year.setText(load);
        t_country.setText(load);
        t_genre.setText(load);
        t_time.setText(load);
        t_quality.setText(load);
        t_tranlator.setText(load);
        t_description.setText(load);
        t_director.setText(load);
        t_actor.setText(load);

        if (ParserHtml.itemDetail != null) {
            t_year.setText(ParserHtml.itemDetail.getYear());
            t_country.setText(ParserHtml.itemDetail.getCountry());
            t_genre.setText(ParserHtml.itemDetail.getGenre());
            t_time.setText(ParserHtml.itemDetail.getTime());
            t_quality.setText(ParserHtml.itemDetail.getQuality());
            t_tranlator.setText(ParserHtml.itemDetail.getTranslator());
            t_description.setText("\t" + ParserHtml.itemDetail.getDescription());
            t_director.setText("\t" + ParserHtml.itemDetail.getDirector());
            t_actor.setText("\t" + ParserHtml.itemDetail.getActors());
            iframe = ParserHtml.itemDetail.getIframe();

            addToDB("history");
        }

        if (fragm_tor != null){
            if (lt) {
                RecyclerView rv = DetailActivity.fragm_tor.findViewById(R.id.tor_item_list);
                rv.setAdapter(new AdapterTorrents());

                ParserTorrent parserTorrent = new ParserTorrent(title, rv);
                parserTorrent.execute();
                lt = false;
            }
        }
        if (fragm_vid != null) {
            if (lv) {
                ParserBase parserBase = new ParserBase(title.split(" \\(")[0], type, "catalog");
                parserBase.execute();
                lv = false;
            }
        }
    }

    private void addToDBbtn(MenuItem item) {
        if (!dbHelper.getRepeat("favor", getTitle() + "") && !ItemMain.isLoading) {
            addToDB("favor");
            item.setIcon(R.drawable.ic_menu_fav);
            Toast.makeText(this,
                    "Добавленно в избранное", Toast.LENGTH_LONG).show();
        } else if (dbHelper.getRepeat("favor", getTitle() + "") && !ItemMain.isLoading) {
            dbHelper.delete("favor", title);
            item.setIcon(R.drawable.ic_menu_fav_add);
            Toast.makeText(this,
                    "Удалено из избранного", Toast.LENGTH_LONG).show();
        }
    }

    private static void addToDB (String db) {
        if (season.equals("")) season = "0";
        DBHelper dbHelper;
        dbHelper = new DBHelper(activity);
        if (dbHelper.getRepeat(db, title) && db.equals("history"))
            dbHelper.delete(db, title);
        dbHelper.Write();
        dbHelper.insert(db, title, url_poster, link, voice, quality,
                Integer.parseInt(season), ParserHtml.itemDetail.getSeries());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail, menu);
        MenuItem fav = menu.findItem(R.id.fav);
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
                addToDBbtn(item);
                break;
            case R.id.action_refresh:
                if (!isLoading || lt || lv || li) {
                    lt = true;
                    lv = true;
                    li = true;
                    pb.setVisibility(View.VISIBLE);
                    ParserHtml.itemDetail = null;
                    ParserHtml parserHtml = new ParserHtml(link, "detail", null, pb);
                    parserHtml.execute();
                }
                break;
            case R.id.action_cd:
                final String[] ctg_list = {
                        "Открыть в Браузере",
                        "Скопировать..."};
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this, 2);
                builder.setTitle("Выберите категорию").setItems(ctg_list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(link));
                            startActivity(intent);
                        } else if (i == 1) {
                            final String[] ctg_list = {
                                    "Название",
                                    "Режисер",
                                    "Акторы",
                                    "Жанр"};
                            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this, 2);
                            builder.setTitle("Выберите категорию").setItems(ctg_list, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    String copiedText = "";
                                    if (i == 0) {
                                        copiedText = title.split("\\(")[0];
                                    } else if (i == 1) {
                                        copiedText = ParserHtml.itemDetail.getDirector();
                                    } else if (i == 2) {
                                        copiedText = ParserHtml.itemDetail.getActors();
                                    } else if (i == 3) {
                                        copiedText = ParserHtml.itemDetail.getGenre();
                                    }
                                    int sdk = android.os.Build.VERSION.SDK_INT;
                                    if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                                        @SuppressWarnings("deprecation") android.text.ClipboardManager clipboard = (android.text.ClipboardManager) DetailActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                                        clipboard.setText(copiedText);
                                    } else {
                                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) DetailActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                                        android.content.ClipData clip = android.content.ClipData.newPlainText("TAG", copiedText);
                                        clipboard.setPrimaryClip(clip);
                                    }
                                    Toast.makeText(DetailActivity.this, "Сделано", Toast.LENGTH_SHORT).show();
                                }
                            });
                            builder.create().show();
                        }
                    }
                });
                builder.create().show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {
        private static final String ARG_SECTION = "section";

        public DetailFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static DetailFragment newInstance(String section) {
            DetailFragment fragment = new DetailFragment();
            Bundle args = new Bundle();
            args.putString(ARG_SECTION, section);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;

            if (getArguments().getString(ARG_SECTION).equals("Информация")) {
                rootView = inflater.inflate(R.layout.fragment_detail_inf, container, false);
                fragm_inf = rootView;
                pb = (LinearLayout) fragm_inf.findViewById(R.id.inf_pb);
                if (info == null) {
                    LinearLayout info_s = (LinearLayout) rootView.findViewById(R.id.l_info);
                    info_s.setVisibility(View.VISIBLE);
                }
                if (li) {
                    ParserHtml parserHtml = new ParserHtml(link, "detail", null, pb);
                    parserHtml.execute();
                    li = false;
                }
                if (!ItemMain.isLoading)
                    pb.setVisibility(View.GONE);
            } else if (getArguments().getString(ARG_SECTION).equals("Видео")) {
                rootView = inflater.inflate(R.layout.fragment_detail_vid, container, false);
                fragm_vid = rootView;
            } else if (getArguments().getString(ARG_SECTION).equals("Торренты")) {
                rootView = inflater.inflate(R.layout.fragment_detail_tor, container, false);
                fragm_tor = rootView;
                RecyclerView rv = DetailActivity.fragm_tor.findViewById(R.id.tor_item_list);
                rv.setAdapter(new AdapterTorrents());
            } else rootView = null;
            setInfo();
            return rootView;
        }
    }

    /**
     * that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 2)
                return DetailFragment.newInstance("Торренты");
            if (position == 1)
                return DetailFragment.newInstance("Видео");
            return DetailFragment.newInstance("Информация");
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Информация";
                case 1:
                    return "Видео";
                case 2:
                    return "Торренты";
            }
            return null;
        }
    }
}
