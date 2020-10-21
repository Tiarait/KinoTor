package com.kinotor.tiar.kinotor.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.parser.catalog.filmix.ParserFilmixHistUpd;
import com.kinotor.tiar.kinotor.ui.fragments.DetailTorrents;
import com.kinotor.tiar.kinotor.ui.fragments.DetailVideo;
import com.kinotor.tiar.kinotor.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DetailActivityVid extends AppCompatActivity {
    private ItemHtml itempath;
    private Fragment fVid;
    private TabLayout tabLayout;
    boolean full = false;
    SharedPreferences preference;
    private int colorStatus = R.color.colorPrimaryLight;
    private int colorBg = R.color.colorPrimaryLight;
    private int colorText = R.color.colorWhite;

    private EditText dTitle;
    private static final int REQUEST_CODE = 1121;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Statics.adbWached = false;
        Statics.MOON_ID = "error";
        Statics.KP_ID = "error";
        preference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        switch (preference.getString("theme_list", "gray")) {
            case "black":
                colorBg = R.color.colorBlack;
                colorStatus = R.color.colorBlack;
                colorText = R.color.colorWhite;
                break;
            case "white":
                colorBg = R.color.colorWhite;
                colorStatus = R.color.colorBlack;
                colorText = R.color.colorBlack;
                break;
        }
        if (preference.getBoolean("fullscreen", false)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.activity_detail_vid);


        final ViewPager mViewPager = findViewById(R.id.container);
        final SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setFocusable(true);
        //mViewPager.requestFocus();

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setFocusable(true);
//        tabLayout.requestFocus();

        itempath = null;

        findViewById(R.id.linear_content).setBackgroundColor(getResources().getColor(colorBg));

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String error = "error";
            full = bundle.getBoolean("Full");
            String tr = "error";
            if (bundle.getString("Translator") != null)
                tr = bundle.getString("Translator");
            if (Statics.itemLast != null) {
                itempath = Statics.itemLast;
            } else {
                if (bundle.getBoolean("search")) {
                    itempath = new ItemHtml();
                    itempath.setTitle("");
                    itempath.setSubTitle("");
                    itempath.setDate("");
                    itempath.setKpId("");
                    itempath.setUrl("");
                    itempath.setType("");
                    itempath.setIframe("");
                    itempath.setSeason(0);
                    itempath.setSeries(0);
                    itempath.setImg("");
                    itempath.setQuality("");
                    itempath.setVoice("");
                    itempath.setRating("");
                    itempath.setDescription("");
                    itempath.setCountry("");
                    itempath.setGenre("");
                    itempath.setDirector("");
                    itempath.setActors("");
                    itempath.setTime("");
                    itempath.setPreImg("");
                    startSearch();
                } else {
                    itempath = new ItemHtml();
                    itempath.setTitle(bundle.getString("Title"));
                    itempath.setSubTitle(bundle.getString("Subtitle"));
                    itempath.setDate(bundle.getString("Year"));
                    itempath.setKpId(bundle.getString("Id"));
                    itempath.setUrl(bundle.getString("Url"));
                    itempath.setType(bundle.getString("Type"));
                    itempath.setIframe(bundle.getString("Iframe"));
                    itempath.setSeason(bundle.getInt("Season"));
                    itempath.setSeries(bundle.getInt("Episode"));
                    itempath.setImg(error);
                    itempath.setQuality(error);
                    itempath.setVoice(tr);
                    itempath.setRating(error);
                    itempath.setDescription(error);
                    itempath.setCountry(error);
                    itempath.setGenre(error);
                    itempath.setDirector(error);
                    itempath.setActors(error);
                    itempath.setTime(error);
                    itempath.setPreImg(error);
                }
            }
            setVideoDraw();
        } else finish();
    }

    @Override
    public void onBackPressed() {
        if (tabLayout != null) {
            if (tabLayout.getSelectedTabPosition() == 0 & fVid != null &&
                    (Statics.itemsVidSeason != null || Statics.itemsVidVoice != null)) {
                Statics.backClick = true;
                fVid.onResume();
            } else if (tabLayout.getSelectedTabPosition() != 0) {
                TabLayout.Tab tab = tabLayout.getTabAt(0);
                if (tab != null)
                    tab.select();
                else {
                    super.onBackPressed();
                    if (!full)
                        overridePendingTransition(R.anim.right_to_left_2, R.anim.right_to_left);
                }
            } else {
                super.onBackPressed();
                if (!full)
                    overridePendingTransition(R.anim.right_to_left_2, R.anim.right_to_left);
            }
        } else {
            super.onBackPressed();
            if (!full)
                overridePendingTransition(R.anim.right_to_left_2, R.anim.right_to_left);
        }
    }


    private void setVideoDraw() {
        TextView t = findViewById(R.id.title);
        t.setText(itempath.getTitle(0));

        final ViewPager mViewPager = findViewById(R.id.container);
        final DetailActivityVid.SectionsPagerAdapter mSectionsPagerAdapter = new DetailActivityVid.SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        Fragment fTor = null;

        if (preference.getBoolean("tab_video", true)) {
            fVid = new DetailVideo().newInstance(itempath);
            mSectionsPagerAdapter.addFragment(fVid, "Видео");
        }
        if (preference.getBoolean("tab_torrent", true)) {
            fTor = new DetailTorrents().newInstance(itempath, "");
            mSectionsPagerAdapter.addFragment(fTor, "Торренты");
        }
        mViewPager.getAdapter().notifyDataSetChanged();

        ImageView closeDraw  = findViewById(R.id.btn_draw_close);
        closeDraw.setOnClickListener(view -> finish());

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        Fragment finalFTor = fTor;
        Fragment finalFVid = fVid;
        ImageView sortDraw  = findViewById(R.id.btn_draw_sort);
        sortDraw.setVisibility(View.VISIBLE);
        sortDraw.setOnClickListener(view -> {
            if (preference.getBoolean("tab_video", true) && tabLayout.getSelectedTabPosition() == 0) {
                String[] allList = getBaseContext().getResources().getStringArray(R.array.pref_list_filtr_vid);
                String[] allListVal = getBaseContext().getResources().getStringArray(R.array.pref_val_filtr_vid);
                final String[] cur = {preference.getString("filter_vid", "none")};
                int index = new ArrayList<>(Arrays.asList(allListVal)).indexOf(cur[0].trim());

                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
                builder.setSingleChoiceItems(allList, index, (dialog, i) -> {
                    SharedPreferences.Editor editor = preference.edit();
                    editor.putString("filter_vid", allListVal[i]);
                    editor.apply();
                    if (finalFVid != null) {
                        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.detach(finalFVid);
                        ft.attach(finalFVid);
                        ft.commit();
                    } else Log.e("vid", "setVideoDraw: finalFVid null");
                    dialog.dismiss();
                }).create().show();
            } else {
                String[] allList = getBaseContext().getResources().getStringArray(R.array.pref_list_filtr_tor);
                String[] allListVal = getBaseContext().getResources().getStringArray(R.array.pref_val_filtr_tor);
                final String[] cur = {preference.getString("filter_tor", "none")};
                int index = new ArrayList<>(Arrays.asList(allListVal)).indexOf(cur[0].trim());

                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
                builder.setSingleChoiceItems(allList, index, (dialog, i) -> {
                    SharedPreferences.Editor editor = preference.edit();
                    if (allListVal[i].equals("words")){
                        AlertDialog.Builder w = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
                        final EditText input = new EditText(this);
                        input.setText(preference.getString("filter_tor_word", ""));
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.MATCH_PARENT);
                        lp.setMargins(32,0,32,0);
                        input.setTextColor(getResources().getColor(R.color.colorWhite));
                        input.setLines(1);
                        input.setLayoutParams(lp);
                        w.setView(input);
                        w.setTitle("Введите текст для фильтра");
                        w.setPositiveButton("Применить", (dialogInterface, i1) -> {
                            editor.putString("filter_tor_word", input.getText().toString());
                            editor.putString("filter_tor", allListVal[i]);
                            editor.apply();
                            if (finalFTor != null) {
                                final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                                ft.detach(finalFTor);
                                ft.attach(finalFTor);
                                ft.commit();
                            }
                        });
                        w.setNegativeButton("Отмена", (dialogInterface, i1) -> {
                            dialog.dismiss();
                        });
                        w.show();
                    } else {
                        editor.putString("filter_tor", allListVal[i]);
                        editor.apply();
                        if (finalFTor != null) {
                            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                            ft.detach(finalFTor);
                            ft.attach(finalFTor);
                            ft.commit();
                        }
                        dialog.dismiss();
                    }
                }).create().show();
            }

        });
        ImageView listDraw  = findViewById(R.id.btn_draw_list);
        listDraw.setOnClickListener(view -> {
            if (preference.getBoolean("tab_video", true) && tabLayout.getSelectedTabPosition() == 0) {
                Set<String> pref_base;
                String[] vidBaseArr = {"hdgo", "moonwalk", "filmix", "hdbaza", "kinolive", "kinodom","zombiefilm"};

                HashSet<String> def = new HashSet<>(Arrays.asList(vidBaseArr));
                pref_base = preference.getStringSet("base_video", def);
                String vidBase = pref_base.toString();

                final String[] availableTypes = new String[]{"kinosha", "filmix", "moonwalk", "hdgo",
                        "hdbaza", "kinohd", "kinolive", "kinopub", "kinodom", "zombiefilm", "anidub", "animedia", "animevost"};
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
            } else {
                Set<String> pref_base;
                String[] vidBaseArr = {"tparser", "freerutor", "nnm", "bitru", "yohoho", "rutor(orig)", "megapeer",
                        "piratbit", "kinozal", "hurtom", "torlook", "greentea", "rutracker"};

                HashSet<String> def = new HashSet<>(Arrays.asList(vidBaseArr));
                pref_base = preference.getStringSet("base_tparser", def);
                String vidBase = pref_base.toString();


                final String[] availableTypes = {"anidub", "tparser", "freerutor", "nnm", "bitru", "yohoho", "rutor(orig)", "megapeer",
                        "piratbit", "kinozal", "hurtom", "torlook", "greentea", "rutracker"};
                final boolean[] selectedTypes = {
                        boolTF(vidBase, "anidub"),
                        boolTF(vidBase, "tparser"),
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
                    if (finalFTor!=null) {
                        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                        ft.detach(finalFTor);
                        ft.attach(finalFTor);
                        ft.commit();
                    }
                }).setNegativeButton("Отмена", (dialog, id) -> {
                    dialog.dismiss();
                }).create().show();
            }
        });

        boolean pro = preference.getBoolean("pro_version", false);
        boolean seeit = preference.getBoolean("side_left", false);
        boolean setit = preference.getBoolean("side_exist", false);
        boolean setet = preference.getBoolean("side_video", false);

        ImageView searchDraw = findViewById(R.id.btn_draw_search);
        searchDraw.setVisibility(Utils.boolToVisible(setit && pro));
        searchDraw.setOnClickListener(view -> {
            startSearch();
        });
    }

    private void startSearch() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
        LayoutInflater inflater = this.getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_video_search, null);
        RadioButton serial, film;
        EditText subtitle, year, kpId;
        dTitle      = v.findViewById(R.id.text_title);
        subtitle    = v.findViewById(R.id.text_title_orig);
        year        = v.findViewById(R.id.text_year);
        kpId        = v.findViewById(R.id.text_kp);
        serial      = v.findViewById(R.id.rb_Serial);
        film        = v.findViewById(R.id.rb_Film);

        dTitle      .setText(itempath.getTitle(0).replace("error",""));
        subtitle    .setText(itempath.getSubTitle(0).replace("error",""));
        year        .setText(itempath.getDate(0).replace("error",""));
        kpId        .setText(itempath.getKpId().replace("error",""));


        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        ImageButton btnVoice = v.findViewById(R.id.btn_voice);
        btnVoice.setOnClickListener(view -> {
            if (activities.size() == 0) {
                Toast.makeText(getApplicationContext(), "Приложение для распознавания не найдено...", Toast.LENGTH_SHORT).show();
            } else speakButtonClicked();
        });

        dTitle.setFocusable(true);
        subtitle.setFocusable(true);
        year.setFocusable(true);
        kpId.setFocusable(true);
        serial.setFocusable(true);
        film.setFocusable(true);

        dTitle.setOnFocusChangeListener(this::changeListener);
        subtitle.setOnFocusChangeListener(this::changeListener);
        year.setOnFocusChangeListener(this::changeListener);
        kpId.setOnFocusChangeListener(this::changeListener);
        serial.setOnFocusChangeListener(this::changeListener);
        film.setOnFocusChangeListener(this::changeListener);


        builder.setView(v)
                .setPositiveButton("Поиск", (dialogInterface, i) -> {
                    Statics.itemLast = null;
                    String tTitle = dTitle.getText().toString().trim();
                    String tSubtitle = subtitle.getText().toString().trim();
                    String tYear = year.getText().toString().trim();
                    String tKpId = kpId.getText().toString().trim();
                    String type = serial.isChecked() ? "serial" : "film";
                    int season = serial.isChecked() ? 1 : 0;
                    int episode = serial.isChecked() ? 1 : 0;

                    if (tTitle.contains("error")) {
                        dTitle.setBackgroundColor(getResources().getColor(R.color.colorAccentRed));
                        dTitle.requestFocus();
                    } else {
                        String ttt = tTitle.isEmpty() ? "error" : tTitle;
                        Statics.KP_ID = "error";
                        ItemHtml item = new ItemHtml();
                        item.setTitle(ttt.contains("(") ? ttt.split("\\(")[0].trim() : ttt);
                        item.setSubTitle(tSubtitle.isEmpty() ? "error" : tSubtitle);
                        item.setDate(tYear.isEmpty() ? "error" : tYear);
                        item.setKpId(tKpId.isEmpty() ? "error" : tKpId);
                        item.setUrl("error");
                        item.setType(type);
                        item.setIframe("error");
                        item.setSeason(season);
                        item.setSeries(episode);
                        item.setImg("error");
                        item.setQuality("error");
                        item.setVoice("error");
                        item.setRating("error");
                        item.setDescription("error");
                        item.setCountry("error");
                        item.setGenre("error");
                        item.setDirector("error");
                        item.setActors("error");
                        item.setTime("error");
                        item.setPreImg("error");
                        this.itempath = item;
                        setVideoDraw();
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.dismiss())
                .create().show();
    }

    private void speakButtonClicked() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Скажите что искать?...");
        startActivityForResult(intent, REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);

            if (matches != null) {
                if (matches.size() > 0 && dTitle != null) {
                    dTitle.setText(matches.get(0));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void changeListener(View view, boolean b) {
        if (!view.isSelected()) {
            view.setBackgroundColor(view.getResources().getColor(R.color.colorPrimaryLight));
        }
        else view.setBackgroundColor(view.getResources().getColor(R.color.colorPrimaryDark));
        view.setSelected(b);
    }

    public boolean boolTF(String sours, String val) {
        return sours.contains(val);
    }
    public String stringTF(String val, boolean t) {
        if (t) return val + " ";
        else return "";
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
