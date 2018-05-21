package com.kinotor.tiar.kinotor.ui;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemTorrent;
import com.kinotor.tiar.kinotor.parser.ParserAmcet;
import com.kinotor.tiar.kinotor.parser.ParserHtml;
import com.kinotor.tiar.kinotor.parser.animevost.ParserAnimevost;
import com.kinotor.tiar.kinotor.parser.torrents.Freerutor;
import com.kinotor.tiar.kinotor.parser.torrents.Tparser;
import com.kinotor.tiar.kinotor.parser.torrents.Zooqle;
import com.kinotor.tiar.kinotor.utils.AdapterTorrents;
import com.kinotor.tiar.kinotor.utils.OnTaskCallback;
import com.kinotor.tiar.kinotor.utils.OnTaskTorrentCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Tiar on 04.2018.
 */
public class DetailTorrents extends Fragment {
    private ItemHtml item;
    private RecyclerView rv;
    private LinearLayout pb;
    private String[] torBaseArr = {"zooqle.com", "underverse.me", "kinozal.tv"};
    private String torBase = Arrays.toString(torBaseArr);


    public DetailTorrents() {
    }

    public DetailTorrents(ItemHtml item) {
        this.item = item;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_tor, container, false);
        pb = view.findViewById(R.id.tor_pb);
        //pbText = view.findViewById(R.id.pb_text);
        rv = view.findViewById(R.id.tor_item_list);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 1));
        if (item != null)
            setTorrents(item);
        else {
            if (DetailActivity.url.contains("amcet")) {
                ParserAmcet parserAmcet = new ParserAmcet(DetailActivity.url, null, new ItemHtml(),
                        new OnTaskCallback() {
                            @Override
                            public void OnCompleted(ArrayList<ItemHtml> items, ItemHtml itempath) {
                                setTorrents(itempath);
                            }
                        });
                parserAmcet.execute();
            } else if (DetailActivity.url.contains("animevost")) {
                ParserAnimevost parserAnimevost = new ParserAnimevost(DetailActivity.url, null, new ItemHtml(),
                        new OnTaskCallback() {
                            @Override
                            public void OnCompleted(ArrayList<ItemHtml> items, ItemHtml itempath) {
                                setTorrents(itempath);
                            }
                        });
                parserAnimevost.execute();
            } else {
                ParserHtml parserHtml = new ParserHtml(DetailActivity.url, null, new ItemHtml(),
                        new OnTaskCallback() {
                            @Override
                            public void OnCompleted(ArrayList<ItemHtml> items, ItemHtml itempath) {
                                setTorrents(itempath);
                            }
                        });
                parserHtml.execute();
            }
        }
        return view;
    }

    public void setTorrents(ItemHtml torrents) {
        this.item = torrents;
        ItemTorrent torrent = new ItemTorrent();
        if (item.tortitle.size() > 0)
            torrent.addHtmlItems(item);
        rv.setAdapter(new AdapterTorrents(getContext(), torrent));

        HashSet<String> def = new HashSet<>(Arrays.asList(torBaseArr));
        Set<String> pref_base = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getStringSet("base_tparser", def);
        torBase = pref_base.toString();
        if (pref_base.contains("zooqle.com")) {
            pb.setVisibility(View.VISIBLE);
            Zooqle zooqle = new Zooqle(item, new OnTaskTorrentCallback() {
                @Override
                public void OnCompleted(ItemTorrent item) {
                    itemAddRv(item, "zooqle.com");
                }
            });
            zooqle.execute();
        }
        if (pref_base.contains("rutor.info")) {
            pb.setVisibility(View.VISIBLE);
            Tparser tparser = new Tparser(item, "rutor.info", new OnTaskTorrentCallback() {
                @Override
                public void OnCompleted(ItemTorrent item) {
                    itemAddRv(item, "rutor.info");
                }
            });
            tparser.execute();
        }
        if (pref_base.contains("freerutor.me")) {
            pb.setVisibility(View.VISIBLE);
            Freerutor freerutor = new Freerutor(item, new OnTaskTorrentCallback() {
                @Override
                public void OnCompleted(ItemTorrent item) {
                    itemAddRv(item, "freerutor.me");
                }
            });
            freerutor.execute();
        }
        if (pref_base.contains("rutracker.org")) {
            pb.setVisibility(View.VISIBLE);
            Tparser tparser = new Tparser(item, "rutracker.org", new OnTaskTorrentCallback() {
                @Override
                public void OnCompleted(ItemTorrent item) {
                    itemAddRv(item, "rutracker.org");
                }
            });
            tparser.execute();
        }
        if (pref_base.contains("underverse.me")) {
            pb.setVisibility(View.VISIBLE);
            Tparser tparser = new Tparser(item, "underverse.me", new OnTaskTorrentCallback() {
                @Override
                public void OnCompleted(ItemTorrent item) {
                    itemAddRv(item, "underverse.me");
                }
            });
            tparser.execute();
        }
        if (pref_base.contains("kinozal.tv")) {
            pb.setVisibility(View.VISIBLE);
            Tparser tparser = new Tparser(item, "kinozal.tv", new OnTaskTorrentCallback() {
                @Override
                public void OnCompleted(ItemTorrent item) {
                    itemAddRv(item, "kinozal.tv");
                }
            });
            tparser.execute();
        }
        if (pref_base.isEmpty())
            pb.setVisibility(View.GONE);
    }
    private void itemAddRv(ItemTorrent items, String source) {
        torBase = torBase.replace(source, "");
        if (torBase.contains("rutor.info") || torBase.contains("zooqle.com") ||
                torBase.contains("rutracker.org") || torBase.contains("underverse.me")
                || torBase.contains("kinozal.tv") || torBase.contains("freerutor.me")) {
            pb.setVisibility(View.VISIBLE);
        } else pb.setVisibility(View.GONE);
        pb.setVisibility(View.GONE);
        ((AdapterTorrents) rv.getAdapter()).addItems(items);
        rv.getAdapter().notifyDataSetChanged();
    }
}