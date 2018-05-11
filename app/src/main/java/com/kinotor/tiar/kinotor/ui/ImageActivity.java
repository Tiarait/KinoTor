package com.kinotor.tiar.kinotor.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.kinotor.tiar.kinotor.R;
import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            url = bundle.getString("Url");
        else url = "error";

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ImageView img = findViewById(R.id.img);
        ImageView bg = findViewById(R.id.img_bg);

        Picasso.with(this)
                .load(url)
                .into(img);
        Picasso.with(this)
                .load(url)
                .into(bg);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
