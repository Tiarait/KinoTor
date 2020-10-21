package com.kinotor.tiar.kinotor.updater;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kinotor.tiar.kinotor.BuildConfig;
import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.Statics;

import java.util.Objects;

/**
 * Created by Tiar on 02.01.2018.
 */
public class UpdateDialog extends DialogFragment {
    String url;
//    Uri uri;
    Context context;
    Button ok;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        url = getTag();
        if (getDialog().getWindow() != null)
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setStyle(STYLE_NO_TITLE, 0);
        @SuppressLint("InflateParams")
        View v = inflater.inflate(R.layout.dialog_update, null);
        final LinearLayout upd = v.findViewById(R.id.dialog_update);
        final RelativeLayout down = v.findViewById(R.id.dialog_down);
        TextView log = v.findViewById(R.id.dialog_update_log);
        ok = v.findViewById(R.id.ok);
        log.setText(Statics.newVerLog);
        upd.setVisibility(View.VISIBLE);
        down.setVisibility(View.GONE);
        v.findViewById(R.id.yes).setOnClickListener(view -> {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            } else {
                upd.setVisibility(View.GONE);
                down.setVisibility(View.VISIBLE);
                updateApp();
            }
        });
        v.findViewById(R.id.url).setVisibility(View.VISIBLE);
        v.findViewById(R.id.url).setOnClickListener(view -> onSite());

        ok.setVisibility(View.GONE);
//        ok.setOnClickListener(view -> startIntent());

        v.findViewById(R.id.no).setOnClickListener(view -> dismiss());
        return v;
    }

    private void onSite(){
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(BuildConfig.SITE_UPD + "/"));
            startActivity(intent);

            dismiss();
        } catch (Exception e) {
            Toast.makeText(this.context, "Не удалось перейти по ссылке", Toast.LENGTH_LONG).show();
            dismiss();
        }
    }

    private void updateApp() {
        String destination = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/";
        String fileName = url.split("/")[url.split("/").length-1].trim();

//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//            StorageManager sm = (StorageManager)context.getSystemService(Context.STORAGE_SERVICE);
//            StorageVolume volume = null;
//            volume = sm.getPrimaryStorageVolume();
//            Intent intent = volume.createAccessIntent(Environment.DIRECTORY_DOWNLOADS);
//            startActivityForResult(intent, Activity.RESULT_OK);
//        }

        destination += fileName;
        Uri uri = Uri.parse("file://" + destination);

        //Delete update file if exists
//        File file = new File(destination);
//        if (file.exists())
//            if (file.delete()) {
//                Log.e("upd", fileName + "was deleted");
//            } else {
//                Log.e("upd", fileName + "not be deleted");
//            }


        // get download service and enqueue file
        final DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        //set downloadmanager
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        request.setVisibleInDownloadsUi(true);
//        request.setNotificationVisibility(View.VISIBLE);
        request.setTitle(fileName);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            if (manager != null) manager.enqueue(request);
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {
                    if (Objects.requireNonNull(intent.getAction()).equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equalsIgnoreCase(intent.getAction())) {
                            DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                            DownloadManager.Query query = new DownloadManager.Query();
                            query.setFilterById(id);
                            Cursor cursor = dm.query(query);
                            if (cursor != null && cursor.moveToFirst()) {
                                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                                    String localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                    Log.e("test", "onReceive: " + localUri);
                                    Intent install = new Intent(Intent.ACTION_VIEW);
                                    install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    install.setDataAndType(Uri.parse(localUri), "application/vnd.android.package-archive");
                                    install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                    ok.setVisibility(View.VISIBLE);
                                    context.startActivity(install);
                                    context.unregisterReceiver(this);
                                }
                                cursor.close();
                            }
                        }
                    }
                }
            };
            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } else {
            request.setDestinationUri(uri);
            if (manager != null) manager.enqueue(request);

            //set BroadcastReceiver to install app when .apk is downloaded
            BroadcastReceiver onComplete = new BroadcastReceiver() {
                public void onReceive(Context ctxt, Intent intent) {
                    if (Objects.requireNonNull(intent.getAction()).equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                        Intent install = new Intent(Intent.ACTION_VIEW);
                        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        install.setDataAndType(uri, "application/vnd.android.package-archive");
                        context.startActivity(install);
                        ok.setVisibility(View.VISIBLE);
                        context.startActivity(install);
                        Log.e("test", "onReceive: "+uri.toString());
                    }
                }
            };
            //register receiver for when .apk download is compete
            context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }
    }

//    private void startIntent() {
//        Intent install = new Intent(Intent.ACTION_VIEW);
////        install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        install.setDataAndType(uri, "application/vnd.android.package-archive");
//        startActivity(install);
//
//        dismiss();
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    public void onCancel(DialogInterface dialog) {
        dismiss();
    }
}
