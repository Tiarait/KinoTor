package com.kinotor.tiar.kinotor.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemCatalogUrls;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.updater.Update;
import com.kinotor.tiar.kinotor.utils.DBHelper;
import com.kinotor.tiar.kinotor.utils.Utils;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class MainCatalogActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private static String subtitle = "Фильмы", catalog;
    private boolean first_start = true;
    boolean doubleBackToExitPressedOnce = false;
    private Utils utils;
    private ItemCatalogUrls catalogUrls;
    private boolean coldFilm, animevost, exit, side_menu;
    private SharedPreferences preference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        coldFilm = preference.getBoolean("coldfilm_menu", false);
        animevost = preference.getBoolean("animevost_menu", false);
        exit = preference.getBoolean("exit", false);
        side_menu = preference.getBoolean("side_menu", true);
        catalog = preference.getString("catalog", "amcet");

        Statics.KOSHARA_URL = "http://koshara." + preference.getString("koshara_url", "co");
        Statics.COLDFILM_URL = "http://coldfilm." + preference.getString("coldfilm_url", "info");
        Statics.ANIMEVOST_URL = "http://animevost." + preference.getString("animevost_url", "org");
        Statics.AMCET_URL = "https://amcet." + preference.getString("amcet_url", "net");

        if (preference.getBoolean("fullscreen", false)) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            this.getWindow().setStatusBarColor(getResources().getColor(R.color.colorBlack));
        } else {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setNavigationBarTintEnabled(true);
            tintManager.setTintColor(getResources().getColor(R.color.colorBlack));

            tintManager.setNavigationBarTintResource(getResources().getColor(R.color.colorBlack));
        }

        catalogUrls = new ItemCatalogUrls();
        utils = new Utils();

        if (first_start){
            subtitle = preference.getString("start_category", "Фильмы");
            setCurURL();
            first_start = false;
        }

        refreshBar();

        boolean upd_in_start = preference.getBoolean("auto_update", true);
        if (upd_in_start) {
            Update updator = new Update(this);
            updator.execute();
        }
        OnPage(ItemMain.cur_url, subtitle);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        //take our menu items
        // large-screen layouts (res/values-w900dp)
        if (recyclerView != null) {
            ItemMain menu_item = new ItemMain();
            menu_item.delItem();
            menu_item.addItem(new ItemMain.Item(0, "Фильмы", catalogUrls.film(catalog)));
            menu_item.addItem(new ItemMain.Item(1, "Сериалы", catalogUrls.serial(catalog)));
            menu_item.addItem(new ItemMain.Item(2, "Мультфильмы", catalogUrls.mult(catalog)));
            if (catalog.equals("amcet")) menu_item.addItem(new ItemMain.Item(3, "Аниме", catalogUrls.anime(catalog)));
            menu_item.addItem(new ItemMain.Item(4, "...", ""));
            menu_item.addItem(new ItemMain.Item(5, "Избранное", "http:// /"));
            menu_item.addItem(new ItemMain.Item(6, "История", "http:// /"));
            if (coldFilm || animevost)
                menu_item.addItem(new ItemMain.Item(7, "...", ""));
            if (coldFilm) menu_item.addItem(new ItemMain.Item(8, "Coldfilm", Statics.COLDFILM_URL + "/news/"));
            if (animevost) menu_item.addItem(new ItemMain.Item(8, "AnimeVost", Statics.ANIMEVOST_URL + "/"));
            if (exit) {
                menu_item.addItem(new ItemMain.Item(9, "...", ""));
                menu_item.addItem(new ItemMain.Item(10, "Выход", "http:// /"));
            }

            recyclerView.setAdapter(new ItemRVAdapter(ItemMain.ITEMS));
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
        View recyclerView = findViewById(R.id.item_list);
        setupRecyclerView((RecyclerView) recyclerView);
        if (recyclerView != null) {
            if (side_menu && this.getResources().getConfiguration().orientation == 2)
                toggle.setDrawerIndicatorEnabled(false);
            else {
                toggle.setDrawerIndicatorEnabled(true);
                recyclerView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshBar();
    }

    private void setCurURL(){
        if (subtitle.equals("Coldfilm")) ItemMain.cur_url = Statics.COLDFILM_URL + "/news/";
        if (subtitle.equals("AnimeVost")) ItemMain.cur_url = Statics.ANIMEVOST_URL + "/";
        if (subtitle.equals("Фильмы")) ItemMain.cur_url = catalogUrls.film(catalog);
        if (subtitle.equals("Сериалы")) ItemMain.cur_url = catalogUrls.serial(catalog);
        if (subtitle.equals("Мультфильмы")) ItemMain.cur_url = catalogUrls.mult(catalog);
        if (subtitle.equals("Аниме")) ItemMain.cur_url = catalogUrls.anime(catalog);
    }

    public void setTitle() {
        toolbar.setTitle(subtitle);
        toolbar.setSubtitle(catalog);
        if (ItemMain.cur_url.contains("coldfilm")) {
            setTitle("ColdFilm");
            toolbar.setSubtitle("coldfilm");
        }
        if (ItemMain.cur_url.contains("animevost")) {
            setTitle("AnimeVost");
            toolbar.setSubtitle("animevost");
        }
        if (subtitle.equals("История") || subtitle.equals("Избранное")) {
            toolbar.setSubtitle("kinotor");
        }
    }

    private void OnPage (String url, String category) {
        if (utils.isOnline(this)) {
            ItemMain.cur_url = url;
            subtitle = category;

            invalidateOptionsMenu();
            onAttachedToWindow();

            ItemMain.cur_items = 0;

            if (subtitle.contains("Поиск:")) {
                Log.d("Main", "OnPage: " + category + " " + url);
                if (catalog.contains("amcet")) ItemMain.cur_url = Statics.AMCET_URL;
                else if (catalog.contains("koshara")) ItemMain.cur_url = Statics.KOSHARA_URL;
                else if (ItemMain.cur_url.contains("coldfim")) ItemMain.cur_url = Statics.COLDFILM_URL;
                else if (ItemMain.cur_url.contains("animevost")) ItemMain.cur_url = Statics.ANIMEVOST_URL;
                searchGo(subtitle.split("Поиск: ")[1].trim());
            } else {
                Log.d("Main", "OnPage: " + category + " " + url);
                MainCatalogFragment fragment = new MainCatalogFragment(url, category);
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
        if (id == R.id.films) {
            OnPage(catalogUrls.film(catalog), "Фильмы");
        } else if (id == R.id.serials) {
            OnPage(catalogUrls.serial(catalog), "Сериалы");
        } else if (id == R.id.mults) {
            OnPage(catalogUrls.mult(catalog), "Мультфильмы");
        } else if (id == R.id.anime) {
            OnPage(catalogUrls.anime(catalog), "Аниме");
        } else if (id == R.id.favor) {
            OnPage("http:// /", "Избранное");
        } else if (id == R.id.history) {
            OnPage("http:// /", "История");
        } else if (id == R.id.animevost) {
            OnPage(Statics.ANIMEVOST_URL + "/", "AnimeVost");
        } else if (id == R.id.coldfilm) {
            OnPage(Statics.COLDFILM_URL + "/", "Coldfilm");
        } else if (id == R.id.exit) {
            ItemMain.cur_url = "q";
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

        if (!coldFilm) navigationView.getMenu().findItem(R.id.coldfilm).setVisible(false);
        else navigationView.getMenu().findItem(R.id.coldfilm).setVisible(true);
        if (!animevost) navigationView.getMenu().findItem(R.id.animevost).setVisible(false);
        else navigationView.getMenu().findItem(R.id.animevost).setVisible(true);
        if (!exit) navigationView.getMenu().findItem(R.id.exit).setVisible(false);
        if (!catalog.equals("amcet")) navigationView.getMenu().findItem(R.id.anime).setVisible(false);
        else navigationView.getMenu().findItem(R.id.anime).setVisible(true);


        if (subtitle.equals("Coldfilm"))
            navigationView.getMenu().findItem(R.id.coldfilm).setChecked(true);
        else navigationView.getMenu().findItem(R.id.coldfilm).setChecked(false);

        if (subtitle.equals("AnimeVost"))
            navigationView.getMenu().findItem(R.id.animevost).setChecked(true);
        else navigationView.getMenu().findItem(R.id.animevost).setChecked(false);

        if (subtitle.equals("История") || getTitle().equals("История"))
            navigationView.getMenu().findItem(R.id.history).setChecked(true);
        else navigationView.getMenu().findItem(R.id.history).setChecked(false);

        if (subtitle.equals("Избранное") || getTitle().equals("Избранное"))
            navigationView.getMenu().findItem(R.id.favor).setChecked(true);
        else navigationView.getMenu().findItem(R.id.favor).setChecked(false);

        if (subtitle.equals("Мультфильмы"))
            navigationView.getMenu().findItem(R.id.mults).setChecked(true);
        else navigationView.getMenu().findItem(R.id.mults).setChecked(false);

        if (subtitle.equals("Аниме"))
            navigationView.getMenu().findItem(R.id.anime).setChecked(true);
        else navigationView.getMenu().findItem(R.id.anime).setChecked(false);

        if (subtitle.equals("Сериалы"))
            navigationView.getMenu().findItem(R.id.serials).setChecked(true);
        else navigationView.getMenu().findItem(R.id.serials).setChecked(false);

        if (subtitle.equals("Фильмы"))
            navigationView.getMenu().findItem(R.id.films).setChecked(true);
        else navigationView.getMenu().findItem(R.id.films).setChecked(false);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem menuSort = menu.findItem(R.id.action_sort);
        MenuItem menuCategory = menu.findItem(R.id.action_category);
        MenuItem amcet = menu.findItem(R.id.menuСatalogAmcet);
        MenuItem koshara = menu.findItem(R.id.menuСatalogKoshara);
        MenuItem dbSave = menu.findItem(R.id.action_db_save);
        MenuItem dbDel = menu.findItem(R.id.action_db_del);
        MenuItem dbRest = menu.findItem(R.id.action_db_restore);
        MenuItem menuCatalog = menu.findItem(R.id.action_catalog);
        MenuItem menuSortCountry = menu.findItem(R.id.menuSortOrderCountry);

        if (catalog.equals("amcet")) {
            menuSort.setVisible(true);
            amcet.setChecked(true);
            amcet.setEnabled(false);
            koshara.setChecked(false);
        } else {
            menuSort.setVisible(false);
            amcet.setChecked(false);
            koshara.setChecked(true);
            koshara.setEnabled(false);
        }

        if (subtitle.equals("История") || subtitle.equals("Избранное")) {
            dbSave.setVisible(true);
            dbDel.setVisible(true);
            dbRest.setVisible(true);
        } else {
            dbSave.setVisible(false);
            dbDel.setVisible(false);
            dbRest.setVisible(false);
        }

        if (ItemMain.cur_url.contains("coldfilm") || subtitle.equals("Избранное") ||
                subtitle.equals("История")) {
            menuSort.setVisible(false);
            menuCategory.setVisible(false);
            menuCatalog.setVisible(false);
        } else if (ItemMain.cur_url.contains("animevost")) {
            menuSort.setVisible(true);
            menuSortCountry.setVisible(false);
            menuCategory.setVisible(false);
            menuCatalog.setVisible(false);
        } else {
            menuCatalog.setVisible(true);
            menuSortCountry.setVisible(true);
        }

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchGo(query);
                searchView.clearFocus();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void searchGo(String query){
        subtitle = "Поиск: " + query;
        onAttachedToWindow();
        ItemMain.cur_items = 0;
        ItemMain.xs_value = "";

        if (ItemMain.cur_url.contains("koshara"))
            try {
                query = URLEncoder.encode(query, "windows-1251");
            } catch (UnsupportedEncodingException e) {
                query = "error";
            }

        View recyclerView = findViewById(R.id.item_list);
        setupRecyclerView((RecyclerView) recyclerView);

        if (ItemMain.cur_url.contains("koshara"))
            ItemMain.cur_url = Statics.KOSHARA_URL + "/index.php?do=search&subaction=search&titleonly=3&story=" + query;
        if (ItemMain.cur_url.contains("coldfilm"))
            ItemMain.cur_url = Statics.COLDFILM_URL + "/search/?q=" + query;
        if (ItemMain.cur_url.contains("amcet"))
            ItemMain.xs_search = query;
        if (ItemMain.cur_url.contains("animevost"))
            ItemMain.xs_search = query;

        MainCatalogFragment fragment = new MainCatalogFragment(ItemMain.cur_url, "Поиск");
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_detail_container, fragment)
                .commit();
        setTitle();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_category:
                final String[] ctg_list = catalogUrls.getGenre(catalog);
                final String[] url_list = catalogUrls.getGenreUrl(catalog);

                AlertDialog.Builder builder = new AlertDialog.Builder(this, 2);
                builder.setTitle("Выберите категорию").setItems(ctg_list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ItemMain.xs_value = "";
                        OnPage(url_list[i], ctg_list[i]);
                    }
                });
                builder.create().show();
                break;
            case R.id.action_settings:
                finish();
                Intent i = new Intent(this, SettingsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                this.startActivity(i);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDBsave(MenuItem item) {
        DBHelper dbHelper = new DBHelper(getBaseContext());
        File f = Environment.getExternalStorageDirectory();
        if (f.exists()) {
            dbHelper.copyDataBaseToSd();
            Toast.makeText(getBaseContext(), "БД успешно сохранена на sd карту", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(getBaseContext(), "Sd карта отсутствует", Toast.LENGTH_SHORT).show();
        }
        OnPage("http:// /", subtitle);
    }

    public void onDBdel(MenuItem item) {
        DBHelper dbHelper = new DBHelper(getBaseContext());
        dbHelper.deleteAll(subtitle.equals("Избранное") ? "favor" : "history");
        Toast.makeText(getBaseContext(), subtitle.equals("Избранное") ?
                subtitle + " очищенно" : subtitle + " очищенна", Toast.LENGTH_SHORT).show();
        OnPage("http:// /", subtitle);
    }

    public void onDBrest(MenuItem item) {
        DBHelper dbHelper = new DBHelper(getBaseContext());
        File f = new File(Environment.getExternalStorageDirectory() + "/" +
                getBaseContext().getString(R.string.app_name) + "/DB");
        if (f.exists()) {
            dbHelper.copyDataBaseToData();
            Toast.makeText(getBaseContext(), "БД успешно восстановленна", Toast.LENGTH_SHORT).show();
        } else{
            Toast.makeText(getBaseContext(), "Файл \"" +
                            getBaseContext().getString(R.string.app_name) + "/DB\" не найден",
                    Toast.LENGTH_SHORT).show();
        }
        OnPage("http:// /", subtitle);
    }

    public void onSortAll(MenuItem item) {
//        DialogFragment sort = new SortDialog() {
//            @Override
//            public void ok(String[] x) {
//                ItemMain.xs_value = x;
//                OnPage(ItemMain.cur_url, subtitle);
//            }
//        };
//        sort.show(this.getFragmentManager(), subtitle);
    }

    public void onSortOrderList(MenuItem item) {
        if (ItemMain.cur_url.contains("animevost")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this, 2);
            builder.setTitle("Выберите тип").setItems(catalogUrls.getSortName("animevost"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    OnPage(catalogUrls.getSortUrl("animevost")[i], subtitle);
                }
            });
            builder.create().show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, 2);
            builder.setTitle("Выберите тип").setItems(catalogUrls.getSortName("amcet"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ItemMain.xs_field = "defaultsort";
                    ItemMain.xs_value = catalogUrls.getSortUrl("amcet")[i];
                    OnPage(ItemMain.cur_url, subtitle);
                }
            });
            builder.create().show();
        }
    }

    public void onSortOrderGenre(MenuItem item) {
        if (ItemMain.cur_url.contains("animevost")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this, 2);
            builder.setTitle("Выберите жанр").setItems(catalogUrls.getGenre("animevost"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    OnPage(catalogUrls.getGenreUrl("animevost")[i], subtitle);
                }
            });
            builder.create().show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, 2);
            builder.setTitle("Выберите жанр").setItems(catalogUrls.getGenre("amcet"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ItemMain.xs_field = "category";
                    ItemMain.xs_value = catalogUrls.getGenreUrl("amcetID")[i];
                    OnPage(ItemMain.cur_url, subtitle);
                }
            });
            builder.create().show();
        }
    }

    public void onSortOrderYear(MenuItem item) {
        if (ItemMain.cur_url.contains("animevost")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this, 2);
            builder.setTitle("Выберите год").setItems(catalogUrls.getYear("animevost"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    OnPage(Statics.ANIMEVOST_URL + "/god/" + catalogUrls.getYear("animevost")[i] + "/", subtitle);
                }
            });
            builder.create().show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, 2);
            builder.setTitle("Выберите год").setItems(catalogUrls.getYear("amcet"), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ItemMain.xs_field = "year";
                    ItemMain.xs_value = catalogUrls.getYear("amcet")[i];
                    OnPage(ItemMain.cur_url, subtitle);
                }
            });
            builder.create().show();
        }
    }

    public void onSortOrderCountry(MenuItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, 2);
        builder.setTitle("Выберите страну").setItems(catalogUrls.getSortAmcetCountry(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ItemMain.xs_field = "country";
                ItemMain.xs_value = catalogUrls.getSortAmcetCountry()[i];
                OnPage(ItemMain.cur_url, subtitle);
            }
        });
        builder.create().show();
    }

    public void onSelectCatalog(MenuItem item) {
        item.setChecked(true);
        SharedPreferences.Editor editor = preference.edit();
        if (item.getTitle().equals("Amcet"))
            editor.putString("catalog", "amcet");
        else editor.putString("catalog", "koshara");
        editor.apply();

        catalog = preference.getString("catalog", "amcet");
        setCurURL();

        View recyclerView = findViewById(R.id.item_list);
        setupRecyclerView((RecyclerView) recyclerView);

        OnPage(ItemMain.cur_url, subtitle);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                ItemMain.cur_url = "q";
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Нажмите назад для выхода", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    //Our menu/draweble
    public class ItemRVAdapter
            extends RecyclerView.Adapter<ItemRVAdapter.ViewHolder> {
        private List<ItemMain.Item> mValues;
        ItemRVAdapter(List<ItemMain.Item> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final int cur = position;

            if (mValues.get(position).name.equals(subtitle)) {
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

            holder.mView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!view.isSelected()) {
                        view.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                        holder.mName.setTextColor(getResources().getColor(R.color.colorWhite));
                    }
                    else if (mValues.get(cur).name.equals(subtitle)) {
                        Check(true, holder);
                    }
                    else {
                        holder.mName.setTextColor(getResources().getColor(R.color.colorDarkWhite));
                        Check(false, holder);
                    }
                    view.setSelected(b);
                }
            });

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ItemMain.xs_value = "";
                    if (holder.mItem.getName().equals("Выход")) {
                        ItemMain.cur_url = "q";
                        finish();
                    } else {
                        View recyclerView = findViewById(R.id.item_list);
                        setupRecyclerView((RecyclerView) recyclerView);
                        OnPage(holder.mItem.getdetails(), holder.mItem.getName());
                    }
                }
            });
        }

        void Check(boolean check, final ViewHolder holder){
            if (check){
                holder.mView.setEnabled(false);
                holder.mView.setFocusable(false);
                holder.select.setVisibility(View.VISIBLE);
                holder.mView.setBackgroundColor(getResources().getColor(R.color.colorAccentDark));
                holder.mName.setTextColor(getResources().getColor(R.color.colorWhite));
            } else {
                holder.mView.setFocusable(true);
                holder.mView.setEnabled(true);
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
            public final TextView mName;
            public final ImageView select;
            public ItemMain.Item mItem;

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
