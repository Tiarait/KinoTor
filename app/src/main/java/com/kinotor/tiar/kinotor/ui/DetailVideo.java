package com.kinotor.tiar.kinotor.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.parser.GetLocation;
import com.kinotor.tiar.kinotor.parser.ParserAmcet;
import com.kinotor.tiar.kinotor.parser.ParserHtml;
import com.kinotor.tiar.kinotor.parser.animevost.AnimevostSeries;
import com.kinotor.tiar.kinotor.parser.animevost.ParserAnimevost;
import com.kinotor.tiar.kinotor.parser.hdgo.HdgoIframe;
import com.kinotor.tiar.kinotor.parser.hdgo.ParserHdgo;
import com.kinotor.tiar.kinotor.parser.moonwalk.ParserMoonwalk;
import com.kinotor.tiar.kinotor.parser.trailer.ParserTrailer;
import com.kinotor.tiar.kinotor.utils.AdapterVideo;
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
            public void update(ItemVideo items) {
                itemAddRv(items);
            }

            @Override
            public void reload(ItemVideo items) {
                itemSetRv(items);
            }

            @Override
            public void play(String[] quality, final String[] url, final String s, final String e, final boolean play) {
                pb.setVisibility(View.GONE);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), 2);
                builder.setTitle("Выберите качество").setItems(quality, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!url[i].equals("error")) {
                            if (url[i].contains("cdn") || url[i].contains("[hdgo]")) {
                                if (url[i].contains("[hdgo]"))
                                    url[i] = url[i].replace("[hdgo]", "");
                                pb.setVisibility(View.VISIBLE);
                                GetLocation click = new GetLocation(url[i], new OnTaskLocationCallback() {
                                    @Override
                                    public void OnCompleted(String location) {
                                        videoIntent(location, s, e, play);
                                    }
                                });
                                click.execute();
                            } else {
                                videoIntent(url[i], s, e, play);
                            }
                        } else dialogInterface.cancel();
                    }
                });
                builder.create().show();
            }
        });
        HashSet<String> def = new HashSet<>(Arrays.asList("hdgo", "moonwalk"));
        Set<String> pref_base = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getStringSet("base_video", def);

        ParserTrailer parserTrailer = new ParserTrailer(item, new OnTaskVideoCallback() {
            @Override
            public void OnCompleted(ItemVideo items) {
                itemAddRv(items);
            }
        });
        parserTrailer.execute();
        if (item.getIframe(0).contains("hdgo")){
            Log.d("qwer", "setVideo: " + item.getIframe(0));
            pb.setVisibility(View.VISIBLE);
            HdgoIframe getIframe = new HdgoIframe(item, true, new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    itemAddRv(items);
                }
            });
            getIframe.execute();
        } else if (DetailActivity.url.contains("animevost")){
            pb.setVisibility(View.VISIBLE);
            AnimevostSeries getList = new AnimevostSeries(item, true, new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    itemAddRv(items);
                }
            });
            getList.execute();
        }
        if (pref_base.contains("moonwalk")) {
            pb.setVisibility(View.VISIBLE);
            ParserMoonwalk getList = new ParserMoonwalk(item, new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    itemAddRv(items);
                }
            });
            getList.execute();
        }
        if (pref_base.contains("hdgo")) {
            pb.setVisibility(View.VISIBLE);
            ParserHdgo getList = new ParserHdgo(item, new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    itemAddRv(items);
                }
            });
            getList.execute();
        }
        if (pref_base.isEmpty())
            pb.setVisibility(View.GONE);
    }

    private void videoIntent(String url, String s, String e, boolean play) {
        pb.setVisibility(View.GONE);
        String t = item.getTitle(0).contains("(") ? item.getTitle(0).split("\\(")[0] :
                item.getTitle(0);
        String title = t.trim() + " " + item.getDate(0);
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
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.parse(url), "video/*");
            intent.putExtra("title", title);

            PackageManager packageManager = getContext().getPackageManager();
            List<ApplicationInfo> activities = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
            if (activities.size() > 0)
                getContext().startActivity(intent);
            Log.d("play", title + " " + url);
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            getContext().startActivity(intent);
            Log.d("download", title + " " + url);
        }
    }

    private void itemSetRv(ItemVideo items) {
        pb.setVisibility(View.GONE);
        ((AdapterVideo) rv.getAdapter()).setItems(items);
        rv.getRecycledViewPool().clear();
        rv.getAdapter().notifyDataSetChanged();
    }

    private void itemAddRv(ItemVideo items) {
        pb.setVisibility(View.GONE);
        ((AdapterVideo) rv.getAdapter()).addItems(items);
        rv.getRecycledViewPool().clear();
        rv.getAdapter().notifyDataSetChanged();
    }
}