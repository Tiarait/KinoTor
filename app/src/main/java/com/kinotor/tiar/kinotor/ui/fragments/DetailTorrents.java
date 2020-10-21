package com.kinotor.tiar.kinotor.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemTorrent;
import com.kinotor.tiar.kinotor.parser.torrents.AnidubTr;
import com.kinotor.tiar.kinotor.parser.torrents.Bitru;
import com.kinotor.tiar.kinotor.parser.torrents.Freerutor;
import com.kinotor.tiar.kinotor.parser.torrents.GreenTea;
import com.kinotor.tiar.kinotor.parser.torrents.Hurtom;
import com.kinotor.tiar.kinotor.parser.torrents.Kinozal;
import com.kinotor.tiar.kinotor.parser.torrents.Megapeer;
import com.kinotor.tiar.kinotor.parser.torrents.NNM;
import com.kinotor.tiar.kinotor.parser.torrents.PiratBit;
import com.kinotor.tiar.kinotor.parser.torrents.Rutor;
import com.kinotor.tiar.kinotor.parser.torrents.Rutracker;
import com.kinotor.tiar.kinotor.parser.torrents.Torlook;
import com.kinotor.tiar.kinotor.parser.torrents.Tparser;
import com.kinotor.tiar.kinotor.parser.torrents.Yohoho;
import com.kinotor.tiar.kinotor.ui.DetailActivity;
import com.kinotor.tiar.kinotor.utils.DBHelper;
import com.kinotor.tiar.kinotor.utils.adapters.AdapterTorrents;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Tiar on 04.2018.
 */
public class DetailTorrents extends Fragment {
    private static ItemHtml item;
    private RecyclerView rv;
    private static String type = "";
    private LinearLayout pb;
    private TextView pbText;
    private Set<String> pref_base;
    private String[] torBaseArr = {"tparser", "freerutor", "nnm", "bitru", "yohoho", "rutor(orig)", "megapeer",
            "piratbit", "kinozal", "hurtom", "torlook", "greentea","rutracker"};
    private String torBase = Arrays.toString(torBaseArr);

    public static DetailTorrents newInstance(ItemHtml items, String types) {
        item = items;
        type = types;
        return new DetailTorrents();
    }
    public DetailTorrents() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_tor, container, false);
        pb = view.findViewById(R.id.tor_pb);
        pbText = view.findViewById(R.id.pb_text);
        rv = view.findViewById(R.id.tor_item_list);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 1));

        DBHelper dbHelper = new DBHelper(getContext());
        if (type.equals("favor")) {
            pb.setVisibility(View.GONE);
            rv.setAdapter(new AdapterTorrents(getContext(), type));
        } else  if (item != null)
            setTorrents(item);
        else {
            if (dbHelper.getRepeatCache(DetailActivity.url)) {
                setTorrents(dbHelper.getDbItemsCache(DetailActivity.url));
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
        if (getContext() != null) {
            pref_base = PreferenceManager.getDefaultSharedPreferences(getContext())
                    .getStringSet("base_tparser", def);
        } else pref_base = def;
        torBase = pref_base.toString();

        pbText.setText("Поиск: " + "0 из " + pref_base.size());
        if (pref_base.contains("rutor(orig)")) {
            pb.setVisibility(View.VISIBLE);
            Rutor rutor = new Rutor(item, item -> itemAddRv(item, "rutor(orig)"));
            rutor.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (pref_base.contains("tparser")) {
            pb.setVisibility(View.VISIBLE);
            Tparser tparser = new Tparser(item,  item ->
                    itemAddRv(item, "tparser"));
            tparser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (pref_base.contains("nnm")) {
            pb.setVisibility(View.VISIBLE);
            NNM nnm = new NNM(item, item -> itemAddRv(item, "nnm"));
            nnm.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (pref_base.contains("freerutor")) {
            pb.setVisibility(View.VISIBLE);
            Freerutor freerutor = new Freerutor(item, item ->
                    itemAddRv(item, "freerutor"));
            freerutor.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (pref_base.contains("kinozal")) {
            pb.setVisibility(View.VISIBLE);
            Kinozal kinozal = new Kinozal(item, item -> itemAddRv(item, "kinozal"));
            kinozal.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (pref_base.contains("bitru")) {
            pb.setVisibility(View.VISIBLE);
            Bitru bitru = new Bitru(item, item -> itemAddRv(item, "bitru"));
            bitru.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (pref_base.contains("yohoho")) {
            pb.setVisibility(View.VISIBLE);
            Yohoho yohoho = new Yohoho(item, item -> itemAddRv(item, "yohoho"));
            yohoho.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (pref_base.contains("megapeer")) {
            pb.setVisibility(View.VISIBLE);
            Megapeer megapeer = new Megapeer(item, item -> itemAddRv(item, "megapeer"));
            megapeer.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (pref_base.contains("piratbit")) {
            pb.setVisibility(View.VISIBLE);
            PiratBit piratBit = new PiratBit(item, item -> itemAddRv(item, "piratbit"));
            piratBit.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (pref_base.contains("hurtom")) {
            pb.setVisibility(View.VISIBLE);
            Hurtom hurtom = new Hurtom(item, item -> itemAddRv(item, "hurtom"));
            hurtom.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (pref_base.contains("anidub")) {
            pb.setVisibility(View.VISIBLE);
            AnidubTr anidub = new AnidubTr(item, item -> itemAddRv(item, "anidub"));
            anidub.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (pref_base.contains("torlook")) {
            pb.setVisibility(View.VISIBLE);
            Torlook torlook = new Torlook(item, item -> itemAddRv(item, "torlook"));
            torlook.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (pref_base.contains("greentea")) {
            pb.setVisibility(View.VISIBLE);
            GreenTea greentea = new GreenTea(item, item -> itemAddRv(item, "greentea"));
            greentea.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (pref_base.contains("rutracker")) {
            pb.setVisibility(View.VISIBLE);
            Rutracker rutracker = new Rutracker(item, item -> itemAddRv(item, "rutracker"));
            rutracker.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        if (pref_base.toString().replace("[", "")
                .replace("]", "").trim().isEmpty())
            pb.setVisibility(View.GONE);
    }
    private void itemAddRv(ItemTorrent items, String source) {
        torBase = torBase.replace(source, "").replace(" ", "")
                .replace(",,", "").replace("[", "")
                .replace("]", "").trim();
        if (torBase.startsWith(",")) torBase = torBase.substring(1);
        if (torBase.endsWith(",")) torBase = torBase.substring(0, torBase.length()-1);
        pbText.setText("Поиск: " + (pref_base.size() - torBase.split(",").length) +
                " из " + pref_base.size());
//        Log.e("test", "itemAddRv: "+torBase);
        if (torBase.contains("tparser")
                || torBase.contains("freerutor")
                || torBase.contains("yohoho")
                || torBase.contains("bitru")
                || torBase.contains("nnm")
                || torBase.contains("rutor(orig)")
                || torBase.contains("megapeer")
                || torBase.contains("piratbit")
                || torBase.contains("kinozal")
                || torBase.contains("hurtom")
                || torBase.contains("anidub")
                || torBase.contains("torlook")
                || torBase.contains("greentea")
                || torBase.contains("rutracker")) {
            pb.setVisibility(View.VISIBLE);
        } else {
            pb.setVisibility(View.GONE);
            pbText.setText("Подождите...");
        }
        ((AdapterTorrents) rv.getAdapter()).addItems(items);
        rv.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            if (getActivity().findViewById(R.id.detail_pb) != null)
                getActivity().findViewById(R.id.detail_pb).setVisibility(View.GONE);
        }
    }
}