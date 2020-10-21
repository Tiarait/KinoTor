package com.kinotor.tiar.kinotor.ui;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.tasks.Task;
import com.kinotor.tiar.kinotor.BuildConfig;
import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.parser.catalog.filmix.ParserFilmixLogin;
import com.kinotor.tiar.kinotor.parser.catalog.ParserKinopubLogin;
import com.kinotor.tiar.kinotor.parser.torrents.AnidubTrLogin;
import com.kinotor.tiar.kinotor.parser.torrents.GreenTeaTrLogin;
import com.kinotor.tiar.kinotor.parser.torrents.HurtomLogin;
import com.kinotor.tiar.kinotor.parser.torrents.KinozalLogin;
import com.kinotor.tiar.kinotor.parser.torrents.RutrackerLogin;
import com.kinotor.tiar.kinotor.updater.Update;
import com.kinotor.tiar.kinotor.utils.AppCompatPreferenceActivity;
import com.kinotor.tiar.kinotor.utils.DBHelper;
import com.kinotor.tiar.kinotor.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingsActivity extends AppCompatPreferenceActivity {

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Statics.refreshMain = true;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        setupActionBar();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
//            if (getTitle().toString().toLowerCase().equals("настройки"))
//                actionBar.setHomeAsUpIndicator(R.drawable.ic_check);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || InfoPreferenceFragment.class.getName().equals(fragmentName)
                || InterfPreferenceFragment.class.getName().equals(fragmentName)
                || SettingsPreferenceFragment.class.getName().equals(fragmentName)
                || DomensPreferenceFragment.class.getName().equals(fragmentName)
                || ProPreferenceFragment.class.getName().equals(fragmentName)
                || AccountsPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class InfoPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_information);

            Preference version = findPreference("pref_static_field_key");
            version.setSummary("версия : " + BuildConfig.VERSION_NAME);

            Preference btn_update = findPreference("update");
            btn_update.setOnPreferenceClickListener(preference -> {
                Update update = new Update(getActivity(), "version", null);
                update.execute();
                return true;
            });

            Preference btn_email = findPreference("email");
            btn_email.setOnPreferenceClickListener(preference -> {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "KinoTor");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"tiarait.dev@gmail.com"});
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                emailIntent.setType("message/rfc822");
                startActivity(Intent.createChooser(emailIntent, "Выберите email клиент :"));
                return true;
            });
            Preference btn_telega = findPreference("telega_chat");
            btn_telega.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://t.me/KinoTor"));
                startActivity(intent);
                return true;
            });
            Preference btn_telega2 = findPreference("telega_channel");
            btn_telega2.setOnPreferenceClickListener(preference -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://t.me/Kino_Tor"));
                startActivity(intent);
                return true;
            });
            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class InterfPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_tv);


            setHasOptionsMenu(true);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class SettingsPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_settings);
            setHasOptionsMenu(true);

            Preference btn_dell_historyWatch = findPreference("db_del_historyWatch");
            btn_dell_historyWatch.setOnPreferenceClickListener(preference -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                builder.setMessage("Вы уверены что хотите очистить историю просмотренных серий?")
                        .setNegativeButton("Отмена", (dialog, id) -> dialog.dismiss())
                        .setPositiveButton("Да", (dialog, id) -> {
                            DBHelper dbHelper = new DBHelper(getActivity());
                            dbHelper.deleteAll("historyWatch");
                        })
                        .create().show();
                return true;
            });

            Preference btn_dell_cache = findPreference("db_del_cache");
            btn_dell_cache.setOnPreferenceClickListener(preference -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                builder.setMessage("Вы уверены что хотите очистить сохраненную информацию об фильмах?")
                        .setNegativeButton("Отмена", (dialog, id) -> dialog.dismiss())
                        .setPositiveButton("Да", (dialog, id) -> {
                            DBHelper dbHelper = new DBHelper(getActivity());
                            dbHelper.deleteAll("cacheWatch");
                        })
                        .create().show();
                return true;
            });

            Preference btn_dell_all = findPreference("db_del_all");
            btn_dell_all.setOnPreferenceClickListener(preference -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                builder.setMessage("Вы уверены что хотите очистить ВСЮ базу данных?")
                        .setNegativeButton("Отмена", (dialog, id) -> dialog.dismiss())
                        .setPositiveButton("Да", (dialog, id) -> {
                            DBHelper dbHelper = new DBHelper(getActivity());
                            dbHelper.deleteAll("favor");
                            dbHelper.deleteAll("history");
                            dbHelper.deleteAll("historyWatch");
                            dbHelper.deleteAll("cacheWatch");
                        })
                        .create().show();
                return true;
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AccountsPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_acc);
            setHasOptionsMenu(true);

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());


            Preference btn_in_filmix = findPreference("sing_in_filmix");
            if (!pref.getString("filmix_acc", "null").equals("null")) {
                btn_in_filmix.setTitle("Filmix (выйти)");
                btn_in_filmix.setSummary(pref.getString("filmix_acc", "null"));
                btn_in_filmix.setOnPreferenceClickListener(preference -> {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("filmix_acc", "null");
                    editor.putBoolean("filmix_pro", false);
                    editor.putString("filmix_coockie", "dle_user_id=deleted, dle_password=deleted, dle_hash=deleted, remember_me=deleted");
                    editor.apply();
                    btn_in_filmix.setTitle("Filmix");
                    btn_in_filmix.setSummary("");
                    return true;
                });
            } else {
                btn_in_filmix.setOnPreferenceClickListener(preference -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                    LayoutInflater inflater = getActivity().getLayoutInflater();

                    View dialog_layout = inflater.inflate(R.layout.dialog_login, null);
                    final EditText txtun = dialog_layout.findViewById(R.id.txtusername);
                    final EditText txtps = dialog_layout.findViewById(R.id.txtuserpass);
                    final TextView header = dialog_layout.findViewById(R.id.textView);
                    txtun.setText(pref.getString("filmix_acc", Statics.FILMIX_ACC));
                    header.setText("Filmix");
                    builder.setView(dialog_layout)
                            .setPositiveButton("Вход", (dialogInterface, i) -> {
                                String uname = txtun.getText().toString();
                                String pass = txtps.getText().toString();
                                ParserFilmixLogin p = new ParserFilmixLogin(uname, pass, location -> {
                                    switch (location) {
                                        case "null":
                                            Toast.makeText(getActivity(), "Ошибка соединения.", Toast.LENGTH_SHORT).show();
                                            break;
                                        case "error":
                                            Toast.makeText(getActivity(), "Неправильный логин/пароль", Toast.LENGTH_SHORT).show();
                                            break;
                                        default:
                                            SharedPreferences.Editor editor = pref.edit();
                                            editor.putString("filmix_acc", uname);
                                            if (location.contains("[!pro!]")) {
                                                location = location.replace("[!pro!]", "");
                                                editor.putBoolean("filmix_pro", true);
                                                Statics.FILMIX_PRO = true;
                                            }
                                            editor.putString("filmix_coockie", location.trim());
                                            editor.apply();
                                            if (pref.getBoolean("filmix_pro", false))
                                                btn_in_filmix.setSummary(pref.getString("filmix_acc", "null") + " [PRO]");
                                            btn_in_filmix.setSummary(pref.getString("filmix_acc", "null"));
                                            break;
                                    }
                                });
                                p.execute();
                            }).setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.dismiss())
                            .create().show();
                    return true;
                });
            }
//            Preference btn_out_filmix = findPreference("sing_out_filmix");
//            btn_out_filmix.setOnPreferenceClickListener(preference -> {
//                SharedPreferences.Editor editor = pref.edit();
//                editor.putString("filmix_acc", "null");
//                editor.putBoolean("filmix_pro", false);
//                editor.putString("filmix_coockie", location.trim());
//                editor.apply();
//                btn_in_filmix.setSummary(pref.getString("filmix_acc", "null"));
//                return true;
//            });
            Preference btn_in_kinozal = findPreference("sing_in_kinozal");
            if (!pref.getString("kinozal_acc", "null").equals("null"))
                btn_in_kinozal.setSummary(pref.getString("kinozal_acc", "null"));
            btn_in_kinozal.setOnPreferenceClickListener(preference -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                LayoutInflater inflater = getActivity().getLayoutInflater();

                View dialog_layout = inflater.inflate(R.layout.dialog_login,null);
                final EditText txtun = dialog_layout.findViewById(R.id.txtusername);
                final EditText txtps = dialog_layout.findViewById(R.id.txtuserpass);
                final TextView header = dialog_layout.findViewById(R.id.textView);
                txtun.setText(pref.getString("kinozal_acc", Statics.KINOZAL_ACC));
                header.setText("Kinozal");
                builder.setView(dialog_layout)
                        .setPositiveButton("Вход", (dialogInterface, i) -> {
                            String uname = txtun.getText().toString();
                            String pass = txtps.getText().toString();
                            KinozalLogin p = new KinozalLogin(uname, pass, location -> {
                                switch (location) {
                                    case "null":
                                        Toast.makeText(getActivity(), "Ошибка соединения.", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "error":
                                        Toast.makeText(getActivity(), "Неправильный логин/пароль", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("kinozal_acc", uname);
                                        Log.e("kinozal", "onCreate: "+location.trim());
                                        editor.putString("kinozal_coockie", location.trim());
                                        editor.apply();
                                        btn_in_kinozal.setSummary(pref.getString("kinozal_acc", "null"));
                                        break;
                                }
                            });
                            p.execute();
                        }).setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.dismiss())
                        .create().show();
                return true;
            });
            Preference btn_in_hurtom = findPreference("sing_in_hurtom");
            if (!pref.getString("hurtom_acc", "null").equals("null"))
                btn_in_hurtom.setSummary(pref.getString("hurtom_acc", "null"));
            btn_in_hurtom.setOnPreferenceClickListener(preference -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                LayoutInflater inflater = getActivity().getLayoutInflater();

                View dialog_layout = inflater.inflate(R.layout.dialog_login,null);
                final EditText txtun = dialog_layout.findViewById(R.id.txtusername);
                final EditText txtps = dialog_layout.findViewById(R.id.txtuserpass);
                final TextView header = dialog_layout.findViewById(R.id.textView);
                txtun.setText(pref.getString("hurtom_acc", Statics.HURTOM_ACC));
                header.setText("Hurtom");
                builder.setView(dialog_layout)
                        .setPositiveButton("Вход", (dialogInterface, i) -> {
                            String uname = txtun.getText().toString();
                            String pass = txtps.getText().toString();
                            HurtomLogin p = new HurtomLogin(uname, pass, location -> {
                                switch (location) {
                                    case "null":
                                        Toast.makeText(getActivity(), "Ошибка соединения.", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "error":
                                        Toast.makeText(getActivity(), "Неправильный логин/пароль", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("hurtom_acc", uname);
                                        editor.putString("hurtom_pass", pass);
                                        Log.e("hurtom", "onCreate: "+location.trim());
                                        editor.putString("hurtom_coockie", location.trim());
                                        editor.apply();
                                        btn_in_hurtom.setSummary(pref.getString("hurtom_acc", "null"));
                                        break;
                                }
                            });
                            p.execute();
                        }).setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.dismiss())
                        .create().show();
                return true;
            });
            Preference btn_in_kinopub = findPreference("sing_in_kinopub");
            if (!pref.getString("kinopub_acc", "null").equals("null"))
                btn_in_kinopub.setSummary(pref.getString("kinopub_acc", "null"));
            btn_in_kinopub.setOnPreferenceClickListener(preference -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                LayoutInflater inflater = getActivity().getLayoutInflater();

                View dialog_layout = inflater.inflate(R.layout.dialog_login,null);
                final EditText txtun = dialog_layout.findViewById(R.id.txtusername);
                final EditText txtps = dialog_layout.findViewById(R.id.txtuserpass);
                final TextView header = dialog_layout.findViewById(R.id.textView);
                txtun.setText(pref.getString("kinopub_acc", Statics.HURTOM_ACC));
                header.setText("Kinopub");
                builder.setView(dialog_layout)
                        .setPositiveButton("Вход", (dialogInterface, i) -> {
                            String uname = txtun.getText().toString();
                            String pass = txtps.getText().toString();
                            ParserKinopubLogin p = new ParserKinopubLogin(uname, pass, location -> {
                                switch (location) {
                                    case "null":
                                        Toast.makeText(getActivity(), "Ошибка соединения.", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "error":
                                        Toast.makeText(getActivity(), "Неправильный логин/пароль", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("kinopub_acc", uname);
                                        editor.putString("kinopub_pass", pass);
                                        Log.e("kinopub", "onCreate: "+location.trim());
                                        editor.putString("kinopub_coockie", location.trim());
                                        editor.apply();
                                        btn_in_kinopub.setSummary(pref.getString("kinopub_acc", "null"));
                                        break;
                                }
                            });
                            p.execute();
                        }).setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.dismiss())
                        .create().show();
                return true;
            });
            Preference btn_in_anidub_tr = findPreference("sing_in_anidub_tr");
            if (!pref.getString("anidub_tr_acc", "null").equals("null"))
                btn_in_anidub_tr.setSummary(pref.getString("anidub_tr_acc", "null"));
            btn_in_anidub_tr.setOnPreferenceClickListener(preference -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                LayoutInflater inflater = getActivity().getLayoutInflater();

                View dialog_layout = inflater.inflate(R.layout.dialog_login,null);
                final EditText txtun = dialog_layout.findViewById(R.id.txtusername);
                final EditText txtps = dialog_layout.findViewById(R.id.txtuserpass);
                final TextView header = dialog_layout.findViewById(R.id.textView);
                txtun.setText(pref.getString("anidub_tr_acc", Statics.ANIDUB_TR_ACC));
                header.setText("Anidub Tracker");
                builder.setView(dialog_layout)
                        .setPositiveButton("Вход", (dialogInterface, i) -> {
                            String uname = txtun.getText().toString();
                            String pass = txtps.getText().toString();
                            AnidubTrLogin p = new AnidubTrLogin(uname, pass, location -> {
                                switch (location) {
                                    case "null":
                                        Toast.makeText(getActivity(), "Ошибка соединения.", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "error":
                                        Toast.makeText(getActivity(), "Неправильный логин/пароль", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("anidub_tr_acc", uname);
                                        editor.putString("anidub_tr_pass", pass);
                                        Log.e("anidub", "onCreate: "+location.trim());
                                        editor.putString("anidub_tr_coockie", location.trim());
                                        editor.apply();
                                        btn_in_anidub_tr.setSummary(pref.getString("anidub_tr_acc", "null"));
                                        break;
                                }
                            });
                            p.execute();
                        }).setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.dismiss())
                        .create().show();
                return true;
            });
            Preference btn_in_greentea_tr = findPreference("sing_in_greentea_tr");
            if (!pref.getString("greentea_tr_acc", "null").equals("null"))
                btn_in_greentea_tr.setSummary(pref.getString("greentea_tr_acc", "null"));
            btn_in_greentea_tr.setOnPreferenceClickListener(preference -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                LayoutInflater inflater = getActivity().getLayoutInflater();

                View dialog_layout = inflater.inflate(R.layout.dialog_login,null);
                final EditText txtun = dialog_layout.findViewById(R.id.txtusername);
                final EditText txtps = dialog_layout.findViewById(R.id.txtuserpass);
                final TextView header = dialog_layout.findViewById(R.id.textView);
                txtun.setText(pref.getString("greentea_tr_acc", Statics.GREENTEA_TR_ACC));
                header.setText("GreenTea Tracker");
                builder.setView(dialog_layout)
                        .setPositiveButton("Вход", (dialogInterface, i) -> {
                            String uname = txtun.getText().toString();
                            String pass = txtps.getText().toString();
                            GreenTeaTrLogin p = new GreenTeaTrLogin(uname, pass, location -> {
                                switch (location) {
                                    case "null":
                                        Toast.makeText(getActivity(), "Ошибка соединения.", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "error":
                                        Toast.makeText(getActivity(), "Неправильный логин/пароль", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("greentea_tr_acc", uname);
                                        editor.putString("greentea_tr_pass", pass);
                                        Log.e("greentea", "onCreate: "+location.trim());
                                        editor.putString("greentea_tr_coockie", location.trim());
                                        editor.apply();
                                        btn_in_greentea_tr.setSummary(pref.getString("greentea_tr_acc", "null"));
                                        break;
                                }
                            });
                            p.execute();
                        }).setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.dismiss())
                        .create().show();
                return true;
            });
            Preference btn_in_rutracker = findPreference("sing_in_rutracker");
            if (!pref.getString("rutracker_acc", "null").equals("null"))
                btn_in_rutracker.setSummary(pref.getString("rutracker_acc", "null"));
            btn_in_rutracker.setOnPreferenceClickListener(preference -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                LayoutInflater inflater = getActivity().getLayoutInflater();

                View dialog_layout = inflater.inflate(R.layout.dialog_login,null);
                final EditText txtun = dialog_layout.findViewById(R.id.txtusername);
                final EditText txtps = dialog_layout.findViewById(R.id.txtuserpass);
                final TextView header = dialog_layout.findViewById(R.id.textView);
                txtun.setText(pref.getString("rutracker_acc", Statics.RUTRACKER_ACC));
                header.setText("Rutracker");
                builder.setView(dialog_layout)
                        .setPositiveButton("Вход", (dialogInterface, i) -> {
                            String uname = txtun.getText().toString();
                            String pass = txtps.getText().toString();
                            RutrackerLogin p = new RutrackerLogin(uname, pass, location -> {
                                switch (location) {
                                    case "null":
                                        Toast.makeText(getActivity(), "Ошибка соединения.", Toast.LENGTH_SHORT).show();
                                        break;
                                    case "error":
                                        Toast.makeText(getActivity(), "Неправильный логин/пароль", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        SharedPreferences.Editor editor = pref.edit();
                                        editor.putString("rutracker_acc", uname);
                                        editor.putString("rutracker_pass", pass);
                                        Log.e("rutracker", "onCreate: "+location.trim());
                                        editor.putString("rutracker_coockie", location.trim());
                                        editor.apply();
                                        btn_in_rutracker.setSummary(pref.getString("rutracker_acc", "null"));
                                        break;
                                }
                            });
                            p.execute();
                        }).setNegativeButton("Отмена", (dialogInterface, i) -> dialogInterface.dismiss())
                        .create().show();
                return true;
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DomensPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_domen);
            setHasOptionsMenu(true);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());


            setSub();
            setAutoCheck(pref.getBoolean("domen_auto_check", true));

            Preference btn_auto_check = findPreference("domen_check");
            btn_auto_check.setOnPreferenceClickListener(preference -> {
                Update update = new Update(getActivity(), "domen", location -> {
                    Utils.setDomen(location, getActivity());
                    setSub();
                    Toast.makeText(getActivity(), "Ссылки обновленны", Toast.LENGTH_SHORT).show();
                });
                update.execute();
                return true;
            });
            Preference btn_check = findPreference("domen_auto_check");
            btn_check.setOnPreferenceClickListener(preference -> {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1)
                    setAutoCheck(pref.getBoolean("domen_auto_check", true));
                else setAutoCheck(!pref.getBoolean("domen_auto_check", true));
                return true;
            });
            btn_check.setOnPreferenceChangeListener((preference, o) -> {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1)
                    setAutoCheck(pref.getBoolean("domen_auto_check", true));
                else setAutoCheck(!pref.getBoolean("domen_auto_check", true));
                return true;
            });
//            EditTextPreference rutorUrl = (EditTextPreference) findPreference("rutor_furl");
//            rutorUrl.setSummary(preference.getString("rutor_furl", Statics.RUTOR_URL));
        }

        private void setAutoCheck(boolean v){
            PreferenceCategory catalog = (PreferenceCategory) findPreference("domen_catalog");
            PreferenceCategory catalog_plus = (PreferenceCategory) findPreference("domen_catalog_plus");
            PreferenceCategory vid = (PreferenceCategory) findPreference("domen_vid");
            PreferenceCategory tor = (PreferenceCategory) findPreference("domen_tor");

            catalog.setEnabled(!v);
            catalog_plus.setEnabled(!v);
            vid.setEnabled(!v);
            tor.setEnabled(!v);
        }

        private void setSub(){
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

            EditTextPreference kosharaUrl = (EditTextPreference) findPreference("koshara_furl");
            kosharaUrl.setSummary(pref.getString("koshara_furl", Statics.KOSHARA_URL));
//            EditTextPreference amcetUrl = (EditTextPreference) findPreference("amcet_furl");
//            amcetUrl.setSummary(pref.getString("amcet_furl", Statics.AMCET_URL));
            EditTextPreference kinofsUrl = (EditTextPreference) findPreference("kinofs_furl");
            kinofsUrl.setSummary(pref.getString("kinofs_furl", Statics.KINOFS_URL));
            EditTextPreference kinoxaUrl = (EditTextPreference) findPreference("kinoxa_furl");
            kinoxaUrl.setSummary(pref.getString("kinoxa_furl", Statics.KINOXA_URL));
            EditTextPreference rufilmtvUrl = (EditTextPreference) findPreference("rufilmtv_furl");
            rufilmtvUrl.setSummary(pref.getString("rufilmtv_furl", Statics.RUFILMTV_URL));
            EditTextPreference topkinoUrl = (EditTextPreference) findPreference("topkino_furl");
            topkinoUrl.setSummary(pref.getString("topkino_furl", Statics.TOPKINO_URL));
            EditTextPreference myhitUrl = (EditTextPreference) findPreference("myhit_furl");
            myhitUrl.setSummary(pref.getString("myhit_furl", Statics.MYHIT_URL));
            EditTextPreference kinopubUrl = (EditTextPreference) findPreference("kinopub_furl");
            kinopubUrl.setSummary(pref.getString("kinopub_furl", Statics.KINOPUB_URL));


            EditTextPreference animevostUrl = (EditTextPreference) findPreference("animevost_furl");
            animevostUrl.setSummary(pref.getString("animevost_furl", Statics.ANIMEVOST_URL));
            EditTextPreference coldfilmUrl = (EditTextPreference) findPreference("coldfilm_furl");
            coldfilmUrl.setSummary(pref.getString("coldfilm_furl", Statics.COLDFILM_URL));
            EditTextPreference fanserialsUrl = (EditTextPreference) findPreference("fanserials_furl");
            fanserialsUrl.setSummary(pref.getString("fanserials_furl", Statics.FANSERIALS_URL));

            EditTextPreference anidubUrl = (EditTextPreference) findPreference("anidub_furl");
            anidubUrl.setSummary(pref.getString("anidub_furl", Statics.ANIDUB_URL));
            EditTextPreference animediaUrl = (EditTextPreference) findPreference("animedia_furl");
            animediaUrl.setSummary(pref.getString("animedia_furl", Statics.ANIMEDIA_URL));
            EditTextPreference kinoshaUrl = (EditTextPreference) findPreference("kinosha_furl");
            kinoshaUrl.setSummary(pref.getString("kinosha_furl", Statics.KINOSHA_URL));
            EditTextPreference filmixUrl = (EditTextPreference) findPreference("filmix_furl");
            filmixUrl.setSummary(pref.getString("filmix_furl", Statics.FILMIX_URL));
            EditTextPreference kinohdUrl = (EditTextPreference) findPreference("kinohd_furl");
            kinohdUrl.setSummary(pref.getString("kinohd_furl", Statics.KINOHD_URL));
            EditTextPreference kinoliveUrl = (EditTextPreference) findPreference("kinolive_furl");
            kinoliveUrl.setSummary(pref.getString("kinolive_furl", Statics.KINOLIVE_URL));
            EditTextPreference kinodom = (EditTextPreference) findPreference("kinodom_furl");
            kinodom.setSummary(pref.getString("kinodom_furl", Statics.KINODOM_URL));
            EditTextPreference zombiefilm = (EditTextPreference) findPreference("zombiefilm_furl");
            zombiefilm.setSummary(pref.getString("zombiefilm_furl", Statics.ZOMBIEFILM_URL));

            EditTextPreference anidubTrUrl = (EditTextPreference) findPreference("anidub_tr_furl");
            anidubTrUrl.setSummary(pref.getString("anidub_tr_furl", Statics.ANIDUB_TR_URL));
            EditTextPreference rutrackerUrl = (EditTextPreference) findPreference("rutracker_furl");
            rutrackerUrl.setSummary(pref.getString("rutracker_furl", Statics.RUTRACKER_URL));
            EditTextPreference tparserUrl = (EditTextPreference) findPreference("tparser_furl");
            tparserUrl.setSummary(pref.getString("tparser_furl", Statics.TPARSER_URL));
            EditTextPreference freerutorUrl = (EditTextPreference) findPreference("freerutor_furl");
            freerutorUrl.setSummary(pref.getString("freerutor_furl", Statics.FREERUTOR_URL));
            EditTextPreference rutorUrl = (EditTextPreference) findPreference("rutor_furl");
            rutorUrl.setSummary(pref.getString("rutor_furl", Statics.RUTOR_URL));
            EditTextPreference nnmUrl = (EditTextPreference) findPreference("nnm_furl");
            nnmUrl.setSummary(pref.getString("nnm_furl", Statics.NNM_URL));
            EditTextPreference bitruUrl = (EditTextPreference) findPreference("bitru_furl");
            bitruUrl.setSummary(pref.getString("bitru_furl", Statics.BITRU_URL));
            EditTextPreference greenteaTrUrl = (EditTextPreference) findPreference("greentea_tr_furl");
            greenteaTrUrl.setSummary(pref.getString("greentea_tr_furl", Statics.GREENTEA_TR_URL));
            EditTextPreference megapeerUrl = (EditTextPreference) findPreference("megapeer_furl");
            megapeerUrl.setSummary(pref.getString("megapeer_furl", Statics.MEGAPEER_URL));
            EditTextPreference piratbitUrl = (EditTextPreference) findPreference("piratbit_furl");
            piratbitUrl.setSummary(pref.getString("piratbit_furl", Statics.PIRATBIT_URL));
            EditTextPreference kinozal = (EditTextPreference) findPreference("kinozal_furl");
            kinozal.setSummary(pref.getString("kinozal_furl", Statics.KINOZAL_URL));
            EditTextPreference hurtom = (EditTextPreference) findPreference("hurtom_furl");
            hurtom.setSummary(pref.getString("hurtom_furl", Statics.HURTOM_URL));
            EditTextPreference torlook = (EditTextPreference) findPreference("torlook_furl");
            torlook.setSummary(pref.getString("torlook_furl", Statics.TORLOOK_URL));

            Preference btn_status = findPreference("domen_def");
            btn_status.setOnPreferenceClickListener(preference -> {
                Statics.defDomen();
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("koshara_furl", Statics.KOSHARA_URL);
                editor.putString("kinofs_furl", Statics.KINOFS_URL);
                editor.putString("kinoxa_furl", Statics.KINOXA_URL);
                editor.putString("rufilmtv_furl", Statics.RUFILMTV_URL);
                editor.putString("topkino_furl", Statics.TOPKINO_URL);
                editor.putString("myhit_furl", Statics.MYHIT_URL);

                editor.putString("animevost_furl", Statics.ANIMEVOST_URL);
                editor.putString("coldfilm_furl", Statics.COLDFILM_URL);
                editor.putString("fanserials_furl", Statics.FANSERIALS_URL);

                editor.putString("kinosha_furl", Statics.KINOSHA_URL);
                editor.putString("anidub_furl", Statics.ANIDUB_URL);
                editor.putString("animedia_furl", Statics.ANIMEDIA_URL);
                editor.putString("filmix_furl", Statics.FILMIX_URL);
                editor.putString("kinohd_furl", Statics.KINOHD_URL);
                editor.putString("kinolive_furl", Statics.KINOLIVE_URL);
                editor.putString("kinodom_furl", Statics.KINODOM_URL);
                editor.putString("zombiefilm_furl", Statics.ZOMBIEFILM_URL);

                editor.putString("freerutor_furl", Statics.FREERUTOR_URL);
                editor.putString("anidub_tr_furl", Statics.ANIDUB_TR_URL);
                editor.putString("rutracker_furl", Statics.RUTRACKER_URL);
                editor.putString("rutor_furl", Statics.RUTOR_URL);
                editor.putString("nnm_furl", Statics.NNM_URL);
                editor.putString("tparser_furl", Statics.TPARSER_URL);
                editor.putString("bitru_furl", Statics.BITRU_URL);
                editor.putString("greentea_tr_furl", Statics.GREENTEA_TR_URL);
                editor.putString("megapeer_furl", Statics.MEGAPEER_URL);
                editor.putString("piratbit_furl", Statics.PIRATBIT_URL);
                editor.putString("kinozal_furl", Statics.KINOZAL_URL);
                editor.putString("hurtom_furl", Statics.HURTOM_URL);
                editor.putString("torlook_furl", Statics.TORLOOK_URL);
                editor.apply();

                kosharaUrl.setSummary(pref.getString("koshara_furl",Statics.KOSHARA_URL));
//                amcetUrl.setSummary(pref.getString("amcet_furl",Statics.AMCET_URL));
                kinofsUrl.setSummary(pref.getString("kinofs_furl",Statics.KINOFS_URL));
                kinoxaUrl.setSummary(pref.getString("kinoxa_furl",Statics.KINOXA_URL));
                rufilmtvUrl.setSummary(pref.getString("rufilmtv_furl",Statics.RUFILMTV_URL));
                topkinoUrl.setSummary(pref.getString("topkino_furl",Statics.TOPKINO_URL));
                myhitUrl.setSummary(pref.getString("myhit_furl",Statics.MYHIT_URL));
                kinopubUrl.setSummary(pref.getString("kinopub_furl", Statics.KINOPUB_URL));

                animevostUrl.setSummary(pref.getString("animevost_furl",Statics.ANIMEVOST_URL));
                coldfilmUrl.setSummary(pref.getString("coldfilm_furl",Statics.COLDFILM_URL));
                fanserialsUrl.setSummary(pref.getString("fanserials_furl",Statics.FANSERIALS_URL));

                anidubUrl.setSummary(pref.getString("anidub_furl",Statics.ANIDUB_URL));
                animediaUrl.setSummary(pref.getString("animedia_furl",Statics.ANIMEDIA_URL));
                kinoshaUrl.setSummary(pref.getString("kinosha_furl",Statics.KINOSHA_URL));
                filmixUrl.setSummary(pref.getString("filmix_furl",Statics.FILMIX_URL));
                kinohdUrl.setSummary(pref.getString("kinohd_furl",Statics.KINOHD_URL));
                kinoliveUrl.setSummary(pref.getString("kinolive_furl",Statics.KINOLIVE_URL));
                kinodom.setSummary(pref.getString("kinodom_furl",Statics.KINODOM_URL));
                zombiefilm.setSummary(pref.getString("zombiefilm_furl",Statics.ZOMBIEFILM_URL));


                tparserUrl.setSummary(pref.getString("tparser_furl",Statics.TPARSER_URL));
                freerutorUrl.setSummary(pref.getString("freerutor_furl",Statics.FREERUTOR_URL));
                rutorUrl.setSummary(pref.getString("rutor_furl",Statics.RUTOR_URL));
                nnmUrl.setSummary(pref.getString("nnm_furl",Statics.NNM_URL));
                anidubTrUrl.setSummary(pref.getString("anidub_tr_furl",Statics.ANIDUB_TR_URL));
                rutrackerUrl.setSummary(pref.getString("rutracker_furl",Statics.RUTRACKER_URL));
                bitruUrl.setSummary(pref.getString("bitru_furl",Statics.BITRU_URL));
                greenteaTrUrl.setSummary(pref.getString("greentea_tr_furl",Statics.GREENTEA_TR_URL));
                megapeerUrl.setSummary(pref.getString("megapeer_furl",Statics.MEGAPEER_URL));
                piratbitUrl.setSummary(pref.getString("piratbit_furl",Statics.PIRATBIT_URL));
                kinozal.setSummary(pref.getString("kinozal_furl",Statics.KINOZAL_URL));
                hurtom.setSummary(pref.getString("hurtom_furl",Statics.HURTOM_URL));
                torlook.setSummary(pref.getString("torlook_furl",Statics.TORLOOK_URL));
                return true;
            });


//            EditTextPreference proxy = (EditTextPreference) findPreference("proxy_cur");
//            proxy.setSummary(preference.getString("proxy_cur", Statics.ProxyCur));
        }

        @Override
        public void onResume() {
            super.onResume();
            setSub();
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class ProPreferenceFragment extends PreferenceFragment {
        private static final int REQUEST_CODE_SIGN_IN = 0;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_disabled);
            setHasOptionsMenu(true);

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

            boolean pro = pref.getBoolean("pro_version", false);
            boolean side_left = pref.getBoolean("side_left", false);
            boolean st = pref.getBoolean("side_exist", false) && pro;
            boolean side_video = pref.getBoolean("side_video", false);

            if ((side_left && st) || (side_video && st) || (side_left && pro) || (side_video && pro) || (side_left && side_video)) {
                getActivity().finish();
            }

            String tr = "+";
            String fr = "-";
            try {
                tr = new String(Base64.decode("0KHRgtCw0YLRg9GBOiDQsNC60YLQuNCy0LjRgNC+0LLQsNC90LA=", Base64.DEFAULT),
                        "UTF-8");
                fr = new String(Base64.decode("0KHRgtCw0YLRg9GBOiDQvdC1INCw0LrRgtC40LLQuNGA0L7QstCw0L3QsA==", Base64.DEFAULT),
                        "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Preference btn_status = findPreference("pro_status");
            btn_status.setSummary(st ? tr : fr);
            String finalTr = tr;
            btn_status.setOnPreferenceClickListener(preference -> {
                if (!pref.getString("cur_google_account", "Вход не выполнен")
                        .equals("Вход не выполнен") || !pref.getString("filmix_acc","").isEmpty()) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("pro_acc", pref.getString("cur_google_account", "Вход не выполнен"));
                    editor.apply();

                    Update update = new Update(getActivity(), "acc", location -> {
                        if (location.contains("yesacc"))
                            btn_status.setSummary(finalTr);
                    });
                    update.execute();

                } else if (pref.getString("cur_google_account", "Вход не выполнен")
                        .equals("Вход не выполнен")) {
                    Toast.makeText(getActivity().getBaseContext(), "Войдите в аккаунт", Toast.LENGTH_SHORT).show();
                }
                return true;
            });

            Preference btn_info = findPreference("pro_more");
            btn_info.setOnPreferenceClickListener(preference -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                builder.setMessage("Отличие про версии\n\n" +
                        "\t* Отключение рекламы (не касается вшитой в видео)\n" +
                        "\t* Отдельный поиск видео/торрент файлов\n" +
                        "\t* Возможность синхронизировать базу с Google диском\n\n" +
                        "\t* Поиск по всем вкл. каталогам\n\n" +
                        "Список будет пополняться, вся инфомация по получению про версии находится на сайте или же свяжитесь с автором.")
                        .setPositiveButton("Ok", (dialog, id) -> dialog.dismiss())
                        .setNegativeButton("Сайт", (dialog, id) -> {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse("http://tiardev.ru/donate.php"));
                            startActivity(intent);
                            dialog.dismiss();
                        })
                        .create().show();
                return true;
            });

            Preference btn_sing_in = findPreference("sing_in_google");
            btn_sing_in.setSummary(pref.getString("cur_google_account", "Вход не выполнен"));
            btn_sing_in.setOnPreferenceClickListener(preference -> {
                Set<Scope> requiredScopes = new HashSet<>(2);
                requiredScopes.add(Drive.SCOPE_FILE);
                requiredScopes.add(Drive.SCOPE_APPFOLDER);
                GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getActivity());
                if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
                    initializeDriveClient();
                } else {
                    GoogleSignInOptions signInOptions =
                            new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                    .requestEmail()
                                    .build();
                    GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), signInOptions);
                    startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
                }
                return true;
            });

            Preference btn_sing_out = findPreference("sing_out");
            btn_sing_out.setOnPreferenceClickListener(preference -> {
                signOut();
                return true;
            });

            Preference btn_db_save = findPreference("save_db_s");
            btn_db_save.setSelectable(st);
            btn_db_save.setEnabled(st);

            Preference search_all = findPreference("search_all");
            search_all.setSelectable(st);
            search_all.setEnabled(st);

            Preference hide_ts = findPreference("hide_ts");
            hide_ts.setSelectable(st);
            hide_ts.setEnabled(st);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case REQUEST_CODE_SIGN_IN:
                    Log.e("test", "onCreate: " +requestCode +" "+resultCode);

                    if (resultCode != RESULT_OK) {
                        Toast.makeText(getActivity().getBaseContext(), "Ошибка входа в аккаунт", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Task<GoogleSignInAccount> getAccountTask =
                            GoogleSignIn.getSignedInAccountFromIntent(data);
                    if (getAccountTask.isSuccessful()) initializeDriveClient();
                    else {
                        Toast.makeText(getActivity().getBaseContext(), "Ошибка входа в аккаунт", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

        private void initializeDriveClient() {
            Log.e("test", "initializeDriveClient");
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(getActivity());
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            if (acct != null) {
                String personEmail = acct.getEmail();

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("cur_google_account", personEmail);
                editor.apply();

                Log.d("test", "initializeDriveClient: " + personEmail);
                Toast.makeText(getActivity().getBaseContext(), personEmail, Toast.LENGTH_SHORT).show();
            } else Log.d("test", "initializeDriveClient: acct = null");

            Preference btn_sing_in = findPreference("sing_in_google");
            btn_sing_in.setSummary(pref.getString("cur_google_account", "Вход не выполнен"));
        }

        private void signOut() {
            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), signInOptions);
            googleSignInClient.signOut()
                    .addOnCompleteListener(getActivity(), task -> {
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("cur_google_account", "Вход не выполнен");
                        editor.apply();

                        Preference btn_sing_in = findPreference("sing_in_google");
                        btn_sing_in.setSummary(pref.getString("cur_google_account", "Вход не выполнен"));
                    });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                getActivity().onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
