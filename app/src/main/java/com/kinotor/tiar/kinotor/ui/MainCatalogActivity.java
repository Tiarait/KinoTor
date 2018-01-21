package com.kinotor.tiar.kinotor.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.parser.ParserHtml;
import com.kinotor.tiar.kinotor.updater.Update;
import com.kinotor.tiar.kinotor.utils.DBHelper;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import static com.kinotor.tiar.kinotor.items.ItemMain.isLoading;

public class MainCatalogActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private static String subtitle = "Фильмы";
    private boolean upd_in_start, first_start = true;
    boolean doubleBackToExitPressedOnce = false;
    private Update update;
    private boolean coldfilm, exit, side_menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        coldfilm = preference.getBoolean("coldfilm", false);
        exit = preference.getBoolean("exit", false);
        side_menu = preference.getBoolean("side_menu", true);
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

        if (first_start){
            subtitle = preference.getString("start_category", "Фильмы");
            if (subtitle.equals("Фильмы")) ItemMain.cur_url = "http://koshara.co/nerufilm/";
            if (subtitle.equals("Сериалы")) ItemMain.cur_url = "http://koshara.co/serial/";
            if (subtitle.equals("Мультфильмы")) ItemMain.cur_url = "http://koshara.co/index.php?do=search&subaction=search&story=%EC%F3%EB%FC%F2%F4%E8%EB%FC%EC";
            if (subtitle.equals("Coldfilm")) ItemMain.cur_url = "http://coldfilm.ru/news/";
            first_start = false;
        }

        realodBar();

        upd_in_start = preference.getBoolean("auto_update", true);
        if (upd_in_start) {
            update = new Update(this);
            update.execute();
        }
        OnPage(ItemMain.cur_url, subtitle);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        //take our menu items
        // large-screen layouts (res/values-w900dp)
        if (recyclerView != null) {
            ItemMain menu_item = new ItemMain();
            menu_item.delItem();
            menu_item.addItem(new ItemMain.Item(0, "Фильмы", "http://koshara.co/nerufilm/"));
            menu_item.addItem(new ItemMain.Item(1, "Сериалы", "http://koshara.co/serial/"));
            menu_item.addItem(new ItemMain.Item(2, "Мультфильмы", "http://koshara.co/index.php?do=search&subaction=search&story=%EC%F3%EB%FC%F2%F4%E8%EB%FC%EC"));
            menu_item.addItem(new ItemMain.Item(3, "...", ""));
            menu_item.addItem(new ItemMain.Item(4, "Избранное", "http:// /"));
            menu_item.addItem(new ItemMain.Item(5, "История", "http:// /"));
            if (coldfilm) {
                menu_item.addItem(new ItemMain.Item(3, "...", ""));
                menu_item.addItem(new ItemMain.Item(3, "Coldfilm", "http://coldfilm.ru/news/"));
            }
            if (exit) {
                menu_item.addItem(new ItemMain.Item(3, "...", ""));
                menu_item.addItem(new ItemMain.Item(3, "Выход", "http:// /"));
            }

            recyclerView.setAdapter(new ItemRVAdapter(ItemMain.ITEMS));
        }
    }

    private void realodBar () {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle();
        //draweble
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //menu/draweble
        View recyclerView = findViewById(R.id.item_list);
        setupRecyclerView((RecyclerView) recyclerView);
        if (recyclerView != null) {
            if (side_menu)
                toggle.setDrawerIndicatorEnabled(false);
            else {
                toggle.setDrawerIndicatorEnabled(true);
                recyclerView.setVisibility(View.GONE);
            }
        }
    }

    public void setTitle() {
        setTitle(subtitle);
        if (ItemMain.cur_url.contains("http:"))
            toolbar.setSubtitle(ItemMain.cur_url.split("http://")[1].split("/")[0]);
        else if (ItemMain.cur_url.contains("https:"))
            toolbar.setSubtitle(ItemMain.cur_url.split("https://")[1].split("/")[0]);
    }

    private void OnPage (String url, String category) {
        if (isOnline(this) && !isLoading) {
            subtitle = category;

            onAttachedToWindow();

            ItemMain.cur_url = url;
            ItemMain.cur_page = 1;
            ItemMain.cur_items = 0;
            ParserHtml.itemHtml = null;
            ParserHtml.items = null;

            MainCatalogFragment fragment = new MainCatalogFragment(url, category);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();

            setTitle();
        } else if (!isOnline(this)){
            Toast.makeText(this,
                    "Ошибка интернет соединения...",
                    Toast.LENGTH_LONG).show();
        }
    }

    public boolean isOnline(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nInfo = cm.getActiveNetworkInfo();
        if (nInfo != null && nInfo.isConnected()) {
            return true;
        }   else {
            return false;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.films) {
            OnPage("http://koshara.co/nerufilm/", "Фильмы");
        } else if (id == R.id.serials) {
            OnPage("http://koshara.co/serial/", "Сериалы");
        } else if (id == R.id.mults) {
            OnPage("http://koshara.co/index.php?do=search&subaction=search&story=%EC%F3%EB%FC%F2%F4%E8%EB%FC%EC",
                    "Мультфильмы");
        } else if (id == R.id.favor) {
            OnPage("http:// /", "Избранное");
        } else if (id == R.id.history) {
            OnPage("http:// /", "История");
        } else if (id == R.id.coldfilm) {
            OnPage("http://coldfilm.ru/", "Coldfilm");
        } else if (id == R.id.exit) {
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setFocusable(true);

        if (!coldfilm)
            navigationView.getMenu().findItem(R.id.coldfilm).setVisible(false);
        if (!exit)
            navigationView.getMenu().findItem(R.id.exit).setVisible(false);


        if (subtitle.equals("Coldfilm"))
            navigationView.getMenu().findItem(R.id.coldfilm).setChecked(true);
        else navigationView.getMenu().findItem(R.id.coldfilm).setChecked(false);

        if (subtitle.equals("История"))
            navigationView.getMenu().findItem(R.id.history).setChecked(true);
        else navigationView.getMenu().findItem(R.id.history).setChecked(false);

        if (subtitle.equals("Избранное"))
            navigationView.getMenu().findItem(R.id.favor).setChecked(true);
        else navigationView.getMenu().findItem(R.id.favor).setChecked(false);

        if (subtitle.equals("Мультфильмы"))
            navigationView.getMenu().findItem(R.id.mults).setChecked(true);
        else navigationView.getMenu().findItem(R.id.mults).setChecked(false);

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
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    subtitle = "Поиск: " + query;
                    onAttachedToWindow();

                    if (ItemMain.cur_url.contains("koshara.co"))
                        query = URLEncoder.encode(query, "windows-1251");

                    View recyclerView = findViewById(R.id.item_list);
                    setupRecyclerView((RecyclerView) recyclerView);

                    ItemMain.cur_page = 1;
                    ItemMain.cur_items = 0;
                    if (ItemMain.cur_url.contains("koshara.co"))
                        ItemMain.cur_url = "http://koshara.co/index.php?do=search&subaction=search&story=" + query;
                    if (ItemMain.cur_url.contains("coldfilm.ru"))
                        ItemMain.cur_url = "http://coldfilm.ru/search/?q=" + query;

                    ParserHtml.itemHtml = null;
                    ParserHtml.items = null;

                    MainCatalogFragment fragment = new MainCatalogFragment(ItemMain.cur_url, "Поиск");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.item_detail_container, fragment)
                            .commit();
                    searchView.clearFocus();
                    setTitle();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_category:
                if (subtitle.equals("Избранное") || subtitle.equals("История")){
                    final String[] ctg_list = {"Сохранить",
                            "Восстановить",
                            "Очистить"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, 2);
                    builder.setTitle("Выберите действие").setItems(ctg_list, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DBHelper dbHelper;
                            dbHelper = new DBHelper(getBaseContext());
                            if (i == 0) {
                                File f = Environment.getExternalStorageDirectory();
                                if (f.exists()) {
                                    dbHelper.copyDataBaseToSd();
                                    Toast.makeText(getBaseContext(), "БД успешно сохранена на sd карту", Toast.LENGTH_SHORT).show();
                                } else{
                                    Toast.makeText(getBaseContext(), "Sd карта отсутствует", Toast.LENGTH_SHORT).show();
                                }
                            } else if (i == 1) {
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
                            } else {
                                dbHelper.deleteAll(subtitle.equals("Избранное") ? "favor" : "history");
                                Toast.makeText(getBaseContext(), subtitle.equals("Избранное") ?
                                        subtitle + " очищенно" : subtitle + " очищенна", Toast.LENGTH_SHORT).show();
                            }
                            OnPage("http:// /", subtitle);
                        }
                    });
                    builder.create().show();
                } else if (ItemMain.cur_url.contains("koshara.co")) {
                    final String[] ctg_list = {"CAMRIP / TS",
                            "WEBRIP / WEB-DL",
                            "HDRIP / BDRIP",
                            "ОТЕЧЕСТВЕННЫЕ ФИЛЬМЫ",
                            "ЗАРУБЕЖНЫЕ ФИЛЬМЫ",
                            "СЕРИАЛЫ"};
                    final String[] url_list = {"http://koshara.co/nerufilm/camrip/",
                            "http://koshara.co/nerufilm/webrips/",
                            "http://koshara.co/nerufilm/hd/",
                            "http://koshara.co/rufilm/",
                            "http://koshara.co/nerufilm/",
                            "http://koshara.co/serial/"};

                    AlertDialog.Builder builder = new AlertDialog.Builder(this, 2);
                    builder.setTitle("Выберите категорию").setItems(ctg_list, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            OnPage(url_list[i], ctg_list[i]);
                        }
                    });
                    builder.create().show();
                } else if (ItemMain.cur_url.contains("coldfilm.ru")) {
                    final String[] ctg_list = {"Каталог сериалов"};
                    final String[] url_list = {"http://coldfilm.ru/load/"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(this, 2);
                    builder.setTitle("Выберите категорию").setItems(ctg_list, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            OnPage(url_list[i], ctg_list[i]);
                        }
                    });
                    builder.create().show();
                }
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
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

        public ItemRVAdapter(List<ItemMain.Item> items) {
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
                        holder.mView.setAlpha((float) 0.7);
                    }
                    else if (mValues.get(cur).name.equals(subtitle)) {
                        view.setAlpha((float) 1);
                        Check(true, holder);
                    }
                    else {
                        view.setAlpha((float) 1);
                        Check(false, holder);
                    }
                    view.setSelected(b);
                }
            });

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.mItem.getName().equals("Выход")) {
                        finish();
                    } else if (!ItemMain.isLoading) {
                        View recyclerView = findViewById(R.id.item_list);
                        setupRecyclerView((RecyclerView) recyclerView);

                        OnPage(holder.mItem.getdetails(), holder.mItem.getName());
                    }
                }
            });
        }

        public void Check(boolean check, final ViewHolder holder){
            if (check){
                holder.mView.setEnabled(false);
                holder.mView.setFocusable(false);
                holder.select.setVisibility(View.VISIBLE);
                holder.mView.setBackgroundColor(getResources().getColor(R.color.colorAccentDark));
                holder.mName.setTextColor(getResources().getColor(R.color.colorBlack));
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
            public final View mView, separ;
            public final TextView mName;
            public final ImageView select;
            public ItemMain.Item mItem;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                select = (ImageView) view.findViewById(R.id.select_list);
                separ = (View) view.findViewById(R.id.separ);
                mName = (TextView) view.findViewById(R.id.content);
            }
        }
    }
}
