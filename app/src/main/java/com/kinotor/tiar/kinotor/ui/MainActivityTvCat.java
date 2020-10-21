package com.kinotor.tiar.kinotor.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.ui.fragments.MainCatalogFragment;

public class MainActivityTvCat extends AppCompatActivity {

    SharedPreferences preference;
    private RecyclerView rv_catalog;
    private LinearLayout pb;
    private String url = "error", category = "error", catalog = "error";
    private int colorStatus = R.color.colorPrimaryLight;
    private int colorBg = R.color.colorPrimaryLight;
    private int colorText = R.color.colorWhite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        setContentView(R.layout.activity_main_tv_cat);

        ImageView search = findViewById(R.id.img_action_search);
        ImageView back = findViewById(R.id.img_action_back);
        ImageView filter = findViewById(R.id.img_action_filter);
        ScrollView scroll = findViewById(R.id.scrol);

        search.setFocusable(true);
        back.setFocusable(true);
        filter.setFocusable(true);

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
        back.setOnClickListener(view -> {
            finish();
        });
        back.setOnFocusChangeListener((view, b) -> {
            scroll.scrollTo(0,0);
            if (b) {
                back.setScaleX((float) 1.3);
                back.setScaleY((float) 1.3);
            } else {
                back.setScaleX((float) 1);
                back.setScaleY((float) 1);
            }
        });
        filter.setOnClickListener(view -> {
        });
        filter.setOnFocusChangeListener((view, b) -> {
            scroll.scrollTo(0,0);
            if (b) {
                filter.setScaleX((float) 1.3);
                filter.setScaleY((float) 1.3);
            } else {
                filter.setScaleX((float) 1);
                filter.setScaleY((float) 1);
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("Url") != null)
                url = bundle.getString("Url");
            if (bundle.getString("Category") != null)
                category = bundle.getString("Category");
            if (bundle.getString("Catalog") != null)
                catalog = bundle.getString("Catalog");
            TextView title = findViewById(R.id.linear_toolbar_title);
            title.setText(category);
            start();
        } else finish();
    }

    private void start() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_cat_container, new MainCatalogFragment().newInstance(url, category, catalog))
                .commit();
    }
}
