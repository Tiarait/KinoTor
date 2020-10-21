package com.kinotor.tiar.kinotor.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.updater.Update;
import com.kinotor.tiar.kinotor.utils.SignatureUtil;

public class SplashActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setContentView(R.layout.activity_splash);

//        if (SignatureUtil.isGetHooked())
//            finish();
//        else if (SignatureUtil.validateAppSignature(this))
            onPlay();
//        else finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onPlay();
    }

    private void onPlay() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        else {
            TextView splash_title = findViewById(R.id.splash_title);
            TextView splash_subtitle = findViewById(R.id.splash_subtitle);
            Animation splash = AnimationUtils.loadAnimation(this, R.anim.splash);

            splash_title.startAnimation(splash);
            splash_subtitle.startAnimation(splash);

            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                try {
                    ProviderInstaller.installIfNeeded(this);
                } catch (GooglePlayServicesRepairableException e) {
                    // Thrown when Google Play Services is not installed, up-to-date, or enabled
                    // Show dialog to allow users to install, update, or otherwise enable Google Play services.
                    GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), this, 0);
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.e("SecurityException", "Google Play Services not available.");
                }
            }

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

            boolean pro = preferences.getBoolean("pro_version", false);
            boolean sext = preferences.getBoolean("side_exist", false) && pro;
            Class<?> mainClass;
//        if (preferences.getBoolean("tv_activity_main", true))
//            mainClass = MainActivityTv.class;
//        else
            mainClass = MainCatalogActivity.class;
            if (!preferences.getBoolean("flag_respons", false)) {
                PreferenceManager.setDefaultValues(this, R.xml.pref_acc, false);
                PreferenceManager.setDefaultValues(this, R.xml.pref_disabled, false);
                PreferenceManager.setDefaultValues(this, R.xml.pref_domen, false);
                PreferenceManager.setDefaultValues(this, R.xml.pref_settings, false);
                PreferenceManager.setDefaultValues(this, R.xml.pref_tv, false);
                PreferenceManager.setDefaultValues(this, R.xml.pref_information, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
                builder.setCancelable(false).setTitle("Условия использования")
                        .setMessage("Данное приложение является бесплатным и представляет собой данного рода поисковик.\n" +
                                "Приложение осуществляет поиск только на открытых ресурсах.\n" +
                                "Автор приложения не несет ответственности за найденный контент на этих ресурсах.\n" +
                                "Если вы нашли материал, который нарушает Ваше право интеллектуальной собственности, прошу обратиться к администрации каталога на котором обнаружен материал.\n" +
                                "\n" +
                                "Скачать стабильную версию вы всегда можете на сайте tiardev.ru.")
                        .setPositiveButton("Принимаю", (dialog, id) -> {
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putBoolean("flag_respons", true);
                            editor.apply();
                            Intent intent = new Intent(SplashActivity.this, mainClass);
                            startActivity(intent);
                            finish();
                        })
                        .setNegativeButton("Отказ", (dialog, id) -> finish())
                        .create().show();
            } else if (sext) {
                Update update = new Update(this, "acc", location -> {
                    Thread logoTimer = new Thread() {
                        public void run() {
                            try {
                                int logoTimer = 0;
                                while (logoTimer < 200) {
                                    sleep(100);
                                    logoTimer += 100;
                                }
                                Intent intent = new Intent(SplashActivity.this, mainClass);
                                startActivity(intent);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                finish();
                            }
                        }
                    };
                    logoTimer.start();
                });
                update.execute();
            } else {
                Thread logoTimer = new Thread() {
                    public void run() {
                        try {
                            int logoTimer = 0;
                            while (logoTimer < 2000) {
                                sleep(100);
                                logoTimer += 100;
                            }
                            Intent intent = new Intent(SplashActivity.this, mainClass);
                            startActivity(intent);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            finish();
                        }
                    }
                };
                logoTimer.start();
            }
        }
    }
}
