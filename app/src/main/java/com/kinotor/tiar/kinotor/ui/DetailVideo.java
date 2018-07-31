package com.kinotor.tiar.kinotor.ui;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.parser.GetLocation;
import com.kinotor.tiar.kinotor.parser.ParserAmcet;
import com.kinotor.tiar.kinotor.parser.ParserHtml;
import com.kinotor.tiar.kinotor.parser.ParserKinoFS;
import com.kinotor.tiar.kinotor.parser.animevost.AnimevostSeries;
import com.kinotor.tiar.kinotor.parser.animevost.ParserAnimevost;
import com.kinotor.tiar.kinotor.parser.video.hdgo.HdgoIframe;
import com.kinotor.tiar.kinotor.parser.video.hdgo.ParserHdgo;
import com.kinotor.tiar.kinotor.parser.video.kinosha.ParserKinosha;
import com.kinotor.tiar.kinotor.parser.video.moonwalk.ParserMoonwalk;
import com.kinotor.tiar.kinotor.parser.video.trailer.ParserTrailer;
import com.kinotor.tiar.kinotor.utils.AdapterVideo;
import com.kinotor.tiar.kinotor.utils.DBHelper;
import com.kinotor.tiar.kinotor.utils.OnTaskCallback;
import com.kinotor.tiar.kinotor.utils.OnTaskLocationCallback;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Tiar on 04.2018.
 */
public class DetailVideo extends Fragment {
    private ItemHtml item;
    private RecyclerView rv;
    private LinearLayout pb;
    private TextView pbText;
    private Set<String> pref_base;
    private String[] vidBaseArr = {"hdgo", "moonwalk", "kinomania"};
    private String vidBase = Arrays.toString(vidBaseArr);

    public DetailVideo() {
    }

    public DetailVideo(ItemHtml item) {
        this.item = item;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_vid, container, false);
        pb = view.findViewById(R.id.vid_pb);
        pbText = view.findViewById(R.id.vid_pb_text);
        rv = view.findViewById(R.id.vid_item_list);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 1));
        if (item != null)
            setVideo(item);
        else {
            if (DetailActivity.url.contains("amcet")) {
                ParserAmcet parserAmcet = new ParserAmcet(DetailActivity.url, null, new ItemHtml(),
                        new OnTaskCallback() {
                            @Override
                            public void OnCompleted(ArrayList<ItemHtml> items, ItemHtml itempath) {
                                setVideo(itempath);
                            }
                        });
                parserAmcet.execute();
            }  else if (DetailActivity.url.contains("kino-fs")) {
                ParserKinoFS parserKinoFS = new ParserKinoFS(DetailActivity.url, null, new ItemHtml(),
                        new OnTaskCallback() {
                            @Override
                            public void OnCompleted(ArrayList<ItemHtml> items, ItemHtml itempath) {
                                setVideo(itempath);
                            }
                        });
                parserKinoFS.execute();
            } else if (DetailActivity.url.contains("animevost")) {
                ParserAnimevost parserAnimevost = new ParserAnimevost(DetailActivity.url, null, new ItemHtml(),
                        new OnTaskCallback() {
                            @Override
                            public void OnCompleted(ArrayList<ItemHtml> items, ItemHtml itempath) {
                                setVideo(itempath);
                            }
                        });
                parserAnimevost.execute();
            } else {
                ParserHtml parserHtml = new ParserHtml(DetailActivity.url, null, new ItemHtml(),
                        new OnTaskCallback() {
                            @Override
                            public void OnCompleted(ArrayList<ItemHtml> items, ItemHtml itempath) {
                                setVideo(itempath);
                            }
                        });
                parserHtml.execute();
            }
        }
        return view;
    }

    public void setVideo(final ItemHtml item) {
        rv.setAdapter(new AdapterVideo(getContext(), item, pb) {
            @Override
            public void update(ItemVideo items, String source) {
                itemAddRv(items, source);
            }

            @Override
            public void reload(ItemVideo items) {
                itemSetRv(items);
            }

            @Override
            public void play(final String[] quality, final String[] url, final String translator, final String s, final String e, final boolean play) {
                pb.setVisibility(View.GONE);
                if (getContext() != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), 2);
                    builder.setTitle("Выберите качество").setItems(quality, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Log.d("DetailVideo", "onClick: " + url.toString());
                            if (url[i].contains("cdn") || url[i].contains("[hdgo]")) {
                                if (url[i].contains("[hdgo]"))
                                    url[i] = url[i].replace("[hdgo]", "");
                                pb.setVisibility(View.VISIBLE);
                                final int j = i;
                                GetLocation click = new GetLocation(url[i], new OnTaskLocationCallback() {
                                    @Override
                                    public void OnCompleted(String location) {
                                        videoIntent(quality[j], location, translator, s, e, play);
                                    }
                                });
                                click.execute();
                            } else videoIntent(quality[i], url[i], translator, s, e, play);
                        }
                    });
                    builder.create().show();
                }
            }
        });
        HashSet<String> def = new HashSet<>(Arrays.asList(vidBaseArr));
        pref_base = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getStringSet("base_video", def);
        vidBaseArr = pref_base.toArray(new String[pref_base.size()]);
        vidBase = pref_base.toString();

        pbText.setText("Поиск: " + "0 из " + pref_base.size());

        if (pref_base.contains("kinomania")) {
            ParserTrailer parserTrailer = new ParserTrailer(item, new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    itemAddRv(items, "kinomania");
                }
            });
            parserTrailer.execute();
        }
        if (item.getIframe(0).contains("hdgo")){
            pb.setVisibility(View.VISIBLE);
            HdgoIframe getIframe = new HdgoIframe(item, true, new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    itemAddRv(items, "iframe");
                }
            });
            getIframe.execute();
        } else if (DetailActivity.url.contains("animevost")){
            pb.setVisibility(View.VISIBLE);
            AnimevostSeries getList = new AnimevostSeries(item, true, new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    itemAddRv(items, "animevost");
                }
            });
            getList.execute();
        }
        //BASE
        if (pref_base.contains("kinosha")){
            pb.setVisibility(View.VISIBLE);
            ParserKinosha getList = new ParserKinosha(item, new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    itemAddRv(items, "kinosha");
                }
            });
            getList.execute();
        }
        if (pref_base.contains("moonwalk")) {
            pb.setVisibility(View.VISIBLE);
            ParserMoonwalk getList = new ParserMoonwalk(item, new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    itemAddRv(items, "moonwalk");
                }
            });
            getList.execute();
        }
        if (pref_base.contains("hdgo")) {
            pb.setVisibility(View.VISIBLE);
            ParserHdgo getList = new ParserHdgo(item, new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    itemAddRv(items, "hdgo");
                }
            });
            getList.execute();
        }
        if (pref_base.isEmpty())
            pb.setVisibility(View.GONE);
    }

    private void videoIntent(String q, String url, String translator, String s, String e, boolean play) {
        Log.d("DetailVideo", "videoIntent: " + url);
        pb.setVisibility(View.GONE);
        String t = item.getTitle(0).contains("(") ? item.getTitle(0).split("\\(")[0] :
                item.getTitle(0);
        String title = t.trim() + " (" + item.getDate(0).trim() + ")";
        if (item.getType(0).contains("serial")) {
            title = t.trim();
            if (!s.contains("error") && !s.trim().equals("0"))
                title = title + " / s" + s;
            if (!e.contains("error") && !e.trim().equals("0"))
                title = title + " / e" + e;
            if (s.contains("error") && e.contains("error"))
                title = title + " " + item.getDate(0);
        }
        if (play) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.trim()));
            if (url.endsWith(".mp4")) intent.setDataAndType(Uri.parse(url.trim()), "video/mp4");
            else intent.setDataAndType(Uri.parse(url.trim()), "video/*");
            intent.putExtra("title", title);

            PackageManager packageManager = getContext().getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);

            Log.d("play", title + " " + url);
            if (activities.size() > 0)
                getContext().startActivity(intent);
            else {
//                videoIntent(url, translator, s, e, false);
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url.trim()));
                List<ResolveInfo> a = packageManager.queryIntentActivities(i, 0);
                Log.d("download", title + " " + url);
                if (a.size() > 0)
                    getContext().startActivity(intent);
            }
        } else {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

            if (q.contains(" "))
                q = q.trim().split(" ")[0];
            String nameOfFile = title.trim().replace(" ", "_") + "." + q + "p.mp4";
            //set title for notification in status_bar
            request.setTitle(nameOfFile);
            //flag for if you want to show notification in status or not
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return;
            } else {
                // Location permission has been granted, continue as usual.
                request.setDestinationInExternalPublicDir("/Download/KinoTor/",
                        nameOfFile);
                DownloadManager downloadManager = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                if (downloadManager != null) {
                    downloadManager.enqueue(request);
                    Toast.makeText(getContext(),
                            "Загрузка начата...",
                            Toast.LENGTH_SHORT).show();
                }

                Log.d("download", title + "|" + url);
            }
        }

        DBHelper dbHelper = new DBHelper(getContext());
        if (!dbHelper.getRepeatWatch(3, item.getTitle(0).trim(),
                translator.trim(), s.trim(), e.trim())) {
            dbHelper.Write();
            dbHelper.insertWatch(item.getTitle(0).trim(), translator.trim(), s, e);
            rv.getRecycledViewPool().clear();
            rv.getAdapter().notifyDataSetChanged();
        }

    }

    private void itemSetRv(ItemVideo items) {
        pb.setVisibility(View.GONE);
        ((AdapterVideo) rv.getAdapter()).setItems(items);
        rv.getRecycledViewPool().clear();
        rv.getAdapter().notifyDataSetChanged();
    }

    private void itemAddRv(ItemVideo items, String source) {
        vidBase = vidBase.replace(source, "").replace(" ", "")
                .replace(",,", "").replace("[", "")
                .replace("]", "").trim();
        if (vidBase.startsWith(",")) vidBase = vidBase.substring(1);
        if (vidBase.endsWith(",")) vidBase = vidBase.substring(0, vidBase.length()-1);
        pbText.setText("Поиск: " + (pref_base.size() - vidBase.split(",").length) +
                " из " + pref_base.size());
        if (vidBase.contains("hdgo") || vidBase.contains("moonwalk") || vidBase.contains("kinosha")
                || vidBase.contains("kinomania") || vidBase.contains("kinokiwi")) {
            pb.setVisibility(View.VISIBLE);
        } else {
            pb.setVisibility(View.GONE);
            pbText.setText("Подождите...");
            vidBase = Arrays.toString(vidBaseArr);
        }
        ((AdapterVideo) rv.getAdapter()).addItems(items);
        rv.getRecycledViewPool().clear();
        rv.getAdapter().notifyDataSetChanged();
    }
}