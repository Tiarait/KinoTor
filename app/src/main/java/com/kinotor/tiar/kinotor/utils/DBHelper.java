package com.kinotor.tiar.kinotor.utils;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemNewTor;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 07.10.2017.
 */

public class DBHelper extends SQLiteOpenHelper {
    private SQLiteDatabase sqLiteDatabase;
    private Context context;
    private final static String DATABASE_NAME = "DB";
    private static final int DATABASE_VERSION = 6;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("mydebug", "--- onCreate database ---");
        db.execSQL("create table favor (" +
                "id integer primary key autoincrement," +
                "title text," +
                "link text," +
                "img text," +
                "voice text," +
                "quality text," +
                "season integer," +
                "series integer" +
                ");");
        db.execSQL("create table history (" +
                "id integer primary key autoincrement," +
                "title text," +
                "link text," +
                "img text," +
                "voice text," +
                "quality text," +
                "season integer," +
                "series integer" +
                ");");
        db.execSQL("create table later (" +
                "id integer primary key autoincrement," +
                "title text," +
                "link text," +
                "img text," +
                "voice text," +
                "quality text," +
                "season integer," +
                "series integer" +
                ");");
        db.execSQL("create table historyWatch (" +
                "id integer primary key autoincrement," +
                "title text," +
                "translator text," +
                "season text," +
                "series text" +
                ");");
        db.execSQL("create table cacheWatch (" +
                "id integer primary key autoincrement," +
                "url text," +
                "json text" +
                ");");
        db.execSQL("create table cacheVideo (" +
                "id integer primary key autoincrement," +
                "url text," +
                "json text" +
                ");");
        db.execSQL("create table historyTorrent (" +
                "id integer primary key autoincrement," +
                "title text," +
                "torrent text," +
                "magnet text" +
                ");");
        db.execSQL("create table favorTorrent (" +
                "id integer primary key autoincrement," +
                "title text," +
                "json text" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("create table favorTorrent (" +
                "id integer primary key autoincrement," +
                "title text," +
                "json text" +
                ");");
    }

    private void Read() throws android.database.SQLException {
//        File dbfile = new File(Environment.getExternalStorageDirectory() + File.separator +
//                context.getString(R.string.app_name) + File.separator + "KinotorDB" );
//        if (dbfile.exists()) {
//            sqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbfile, null);
//        } else {
            sqLiteDatabase = this.getReadableDatabase();
//        }
        check(sqLiteDatabase);
    }
    private void check(SQLiteDatabase db) {
        db.execSQL("create table if not exists favor (" +
                "id integer primary key autoincrement," +
                "title text," +
                "link text," +
                "img text," +
                "voice text," +
                "quality text," +
                "season integer," +
                "series integer" +
                ");");
        db.execSQL("create table if not exists history (" +
                "id integer primary key autoincrement," +
                "title text," +
                "link text," +
                "img text," +
                "voice text," +
                "quality text," +
                "season integer," +
                "series integer" +
                ");");
        db.execSQL("create table if not exists later (" +
                "id integer primary key autoincrement," +
                "title text," +
                "link text," +
                "img text," +
                "voice text," +
                "quality text," +
                "season integer," +
                "series integer" +
                ");");
        db.execSQL("create table if not exists historyWatch (" +
                "id integer primary key autoincrement," +
                "title text," +
                "translator text," +
                "season text," +
                "series text" +
                ");");
        db.execSQL("create table if not exists cacheWatch (" +
                "id integer primary key autoincrement," +
                "url text," +
                "json text" +
                ");");
        db.execSQL("create table if not exists cacheVideo (" +
                "id integer primary key autoincrement," +
                "url text," +
                "json text" +
                ");");
        db.execSQL("create table if not exists historyTorrent (" +
                "id integer primary key autoincrement," +
                "title text," +
                "torrent text," +
                "magnet text" +
                ");");
        db.execSQL("create table if not exists favorTorrent (" +
                "id integer primary key autoincrement," +
                "title text," +
                "json text" +
                ");");
    }

    public void Write() throws android.database.SQLException {
        sqLiteDatabase = this.getWritableDatabase();
    }

    public long insertHistoryTor(String title, String torrent, String magnet){
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("torrent", torrent);
        cv.put("magnet", magnet);
        Log.d(TAG, "--- " + title + " Insert to historyTorrent ---");
        return sqLiteDatabase.insert("historyTorrent", null, cv);
    }

    public void deleteWatch(String title, String translator, String season, String series){
        Write();
        sqLiteDatabase.delete("historyWatch", "title='"+title+"' and " +
                "translator='"+translator+"' and season='"+season+"' and series='"+series+"'", null);
    }
    public void deleteWatch(String title, String translator, String season){
        Write();
        sqLiteDatabase.delete("historyWatch", "title='"+title+"' and " +
                "translator='"+translator+"' and season='"+season+"'", null);
    }
    public void deleteWatch(String title, String translator){
        Write();
        sqLiteDatabase.delete("historyWatch", "title='"+title+"' and " +
                "translator='"+translator+"'", null);
    }

    public long insertWatch(String title, String translator, String season, String series){
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("translator", translator);
        cv.put("season", season);
        cv.put("series", series);
        Log.d(TAG, "--- " + title + " Insert to historyWatch ---");
        return sqLiteDatabase.insert("historyWatch", null, cv);
    }

    public long insertCacheWatch(ItemHtml item){
        Gson gson = new Gson();
        ContentValues cv = new ContentValues();
        cv.put("url", Utils.urlEncode(item.getUrl(0)));
        cv.put("json", Utils.urlEncode(gson.toJson(item)));
        Log.d(TAG, "--- " + item.getTitle(0) + " Insert to cacheWatch ---");
        return sqLiteDatabase.insert("cacheWatch", null, cv);
    }
    public long insertCacheVideo(String url, ItemVideo item){
        Gson gson = new Gson();
        ContentValues cv = new ContentValues();
        cv.put("url", url);
        cv.put("json", gson.toJson(item));
        return sqLiteDatabase.insert("cacheVideo", null, cv);
    }
    public long insertFavorTorrent(String title, ItemNewTor item){
        Gson gson = new Gson();
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("json", gson.toJson(item));
        return sqLiteDatabase.insert("favorTorrent", null, cv);
    }

    public long insert(String db, String title, String img_src, String link, String voice,
                       String quality, int season, int series){
        ContentValues cv = new ContentValues();
        cv.put("title", title.replace("'", ""));
        cv.put("link", Utils.urlEncode(link));
        cv.put("img", img_src);
        cv.put("voice", voice);
        cv.put("quality", quality);
        cv.put("season", season);
        cv.put("series", series);
        Log.d(TAG, "--- " + title + " Insert to "+ db +" ---");
        return sqLiteDatabase.insert(db, null, cv);
    }

    public void delete(String db, String title){
        Write();
        sqLiteDatabase.delete(db, "title" + "='" + title.replace("'", "") + "'", null);
        Log.d("mydebug", "--- " + title + " Delete on "+ db +" ---");
    }

    public void deleteCache(String url){
        Write();
        url = Utils.urlEncode(url);
        sqLiteDatabase.delete("cacheWatch", "url='" + url.replace("'","")  + "'", null);
    }
    public void deleteCacheVid(String url){
        Write();
        sqLiteDatabase.delete("cacheVideo", "url='" + url.replace("'","")  + "'", null);
    }

    public void deleteAll(String db){
        Write();
        sqLiteDatabase.delete(db, null, null);
        Log.d("mydebug", "--- Delete "+ db +" ---");
    }

    public void copyDataBaseToSd(Activity activity){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
        } else {
            dataToSd();
        }
    }

    private void dataToSd(){
        String db_name = "DB";
        String out_path = Environment.getExternalStorageDirectory() + File.separator +
                context.getString(R.string.app_name) + File.separator;
        String db_path = context.getDatabasePath(db_name).getPath();
        db_path = db_path.contains(".sqlite") ? db_path.split("\\.sqlite")[0] : db_path;
        try {
            File fileExt = new File(out_path);
            try{
                if(fileExt.mkdirs()) {
                    Log.d(TAG, "Directory created: " + out_path);
                } else {
                    Log.d(TAG, "Directory is not created: " + out_path);
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            InputStream mInputStream = new FileInputStream(db_path);
            OutputStream mOutputStream = new FileOutputStream(out_path + "KinotorDB");
            byte[] buffer = new byte[1024];
            int length;
            while ((length = mInputStream.read(buffer)) != -1) {
                mOutputStream.write(buffer, 0, length);
            }
            mInputStream.close();
            mOutputStream.flush();
            mOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void copyDataBaseSend(Activity activity){
        File f = Environment.getExternalStorageDirectory();
        if (f.exists()) {
            copyDataBaseToSd(activity);
            String db_name = "KinotorDB";
            String out_path = Environment.getExternalStorageDirectory() + "/" +
                    context.getString(R.string.app_name) + "/" + db_name;
            out_path = out_path.startsWith("file://") ? out_path : "file://" + out_path;

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("*/*");
            Uri uri = Uri.parse(out_path);
            intent.putExtra(Intent.EXTRA_STREAM, uri);

            activity.startActivity(Intent.createChooser(intent, "Передать:"));
        } else{
            Log.e(TAG, "copyDataBaseSend: "+f.getPath() );
            Toast.makeText(activity.getApplicationContext(), "Ошибка записи", Toast.LENGTH_SHORT).show();
        }
    }
    public void copyDataBaseToData(Context c){
//        File dbfile = new File(Environment.getExternalStorageDirectory() + File.separator +
//                context.getString(R.string.app_name) + File.separator + "KinotorDB" );
//        if (dbfile.exists()) {
//            Write();
//            SQLiteDatabase newDatabase = SQLiteDatabase.openDatabase(Environment.getExternalStorageDirectory() + File.separator +
//                    context.getString(R.string.app_name) + File.separator + "KinotorDB", null, 0);
//            check(newDatabase);
//            //favorTorrent
//            deleteAll("favorTorrent");
//            Cursor cursorFT = newDatabase.query("favorTorrent", null, null, null, null, null, "id desc");
//            if (cursorFT.moveToFirst()) {
//                int jsonColIndex = cursorFT.getColumnIndex("json");
//                Gson gson = new Gson();
//                do {
//                    insertFavorTorrent(cursorFT.getString(cursorFT.getColumnIndex("title")),
//                            gson.fromJson(cursorFT.getString(jsonColIndex), ItemNewTor.class));
//                } while (cursorFT.moveToNext());
//                cursorFT.close();
//            }
//            //favor
//            deleteAll("favor");
//            Cursor cursorF = newDatabase.query("favor", null, null, null, null, null, "id desc");
//            if (cursorF.moveToFirst()) {
//                int titleColIndex = cursorF.getColumnIndex("title");
//                int imgColIndex = cursorF.getColumnIndex("img");
//                int linkColIndex = cursorF.getColumnIndex("link");
//                int voiceColIndex = cursorF.getColumnIndex("voice");
//                int qualityColIndex = cursorF.getColumnIndex("quality");
//                int seasonColIndex = cursorF.getColumnIndex("season");
//                int seriesColIndex = cursorF.getColumnIndex("series");
//                do {
//                    if (!cursorF.getString(titleColIndex).contains("'")) {
//                        Write();
//                        insert("favor",
//                                cursorF.getString(titleColIndex),
//                                cursorF.getString(imgColIndex),
//                                cursorF.getString(linkColIndex),
//                                cursorF.getString(voiceColIndex),
//                                cursorF.getString(qualityColIndex),
//                                Integer.parseInt(cursorF.getString(seasonColIndex)),
//                                Integer.parseInt(cursorF.getString(seriesColIndex)));
//                    }
//                } while (cursorF.moveToNext());
//                cursorF.close();
//            }
//            //history
//            deleteAll("history");
//            Cursor cursorH = newDatabase.query("favor", null, null, null, null, null, "id desc");
//            if (cursorF.moveToFirst()) {
//                int titleColIndex = cursorH.getColumnIndex("title");
//                int imgColIndex = cursorH.getColumnIndex("img");
//                int linkColIndex = cursorH.getColumnIndex("link");
//                int voiceColIndex = cursorH.getColumnIndex("voice");
//                int qualityColIndex = cursorH.getColumnIndex("quality");
//                int seasonColIndex = cursorH.getColumnIndex("season");
//                int seriesColIndex = cursorH.getColumnIndex("series");
//                do {
//                    if (!cursorH.getString(titleColIndex).contains("'")) {
//                        Write();
//                        Log.e("wer", cursorH.getString(titleColIndex));
//                        insert("history",
//                                cursorH.getString(titleColIndex),
//                                cursorH.getString(imgColIndex),
//                                cursorH.getString(linkColIndex),
//                                cursorH.getString(voiceColIndex),
//                                cursorH.getString(qualityColIndex),
//                                Integer.parseInt(cursorH.getString(seasonColIndex)),
//                                Integer.parseInt(cursorH.getString(seriesColIndex)));
//                    }
//                } while (cursorH.moveToNext());
//                cursorH.close();
//            }
//            //later
//            deleteAll("later");
//            Cursor cursorL = newDatabase.query("later", null, null, null, null, null, "id desc");
//            if (cursorF.moveToFirst()) {
//                int titleColIndex = cursorL.getColumnIndex("title");
//                int imgColIndex = cursorL.getColumnIndex("img");
//                int linkColIndex = cursorL.getColumnIndex("link");
//                int voiceColIndex = cursorL.getColumnIndex("voice");
//                int qualityColIndex = cursorL.getColumnIndex("quality");
//                int seasonColIndex = cursorL.getColumnIndex("season");
//                int seriesColIndex = cursorL.getColumnIndex("series");
//                do {
//                    if (!cursorL.getString(titleColIndex).contains("'")) {
//                        insert("history",
//                                cursorL.getString(titleColIndex),
//                                cursorL.getString(imgColIndex),
//                                cursorL.getString(linkColIndex),
//                                cursorL.getString(voiceColIndex),
//                                cursorL.getString(qualityColIndex),
//                                Integer.parseInt(cursorL.getString(seasonColIndex)),
//                                Integer.parseInt(cursorL.getString(seriesColIndex)));
//                    }
//                } while (cursorL.moveToNext());
//                cursorL.close();
//            }
//            //cacheWatch
//            deleteAll("cacheWatch");
//            //historyWatch
//            deleteAll("historyWatch");
//            Cursor cursorHW = newDatabase.query("historyWatch", null, null, null, null, null, "id desc");
//            if (cursorF.moveToFirst()) {
//                int titleColIndex = cursorHW.getColumnIndex("title");
//                int translatorColIndex = cursorHW.getColumnIndex("translator");
//                int seasonColIndex = cursorHW.getColumnIndex("season");
//                int seriesColIndex = cursorHW.getColumnIndex("series");
//                do {
//                    if (!cursorHW.getString(titleColIndex).contains("'")) {
//                        insertWatch(cursorHW.getString(titleColIndex),
//                                cursorHW.getString(translatorColIndex),
//                                cursorHW.getString(seasonColIndex),
//                                cursorHW.getString(seriesColIndex));
//                    }
//                } while (cursorHW.moveToNext());
//                cursorHW.close();
//            }
//            //historyTorrent
//            deleteAll("historyTorrent");
//            Cursor cursorHT = newDatabase.query("historyTorrent", null, null, null, null, null, "id desc");
//            if (cursorF.moveToFirst()) {
//                int titleColIndex = cursorHT.getColumnIndex("title");
//                int torrentColIndex = cursorHT.getColumnIndex("torrent");
//                int magnetColIndex = cursorHT.getColumnIndex("magnet");
//                do {
//                    if (!cursorHT.getString(titleColIndex).contains("'")) {
//                        insertHistoryTor(cursorHT.getString(titleColIndex),
//                                cursorHT.getString(torrentColIndex),
//                                cursorHT.getString(magnetColIndex));
//                    }
//                } while (cursorHT.moveToNext());
//                cursorHT.close();
//            }
//            //cacheVideo
//            deleteAll("cacheVideo");
//            Cursor cursorC = sqLiteDatabase.query("cacheVideo", null, null, null, null, null, "id desc");
//            if (cursorC.moveToFirst()) {
//                int jsonColIndex = cursorC.getColumnIndex("json");
//                int urlColIndex = cursorC.getColumnIndex("url");
//                Gson gson = new Gson();
//                do {
//                    insertCacheVideo(cursorC.getString(urlColIndex),
//                            gson.fromJson(cursorC.getString(jsonColIndex), ItemVideo.class));
//                } while (cursorC.moveToNext());
//                cursorC.close();
//            }
//            Toast.makeText(c, "База восстановлена",
//                    Toast.LENGTH_SHORT).show();
//        } else Toast.makeText(c, "Файл \"" +
//                            c.getString(R.string.app_name) + "/KinotorDB\" не найден",
//                    Toast.LENGTH_SHORT).show();
        String db_name = "DB";
        String db_path = Environment.getExternalStorageDirectory() + "/" +
                context.getString(R.string.app_name) + "/" + "KinotorDB";
        File f = new File(db_path);
        if (f.exists()) {
            Log.d("DBHelper", "file exist "+db_path);
            Log.d("DBHelper", "old file "+f.length());
        } else {
            Log.e("DBHelper", "file not exist ");
            String newDBpath = Environment.getExternalStorageDirectory() + "/" +
                    context.getString(R.string.app_name) + "/" + "DB";
            f = new File(newDBpath);
            if (f.exists()) {
                db_path = newDBpath;
            } else {
                newDBpath = Environment.getExternalStorageDirectory() + "/Download/KinotorDB";
                f = new File(newDBpath);
                if (f.exists()) {
                    db_path = newDBpath;
                } else {
                    newDBpath = Environment.getExternalStorageDirectory() + "/bluetooth/KinotorDB";
                    f = new File(newDBpath);
                    if (f.exists()) {
                        db_path = newDBpath;
                    } else {
                        db_path = "error";
                    }
                }
            }

            Log.d("DBHelper", "path "+db_path);
        }
        String out_path = context.getDatabasePath(db_name).getPath();
        Log.d("DBHelper", "orig file db "+out_path);
        Log.d("DBHelper", "orig file db "+new File(out_path).length());
        try {
            Log.d("DBHelper", "exit orig file "+out_path);
            File fileExt = new File(out_path);
            if (!fileExt.exists()) fileExt.mkdirs();

            InputStream mInputStream = new FileInputStream(db_path);
            OutputStream mOutputStream = new FileOutputStream(out_path);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = mInputStream.read(buffer)) != -1) {
                mOutputStream.write(buffer, 0, length);
            }
            mInputStream.close();
            mOutputStream.flush();
            mOutputStream.close();

            Log.d("DBHelper", "file "+fileExt.length());
        } catch (Exception e) {
            Toast.makeText(c, "Файл \"" +
                            c.getString(R.string.app_name) + "/KinotorDB\" не найден",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public boolean getRepeatWatch(int steps, String title, String translator, String season, String series) {
        Read();
        boolean r = false;
        Cursor cursor = sqLiteDatabase.query("historyWatch", null, null,
                null, null, null, null);
        if (cursor.moveToFirst()) {
            int titleColIndex = cursor.getColumnIndex("title");
            int translatorColIndex = cursor.getColumnIndex("translator");
            int seasonColIndex = cursor.getColumnIndex("season");
            int seriesColIndex = cursor.getColumnIndex("series");
            do {
                if (steps == 1) {
                    if (cursor.getString(titleColIndex).trim().equals(title.trim()) &&
                            cursor.getString(translatorColIndex).trim().equals(translator.trim())) {
                        r = true;
                        break;
                    }
                } else if (steps == 2) {
                    if (cursor.getString(titleColIndex).trim().equals(title.trim()) &&
                            cursor.getString(translatorColIndex).trim().equals(translator.trim()) &&
                            cursor.getString(seasonColIndex).trim().equals(season.trim())) {
                        r = true;
                        break;
                    }
                } else if (steps == 3) {
                    if (cursor.getString(titleColIndex).trim().equals(title.trim()) &&
                            cursor.getString(translatorColIndex).trim().equals(translator.trim()) &&
                            cursor.getString(seasonColIndex).trim().equals(season.trim()) &&
                            cursor.getString(seriesColIndex).trim().equals(series.trim())) {
                        r = true;
                        break;
                    }
                } else r = false;
            } while (cursor.moveToNext());
            cursor.close();
            return r;
        } else {
            cursor.close();
            return false;
        }
    }

    public boolean getRepeatCache(String url) {
        Read();
        boolean r = false;

        Cursor cursor = sqLiteDatabase.query("cacheWatch", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int titleColIndex = cursor.getColumnIndex("url");
            do {
                if (cursor.getString(titleColIndex).equals(Utils.urlEncode(url))) {
                    r = true;
                    break;
                }
            } while (cursor.moveToNext());
            cursor.close();
            return r;
        } else {
            cursor.close();
            return false;
        }
    }

    public boolean getRepeatVideo(String url) {
        Read();
        boolean r = false;
        Cursor cursor = sqLiteDatabase.query("cacheVideo", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int titleColIndex = cursor.getColumnIndex("url");
            do {
                if (cursor.getString(titleColIndex).equals(url)) {
                    r = true;
                    break;
                }
            } while (cursor.moveToNext());
            cursor.close();
            return r;
        } else {
            cursor.close();
            return false;
        }
    }

    public boolean getRepeat(String db, String title) {
        Read();
        boolean r = false;
        Cursor cursor = sqLiteDatabase.query(db, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int titleColIndex = cursor.getColumnIndex("title");
            do {
                if (title != null && cursor.getString(titleColIndex) != null)
                if (cursor.getString(titleColIndex).replace("'", "").equals(title.replace("'", ""))) {
                    r = true;
                    break;
                }
            } while (cursor.moveToNext());
            cursor.close();
            return r;
        } else {
            cursor.close();
            return false;
        }
    }

    public ItemHtml getDbItemsCache (String url){
        ItemHtml item = null;
        Read();
        Cursor cursor = sqLiteDatabase.query("cacheWatch", null, null, null, null, null, "id desc");
        if (cursor.moveToFirst()) {
            int jsonColIndex = cursor.getColumnIndex("json");
            int urlColIndex = cursor.getColumnIndex("url");
            Gson gson = new Gson();
            do {
                if (cursor.getString(urlColIndex).contains(Utils.urlEncode(url)))
                    item = gson.fromJson(Utils.urlDecode(cursor.getString(jsonColIndex)), ItemHtml.class);
            } while (cursor.moveToNext());
            cursor.close();
            return item;
        } else {
            Log.d("mydebug", "--- Database empty ---");
            cursor.close();
            return null;
        }
    }

    public ItemVideo getDbItemsCacheVid (String url){
        ItemVideo item = new ItemVideo();
        Read();
        Cursor cursor = sqLiteDatabase.query("cacheVideo", null, null, null, null, null, "id desc");
        if (cursor.moveToFirst()) {
            int jsonColIndex = cursor.getColumnIndex("json");
            int urlColIndex = cursor.getColumnIndex("url");
            Gson gson = new Gson();
            do {
                if (Utils.urlDecode(cursor.getString(urlColIndex)).contains(url))
                    item = gson.fromJson(cursor.getString(jsonColIndex), ItemVideo.class);
            } while (cursor.moveToNext());
            cursor.close();
            return item;
        } else {
            Log.d("mydebug", "--- Database empty ---");
            cursor.close();
            return null;
        }
    }

    public ArrayList<ItemNewTor> getDbItemsTor (){
        ItemNewTor item = null;
        ArrayList<ItemNewTor> newItemList = new ArrayList<>();
        Read();
        Cursor cursor = sqLiteDatabase.query("favorTorrent", null, null, null, null, null, "id desc");
        if (cursor.moveToFirst()) {
            int jsonColIndex = cursor.getColumnIndex("json");
            Gson gson = new Gson();
            do {
                item = gson.fromJson(cursor.getString(jsonColIndex), ItemNewTor.class);
                newItemList.add(item);
            } while (cursor.moveToNext());
            cursor.close();
            return newItemList;
        } else {
            Log.d("mydebug", "--- Database empty ---");
            cursor.close();
            return null;
        }
    }
    public String getDbItemsTorTitle (String db){
        String item = "";
        Read();
        Cursor cursor = sqLiteDatabase.query(db, null, null, null, null, null, "id desc");
        if (cursor.moveToFirst()) {
            int titleColIndex = cursor.getColumnIndex("title");
            do {
                item += "\"" + cursor.getString(titleColIndex) + "\"";
            } while (cursor.moveToNext());
            cursor.close();
            return item;
        } else {
            cursor.close();
            return "";
        }
    }

    public ArrayList<ItemHtml> getDbItems (String db){
        String curdb, status = "all";
        if (db.contains("|")) {
            curdb = db.split("\\|")[0].trim();
            status = db.split("\\|")[1].trim();
        } else curdb = db;
        ArrayList<ItemHtml> allItems = new ArrayList<>();
        ItemHtml item = new ItemHtml();
        Read();
        Cursor cursor = sqLiteDatabase.query(curdb, null, null, null, null, null, "id desc");
        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex("id");
            int titleColIndex = cursor.getColumnIndex("title");
            int imgColIndex = cursor.getColumnIndex("img");
            int linkColIndex = cursor.getColumnIndex("link");
            int voiceColIndex = cursor.getColumnIndex("voice");
            int qualityColIndex = cursor.getColumnIndex("quality");
            int seasonColIndex = cursor.getColumnIndex("season");
            int seriesColIndex = cursor.getColumnIndex("series");
            do {
                boolean b = false;
                if (status.contains("all") || (status.contains("film") && Integer.parseInt(cursor.getString(seasonColIndex)) == 0)
                        || (status.contains("serial") && (Integer.parseInt(cursor.getString(seasonColIndex)) != 0 ||
                        Integer.parseInt(cursor.getString(seriesColIndex)) != 0)))
                    b = true;

                if (!cursor.getString(titleColIndex).contains("'") && b) {
                    item.setTitle(cursor.getString(titleColIndex));
                    item.setImg(Utils.urlDecode(cursor.getString(imgColIndex)));
                    item.setUrl(Utils.urlDecode(cursor.getString(linkColIndex)));
                    item.setVoice(cursor.getString(voiceColIndex));
                    item.setGenre("error");
                    item.setRating("error");
                    item.setQuality(cursor.getString(qualityColIndex));
                    item.setSeason(Integer.parseInt(cursor.getString(seasonColIndex)));
                    item.setSeries(Integer.parseInt(cursor.getString(seriesColIndex)));
//                    Log.d("mydebug", "ID = " + cursor.getInt(idColIndex) + ", title = " + cursor.getString(titleColIndex));
                    allItems.add(item);
                }
            } while (cursor.moveToNext());
            cursor.close();
            return allItems;
        } else {
            Log.d("mydebug", "--- Database empty ---");
            cursor.close();
            return null;
        }
    }
}
