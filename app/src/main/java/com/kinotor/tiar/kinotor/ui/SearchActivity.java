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
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.items.ItemSearch;
import com.kinotor.tiar.kinotor.items.Statics;
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
import com.kinotor.tiar.kinotor.ui.fragments.MainCatalogFragment;
import com.kinotor.tiar.kinotor.utils.adapters.AdapterSuggestion;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.kinotor.tiar.kinotor.utils.Utils.hideKeyboard;
import static com.kinotor.tiar.kinotor.utils.Utils.showKeyboard;

public class SearchActivity extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener{
    private static final String TAG = "SearchActivity";
    private static final int REQUEST_CODE = 1234;
    private SharedPreferences preference;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private String query = "null";
    private EditText search;
    private TextView searchT;

    private int colorStatus = R.color.colorPrimaryLight;
    private int colorBg = R.color.colorPrimaryLight;
    private int colorBgImg = R.drawable.gradient_darkgone_dark;
    private int colorText = R.color.colorWhite;
    public static boolean activeSearch = false;

    @Override
    public void onStart() {
        super.onStart();
        activeSearch = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        activeSearch = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        if (preference.getBoolean("fullscreen", false)) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.activity_search);
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

//        if (getIntent() != null && getIntent().getData() != null) {
//            Uri uri = getIntent().getData();
//            int id = Integer.valueOf(uri.getLastPathSegment());
//
//            boolean startPlayback = getIntent().getBooleanExtra(EXTRA_START_PLAYBACK, false);
//            Log.d(TAG, "Should start playback? " + (startPlayback ? "yes" : "no"));
//        }


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setSubtitle(Statics.CATALOG);
        refreshBar();

        search = findViewById(R.id.search);
        searchT = findViewById(R.id.search_t);
        ConstraintLayout all = findViewById(R.id.main_content);
        View bgImg = findViewById(R.id.bg);
        if (preference.getString("theme_list", "gray").equals("gray"))
            all.setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_angle_light));
        else all.setBackgroundColor(getResources().getColor(colorBg));
        bgImg.setBackgroundDrawable(getResources().getDrawable(colorBgImg));
        if (preference.getString("theme_list", "gray").equals("white")){
            findViewById(R.id.prebg).setVisibility(View.GONE);
            bgImg.setVisibility(View.GONE);
        }

        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() >= 2) {
                    switch (Statics.CATALOG.trim()) {
                        case "koshara":
                            SearchKoshara searchKoshara = new SearchKoshara(charSequence.toString(), items -> suggestion(items));
                            searchKoshara.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        case "coldfilm":
                            //-
                            break;
//                        case "amcet":
//                            SearchAmcet searchAmcet = new SearchAmcet(charSequence.toString(), items -> suggestion(items));
//                            searchAmcet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                            break;
                        case "animevost":
                            SearchAnimevost searchAnimevost = new SearchAnimevost(charSequence.toString(), items -> suggestion(items));
                            searchAnimevost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        case "anidub":
                            SearchAnidub searchAnidub = new SearchAnidub(charSequence.toString(), items -> suggestion(items));
                            searchAnidub.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        case "kinopub":
                            SearchKinopub searchKinopub = new SearchKinopub(charSequence.toString(), items -> suggestion(items));
                            searchKinopub.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        case "kinofs": {
                            SearchKinofs searchKinofs = new SearchKinofs(charSequence.toString(), items -> suggestion(items));
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
                            SearchTopkino searchTopkino = new SearchTopkino(charSequence.toString(), items -> suggestion(items));
                            searchTopkino.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        }
                        case "fanserials": {
                            SearchFanserials searchFanserials = new SearchFanserials(charSequence.toString(), items -> suggestion(items));
                            searchFanserials.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        }
                        case "filmix":
                            SearchFilmix searchFilmix = new SearchFilmix(charSequence.toString(), items -> suggestion(items));
                            searchFilmix.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        case "my-hit":
                            SearchMyhit searchMyhit = new SearchMyhit(charSequence.toString(), items -> suggestion(items));
                            searchMyhit.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        case "kinodom":
//                            SearchKinodom searchKinodom = new SearchKinodom(charSequence.toString(), items -> suggestion(items));
//                            searchKinodom.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        case "kinolive":
                            SearchKinolive search = new SearchKinolive(charSequence.toString(), items -> suggestion(items));
                            search.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                    }
                } else {
                    findViewById(R.id.item_detail_container).setVisibility(View.VISIBLE);
                    findViewById(R.id.item_suggestion).setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        ImageButton btnSearch = findViewById(R.id.btn_search);
        btnSearch.setOnClickListener(view -> {
            if (search.getText().length() > 1) {
                OnSearch(search.getText().toString());
            }
        });
        ImageButton btnVoice = findViewById(R.id.btn_voice);
        btnVoice.setOnClickListener(view -> {
            if (activities.size() == 0) {
                Toast.makeText(getApplicationContext(), "Приложение для распознавания не найдено...", Toast.LENGTH_SHORT).show();
            } else speakButtonClicked();
        });
        search.setOnKeyListener((v1, keyCode, event) -> {
            // If the event is a key-key event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                if (!search.getText().toString().isEmpty())
                    OnSearch(search.getText().toString());
                else search.requestFocus();
                return true;
            } else if (event.getAction() == KeyEvent.KEYCODE_SEARCH){
                if (activities.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Приложение для распознавания не найдено...", Toast.LENGTH_SHORT).show();
                } else speakButtonClicked();
                return true;
            }
            return false;
        });
        searchT.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_SEARCH){
                if (activities.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Приложение для распознавания не найдено...", Toast.LENGTH_SHORT).show();
                } else speakButtonClicked();
                return true;
            } else return false;
        });
        btnVoice.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_SEARCH){
                if (activities.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Приложение для распознавания не найдено...", Toast.LENGTH_SHORT).show();
                } else speakButtonClicked();
                return true;
            } else return false;
        });
        btnSearch.setOnKeyListener((view, i, keyEvent) -> {
            if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_SEARCH){
                if (activities.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Приложение для распознавания не найдено...", Toast.LENGTH_SHORT).show();
                } else speakButtonClicked();
                return true;
            } else return false;
        });

        if (preference.getString("tv_keyboard","def").equals("def")) {
            search.requestFocus();
            showKeyboard(this);
        } else {
            btnVoice.requestFocus();
            search.setVisibility(View.GONE);
            searchT.setVisibility(View.VISIBLE);
        }
    }
    private void suggestion(List<ItemSearch> items){
        findViewById(R.id.item_detail_container).setVisibility(View.GONE);
        RecyclerView rv = findViewById(R.id.item_suggestion);
        rv.setVisibility(View.VISIBLE);
        rv.setLayoutManager(new LinearLayoutManager(this));
        AdapterSuggestion adapterSearch = new AdapterSuggestion(getBaseContext(), items);
        rv.setAdapter(adapterSearch);
        rv.requestDisallowInterceptTouchEvent(false);

//        MainCatalogFragment fragment = new MainCatalogFragment(items);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.item_detail_container, fragment)
//                .commitAllowingStateLoss();
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
                if (matches.size() > 0) {
                    OnSearch(matches.get(0));
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void refreshBar() {
        //draweble
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //menu/draweble
        recyclerView = findViewById(R.id.item_list);
        setupRecyclerView();
        if (recyclerView.getVisibility() == View.VISIBLE) {
            findViewById(R.id.btn_back).setOnClickListener(view -> finish());
            if (preference.getBoolean("side_menu", true)) {
                toggle.setDrawerIndicatorEnabled(false);
            } else {
                toggle.setDrawerIndicatorEnabled(true);
                recyclerView.setVisibility(View.GONE);
                findViewById(R.id.btn_back).setVisibility(View.GONE);
            }
        } else {
            toggle.setDrawerIndicatorEnabled(true);
            findViewById(R.id.btn_back).setVisibility(View.GONE);
        }
    }
    private void setupRecyclerView() {
        ItemMain menu_item = new ItemMain();
        menu_item.delItem();
        boolean pro = preference.getBoolean("pro_version", false);
        boolean sist = preference.getBoolean("side_exist", false) && pro;
        boolean all = preference.getBoolean("search_all", false);

        if (sist && all) {
            menu_item.addItem(new ItemMain.Item(0, "Везде", "all"));
            menu_item.addItem(new ItemMain.Item(0, "...", ""));
        }
        String[] baseArr = {"my-hit", "filmix", "topkino", "kinolive"};
        HashSet<String> def = new HashSet<>(Arrays.asList(baseArr));
        Set<String> pref_base = preference.getStringSet("base_catalog", def);
        if (pref_base.toString().contains("kinolive"))
            menu_item.addItem(new ItemMain.Item(0, "KinoLive", Statics.KINOLIVE_URL));
        if (pref_base.toString().contains("kinofs"))
            menu_item.addItem(new ItemMain.Item(0, "KinoFs", Statics.KINOFS_URL));
        if (pref_base.toString().contains("kinoxa"))
            menu_item.addItem(new ItemMain.Item(0, "Kinoxa", Statics.KINOXA_URL));
        if (pref_base.toString().contains("koshara"))
            menu_item.addItem(new ItemMain.Item(0, "Koshara", Statics.KOSHARA_URL));
        if (pref_base.toString().contains("my-hit"))
            menu_item.addItem(new ItemMain.Item(0, "My-Hit", Statics.MYHIT_URL));
        if (pref_base.toString().contains("rufilmtv"))
            menu_item.addItem(new ItemMain.Item(0, "RuFilmTv", Statics.RUFILMTV_URL));
        if (pref_base.toString().contains("topkino"))
            menu_item.addItem(new ItemMain.Item(0, "TopKino", Statics.TOPKINO_URL));
        if (pref_base.toString().contains("filmix"))
            menu_item.addItem(new ItemMain.Item(0, "Filmix", Statics.FILMIX_URL));
        if (pref_base.toString().contains("kinopub"))
            menu_item.addItem(new ItemMain.Item(0, "Kinopub", Statics.KINOSHA_URL));
        menu_item.addItem(new ItemMain.Item(0, "...", ""));
        String[] baseArrP = {"anidub", "animevost", "coldfilm", "fanserials", "kinodom"};
        HashSet<String> defP = new HashSet<>(Arrays.asList(baseArrP));
        Set<String> pref_baseP = preference.getStringSet("plus_catalog", defP);
        if (pref_baseP.toString().contains("anidub"))
            menu_item.addItem(new ItemMain.Item(0, "Anidub", Statics.ANIDUB_URL));
        if (pref_baseP.toString().contains("animevost"))
            menu_item.addItem(new ItemMain.Item(0, "AnimeVost", Statics.ANIMEVOST_URL));
        if (pref_baseP.toString().contains("coldfilm"))
            menu_item.addItem(new ItemMain.Item(0, "ColdFilm", Statics.COLDFILM_URL));
        if (pref_baseP.toString().contains("fanserials"))
            menu_item.addItem(new ItemMain.Item(0, "FanSerials", Statics.FANSERIALS_URL));
        if (pref_baseP.toString().contains("kinodom"))
            menu_item.addItem(new ItemMain.Item(0, "KinoDom", Statics.KINODOM_URL));
        recyclerView.setAdapter(new ItemRVAdapter(ItemMain.ITEMS));
    }

    private void OnSearch(String query) {
        invalidateOptionsMenu();
        onAttachedToWindow();
        setupRecyclerView();
        findViewById(R.id.item_detail_container).setVisibility(View.VISIBLE);
        RecyclerView rv = findViewById(R.id.item_suggestion);
        rv.setVisibility(View.GONE);

        search.setHint(query);
        searchT.setHint(query);
        hideKeyboard(this);
        if (!query.equals("null")) {
            this.query = query;
            refreshBar();
            ItemMain.xs_search = query;
            switch (toolbar.getSubtitle().toString().toLowerCase()) {
                case "koshara":
                    try {
                        query = URLEncoder.encode(query, "windows-1251");
                    } catch (UnsupportedEncodingException e) {
                        query = "error";
                    }
                    ItemMain.cur_url = Statics.KOSHARA_URL + "/index.php?do=search&subaction=search&titleonly=3&story=" + query;
                    break;
                case "fanserials":
//                    try {
//                        query = URLEncoder.encode(query, "windows-1251");
//                    } catch (UnsupportedEncodingException e) {
//                        query = "error";
//                    }
                    ItemMain.cur_url = Statics.FANSERIALS_URL + "/search/?query=" + query;
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
//                case "amcet":
//                    ItemMain.cur_url = Statics.AMCET_URL + "/?subaction=search&do=search&story=" + query;
//                    break;
                case "animevost":
                    ItemMain.cur_url = Statics.ANIMEVOST_URL;
                    ItemMain.xs_search = query;
                    break;
                case "anidub":
                    ItemMain.cur_url = Statics.ANIDUB_URL;
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
                case "kinolive":
                    try {
                        query = URLEncoder.encode(query, "windows-1251");
                    } catch (UnsupportedEncodingException e) {
                        query = "error";
                    }
                    ItemMain.xs_search = query;
                    ItemMain.cur_url = Statics.KINOLIVE_URL + "/index.php?do=search";
                    break;
                case "my-hit":
                    ItemMain.cur_url = Statics.MYHIT_URL + "/search/?q=" + query;
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
            }

            MainCatalogFragment fragment = new MainCatalogFragment().newInstance(ItemMain.cur_url, "Поиск", toolbar.getSubtitle().toString().toLowerCase());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commitAllowingStateLoss();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DetailActivity.url = "error";
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.back) {
            finish();
        } else {
            toolbar.setSubtitle(item.getTitle().toString().replace("Везде","all"));
            OnSearch(query);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }
    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setFocusable(true);
        
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(this);
        boolean pro = preference.getBoolean("pro_version", false);
        boolean sist = preference.getBoolean("side_exist", false) && pro;
        boolean all = preference.getBoolean("search_all", false);
        
        navigationView.getMenu().findItem(R.id.all).setVisible(sist && all);

        navigationView.getMenu().findItem(R.id.all).setChecked(toolbar.getSubtitle()
                .toString().toLowerCase().equals("all") || toolbar.getSubtitle()
                .toString().toLowerCase().equals("Везде"));
        navigationView.getMenu().findItem(R.id.coldfilm).setChecked(toolbar.getSubtitle()
                .toString().toLowerCase().equals("coldfilm"));
        navigationView.getMenu().findItem(R.id.fanserials).setChecked(toolbar.getSubtitle()
                .toString().toLowerCase().equals("fanserials"));
        navigationView.getMenu().findItem(R.id.animevost).setChecked(toolbar.getSubtitle()
                .toString().toLowerCase().equals("animevost"));
        navigationView.getMenu().findItem(R.id.anidub).setChecked(toolbar.getSubtitle()
                .toString().toLowerCase().equals("anidub"));
        navigationView.getMenu().findItem(R.id.kinolive).setChecked(toolbar.getSubtitle()
                .toString().toLowerCase().equals("kinolive"));
        navigationView.getMenu().findItem(R.id.filmix).setChecked(toolbar.getSubtitle()
                .toString().toLowerCase().equals("filmix"));
        navigationView.getMenu().findItem(R.id.rufilmtv).setChecked(toolbar.getSubtitle()
                .toString().toLowerCase().equals("rufilmtv"));
        navigationView.getMenu().findItem(R.id.myhit).setChecked(toolbar.getSubtitle()
                .toString().toLowerCase().equals("my-hit"));
        navigationView.getMenu().findItem(R.id.koshara).setChecked(toolbar.getSubtitle()
                .toString().toLowerCase().equals("koshara"));
        navigationView.getMenu().findItem(R.id.kinofs).setChecked(toolbar.getSubtitle()
                .toString().toLowerCase().equals("kinofs"));
        navigationView.getMenu().findItem(R.id.kinoxa).setChecked(toolbar.getSubtitle()
                .toString().toLowerCase().equals("kinoxa"));
        navigationView.getMenu().findItem(R.id.topkino).setChecked(toolbar.getSubtitle()
                .toString().toLowerCase().equals("topkino"));
        navigationView.getMenu().findItem(R.id.kinopub).setChecked(toolbar.getSubtitle()
                .toString().toLowerCase().equals("kinopub"));

        String[] baseArr = {"my-hit", "filmix", "topkino", "kinofs"};
        HashSet<String> def = new HashSet<>(Arrays.asList(baseArr));
        Set<String> pref_base = preference.getStringSet("base_catalog", def);
        navigationView.getMenu().findItem(R.id.kinoxa).setVisible(pref_base.toString().contains("kinoxa"));
        navigationView.getMenu().findItem(R.id.kinofs).setVisible(pref_base.toString().contains("kinofs"));
        navigationView.getMenu().findItem(R.id.topkino).setVisible(pref_base.toString().contains("topkino"));
        navigationView.getMenu().findItem(R.id.koshara).setVisible(pref_base.toString().contains("koshara"));
        navigationView.getMenu().findItem(R.id.myhit).setVisible(pref_base.toString().contains("my-hit"));
        navigationView.getMenu().findItem(R.id.rufilmtv).setVisible(pref_base.toString().contains("rufilmtv"));
        navigationView.getMenu().findItem(R.id.filmix).setVisible(pref_base.toString().contains("filmix"));
        navigationView.getMenu().findItem(R.id.kinopub).setVisible(pref_base.toString().contains("kinopub"));
        String[] baseArrP = {"anidub", "animevost", "coldfilm", "fanserials", "kinodom"};
        HashSet<String> defP = new HashSet<>(Arrays.asList(baseArrP));
        Set<String> pref_baseP = preference.getStringSet("plus_catalog", defP);
        navigationView.getMenu().findItem(R.id.anidub).setVisible(pref_baseP.toString().contains("anidub"));
        navigationView.getMenu().findItem(R.id.animevost).setVisible(pref_baseP.toString().contains("animevost"));
        navigationView.getMenu().findItem(R.id.coldfilm).setVisible(pref_baseP.toString().contains("coldfilm"));
        navigationView.getMenu().findItem(R.id.fanserials).setVisible(pref_baseP.toString().contains("fanserials"));
        navigationView.getMenu().findItem(R.id.kinodom).setVisible(pref_baseP.toString().contains("kinodom"));
    }

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

            if (mValues.get(position).name.toLowerCase().equals(toolbar.getSubtitle().toString().toLowerCase())) {
                holder.mView.setFocusable(false);
                Check(true, holder);
            } else {
                holder.mView.setFocusable(true);
                Check(false, holder);
            }
            holder.mItem = mValues.get(position);
            holder.mName.setText(mValues.get(position).name);

//            holder.mView.setFocusableInTouchMode(true);

            if (mValues.get(position).name.equals("...")) {
                holder.mView.setSelected(false);
                holder.mView.setEnabled(false);
                holder.mView.setFocusable(false);
                holder.separ.setVisibility(View.VISIBLE);
                holder.mName.setVisibility(View.GONE);
            }
            holder.mName.setTextColor(getResources().getColor(R.color.colorWhiteTr));

            holder.mView.setOnFocusChangeListener((view, b) -> {
                if (!view.isSelected()) {
                    view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    holder.mName.setTextColor(getResources().getColor(R.color.colorWhite));
                } else if (mValues.get(cur).name.toLowerCase().equals(toolbar.getSubtitle().toString()
                        .replace("all", "Везде").toLowerCase())) {
                    Check(true, holder);
                } else {
                    holder.mName.setTextColor(getResources().getColor(R.color.colorWhiteTr));
                    Check(false, holder);
                }
                view.setSelected(b);
            });

            holder.mView.setOnClickListener(v -> {
                ItemMain.xs_value = "";
                toolbar.setSubtitle(holder.mItem.getName().replace("Везде","all"));
                OnSearch(query);
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
