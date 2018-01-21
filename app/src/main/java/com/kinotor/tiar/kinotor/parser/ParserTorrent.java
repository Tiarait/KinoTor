package com.kinotor.tiar.kinotor.parser;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemDetail;
import com.kinotor.tiar.kinotor.ui.DetailActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashSet;
import java.util.Set;

import static com.kinotor.tiar.kinotor.parser.ParserHtml.itemDetail;

/**
 * Created by Tiar on 16.01.2018.
 */

public class ParserTorrent extends AsyncTask<Void, Void, Void> {
    private String title;
    @SuppressLint("StaticFieldLeak")
    private RecyclerView rv;
    @SuppressLint("StaticFieldLeak")
    private LinearLayout pb;
    private SharedPreferences preference;
    private Set<String> pref_base;

    public ParserTorrent(String title, RecyclerView rv) {
        this.title = title.replaceAll(" ", "%20");
        this.rv = rv;
    }

    @Override
    protected void onPreExecute() {
        HashSet<String> def = new HashSet<>();
        def.add("rutor.info");
        def.add("rutracker.org");
        def.add("underverse.me");
        def.add("kinozal.tv");
        preference = PreferenceManager.getDefaultSharedPreferences(DetailActivity.fragm_vid.getContext());
        pref_base = preference.getStringSet("base_tparser", def);
        pb = (LinearLayout) DetailActivity.fragm_tor.findViewById(R.id.tor_pb);
        pb.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
//        ((AdapterCatalog) rv.getAdapter()).setHtmlItems(items);
        rv.getAdapter().notifyDataSetChanged();
        pb.setVisibility(View.GONE);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (pref_base.contains("rutor.info"))
        Tparser(Getdata("http://js1.tparser.org/js1/2.tor.php?callback=one&jsonpx=" + title));
        if (pref_base.contains("rutracker.org"))
        Tparser(Getdata("http://js3.tparser.org/js3/6.tor.php?callback=one&jsonpx=" + title));
        if (pref_base.contains("underverse.me"))
        Tparser(Getdata("http://js5.tparser.org/js5/9.tor.php?callback=one&jsonpx=" + title));
        if (pref_base.contains("kinozal.tv"))
        Tparser(Getdata("http://js5.tparser.org/js5/10.tor.php?callback=one&jsonpx=" + title));

        //thepiratebay.org
//        Tparser(Getdata("http://js4.tparser.org/js4/8.tor.php?callback=one&jsonpx=" + title));
//        Tparser(Getdata("http://js2.tparser.org/js2/4.tor.php?callback=one&jsonpx=" + title));

        return null;
    }

    private void Tparser(Document data) {
        String all;
        String[] list;
        if (data != null) {
            if (itemDetail == null)
                itemDetail = new ItemDetail();
            if (data.text().contains("sr':[{") && !data.text().contains("'error'")) {
                all = data.text().split("sr':\\[")[1].split("]\\}")[0];
                list = all.split("\\}, \\{");
                for (int i = 0; i < list.length; i ++) {
                    if (list[i].split("z':'")[1].split("',")[0].equals("1") &&
                            !itemDetail.torrents.contains(list[i].split("link':'")[1].split("'")[0])) {
                        itemDetail.setTor_name(list[i].split("name':'")[1].split("'")[0]);
                        itemDetail.setTorrents(list[i].split("link':'")[1].split("'")[0]);
                        itemDetail.setTor_size(list[i].split("size':'")[1].split("'")[0] +
                                " " + list[i].split("t':'")[1].split("',")[0]);
                        String z = list[i].split("link':'")[1].split("',")[0]
                                .contains("kinozal.tv") ? "2" : "1";
                        itemDetail.setTor_magnet("http://tparser.org/magnet.php?t=" + z +
                                list[i].split("img':'")[1].split("',")[0] +
                                list[i].split("d':'")[1].split("',")[0]);
                        itemDetail.setTor_sid(list[i].split("s':'")[1].split("'")[0]);
                        itemDetail.setTor_lich(list[i].split("l':'")[1].split("'")[0]);
                        itemDetail.setTor_content(list[i].split("link':'")[1].split("'")[0]
                                .split("://")[1].split("/")[0]);
                    }
                }
            }
        }
    }

    private Document Getdata(String url) {
        try {
            Document htmlDoc;
            htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(10000).ignoreContentType(true).get();
            Log.d("mydebug","get connected to " + url);
            return htmlDoc;
        } catch (Exception e) {
            Log.d("mydebug","connected false to " + url);
            e.printStackTrace();
            return null;
        }
    }
}
