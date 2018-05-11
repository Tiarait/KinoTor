package com.kinotor.tiar.kinotor.ui;

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.parser.ParserAmcet;
import com.kinotor.tiar.kinotor.parser.ParserHtml;
import com.kinotor.tiar.kinotor.parser.animevost.ParserAnimevost;
import com.kinotor.tiar.kinotor.utils.DBHelper;
import com.kinotor.tiar.kinotor.utils.OnTaskCallback;
import com.kinotor.tiar.kinotor.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private String url_poster = "error", title = "error",
            quality = "error", season = "error", serie = "error";
    public static String url = "error";
    private ItemHtml itempath;
    private LinearLayout pb;
    SharedPreferences preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (preference.getBoolean("fullscreen", false)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_detail);

        Bundle bundle = getIntent().getExtras();

        Toolbar toolbar = findViewById(R.id.toolbar_d);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        pb = findViewById(R.id.detail_pb);
        if (bundle != null) {
            if (bundle.getString("Url") != null) {
                if (bundle.getString("Url").contains("http:")) {
                    toolbar.setSubtitle(bundle.getString("Url").split("http://")[1].split("/")[0]);
                } else if (bundle.getString("Url").contains("https:"))
                    toolbar.setSubtitle(bundle.getString("Url").split("https://")[1].split("/")[0]);
            }
            url_poster = bundle.getString("Img");
            title = bundle.getString("Title");
            url = bundle.getString("Url");
            quality = bundle.getString("Quality");
            season = bundle.getString("Season");
            serie = bundle.getString("Serie");
            setInfo();
        }
        setTitle(title);

        dbHelper = new DBHelper(this);

        final ViewPager mViewPager = findViewById(R.id.container);
        final SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setFocusable(true);
        mViewPager.requestFocus();

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        getItem(mViewPager, mSectionsPagerAdapter);
    }

    public void getItem(final ViewPager mViewPager, final SectionsPagerAdapter mSectionsPagerAdapter) {
        if (url.contains("amcet")) {
            ParserAmcet parserAmcet = new ParserAmcet(url, null, new ItemHtml(),
                    new OnTaskCallback() {
                        @Override
                        public void OnCompleted(ArrayList<ItemHtml> items, ItemHtml itempath) {
                            taskDone(mViewPager, mSectionsPagerAdapter, itempath);
                        }
                    });
            parserAmcet.execute();
        } else if (url.contains("animevost")) {
            ParserAnimevost parserAnimevost = new ParserAnimevost(url, null, new ItemHtml(),
                    new OnTaskCallback() {
                        @Override
                        public void OnCompleted(ArrayList<ItemHtml> items, ItemHtml itempath) {
                            taskDone(mViewPager, mSectionsPagerAdapter, itempath);
                        }
                    });
            parserAnimevost.execute();
        } else {
            ParserHtml parserHtml = new ParserHtml(url, null, new ItemHtml(),
                    new OnTaskCallback() {
                        @Override
                        public void OnCompleted(ArrayList<ItemHtml> items, ItemHtml itempath) {
                            taskDone(mViewPager, mSectionsPagerAdapter, itempath);
                        }
                    });
            parserHtml.execute();
        }
    }

    private void taskDone(ViewPager mViewPager, SectionsPagerAdapter mSectionsPagerAdapter, ItemHtml itempath) {
        if (itempath != null) {
            Log.d("DetailActivity", "taskDone: " + itempath.getTitle(0) + " " + itempath.getType(0));
            pb.setVisibility(View.GONE);
            try {
                itempath.setSeason(Integer.parseInt(season));
                itempath.getSeries(Integer.parseInt(serie));
            } catch (Exception ignored) {
            }
            this.itempath = itempath;
            addToDB("history");
            mSectionsPagerAdapter.addFragment(new DetailInfo(itempath), "Информация");
            mSectionsPagerAdapter.addFragment(new DetailVideo(itempath), "Видео");
            mSectionsPagerAdapter.addFragment(new DetailTorrents(itempath), "Торренты");
            mViewPager.getAdapter().notifyDataSetChanged();
            setInfo();
        } else getItem(mViewPager, mSectionsPagerAdapter);
    }

    private void setInfo() {
        ImageView poster = findViewById(R.id.imgPoster_d);
        Utils utils = new Utils();
        if (utils.isTablet(this) && poster != null) {
            Picasso.with(this)
                    .load(url_poster)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.error)
                    .into(poster);
            TextView t_year = findViewById(R.id.year);
            TextView t_country = findViewById(R.id.country);
            TextView t_genre = findViewById(R.id.genre);
            TextView t_time = findViewById(R.id.time);
            //TextView t_extra = findViewById(R.id.extra);
            TextView t_quality = findViewById(R.id.quality);
            TextView t_voice = findViewById(R.id.translator);
            LinearLayout l_year = findViewById(R.id.l_year);
            LinearLayout l_country = findViewById(R.id.l_country);
            LinearLayout l_genre = findViewById(R.id.l_genre);
            LinearLayout l_time = findViewById(R.id.l_time);
            if (itempath != null) {
                l_year.setVisibility(View.VISIBLE);
                l_country.setVisibility(View.VISIBLE);
                l_genre.setVisibility(View.VISIBLE);
                l_time.setVisibility(View.VISIBLE);
                t_quality.setVisibility(View.VISIBLE);
                if (itempath.getDate(0).contains("error"))
                    l_year.setVisibility(View.GONE);
                else t_year.setText(itempath.getDate(0));
                if (itempath.getCountry(0).contains("error"))
                    l_country.setVisibility(View.GONE);
                else t_country.setText(itempath.getCountry(0));
                if (itempath.getGenre(0).contains("error"))
                    l_genre.setVisibility(View.GONE);
                else t_genre.setText(itempath.getGenre(0));
                if (itempath.getTime(0).contains("error"))
                    l_time.setVisibility(View.GONE);
                else t_time.setText(itempath.getTime(0));
                if (itempath.getQuality(0).contains("error"))
                    t_quality.setVisibility(View.GONE);
                else quality = itempath.getQuality(0);
                if (itempath.getVoice(0).contains("error"))
                    t_voice.setVisibility(View.GONE);
                else t_voice.setText(itempath.getVoice(0));
//                if (itempath.getExtraDetail().contains("error"))
//                    t_extra.setVisibility(View.GONE);
//                else t_extra.setText(itempath.getExtraDetail());
            }
            if (quality.contains("error"))
                t_quality.setVisibility(View.GONE);
            t_quality.setText(quality);
        }
    }

    private void addToDBbtn(MenuItem item) {
        if (!dbHelper.getRepeat("favor", getTitle() + "")) {
            addToDB("favor");
            item.setIcon(R.drawable.ic_menu_fav);
            Toast.makeText(this,
                    "Добавленно в избранное", Toast.LENGTH_LONG).show();
        } else if (dbHelper.getRepeat("favor", getTitle() + "")) {
            dbHelper.delete("favor", title);
            item.setIcon(R.drawable.ic_menu_fav_add);
            Toast.makeText(this,
                    "Удалено из избранного", Toast.LENGTH_LONG).show();
        }
    }

    private void addToDB (String db) {
        DBHelper dbHelper;
        dbHelper = new DBHelper(this);
        if (dbHelper.getRepeat(db, title) && db.equals("history"))
            dbHelper.delete(db, title);
        dbHelper.Write();
        try {
            dbHelper.insert(db, title, url_poster, url, itempath.getVoice(0), quality,
                    itempath.getSeason(0), itempath.getSeries(0));
        } catch (Exception o){
            dbHelper.insert(db, title, url_poster, url, itempath.getVoice(0), quality,
                    0, 0);
        }
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
        if (itempath != null) {
            final String[] ctg_list = {
                    "Название",
                    "Режисер",
                    "Актеры",
                    "Жанр"};
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.this, 2);
            builder.setTitle("Выберите категорию").setItems(ctg_list, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String copiedText = "";
                if (i == 0) {
                    copiedText = title.split("\\(")[0];
                } else if (i == 1) {
                    copiedText = itempath.getDirector(0);
                } else if (i == 2) {
                    copiedText = itempath.getActors(0);
                } else if (i == 3) {
                    copiedText = itempath.getGenre(0);
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
                }
            });
            builder.create().show();
        }
    }

    public void onActionWeb(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
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
