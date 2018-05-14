package com.kinotor.tiar.kinotor.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;

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
    private static final int DATABASE_VERSION = 2;

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
                "quality quality," +
                "season integer," +
                "series integer" +
                ");");
        db.execSQL("create table history (" +
                "id integer primary key autoincrement," +
                "title text," +
                "link text," +
                "img text," +
                "voice text," +
                "quality quality," +
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("create table historyWatch (" +
                "id integer primary key autoincrement," +
                "title text," +
                "translator text," +
                "season text," +
                "series text" +
                ");");
    }

    private void Read() throws android.database.SQLException {
        sqLiteDatabase = this.getReadableDatabase();
    }

    public void Write() throws android.database.SQLException {
        sqLiteDatabase = this.getWritableDatabase();
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

    public long insert(String db, String title, String img_src, String link, String voice,
                       String quality, int season, int series){
        ContentValues cv = new ContentValues();
        cv.put("title", title);
        cv.put("link", link);
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
        sqLiteDatabase.delete(db, "title" + "='" + title + "'", null);
        Log.d("mydebug", "--- " + title + " Delete on "+ db +" ---");
    }

    public void deleteAll(String db){
        Write();
        sqLiteDatabase.delete(db, null, null);
//        sqLiteDatabase.execSQL("delete from "+ db);
        Log.d("mydebug", "--- Delete "+ db +" ---");
    }

    public void copyDataBaseToSd(){
        String db_name = "DB";
        String out_path = Environment.getExternalStorageDirectory() + "/" +
                context.getString(R.string.app_name) + "/";
        String db_path = context.getDatabasePath(db_name).getPath();
        db_path = db_path.contains(".sqlite") ? db_path.split(".sqlite")[0] : db_path;
        try {
            File fileExt = new File(out_path);
            if (!fileExt.exists()) {
                fileExt.mkdirs();
            }

            InputStream mInputStream = new FileInputStream(db_path);
            OutputStream mOutputStream = new FileOutputStream(out_path + db_name);
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

    public void copyDataBaseToData(){
        String db_name = "DB";
        String db_path = Environment.getExternalStorageDirectory() + "/" +
                context.getString(R.string.app_name) + "/" + db_name;
        String out_path = context.getDatabasePath(db_name).getPath();
        out_path = out_path.contains("DB") ? out_path.split("DB")[0] : out_path;
        try {
            File fileExt = new File(out_path);
            if (!fileExt.exists()) {
                fileExt.mkdirs();
            }

            InputStream mInputStream = new FileInputStream(db_path);
            OutputStream mOutputStream = new FileOutputStream(out_path + db_name);
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

    public boolean getRepeat(String db, String title) {
        Read();
        boolean r = false;
        Cursor cursor = sqLiteDatabase.query(db, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int titleColIndex = cursor.getColumnIndex("title");
            do {
                if (cursor.getString(titleColIndex).equals(title)) {
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

    public ArrayList<ItemHtml> getDbItems (String db){
        ArrayList<ItemHtml> allItems = new ArrayList<>();
        ItemHtml item = new ItemHtml();
        Read();
        Cursor cursor = sqLiteDatabase.query(db, null, null, null, null, null, "id desc");
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
                item.setTitle(cursor.getString(titleColIndex));
                item.setImg(cursor.getString(imgColIndex));
                item.setUrl(cursor.getString(linkColIndex));
                item.setVoice(cursor.getString(voiceColIndex));
                item.setQuality(cursor.getString(qualityColIndex));
                item.setSeason(Integer.parseInt(cursor.getString(seasonColIndex)));
                item.setSeries(Integer.parseInt(cursor.getString(seriesColIndex)));
                Log.d("mydebug", "ID = " + cursor.getInt(idColIndex) + ", title = " + cursor.getString(titleColIndex));
                allItems.add(item);
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
