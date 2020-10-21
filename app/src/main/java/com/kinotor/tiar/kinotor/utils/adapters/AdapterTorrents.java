package com.kinotor.tiar.kinotor.utils.adapters;


import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kinotor.tiar.kinotor.BuildConfig;
import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemNewTor;
import com.kinotor.tiar.kinotor.items.ItemTorrent;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.parser.GetLocation;
import com.kinotor.tiar.kinotor.parser.GetMagnet;
import com.kinotor.tiar.kinotor.parser.torrents.FreerutorLocation;
import com.kinotor.tiar.kinotor.parser.torrents.FreerutorUrl;
import com.kinotor.tiar.kinotor.parser.torrents.HurtomTorrent;
import com.kinotor.tiar.kinotor.parser.torrents.KinozalMagnet;
import com.kinotor.tiar.kinotor.parser.torrents.RutrackerMagnet;
import com.kinotor.tiar.kinotor.utils.DBHelper;
import com.kinotor.tiar.kinotor.utils.OnTaskLocationCallback;


import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

/**
 * Created by Tiar on 30.09.2017.
 */

public class AdapterTorrents extends RecyclerView.Adapter<AdapterTorrents.ViewHolder> {
    private Context context;
    private ItemTorrent item;
    private String type = "";
    private ArrayList<ItemNewTor> newItemList = new ArrayList<>();
    private String newItemListFav = "";
    private String newItemListHist = "";
    private int lastFocussedPosition = -1;
    private View view;
    private DBHelper dbHelper;

    public AdapterTorrents(Context context, ItemTorrent item) {

        this.context = context;
        dbHelper = new DBHelper(context);
        if (item != null) {
            this.item = item;
            addItemList();
        }
    }

    public AdapterTorrents(Context context, String type) {
        this.context = context;
        this.item = new ItemTorrent();
        this.type = type;
        dbHelper = new DBHelper(context);
        if (type.equals("favor")) {
            newItemList = dbHelper.getDbItemsTor();
        }
    }

    @NonNull
    @Override
    public AdapterTorrents.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_tor, parent, false);

        newItemListFav = dbHelper.getDbItemsTorTitle("favorTorrent");
        newItemListHist = dbHelper.getDbItemsTorTitle("historyTorrent");

//        Log.e("test", "onCreateViewHolder: "+newItemListFav);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterTorrents.ViewHolder holder, int position) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        int sizetext = Integer.parseInt(preference.getString("text_size_detail", "13"));
        if (newItemList.get(position) != null) {
            final ItemNewTor cur = newItemList.get(position);

            holder.name.setText(cur.getTortitle());
            if (cur.getTorsize() != null) {
                if (cur.getTorsize().contains("error"))
                    holder.size.setVisibility(View.GONE);
                else {
                    holder.size.setVisibility(View.VISIBLE);
                    holder.size.setText(cur.getTorsize());
                }
            } else holder.size.setVisibility(View.GONE);
            if (cur.getTorsid() != null) {
                if (cur.getTorsid().equals("x"))
                    holder.sl.setVisibility(View.GONE);
                else {
                    holder.sl.setVisibility(View.VISIBLE);
                    holder.lich.setText(cur.getTorlich());
                    holder.sid.setText(cur.getTorsid());
                }
            } else holder.sl.setVisibility(View.GONE);
            if (dbHelper.getRepeat("favorTorrent", cur.getTortitle()))
                holder.icon.setImageResource(R.drawable.ic_star);
            else if (dbHelper.getRepeat("historyTorrent", cur.getTortitle()))
                holder.icon.setImageResource(R.drawable.ic_menu_hist);
            else if (!cur.getTorurl().contains("error"))
                holder.icon.setImageResource(R.drawable.ic_torrent_file);
            else if (!cur.getTormagnet().contains("error"))
                holder.icon.setImageResource(R.drawable.ic_magnet);


            if (!cur.getTorcontent().contains("error")) {
                holder.desc.setVisibility(View.VISIBLE);
                holder.desc.setText(cur.getTorcontent());
            }

            //text size
            holder.name.setTextSize(sizetext + 4);
            holder.desc.setTextSize(sizetext);
            holder.lich.setTextSize(sizetext);
            holder.sid.setTextSize(sizetext);
            holder.size.setTextSize(sizetext);

//        if (position == 0) {
//            holder.mView.requestFocus();
//        }


//        holder.mView.setFocusable(true);
//        holder.name.setFocusableInTouchMode(true);

            if (preference.getBoolean("tv_activity_detail", true)) {
                holder.mView.setBackgroundColor(view.getResources().getColor(R.color.colorGone));
                holder.bg.setBackgroundColor(view.getResources().getColor(R.color.colorGone));
            }
            holder.more.setFocusable(true);
            if (Build.VERSION.SDK_INT < 23) {
                holder.more.setOnFocusChangeListener((view, b) -> {
                    if (!view.isSelected()) {
                        holder.more.setBackgroundColor(view.getResources().getColor(R.color.colorAccentDark));
                    } else
                        holder.more.setBackgroundColor(view.getResources().getColor(R.color.colorGone));
                    view.setSelected(b);
                });
            }
            holder.mView.setOnFocusChangeListener((view, b) -> {
                if (b) {
                    if (Build.VERSION.SDK_INT < 23) {
                        holder.mView.setBackgroundColor(view.getResources().getColor(R.color.colorAccentDark));
                        holder.bg.setBackgroundColor(view.getResources().getColor(R.color.colorAccentDark));
                    }
                    if (lastFocussedPosition == -1 || lastFocussedPosition == position) {
                        lastFocussedPosition = position;
                        holder.mView.requestFocus();
                    }
                } else {
                    if (Build.VERSION.SDK_INT < 23) {
                        holder.mView.setBackgroundColor(view.getResources().getColor(R.color.colorPrimaryDark));
                        holder.bg.setBackgroundColor(view.getResources().getColor(R.color.colorPrimaryDark));
                    }
                    lastFocussedPosition = -1;
                }
            });
            holder.mView.setOnClickListener(view -> {
                if (!dbHelper.getRepeat("historyTorrent", cur.getTortitle())) {
                    dbHelper.Write();
                    if (dbHelper.getRepeat("favorTorrent", cur.getTortitle()))
                        holder.icon.setImageResource(R.drawable.ic_star);
                    else holder.icon.setImageResource(R.drawable.ic_menu_hist);
                    dbHelper.insertHistoryTor(cur.getTortitle().trim(), cur.getTorurl().trim(), cur.getTormagnet());
                }
                if (!cur.getTorurl().contains("error")) {
                    if (cur.getTorurl().contains(Statics.GREENTEA_TR_URL) &&
                            Statics.GREENTEA_TR_COOCKIE.contains("bb_data=deleted")) {
                        Toast.makeText(context, "Нужна авторизация", Toast.LENGTH_SHORT).show();
                        ClickMagnet(position);
                    } else torrentClick(cur);
                } else if (!cur.getTormagnet().contains("error")) {
                    ClickMagnet(position);
                }

            });
            holder.mView.setOnLongClickListener(view -> {
                longPress(cur, position, holder);
                return true;
            });
            holder.more.setOnClickListener(view -> {
                longPress(cur, position, holder);
            });

            holder.mView.setNextFocusRightId(holder.more.getId());
            holder.more.setNextFocusLeftId(holder.mView.getId());
        }
    }

    private void longPress(ItemNewTor cur, int position, ViewHolder holder) {
        ArrayList<String> ctg = new ArrayList<>();
        if (!cur.getTorurl().contains("error")) {
            ctg.add("Торрент");
            ctg.add("Скопировать ссылку на торрент");
        }
        if (!cur.getTormagnet().contains("error")) {
            ctg.add("Магнет");
            ctg.add("Скопировать ссылку на магнет");
        }
        if (!cur.getTormagnet().contains("error") || !cur.getTorurl().contains("error")) {
            ctg.add("Поделиться");
        }
        if (!cur.getUrl().contains("error")) {
            ctg.add("Открыть в браузере");
        }
        if (dbHelper.getRepeat("favorTorrent", cur.getTortitle())) {
            ctg.add("Удалить из избранного");
        } else ctg.add("Добавить в избранное");
        if (dbHelper.getRepeat("historyTorrent", cur.getTortitle())) {
            ctg.add("Удалить из истории");
        }
        final String[] list = ctg.toArray(new String[ctg.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
        builder.setTitle(cur.getTortitle()).setItems(list, (dialogInterface, i) -> {
            if (list[i].equals("Торрент")){
                torrentClick(cur);
                if (!dbHelper.getRepeat("historyTorrent", cur.getTortitle())) {
                    dbHelper.Write();
                    dbHelper.insertHistoryTor(cur.getTortitle().trim(), cur.getTorurl().trim(), cur.getTormagnet());
                    if (dbHelper.getRepeat("favorTorrent", cur.getTortitle()))
                        holder.icon.setImageResource(R.drawable.ic_star);
                    else holder.icon.setImageResource(R.drawable.ic_menu_hist);
                }
            }
            if (list[i].equals("Удалить из истории")){
                dbHelper.Write();
                dbHelper.delete("historyTorrent", cur.getTortitle());
                if (dbHelper.getRepeat("favorTorrent", cur.getTortitle())) {
                    holder.icon.setImageResource(R.drawable.ic_star);
                } else if (!cur.getTorurl().contains("error"))
                    holder.icon.setImageResource(R.drawable.ic_torrent_file);
                else holder.icon.setImageResource(R.drawable.ic_magnet);

            }
            if (list[i].equals("Удалить из избранного")){
                dbHelper.Write();
                dbHelper.delete("favorTorrent", cur.getTortitle());
                if (dbHelper.getRepeat("historyTorrent", cur.getTortitle())) {
                    holder.icon.setImageResource(R.drawable.ic_menu_hist);
                } else holder.icon.setImageResource(R.drawable.ic_magnet);
                if (type.equals("favor")) {
                    newItemList = dbHelper.getDbItemsTor();
                    notifyDataSetChanged();
                }
            }
            if (list[i].equals("Добавить в избранное")){
                dbHelper.Write();
                dbHelper.insertFavorTorrent(cur.getTortitle().trim(), cur);
                holder.icon.setImageResource(R.drawable.ic_star);
            }
            if (list[i].equals("Скопировать ссылку на торрент")){
                if (cur.getTorurl().equals("parse hurtom")) {
                    if (Statics.HURTOM_COOCKIE.contains("sid=deleted"))
                        Toast.makeText(context, "Нужна авторизация", Toast.LENGTH_SHORT).show();
                    else {
                        HurtomTorrent getMagnet = new HurtomTorrent(cur.getUrl(), this::onCopy);
                        getMagnet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                } else if (cur.getTorurl().contains(Statics.FREERUTOR_URL)) {
                    FreerutorUrl getTor = new FreerutorUrl(cur.getTorurl(), "play", this::onCopy);
                    getTor.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
//                    else if (cur.getTorurl().contains(Statics.NNM_URL)) {
//                        GetLocation location = new GetLocation(cur.getTorurl(), this::onCopy);
//                        location.execute();
//                    }
                else {
                    onCopy(cur.getTorurl());
                }
            }
            if (list[i].equals("Магнет")) {
                if (!dbHelper.getRepeat("historyTorrent", cur.getTortitle())) {
                    dbHelper.Write();
                    dbHelper.insertHistoryTor(cur.getTortitle().trim(), cur.getTorurl().trim(), cur.getTormagnet());
                    if (dbHelper.getRepeat("favorTorrent", cur.getTortitle()))
                        holder.icon.setImageResource(R.drawable.ic_star);
                    else holder.icon.setImageResource(R.drawable.ic_menu_hist);
                }
                ClickMagnet(position);
            }
            if (list[i].equals("Скопировать ссылку на магнет")){
                if (cur.getTorurl().contains(Statics.FREERUTOR_URL)) {
                    FreerutorUrl getTor = new FreerutorUrl(cur.getTorurl(), "magnet", this::onCopy);
                    getTor.execute();
                } else if (cur.getTorurl().contains(Statics.NNM_URL)) {
                    GetLocation location = new GetLocation(cur.getTormagnet(), this::onCopy);
                    location.execute();
                } else {
                    switch (cur.getTormagnet()) {
                        case "parse kinozal": {
                            if (Statics.KINOZAL_COOCKIE.contains("uid=deleted") && !Statics.KINOZAL_URL.contains(".appspot.com"))
                                Toast.makeText(context, "Нужна авторизация", Toast.LENGTH_SHORT).show();
                            else {
                                KinozalMagnet getMagnet = new KinozalMagnet(cur.getUrl(), this::onCopy);
                                getMagnet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                            break;
                        }
                        case "parse": {
                            GetMagnet getMagnet = new GetMagnet(cur.getUrl(), this::onCopy);
                            getMagnet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            break;
                        }
                        default:
                            onCopy(cur.getTormagnet());
                            break;
                    }
                }
            }
            if (list[i].equals("Поделиться")){
                String tor, mag;
                if (cur.getTorurl().contains(Statics.FREERUTOR_URL)){
                    tor = "\n url:" + cur.getTorurl();
                    mag = "";
                } else {
                    tor = cur.getTorurl().contains("error") ? "" : "\n torrent:" + cur.getTorurl();
                    mag = cur.getTormagnet().contains("error") ? "" : "\n magnet:" + cur.getTormagnet();
                }
                String share = "KinoTor" + tor + mag;


                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/html");
                intent.putExtra(Intent.EXTRA_SUBJECT, "sample");
                intent.putExtra(Intent.EXTRA_TEXT, share);
                context.startActivity(Intent.createChooser(intent, "Отправить"));
            }
            if (list[i].equals("Открыть в браузере") && !cur.getUrl().isEmpty()){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(cur.getUrl()));
                context.startActivity(intent);
            }
        }).create().show();
    }

    private void torrentClick(ItemNewTor cur) {
        String n = cur.getTortitle().substring(0, Math.min(cur.getTortitle().length(), 130))
                .replace("/", "")
                .replace("\\", "")
                .replace("+","")
                .replace(":","").trim();
        if (cur.getTorurl().equals("parse hurtom")) {
            if (Statics.HURTOM_COOCKIE.contains("sid=deleted"))
                Toast.makeText(context, "Нужна авторизация", Toast.LENGTH_SHORT).show();
            else {
                HurtomTorrent getMagnet = new HurtomTorrent(cur.getUrl(), new OnTaskLocationCallback() {
                    @Override
                    public void OnCompleted(String location) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                                PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                        } else {
                            String destination = File.separator + "KinoTor" + File.separator + n + ".torrent";
                            File myFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + destination.replace(".torrent.torrent", ".torrent"));
                            if(myFile.exists()) {
                                startFile(myFile);
                            } else {
                                final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(location));
                                request.addRequestHeader("Cookie", Statics.HURTOM_COOCKIE.replace(",",";"));
                                request.addRequestHeader("accept-encoding", "gzip, deflate, br");
                                request.setTitle(n + ".torrent");
                                request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, destination.replace(".torrent.torrent", ".torrent"));

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    if (manager != null) manager.enqueue(request);
                                    BroadcastReceiver onComplete = new BroadcastReceiver() {
                                        public void onReceive(Context ctxt, Intent intent) {
                                            if (Objects.requireNonNull(intent.getAction()).equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                                                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equalsIgnoreCase(intent.getAction())) {
                                                    startFile(myFile);
                                                }
                                            }
                                            context.unregisterReceiver(this);
                                        }
                                    };
                                    context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                } else {
                                    if (manager != null) manager.enqueue(request);

                                    //set BroadcastReceiver to install app when .apk is downloaded
                                    BroadcastReceiver onComplete = new BroadcastReceiver() {
                                        public void onReceive(Context ctxt, Intent intent) {
                                            if (Objects.requireNonNull(intent.getAction()).equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                                                startFile(myFile);
                                            }
                                        }
                                    };
                                    //register receiver for when .apk download is compete
                                    context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                                }
                            }
                        }
                    }
                });
                getMagnet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else if (cur.getTorurl().contains(Statics.ANIDUB_TR_URL)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            } else {
                String destination = File.separator + "KinoTor" + File.separator + n + ".torrent";
                File myFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + destination.replace(".torrent.torrent", ".torrent"));
                if(myFile.exists()) {
                    startFile(myFile);
                } else {
                    final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(cur.getTorurl()));
                    request.addRequestHeader("Cookie", Statics.ANIDUB_TR_COOCKIE.replace(",", ";"));
                    request.addRequestHeader("accept-encoding", "gzip, deflate, br");
                    request.setTitle(n + ".torrent");
                    request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, destination.replace(".torrent.torrent", ".torrent"));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (manager != null) manager.enqueue(request);
                        BroadcastReceiver onComplete = new BroadcastReceiver() {
                            public void onReceive(Context ctxt, Intent intent) {
                                if (Objects.requireNonNull(intent.getAction()).equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equalsIgnoreCase(intent.getAction())) {
                                        startFile(myFile);
                                    }
                                }
                                context.unregisterReceiver(this);
                            }
                        };
                        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    } else {
                        if (manager != null) manager.enqueue(request);

                        //set BroadcastReceiver to install app when .apk is downloaded
                        BroadcastReceiver onComplete = new BroadcastReceiver() {
                            public void onReceive(Context ctxt, Intent intent) {
                                if (Objects.requireNonNull(intent.getAction()).equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                                    startFile(myFile);
                                }
                            }
                        };
                        //register receiver for when .apk download is compete
                        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }
                }
            }
        } else if (cur.getTorurl().contains(Statics.GREENTEA_TR_URL)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            } else {
                String destination = File.separator + "KinoTor" + File.separator + n + ".torrent";
                File myFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + destination.replace(".torrent.torrent", ".torrent"));
                if(myFile.exists()) {
                    startFile(myFile);
                } else {
                    final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(cur.getTorurl()));
                    request.addRequestHeader("Cookie", Statics.GREENTEA_TR_COOCKIE.replace(",", ";"));
                    request.addRequestHeader("accept-encoding", "gzip, deflate, br");
                    request.setTitle(n + ".torrent");
                    request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, destination.replace(".torrent.torrent", ".torrent"));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (manager != null) manager.enqueue(request);
                        BroadcastReceiver onComplete = new BroadcastReceiver() {
                            public void onReceive(Context ctxt, Intent intent) {
                                if (Objects.requireNonNull(intent.getAction()).equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equalsIgnoreCase(intent.getAction())) {
                                        startFile(myFile);
                                    }
                                }
                                context.unregisterReceiver(this);
                            }
                        };
                        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    } else {
                        if (manager != null) manager.enqueue(request);

                        //set BroadcastReceiver to install app when .apk is downloaded
                        BroadcastReceiver onComplete = new BroadcastReceiver() {
                            public void onReceive(Context ctxt, Intent intent) {
                                if (Objects.requireNonNull(intent.getAction()).equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                                    startFile(myFile);
                                }
                            }
                        };
                        //register receiver for when .apk download is compete
                        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }
                }
            }
        } else if (cur.getTorurl().contains(Statics.RUTRACKER_URL)) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            } else {
                String destination = File.separator + "KinoTor" + File.separator + n + ".torrent";
                File myFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS) + destination.replace(".torrent.torrent", ".torrent"));
                if(myFile.exists()) {
                    startFile(myFile);
                } else {
                    final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(cur.getTorurl()));
                    request.addRequestHeader("Cookie", Statics.RUTRACKER_COOCKIE.replace(",", ";"));
                    request.addRequestHeader("accept-encoding", "gzip, deflate, br");
                    request.setTitle(n + ".torrent");
                    request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, destination.replace(".torrent.torrent", ".torrent"));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        if (manager != null) manager.enqueue(request);
                        BroadcastReceiver onComplete = new BroadcastReceiver() {
                            public void onReceive(Context ctxt, Intent intent) {
                                if (Objects.requireNonNull(intent.getAction()).equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equalsIgnoreCase(intent.getAction())) {
                                        startFile(myFile);
                                    }
                                }
                                context.unregisterReceiver(this);
                            }
                        };
                        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    } else {
                        if (manager != null) manager.enqueue(request);

                        //set BroadcastReceiver to install app when .apk is downloaded
                        BroadcastReceiver onComplete = new BroadcastReceiver() {
                            public void onReceive(Context ctxt, Intent intent) {
                                if (Objects.requireNonNull(intent.getAction()).equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                                    startFile(myFile);
                                }
                            }
                        };
                        //register receiver for when .apk download is compete
                        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }
                }
            }
        } else onMagnet(cur.getTorurl());
    }

    private void startFile(File myFile) {
        if(myFile.exists()) {
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    i.setDataAndType(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", myFile),
                            "application/x-bittorrent");
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    i.setDataAndType(Uri.fromFile(myFile),
                            "application/x-bittorrent");
                }
                context.startActivity(i);
            } catch (Exception e) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    i.setData(FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", myFile));
                    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    i.setData(Uri.fromFile(myFile));
                }
                context.startActivity(i);
            }
        } else Toast.makeText(context, "Ошибка загрузки, повторите", Toast.LENGTH_SHORT).show();
    }

    private void startIntent(String uri) {
        if (uri.contains("magnet") || uri.contains("rutracker") ||
                uri.contains("underverse") || uri.contains("kzal-tv")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri));
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Не найдено подходящего приложения! Ссылка скопирована",
                        Toast.LENGTH_LONG).show();
            }
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(uri), "application/x-bittorrent");
                context.startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(uri));
                context.startActivity(intent);
            }
        }
    }

    private void ClickMagnet (int cur) {
        Log.d("adaptertorrent", "ClickMagnet: " + cur);
        String url = newItemList.get(cur).getTormagnet().replace("'", "")
                .replace("}", "");
        if (url.equals("parse kinozal")){
            if (Statics.KINOZAL_COOCKIE.contains("uid=deleted") && !Statics.KINOZAL_URL.contains(".appspot.com"))
                Toast.makeText(context, "Нужна авторизация", Toast.LENGTH_SHORT).show();
            else {
                KinozalMagnet getMagnet = new KinozalMagnet(newItemList.get(cur).getUrl(), this::onMagnet);
                getMagnet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else if (url.equals("parse rutracker")){
            RutrackerMagnet getMagnet = new RutrackerMagnet(newItemList.get(cur).getUrl(), this::onMagnet);
            getMagnet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.equals("parse")){
            GetMagnet getMagnet = new GetMagnet(newItemList.get(cur).getUrl(), this::onMagnet);
            getMagnet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (url.startsWith(Statics.FREERUTOR_URL)){
            FreerutorUrl getMagnet = new FreerutorUrl(url, "magnet", this::onMagnet);
            getMagnet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            if (url.contains("tparser") || url.contains("kzal-tv")) {
                url = url.replace("'", "").replace("}", "");
                GetLocation getLocation = new GetLocation(url, this::onMagnet);
                getLocation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else onMagnet(url);
        }
    }

    private void onMagnet (String urltor) {
        if (!urltor.isEmpty() && !urltor.contains("error")) {
            if (urltor.startsWith(Statics.FREERUTOR_URL)){
                FreerutorUrl getTor = new FreerutorUrl(urltor, "play", location -> {
                    FreerutorLocation getUrl = new FreerutorLocation(urltor, location, this::startIntent);
                    getUrl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                });
                getTor.execute();
            } else if (urltor.contains(Statics.NNM_URL)) {
                GetLocation location = new GetLocation(urltor, this::startIntent);
                location.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else startIntent(urltor);
            Log.d("adaptertorrent", "onMagnet: " + urltor);
        } else
            Toast.makeText(context, "Ошибка ссылки.",
                    Toast.LENGTH_LONG).show();
    }

    private void onCopy(String location){
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", location);
        if (clipboard != null) clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Cсылка скопирована в буфер обмена", Toast.LENGTH_LONG).show();
    }

    @Override
    public int getItemCount() {
        if (newItemList != null)
            return newItemList.size();
        else
            return 0;
    }

    private void addItemList() {
        newItemList = new ArrayList<>();
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        String torFilter = preference.getString("filter_tor", "none");
        boolean torhist = preference.getBoolean("torrent_hist_f", true);
        for (int i=0; i < item.tortitle.size(); i++) {
            ItemNewTor newTor = new ItemNewTor(item.getTorTitle(i),
                    item.getUrl(i),
                    item.getTorSize(i).replace(",", "."),
                    item.getTorUrl(i),
                    item.getTorMagnet(i),
                    item.getTorSid(i),
                    item.getTorLich(i),
                    item.getTorContent(i));
            if (torFilter.equals("words")) {
                if (preference.getString("filter_tor_word", "").trim().isEmpty())
                    newItemList.add(newTor);
                else if (newTor.getTortitle().toLowerCase().contains(preference
                        .getString("filter_tor_word", "").toLowerCase().trim()))
                    newItemList.add(newTor);
            } else newItemList.add(newTor);
        }
        if (torFilter.equals("size") || torFilter.equals("words")) {
            try {
                Collections.sort(newItemList, (t2, t1) -> {
                    String tf = t1.getTorsize().replace("GB","").replace("ГБ","").trim();
                    String tt = t2.getTorsize().replace("GB","").replace("ГБ","").trim();
                    if (torhist & (newItemListFav.contains("\"" + t1.getTortitle() + "\"") ||
                            newItemListFav.contains("\"" + t2.getTortitle() + "\""))) {
                       int ti = 0;
                       int fi = 0;
                       if (newItemListFav.contains("\"" + t1.getTortitle() + "\""))
                           ti = 1;
                       if (newItemListFav.contains("\"" + t2.getTortitle() + "\""))
                           fi = 1;
                       return (ti - fi);
                    } else if (torhist & (newItemListHist.contains("\"" + t1.getTortitle() + "\"") ||
                            newItemListHist.contains("\"" + t2.getTortitle() + "\""))) {
                        int ti = 0;
                        int fi = 0;
                        if (newItemListHist.contains("\"" + t1.getTortitle() + "\""))
                            ti = 1;
                        if (newItemListHist.contains("\"" + t2.getTortitle() + "\""))
                            fi = 1;
                        return (ti - fi);
                    } else if (tf.contains(" ")) {
                        try {
                            return (int) (
                                    Float.parseFloat(tf.split(" ")[0].trim())*1000 -
                                            Float.parseFloat(tt.split(" ")[0].trim())*1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return t1.getTorsize().compareTo(t2.getTorsize());
                        }
                    } else  {
                        try {
                            return (int) (
                                    Float.parseFloat(tf)*1000 - Float.parseFloat(tt)*1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return t1.getTorsize().compareTo(t2.getTorsize());
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (torFilter.equals("name")) {
            Collections.sort(newItemList, (t2, t1) -> {
                if (torhist & (newItemListFav.contains("\"" + t1.getTortitle() + "\"") ||
                        newItemListFav.contains("\"" + t2.getTortitle() + "\""))) {
                    int ti = 0;
                    int fi = 0;
                    if (newItemListFav.contains("\"" + t1.getTortitle() + "\""))
                        ti = 1;
                    if (newItemListFav.contains("\"" + t2.getTortitle() + "\""))
                        fi = 1;
                    return (ti - fi);
                } else if (torhist & (newItemListHist.contains("\"" + t1.getTortitle() + "\"") ||
                        newItemListHist.contains("\"" + t2.getTortitle() + "\""))) {
                    int ti = 0;
                    int fi = 0;
                    if (newItemListHist.contains("\"" + t1.getTortitle() + "\""))
                        ti = 1;
                    if (newItemListHist.contains("\"" + t2.getTortitle() + "\""))
                        fi = 1;
                    return (ti - fi);
                } else return t2.getTortitle().compareTo(t1.getTortitle());
            });
        }
        if (torFilter.equals("sid")) {
            Collections.sort(newItemList, (t2, t1) -> {
                if (torhist & (newItemListFav.contains("\"" + t1.getTortitle() + "\"") ||
                        newItemListFav.contains("\"" + t2.getTortitle() + "\""))) {
                    int ti = 0;
                    int fi = 0;
                    if (newItemListFav.contains("\"" + t1.getTortitle() + "\""))
                        ti = 1;
                    if (newItemListFav.contains("\"" + t2.getTortitle() + "\""))
                        fi = 1;
                    return (ti - fi);
                } else if (torhist & (newItemListHist.contains("\"" + t1.getTortitle() + "\"") ||
                        newItemListHist.contains("\"" + t2.getTortitle() + "\""))) {
                    int ti = 0;
                    int fi = 0;
                    if (newItemListHist.contains("\"" + t1.getTortitle() + "\""))
                        ti = 1;
                    if (newItemListHist.contains("\"" + t2.getTortitle() + "\""))
                        fi = 1;
                    return (ti - fi);
                } else {
                    try {
                        return Integer.parseInt(t1.getTorsid().trim()) -
                                Integer.parseInt(t2.getTorsid().trim());
                    } catch (Exception e) {
                        return t1.getTorsid().compareTo(t2.getTorsid());
                    }
                }
            });
        }
        if (torFilter.equals("lich")) {
            Collections.sort(newItemList, (t2, t1) -> {
                if (torhist & (newItemListFav.contains("\"" + t1.getTortitle() + "\"") ||
                        newItemListFav.contains("\"" + t2.getTortitle() + "\""))) {
                    int ti = 0;
                    int fi = 0;
                    if (newItemListFav.contains("\"" + t1.getTortitle() + "\""))
                        ti = 1;
                    if (newItemListFav.contains("\"" + t2.getTortitle() + "\""))
                        fi = 1;
                    return (ti - fi);
                } else if (torhist & (newItemListHist.contains("\"" + t1.getTortitle() + "\"") ||
                        newItemListHist.contains("\"" + t2.getTortitle() + "\""))) {
                    int ti = 0;
                    int fi = 0;
                    if (newItemListHist.contains("\"" + t1.getTortitle() + "\""))
                        ti = 1;
                    if (newItemListHist.contains("\"" + t2.getTortitle() + "\""))
                        fi = 1;
                    return (ti - fi);
                } else if (torhist & (dbHelper.getRepeat("historyTorrent", t1.getTortitle()) ||
                        dbHelper.getRepeat("historyTorrent", t2.getTortitle()))) {
                    int ti = 0;
                    int fi = 0;
                    if (dbHelper.getRepeat("historyTorrent", t1.getTortitle()))
                        ti = 1;
                    if (dbHelper.getRepeat("historyTorrent", t2.getTortitle()))
                        fi = 1;
                    return (ti - fi);
                } else {
                    try {
                        return Integer.parseInt(t1.getTorlich().trim()) -
                                Integer.parseInt(t2.getTorlich().trim());
                    } catch (Exception e) {
                        return t1.getTorlich().compareTo(t2.getTorlich());
                    }
                }
            });
        }
    }

    public void setNewItems(ArrayList<ItemNewTor> newItemList) {
        if (newItemList != null)
            this.newItemList = newItemList;
    }

    public void addItems(ItemTorrent item) {
        if (this.item != null) {
            this.item.addItems(item);
            addItemList();
        } else this.item = item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final LinearLayout sl, bg;
        public final TextView name, size, desc, sid, lich;
        public final ImageView icon;
        public final ImageButton more;

        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView.findViewById(R.id.mView);
            bg = itemView.findViewById(R.id.bgView);
            name = itemView.findViewById(R.id.name);
            size = itemView.findViewById(R.id.size);
            desc = itemView.findViewById(R.id.desc);
            sid = itemView.findViewById(R.id.sid);
            lich = itemView.findViewById(R.id.lich);
            icon = itemView.findViewById(R.id.icon);
            more = itemView.findViewById(R.id.more);
            sl = itemView.findViewById(R.id.sl);

        }
    }
}
