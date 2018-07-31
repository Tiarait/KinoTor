package com.kinotor.tiar.kinotor.updater;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.kinotor.tiar.kinotor.R;

import java.io.File;

/**
 * Created by Tiar on 02.01.2018.
 */
public class UpdateDialog extends DialogFragment implements View.OnClickListener {
    long downloadId = 45;
    String url;
    Context context;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        url = getTag();
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setStyle(STYLE_NO_TITLE, 0);
        View v = inflater.inflate(R.layout.dialog_update, null);
        v.findViewById(R.id.yes).setOnClickListener(this);
        v.findViewById(R.id.no).setOnClickListener(this);
        return v;
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yes:

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);

//                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
//                        PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
//                    return;
//                } else {
//                    downlaodFile(url);
////                    dismiss();
//                }
                break;
            case R.id.no:
                dismiss();
                break;
        }
    }

    private void downlaodFile(String url){
        final DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();
        final String nameOfFile = url.split("/")[url.split("/").length-1].trim();
        //set title for notification in status_bar
        request.setTitle(nameOfFile);
        //flag for if you want to show notification in status or not
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // Location permission has been granted, continue as usual.
        //"/Download/KinoTor/"
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                nameOfFile);
        final DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        if (dm != null) {
            BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(final Context context, final Intent intent) {
                    long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    Cursor cursor = dm.query( new DownloadManager.Query().setFilterById(referenceId) );

                    if (cursor.moveToFirst()
                            && cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            == DownloadManager.STATUS_SUCCESSFUL) {
                        String filePath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                        Toast.makeText(getActivity(),
                                filePath,
                                Toast.LENGTH_LONG).show();
                        try {
                            Uri apkUri = Uri.parse(filePath);
                            Intent ii = new Intent(Intent.ACTION_VIEW);
                            ii.setDataAndType(apkUri, "application/vnd.android.package-archive");
                            ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getActivity().startActivity(intent);
                        } catch (Exception ignored) {}
                    }
                    cursor.close();
//                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), nameOfFile);
//                    Log.e("Fragment2", String.valueOf(Uri.fromFile(file)));
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                        Uri apkUri = FileProvider.getUriForFile(getActivity(),
//                                context.getApplicationContext().getPackageName() + ".provider", file);
//                        Intent i = new Intent(Intent.ACTION_INSTALL_PACKAGE);
//                        i.setData(apkUri);
//                        i.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                        getActivity().startActivity(i);
//                    } else {
//                        Uri apkUri = Uri.fromFile(file);
//                        Intent ii = new Intent(Intent.ACTION_VIEW);
//                        ii.setDataAndType(apkUri, "application/vnd.android.package-archive");
//                        ii.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        getActivity().startActivity(intent);
//                    }
                }
            };
            context.registerReceiver(downloadCompleteReceiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            dm.enqueue(request);
            Toast.makeText(getActivity(),
                    "Загрузка "+nameOfFile+" начата...",
                    Toast.LENGTH_SHORT).show();
        }

        Log.d("download", nameOfFile + "|" + url);

    }

    private void openFile(String file) {
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(new File(file)), "application/apk");
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(getActivity(),"No application detected.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity;
    }

    public void onCancel(DialogInterface dialog) {
        dismiss();
    }
}
