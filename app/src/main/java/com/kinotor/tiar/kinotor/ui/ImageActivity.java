package com.kinotor.tiar.kinotor.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.kinotor.tiar.kinotor.R;
import com.squareup.picasso.Picasso;

public class ImageActivity extends AppCompatActivity {
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            url = bundle.getString("Url");
        else url = "error";

        ImageView img = findViewById(R.id.img);
        ImageView bg = findViewById(R.id.img_bg);

        Picasso.with(this)
                .load(url)
                .into(img);
        Picasso.with(this)
                .load(url)
                .into(bg);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
