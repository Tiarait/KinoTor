package com.kinotor.tiar.kinotor.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.kinotor.tiar.kinotor.R;


import java.util.List;

public class Trailer {
    public void play(final String[] quality, final String[] url, final String title, final String source, final Context context) {
        if (url.length > 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
            builder.setTitle("Выберите качество").setItems(quality, (dialogInterface, i) -> {
                if (!url[i].contains("error")) {
                    preIntent(url[i], quality[i], title, source, context);
                } else dialogInterface.dismiss();
            }).setNegativeButton("Отмена", (dialogInterface, i) -> {
                dialogInterface.dismiss();
            });
            builder.create().show();
        } else if (url.length == 0) {
            Toast.makeText(context, "Ошибка url is empty", Toast.LENGTH_SHORT).show();
        } else {
            preIntent(url[0], quality[0], title, source, context);
        }
    }

    private void preIntent(String url, String q, String title, String source, Context context) {
        if (url.equals("error") || q.contains("недоступно")) {
            Toast.makeText(context, "Ошибка " + q, Toast.LENGTH_SHORT).show();
        } else {
            if (q.toLowerCase().contains("ссылка") || q.toLowerCase().contains("youtube")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                context.startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url.trim()));
                intent.putExtra("title", title);
                intent.putExtra("filename", title);
                intent.putExtra("forcename", title);
                intent.putExtra("headers", new String[]{
                        "User-Agent", "Mozilla compatible/1.0",
                        "Referer", Utils.getUrl(source)});
                Intent chooser = Intent.createChooser(intent, title);

                String playV = PreferenceManager.getDefaultSharedPreferences(context)
                        .getString("play_video_p", "default");

                switch (playV) {
                    case "mxplayer":
                        intent.setDataAndType(Uri.parse(url.trim()), "application/com.mxtech.videoplayer.pro|application/com.mxtech.videoplayer.ad");
                        break;
                    case "vlcplayer":
                        intent.setPackage("org.videolan.vlc");
                        intent.setDataAndType(Uri.parse(url.trim()), "video/*");
                        break;
                    case "vimuplayer":
                        intent.setPackage("net.gtvbox.videoplayer");
                        intent.setDataAndType(Uri.parse(url.trim()), "video/*");
                        break;
                    default:
                        intent.setDataAndType(Uri.parse(url.trim()), "video/*");
                        break;
                }

                PackageManager packageManager = context.getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                if (playV.equals("other")) {
                    intent = chooser;
                } else {
                    if (activities.size() == 0) {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url.trim()));
                        List<ResolveInfo> a = packageManager.queryIntentActivities(i, 0);
                    }
                }

                boolean started = false;
                switch (playV) {
                    case "mxplayer":
                        for (ResolveInfo info : activities) {
                            ActivityInfo activityInfo = info.activityInfo;
                            if (activityInfo.packageName.startsWith("com.mxtech.videoplayer.")) {
                                context.startActivity(intent.setClassName(activityInfo.packageName, activityInfo.name));
                                started = true;
                                break;
                            }
                        }
                        if (!started) {
                            try {
                                context.startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(context, "Mx плеер не найден", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case "vlcplayer":
                        for (ResolveInfo info : activities) {
                            ActivityInfo activityInfo = info.activityInfo;
                            if (activityInfo.packageName.startsWith("org.videolan.vlc")) {
                                context.startActivity(intent.setClassName(activityInfo.packageName, activityInfo.name));
                                started = true;
                                break;
                            }
                        }
                        if (!started) {
                            try {
                                context.startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(context, "Vlc плеер не найден", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case "vimuplayer":
                        for (ResolveInfo info : activities) {
                            ActivityInfo activityInfo = info.activityInfo;
                            if (activityInfo.packageName.startsWith("net.gtvbox.videoplayer")) {
                                context.startActivity(intent.setClassName(activityInfo.packageName, activityInfo.name));
                                started = true;
                                break;
                            }
                        }
                        if (!started) {
                            try {
                                context.startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(context, "Vimu плеер не найден", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    default:
                        context.startActivity(intent);
                        break;
                }
            }
        }
    }
}
