package com.kinotor.tiar.kinotor.ui;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.parser.catalog.filmix.ParserFilmixFav;
import com.kinotor.tiar.kinotor.ui.fragments.DetailTorrents;
import com.kinotor.tiar.kinotor.ui.fragments.MainCatalogFragment;
import com.kinotor.tiar.kinotor.utils.DBHelper;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BDActivity extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private String statusDB = "";
    private static final String TAG = "BaseActivity";
    private static final int REQUEST_CODE_SIGN_IN = 0;
    private static final int REQUEST_CODE_OPEN_ITEM = 1;
    private DriveClient mDriveClient;
    private DriveResourceClient mDriveResourceClient;
    private TaskCompletionSource<DriveId> mOpenItemTaskSource;
    private String subtitle = "Избранное";
    public static String show = "all";
    private SharedPreferences preference;
    private RecyclerView recyclerView;
    private final int cFavor = 0, cLater = 1, cHistory = 2, cTor = 3;
    private int colorStatus = R.color.colorPrimaryLight;
    private int colorBg = R.color.colorPrimaryLight;
    private int colorBgImg = R.drawable.gradient_darkgone_dark;
    private int colorText = R.color.colorWhite;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preference = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        show = "all";
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
        setContentView(R.layout.activity_base);
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


        CoordinatorLayout content = findViewById(R.id.content);
        content.setBackgroundColor(getResources().getColor(colorBg));
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        String st = "";
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getString("Status") != null)
                st = bundle.getString("Status");
        }

        mSectionsPagerAdapter.addFragment(new MainCatalogFragment().newInstance("http:// /", "Избранное", Statics.CATALOG), "Избранное");
        mSectionsPagerAdapter.addFragment(new MainCatalogFragment().newInstance("http:// /", "Посмотреть позже", Statics.CATALOG), "Посмотреть позже");
        mSectionsPagerAdapter.addFragment(new MainCatalogFragment().newInstance("http:// /", "История", Statics.CATALOG), "История");
        mSectionsPagerAdapter.addFragment(new DetailTorrents().newInstance(null, "favor"), "Торренты");
//        mViewPager.setOffscreenPageLimit(cHistory + 1);

        mViewPager.getAdapter().notifyDataSetChanged();
        if (st.equals("history")) {
            subtitle = "История";
            mViewPager.setCurrentItem(cHistory);
        } else if (st.equals("later")) {
            subtitle = "Посмотреть позже";
            mViewPager.setCurrentItem(cLater);
        }
        toolbar.setSubtitle(subtitle);
        invalidateOptionsMenu();
        onAttachedToWindow();

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case cHistory:
                        subtitle = "История";
                        break;
                    case cLater:
                        subtitle = "Посмотреть позже";
                        break;
                    case cTor:
                        subtitle = "Торренты";
                        break;
                    default:
                        subtitle = "Избранное";
                        break;

                }

                if (recyclerView != null) {
                    setupRecyclerView();
                }
                toolbar.setSubtitle(subtitle);
                invalidateOptionsMenu();
                onAttachedToWindow();
                setupRecyclerView();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        recyclerView = findViewById(R.id.item_list);
        if (recyclerView != null) {
            setupRecyclerView();
            if (!preference.getBoolean("side_menu", true)) {

            } else tabLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        refresh();
    }
    private void refresh(){
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        switch (subtitle) {
            case "История":
                intent.putExtra("Status", "history");
                break;
            case "Посмотреть позже":
                intent.putExtra("Status", "later");
                break;
            case "Торренты":
                intent.putExtra("Status", "torrent");
                break;
            default:
                intent.putExtra("Status", "favor");
                break;
        }
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    private void reloadFr() {
        invalidateOptionsMenu();
        onAttachedToWindow();
        Fragment f;
        switch (subtitle){
            case "История" :
                f = mSectionsPagerAdapter.getItem(cHistory);
                break;
            case "Посмотреть позже" :
                f = mSectionsPagerAdapter.getItem(cLater);
                break;
            case "Торренты" :
                f = mSectionsPagerAdapter.getItem(cTor);
                break;
            default:
                f = mSectionsPagerAdapter.getItem(cFavor);
                break;

        }

        if (f!=null) {
            getSupportFragmentManager().beginTransaction()
                    .detach(f)
                    .attach(f)
                    .commit();
        }
    }
    private void reloadBaseFr() {
        invalidateOptionsMenu();
        onAttachedToWindow();
        Fragment f = mSectionsPagerAdapter.getItem(cFavor);
        Fragment t = mSectionsPagerAdapter.getItem(cTor);
        Fragment h = mSectionsPagerAdapter.getItem(cHistory);
        Fragment l = mSectionsPagerAdapter.getItem(cLater);

        if (f!=null && h!=null && l!=null) {
            getSupportFragmentManager().beginTransaction()
                    .detach(f)
                    .detach(h)
                    .detach(l)
                    .detach(t)
                    .attach(f)
                    .attach(h)
                    .attach(l)
                    .attach(t)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);

        MenuItem dbRestFilmix = menu.findItem(R.id.action_db_filmix);


        MenuItem showAll = menu.findItem(R.id.action_show_all);
        MenuItem showFilm = menu.findItem(R.id.action_show_film);
        MenuItem showSerial = menu.findItem(R.id.action_show_serial);

        showAll.setChecked(show.contains("all"));
        showFilm.setChecked(show.contains("film"));
        showSerial.setChecked(show.contains("serial"));

        dbRestFilmix.setVisible(!Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted") && !subtitle.equals("Filmix") && !subtitle.equals("История"));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onShowAll(MenuItem item) {
        show = "all";
        reloadBaseFr();
    }
    public void onShowFilm(MenuItem item) {
        show = "film";
        reloadBaseFr();
    }
    public void onShowSerial(MenuItem item) {
        show = "serial";
        reloadBaseFr();
    }
    public void onSetting(MenuItem item) {
        Intent i = new Intent(this, SettingsActivity.class);
        this.startActivity(i);
    }
    public void onDBsave(MenuItem item) {
        boolean pro = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("pro_version", false);
        boolean seeit = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("side_left", false);
        boolean setit = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("side_exist", false);
        boolean setet = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("side_video", false);
        boolean p = pro && setit;
        if (preference.getString("save_db_s", "default").equals("default") || !p) {
            DBHelper dbHelper = new DBHelper(getBaseContext());
            File f = Environment.getExternalStorageDirectory();
            if (f.exists()) {
                dbHelper.copyDataBaseToSd(this);
                Toast.makeText(getBaseContext(), "БД успешно сохранена на устрйство", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), "Ошибка записи", Toast.LENGTH_SHORT).show();
            }
            reloadFr();
        } else {
            statusDB = "save";
            signIn();
        }
    }

    public void onDBsend(MenuItem item) {
        DBHelper dbHelper = new DBHelper(getBaseContext());
        dbHelper.copyDataBaseSend(this);
//        refresh();
    }

    public void onDBdel(MenuItem item) {
        DBHelper dbHelper = new DBHelper(getBaseContext());
        switch (subtitle) {
            case "Избранное":dbHelper.deleteAll("favor");
                Toast.makeText(getBaseContext(), "Избранное очищенно", Toast.LENGTH_SHORT).show();
                break;
            case "История":dbHelper.deleteAll("history");
                Toast.makeText(getBaseContext(), "История очищенна", Toast.LENGTH_SHORT).show();
                break;
            case "Посмотреть позже":
                dbHelper.deleteAll("later");
                Toast.makeText(getBaseContext(), "Посмотреть позже очищенно", Toast.LENGTH_SHORT).show();
                break;
            case "Торренты":
                dbHelper.deleteAll("favorTorrent");
                Toast.makeText(getBaseContext(), "Торренты очищенно", Toast.LENGTH_SHORT).show();
                break;
        }
        reloadFr();

    }

    public void onDBrestFilmix(MenuItem item) {
        RelativeLayout pb = findViewById(R.id.progresB);
        pb.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Подождите...", Toast.LENGTH_SHORT).show();
        ParserFilmixFav parserFilmixFav = new ParserFilmixFav(Statics.FILMIX_URL + "/favorites",
                (items, itempath) -> {
                    for (int i = items.size() - 1; i != -1; i--){

                        DBHelper dbHelper = new DBHelper(this);
                        dbHelper.Write();
                        if (dbHelper.getRepeat("favor", itempath.getTitle(i)))
                            dbHelper.delete("favor", itempath.getTitle(i));
                        dbHelper.insert("favor", itempath.getTitle(i), itempath.getImg(i), itempath.getUrl(i), itempath.getVoice(i), itempath.getQuality(i),
                                itempath.getSeason(i), itempath.getSeries(i));

                    }
                    Toast.makeText(this, "Закладки импортированы ("+items.size()+")", Toast.LENGTH_SHORT).show();
                    pb.setVisibility(View.GONE);
                    reloadFr();
                });
        parserFilmixFav.execute();
    }
    public void onDBrest(MenuItem item) {
        boolean pro = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("pro_version", false);
        boolean seeit = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("side_left", false);
        boolean setit = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("side_video", false);
        boolean setet = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("side_exist", false);
        boolean p = pro && setet;
        
        if (preference.getString("save_db_s", "default").equals("default") || !p) {
            Log.d("BDActivity", "rest from sd");
            DBHelper dbHelper = new DBHelper(getBaseContext());
            dbHelper.copyDataBaseToData(this);
            refresh();
        } else {
            statusDB = "rest";
            signIn();
        }
    }


    //______________________________________________________________________________________________

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("test", "onActivityResult: "+requestCode);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode != RESULT_OK) {
                    Log.e(TAG, "Sign-in failed.");
                    showMessage("Ошибка входа в аккаунт");
                    return;
                }

                Task<GoogleSignInAccount> getAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) initializeDriveClient(getAccountTask.getResult());
                else {
                    Log.e(TAG, "Sign-in failed.");
                    showMessage("Ошибка входа в аккаунт");
                }
                break;
            case REQUEST_CODE_OPEN_ITEM:
                if (resultCode == RESULT_OK) {
                    DriveId driveId = data.getParcelableExtra(
                            OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    retrieveContents(driveId.asDriveFile());
                    mOpenItemTaskSource.setResult(driveId);
                } else {
                    mOpenItemTaskSource.setException(new RuntimeException("Unable to open file"));
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
        Log.d(TAG, "initializeDriveClient: " + signInAccount.getEmail());
        if (statusDB.equals("save"))
            createDbInDrive();
        else if (statusDB.equals("rest"))
            pickFile();
    }
    protected void signIn() {
        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount);
        } else {
            GoogleSignInOptions signInOptions =
                    new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestScopes(Drive.SCOPE_FILE)
                            .requestScopes(Drive.SCOPE_APPFOLDER)
                            .requestEmail()
                            .build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, signInOptions);
            startActivityForResult(googleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }
    }
    protected Task<DriveId> pickFile() {
        OpenFileActivityOptions openOptions =
                new OpenFileActivityOptions.Builder()
                        .setSelectionFilter(Filters.eq(SearchableField.MIME_TYPE, "application/x-sqlite3"))
                        .setActivityTitle("Выберите KinotorDB")
                        .build();
        return pickItem(openOptions);
    }
    private Task<DriveId> pickItem(OpenFileActivityOptions openOptions) {
        mOpenItemTaskSource = new TaskCompletionSource<>();
        mDriveClient
                .newOpenFileActivityIntentSender(openOptions)
                .continueWith((Continuation<IntentSender, Void>) task -> {
                    startIntentSenderForResult(
                            task.getResult(), REQUEST_CODE_OPEN_ITEM, null, 0, 0, 0);
                    return null;
                });
        return mOpenItemTaskSource.getTask();
    }
    private void retrieveContents(final DriveFile file) {
        final Task<DriveContents> openFileTask = mDriveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY);
        openFileTask.continueWithTask(task -> {
            DriveContents contents = task.getResult();

            Log.v(TAG, "File name : " + contents.toString());
            InputStream input = contents.getInputStream();
            String out_path = this.getDatabasePath("DB").getPath();
            try {
                OutputStream output = new FileOutputStream(out_path);
                Log.d(TAG, "retrieveContents: "+out_path);
                byte[] buffer = new byte[1024]; // or other buffer size
                int read;

                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                output.flush();
                output.close();
                input.close();
                Log.d(TAG, "db success restore");
            } catch (Exception e) {
                showMessage("Ошибка загрузки базы");
                e.printStackTrace();
            }
            showMessage("База восстановленна");
            reloadFr();
            return mDriveResourceClient.discardContents(contents);
        }).addOnFailureListener(e -> {
            Log.e(TAG, "db error restore");
            showMessage("Ошибка загрузки базы");
        });
    }

    private void createDbInDrive() {
        final Task<DriveFolder> appFolderTask = mDriveResourceClient.getRootFolder();
        final Task<DriveContents> createContentsTask = mDriveResourceClient.createContents();
        Tasks.whenAll(appFolderTask, createContentsTask)
                .continueWithTask(task -> {
                    DriveFolder parent = appFolderTask.getResult();
                    DriveContents contents = createContentsTask.getResult();
                    OutputStream outputStream = contents.getOutputStream();

                    String db_name = "DB";
                    String db_path = this.getDatabasePath(db_name).getPath();
                    Log.d(TAG, "kk-"+db_path);

                    deleteFile();

                    File dbFile = new File(db_path);
                    final FileInputStream fileInputStream;
                    try {
                        fileInputStream = new FileInputStream(dbFile);
                        byte[] buffer = new byte[4096];
                        int c;

                        while ((c = fileInputStream.read(buffer, 0, buffer.length)) > 0){
                            outputStream.write(buffer, 0, c);
                        }
                        outputStream.flush();
                        outputStream.close();
                        fileInputStream.close();
                    } catch (FileNotFoundException e) {
                        showMessage("Ошибка чтения базы данных");
                        Log.e(TAG, "Could not get input stream from local file\n");
                    }

                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("KinotorDB.sqlite")
                            .setMimeType("application/x-sqlite3")
                            .setStarred(true)
                            .build();

                    return mDriveResourceClient.createFile(parent, changeSet, contents);
                })
                .addOnSuccessListener(this,
                        driveFile -> {
                            Log.d(TAG, "create file");
                            showMessage("Файл успешно загружен");
                        })
                .addOnFailureListener(this, e -> {
                    Log.e(TAG, "Unable to create file", e);
                    showMessage("Не удалось загрузить файл");
                });
    }
    private void deleteFile() {
        final String sFilename = "KinotorDB.sqlite";

        Query query = new Query.Builder()
                .addFilter( Filters.eq( SearchableField.TITLE, sFilename ) )
                .build();

        Task<MetadataBuffer> queryTask = mDriveResourceClient.query(query);

        queryTask.addOnSuccessListener( this,
                metadataBuffer -> {
                    for( Metadata m : metadataBuffer )
                    {
                        DriveResource driveResource = m.getDriveId().asDriveResource();
                        Log.i( TAG, "Deleting file: " + sFilename + "  DriveId:(" + m.getDriveId() + ")" );
                        mDriveResourceClient.delete( driveResource );
                    }

                })
                .addOnFailureListener( this, e -> Log.i( TAG, "ERROR: File not found: " + sFilename ));
    }
    protected void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updPref();
    }
    private void updPref(){
        String[] baseTorSearch = {""};
        HashSet<String> defTorSearch = new HashSet<>(Arrays.asList(baseTorSearch));
        String pref_baseTorSearch = preference.getStringSet("torrent_search_list", defTorSearch).toString();
        //catalog
        Statics.KOSHARA_URL = preference.getString("koshara_furl", Statics.KOSHARA_URL);
        Statics.AMCET_URL = preference.getString("amcet_furl", Statics.AMCET_URL);
        Statics.KINOFS_URL = preference.getString("kinofs_furl", Statics.KINOFS_URL);
        Statics.KINOXA_URL = preference.getString("kinoxa_furl", Statics.KINOXA_URL);
        Statics.RUFILMTV_URL = preference.getString("rufilmtv_furl", Statics.RUFILMTV_URL);
        Statics.TOPKINO_URL = preference.getString("topkino_furl", Statics.TOPKINO_URL);
        Statics.MYHIT_URL = preference.getString("myhit_furl", Statics.MYHIT_URL);
        Statics.KINOPUB_URL = preference.getString("kinopub_furl", Statics.KINOPUB_URL);
        //+catalog
        Statics.ANIMEVOST_URL = preference.getString("animevost_furl", Statics.ANIMEVOST_URL);
        Statics.COLDFILM_URL = preference.getString("coldfilm_furl", Statics.COLDFILM_URL);
        Statics.FANSERIALS_URL = preference.getString("fanserials_furl", Statics.FANSERIALS_URL);
        //video
        Statics.KINOSHA_URL = preference.getString("kinosha_furl", Statics.KINOSHA_URL);
        Statics.ANIDUB_URL = preference.getString("anidub_furl", Statics.ANIDUB_URL);
        Statics.MOONWALK_URL = preference.getString("moonwalk_furl", Statics.MOONWALK_URL);
        Statics.FILMIX_URL = preference.getString("filmix_furl", Statics.FILMIX_URL);
        Statics.MOVIESHD_URL = preference.getString("movieshd_furl", Statics.MOVIESHD_URL);
        Statics.KINOHD_URL = preference.getString("kinohd_furl", Statics.KINOHD_URL);
        Statics.KINOLIVE_URL = preference.getString("kinolive_furl", Statics.KINOLIVE_URL);
        Statics.KINODOM_URL = preference.getString("kinodom_furl", Statics.KINODOM_URL);
        Statics.ZOMBIEFILM_URL = preference.getString("zombiefilm_furl", Statics.ZOMBIEFILM_URL);
        //torrent
        Statics.ANIDUB_TR_URL = preference.getString("anidub_tr_furl", Statics.ANIDUB_TR_URL);
        Statics.ANIDUB_TR_COOCKIE = preference.getString("anidub_tr_coockie", Statics.ANIDUB_TR_COOCKIE);
        Statics.ANIDUB_TR_PASS = preference.getString("anidub_tr_pass", Statics.ANIDUB_TR_PASS);
        Statics.ANIDUB_TR_ACC = preference.getString("anidub_tr_acc", Statics.ANIDUB_TR_ACC);
        Statics.TPARSER_URL = preference.getString("tparser_furl", Statics.TPARSER_URL);
        Statics.RUTOR_URL = preference.getString("rutor_furl", Statics.RUTOR_URL);
        Statics.NNM_URL = preference.getString("nnm_furl", Statics.NNM_URL);
        Statics.FREERUTOR_URL = preference.getString("freerutor_furl", Statics.FREERUTOR_URL);
        Statics.BITRU_URL = preference.getString("bitru_furl", Statics.BITRU_URL);
        Statics.BA3A_URL = preference.getString("ba3a_furl", Statics.BA3A_URL);
        Statics.TPARSER_URL = preference.getString("tparser_furl", Statics.TPARSER_URL);
        Statics.MEGAPEER_URL = preference.getString("megapeer_furl", Statics.MEGAPEER_URL);
        Statics.KINOZAL_URL = preference.getString("kinozal_furl", Statics.KINOZAL_URL);
        Statics.HURTOM_URL = preference.getString("hurtom_furl", Statics.HURTOM_URL);
        Statics.TORLOOK_URL = preference.getString("torlook_furl", Statics.TORLOOK_URL);
        Statics.PIRATBIT_URL = preference.getString("piratbit_furl", Statics.PIRATBIT_URL);
        Statics.GREENTEA_TR_URL = preference.getString("greentea_tr_furl", Statics.GREENTEA_TR_URL);
        Statics.GREENTEA_TR_COOCKIE = preference.getString("greentea_tr_coockie", Statics.GREENTEA_TR_COOCKIE);
        Statics.GREENTEA_TR_ACC = preference.getString("greentea_tr_acc", Statics.GREENTEA_TR_ACC);
        Statics.RUTRACKER_URL = preference.getString("rutracker_furl", Statics.RUTRACKER_URL);
        Statics.RUTRACKER_COOCKIE = preference.getString("rutracker_coockie", Statics.RUTRACKER_COOCKIE);
        Statics.RUTRACKER_ACC = preference.getString("rutracker_acc", Statics.RUTRACKER_ACC);
        //other
        Statics.hideTs = preference.getBoolean("hide_ts", false);
        Statics.rateImdb = preference.getBoolean("rate_imdb", true);
        Statics.torS = pref_baseTorSearch;
        Statics.FILMIX_COOCKIE = preference.getString("filmix_coockie", Statics.FILMIX_COOCKIE);
        Statics.FILMIX_ACC = preference.getString("filmix_acc", Statics.FILMIX_ACC);
        Statics.FILMIX_PRO = preference.getBoolean("filmix_pro", false);
//        Statics.KINODOM_COOCKIE = preference.getString("kinodom_coockie", Statics.KINODOM_COOCKIE);
        Statics.KINODOM_ACC = preference.getString("kinodom_acc", Statics.KINODOM_ACC);
        Statics.KINOZAL_COOCKIE = preference.getString("kinozal_coockie", Statics.KINOZAL_COOCKIE);
        Statics.KINOZAL_ACC = preference.getString("kinozal_acc", Statics.KINOZAL_ACC);
        Statics.HURTOM_COOCKIE = preference.getString("hurtom_coockie", Statics.HURTOM_COOCKIE);
        Statics.HURTOM_PASS = preference.getString("hurtom_pass", Statics.HURTOM_PASS);
        Statics.HURTOM_ACC = preference.getString("hurtom_acc", Statics.HURTOM_ACC);
        Statics.KINOPUB_COOCKIE = preference.getString("kinopub_coockie", Statics.KINOPUB_COOCKIE);
    }

    //______________________________________________________________________________________________


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
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

    private void setupRecyclerView() {
        //take our menu items
        // large-screen layouts (res/values-w900dp)
        if (recyclerView != null) {
            if (preference.getBoolean("side_menu", true) && this.getResources().getConfiguration().orientation == 2) {
                ItemMain menu_item = new ItemMain();
                menu_item.delItem();
                menu_item.addItem(new ItemMain.Item(cFavor, "Избранное", ""));
                menu_item.addItem(new ItemMain.Item(cLater, "Посмотреть позже", ""));
                menu_item.addItem(new ItemMain.Item(cHistory, "История", ""));

                recyclerView.setAdapter(new BDActivity.ItemRVAdapter(ItemMain.ITEMS));
            } else {
                recyclerView.setVisibility(View.GONE);
            }
        }
    }

    //Our menu/draweble
    public class ItemRVAdapter
            extends RecyclerView.Adapter<BDActivity.ItemRVAdapter.ViewHolder> {
        private List<ItemMain.Item> mValues;
        ItemRVAdapter(List<ItemMain.Item> items) {
            mValues = items;
        }

        @NonNull
        @Override
        public BDActivity.ItemRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new BDActivity.ItemRVAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final BDActivity.ItemRVAdapter.ViewHolder holder, int position) {
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

            if (mValues.get(position).name.equals("...")) {
                holder.mView.setSelected(false);
                holder.mView.setEnabled(false);
                holder.mView.setFocusable(false);
                holder.separ.setVisibility(View.VISIBLE);
                holder.mName.setVisibility(View.GONE);
            }

            holder.mView.setOnFocusChangeListener((view, b) -> {
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
            });

            holder.mView.setOnClickListener(v -> {
//                if (holder.mItem.getName().equals("Выход")) {
//                    finish();
//                }
                mViewPager.setCurrentItem(position);
            });
        }

        void Check(boolean check, final BDActivity.ItemRVAdapter.ViewHolder holder){
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
