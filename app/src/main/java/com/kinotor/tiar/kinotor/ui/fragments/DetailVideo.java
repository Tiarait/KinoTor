package com.kinotor.tiar.kinotor.ui.fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.parser.GetKpId;
import com.kinotor.tiar.kinotor.parser.GetLocation;
import com.kinotor.tiar.kinotor.parser.url.ParserKinoliveUrl;
import com.kinotor.tiar.kinotor.parser.video.FanserialsIframe;
import com.kinotor.tiar.kinotor.parser.video.RufilmtvIframe;
import com.kinotor.tiar.kinotor.parser.video.anidub.ParserAnidub;
import com.kinotor.tiar.kinotor.parser.video.animedia.ParserAnimedia;
import com.kinotor.tiar.kinotor.parser.video.animevost.AnimevostSeries;
import com.kinotor.tiar.kinotor.parser.video.animevost.ParserVAnimevost;
import com.kinotor.tiar.kinotor.parser.video.farsihd.FarsihdIframe;
import com.kinotor.tiar.kinotor.parser.video.filmix.ParserFilmix;
import com.kinotor.tiar.kinotor.parser.video.filmix.ParserFilmixAddHist;
import com.kinotor.tiar.kinotor.parser.video.hdgo.HdgoIframe;
import com.kinotor.tiar.kinotor.parser.video.hdgo.ParserHdgo;
import com.kinotor.tiar.kinotor.parser.video.kinodom.KinodomIframeUrl;
import com.kinotor.tiar.kinotor.parser.video.kinodom.ParserKinodom;
import com.kinotor.tiar.kinotor.parser.video.kinohd.KinohdIframe;
import com.kinotor.tiar.kinotor.parser.video.kinolive.ParserKinolive;
import com.kinotor.tiar.kinotor.parser.video.kinopub.KinopubIframeUrl;
import com.kinotor.tiar.kinotor.parser.video.kinopub.ParserKinopubSearch;
import com.kinotor.tiar.kinotor.parser.video.kinosha.ParserKinosha;
import com.kinotor.tiar.kinotor.parser.video.moonwalk.ParserMoonwalk;
import com.kinotor.tiar.kinotor.parser.video.zombiefilm.ParserZombiefilm;
import com.kinotor.tiar.kinotor.ui.reclam.ReclamActivity;
import com.kinotor.tiar.kinotor.utils.DBHelper;
import com.kinotor.tiar.kinotor.utils.Utils;
import com.kinotor.tiar.kinotor.utils.adapters.AdapterVideo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Tiar on 04.2018.
 */
public class DetailVideo extends Fragment {
    private static ItemHtml item;
    private RecyclerView rv;
    private LinearLayout pb;
    private TextView pbText;
    private Set<String> pref_base;
    DBHelper dbHelper;
    private String[] vidBaseArr = {"hdgo", "moonwalk", "kinodom", "filmix", "zombiefilm"};
    private String vidBase = Arrays.toString(vidBaseArr);
    public String rekl;

    public static DetailVideo newInstance(ItemHtml items) {
        item = items;
        return new DetailVideo();
    }
    public DetailVideo() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail_vid, container, false);
        pb = view.findViewById(R.id.vid_pb);
        pbText = view.findViewById(R.id.vid_pb_text);
        rv = view.findViewById(R.id.vid_item_list);
        rv.setLayoutManager(new GridLayoutManager(getContext(), 1));
        Statics.itemsVidVoice = null;
        Statics.itemsVidSeason = null;
        dbHelper = new DBHelper(getContext());
        if (item != null) {
            setVideo(item);
        } else {
            try {
                DBHelper dbHelper = new DBHelper(getContext());
                if (dbHelper.getRepeatCache(item.getUrl(0))) {
                    setVideo(dbHelper.getDbItemsCache(item.getUrl(0)));
                }
            } catch (Exception e){
                e.printStackTrace();
                getActivity().finish();
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
            public void play(final String[] quality, final String[] url, final String translator, final String id,
                             final String s, final String e, final String action, final String reklam) {
                rekl = reklam;
                if (getContext() != null) {
                    String q = PreferenceManager.getDefaultSharedPreferences(getContext())
                            .getString("pref_quality", "select");
                    if (Statics.itemsVideo != null) {
                        dbHelper.deleteCacheVid(item.getUrl(0));
                        dbHelper.insertCacheVideo(item.getUrl(0), Statics.itemsVidSeason);
                    }
                    switch (q) {
                        case "hight":
                            if (!url[0].contains("error")) {
                                if (quality[0].contains("2160")) {
                                    preIntent(quality[0], url[0], translator, id, s, e, action);
                                } else if (quality[quality.length - 1].contains("2160")){
                                    preIntent(quality[quality.length - 1], url[url.length - 1],
                                            translator, id, s, e, action);
                                } else if (quality[0].contains("1080")) {
                                    preIntent(quality[0], url[0], translator, id, s, e, action);
                                } else if (quality[quality.length - 1].contains("1080")){
                                    preIntent(quality[quality.length - 1], url[url.length - 1],
                                            translator, id, s, e, action);
                                } else if (quality[0].contains("720")) {
                                    preIntent(quality[0], url[0], translator, id, s, e, action);
                                } else if (quality[quality.length - 1].contains("720")){
                                    preIntent(quality[quality.length - 1], url[url.length - 1],
                                            translator, id, s, e, action);
                                } else if (quality[0].contains("480")) {
                                    preIntent(quality[0], url[0], translator, id, s, e, action);
                                } else if (quality[quality.length - 1].contains("480")){
                                    preIntent(quality[quality.length - 1], url[url.length - 1],
                                            translator, id, s, e, action);
                                } else
                                    preIntent(quality[0], url[0], translator, id, s, e, action);
                            } else Toast.makeText(getContext(), "Не удалось найти видео", Toast.LENGTH_SHORT).show();
                            break;
                        case "low":
                            if (!url[0].contains("error")) {
                                if (quality[0].contains("360")) {
                                    preIntent(quality[0], url[0], translator, id, s, e, action);
                                } else if (quality[quality.length - 1].contains("360")){
                                    preIntent(quality[quality.length - 1], url[url.length - 1],
                                            translator, id, s, e, action);
                                } else if (quality[0].contains("480")) {
                                    preIntent(quality[0], url[0], translator, id, s, e, action);
                                } else if (quality[quality.length - 1].contains("480")){
                                    preIntent(quality[quality.length - 1], url[url.length - 1],
                                            translator, id, s, e, action);
                                } else if (quality[0].contains("720")) {
                                    preIntent(quality[0], url[0], translator, id, s, e, action);
                                } else if (quality[quality.length - 1].contains("720")){
                                    preIntent(quality[quality.length - 1], url[url.length - 1],
                                            translator, id, s, e, action);
                                } else
                                    preIntent(quality[0], url[0], translator, id, s, e, action);
                            } else Toast.makeText(getContext(), "Не удалось найти видео", Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyAlertDialogStyle);
                            builder.setTitle("Выберите качество").setItems(quality, (dialogInterface, i) -> {
                                Log.d("DetailVideo", "onClick: " + Arrays.toString(url));
                                if (!url[i].contains("error")) {
                                    preIntent(quality[i], url[i], translator,id, s, e, action);
                                } else dialogInterface.dismiss();
                                pbGone();
                            }).setNegativeButton("Отмена", (dialogInterface, i) -> {
                                pbGone();
                                dialogInterface.dismiss();
                            });
                            builder.create().show();
                            pbGone();
                            break;
                    }
                }
            }
        });
        HashSet<String> def = new HashSet<>(Arrays.asList(vidBaseArr));
        try {
            pref_base = PreferenceManager.getDefaultSharedPreferences(getContext())
                    .getStringSet("base_video", def);
        } catch (Exception e) {
            pref_base = def;
        }
        vidBaseArr = pref_base.toArray(new String[pref_base.size()]);
        vidBase = pref_base.toString();

        boolean s = false;
        if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("db_cache_vid", true)) {
            try {
                s = dbHelper.getRepeatVideo(item.getUrl(0)) &&
                        PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("save_serial_position", false);
            } catch (Exception e) {
                dbHelper.deleteAll("cacheVideo");
                e.printStackTrace();
            }
        }
        if (s && !Statics.refreshMain && (item.getSeason(0) > 0 || item.getSeries(0) > 0)) {
            pbGone();
            ItemVideo itm = dbHelper.getDbItemsCacheVid(item.getUrl(0));
            if (!itm.getTitle(0).contains("back")){
                itm.title.add(0, "site back");
                itm.type.add(0, itm.type.get(0));
                itm.token.add(0, itm.token.get(0));
                itm.id_trans.add(0, "null");
                itm.id.add(0, itm.id.get(0));
                itm.url.add(0, itm.url.get(0));
                itm.seasons_count.add(0, itm.seasons_count.get(0));
                itm.episodes_count.add(0, itm.episodes_count.get(0));
                itm.translator.add(0, itm.translator.get(0));
            }
            itemSetRv(itm);
        } else {
            Statics.refreshMain = false;
            Log.e("hdgo", "setVideo: "+item.getIframe(0));
            if (item.getIframe(0).contains("hdgo") || item.getIframe(0).contains("vio.to")) {
                pbVisible();
                HdgoIframe getIframe = new HdgoIframe(item, true, items ->
                        itemAddRv(items, "iframe"));
                getIframe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (item.getIframe(0).contains("farsihd.")) {
                pbVisible();
                FarsihdIframe getIframe = new FarsihdIframe(item, items ->
                        itemAddRv(items, "iframe"));
                getIframe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (item.getUrl(0).toLowerCase().contains("animevost") || item.getUrl(0).contains(Statics.ANIMEVOST_URL)) {
                pbVisible();
                AnimevostSeries getList = new AnimevostSeries(item, true, items ->
                        itemAddRv(items, "animevost"));
                getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (!item.getIframe(0).isEmpty() && !item.getIframe(0).contains("error") &&
                    item.getUrl(0).contains(Statics.RUFILMTV_URL)) {
                pbVisible();
                RufilmtvIframe iframe = new RufilmtvIframe(item, items ->
                        itemAddRv(items, "iframe"));
                iframe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (!item.getIframe(0).isEmpty() && (!item.getIframe(0).contains("error") &&
                    item.getUrl(0).contains(Statics.FANSERIALS_URL) || item.getUrl(0).contains("fanserials"))) {
                pbVisible();
                FanserialsIframe iframe = new FanserialsIframe(item, items ->
                        itemAddRv(items, "iframe"));
                iframe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            //BASE
            if (pref_base.contains("kinosha")) {
                pbVisible();
                ParserKinosha getList = new ParserKinosha(item, items -> itemAddRv(items, "kinosha"));
                getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (pref_base.contains("filmix") && item.getUrl(0).contains(Statics.FILMIX_URL)) {
                pbVisible();
                ParserFilmix getList = new ParserFilmix(item, items -> itemAddRv(items, "filmix"));
                getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (pref_base.contains("anidub") && item.getUrl(0).contains(Statics.ANIDUB_URL)) {
                pbVisible();
                ParserAnidub getAnidub = new ParserAnidub(item, items -> itemAddRv(items, "anidub"));
                getAnidub.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (Statics.KP_ID.contains("error"))
                Statics.KP_ID = item.getKpId();
            if (!item.getUrl(0).contains(Statics.COLDFILM_URL) && !item.getSubTitle(0).trim().isEmpty()) {
                if ((Statics.KP_ID.contains("error") || Statics.KP_ID.isEmpty()) && Statics.MOON_ID.contains("error")) {
                    pbText.setText("Подождите...");
                    pbVisible();
                    GetKpId getList = new GetKpId(item, (n, m) -> {
                        if (pref_base.contains("moonwalk")) {
                            pbVisible();
                            ParserMoonwalk get = new ParserMoonwalk(item, items -> itemAddRv(items, "moonwalk"));
                            get.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                        if (pref_base.contains("hdgo")) {
                            pbVisible();
                            ParserHdgo get = new ParserHdgo(item, items -> itemAddRv(items, "hdgo"));
                            get.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    });
                    getList.execute();
                } else {
                    if (pref_base.contains("moonwalk")) {
                        pbVisible();
                        ParserMoonwalk get = new ParserMoonwalk(item, items -> itemAddRv(items, "moonwalk"));
                        get.execute();
                    }
                    if (pref_base.contains("hdgo")) {
                        pbVisible();
                        ParserHdgo get = new ParserHdgo(item, items -> itemAddRv(items, "hdgo"));
                        get.execute();
                    }
                }
            } else {
                if (pref_base.contains("moonwalk")) {
                    pbVisible();
                    ParserMoonwalk get = new ParserMoonwalk(item, items -> itemAddRv(items, "moonwalk"));
                    get.execute();
                }
                if (pref_base.contains("hdgo")) {
                    pbVisible();
                    ParserHdgo get = new ParserHdgo(item, items -> itemAddRv(items, "hdgo"));
                    get.execute();
                }
            }
            if (pref_base.contains("filmix") && !item.getUrl(0).contains(Statics.FILMIX_URL)) {
                pbVisible();
                ParserFilmix getList = new ParserFilmix(item, items -> itemAddRv(items, "filmix"));
                getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (pref_base.contains("kinohd")) {
                pbVisible();
                KinohdIframe getList = new KinohdIframe(item, items -> itemAddRv(items, "kinohd"));
                getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (pref_base.contains("kinolive")) {
                pbVisible();
                if (item.getUrl(0).toLowerCase().contains("kino-live") || item.getUrl(0).contains(Statics.KINOLIVE_URL)) {
                    ParserKinoliveUrl getList = new ParserKinoliveUrl(item, items -> itemAddRv(items, "kinolive"));
                    getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    ParserKinolive getList = new ParserKinolive(item, items -> itemAddRv(items, "kinolive"));
                    getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
            if (pref_base.contains("kinopub")) {
                pbVisible();
                if (item.getUrl(0).toLowerCase().contains("kino.pub") || item.getUrl(0).contains(Statics.KINOPUB_URL)) {
                    KinopubIframeUrl getList = new KinopubIframeUrl(item, items -> itemAddRv(items, "kinopub"));
                    getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    ParserKinopubSearch getList = new ParserKinopubSearch(item, items -> itemAddRv(items, "kinopub"));
                    getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }

            if (pref_base.contains("kinodom")) {
                pbVisible();
                if (item.getUrl(0).toLowerCase().contains("kino-dom.") || item.getUrl(0).contains(Statics.KINODOM_URL)) {
                    KinodomIframeUrl getList = new KinodomIframeUrl(item, items -> itemAddRv(items, "kinodom"));
                    getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    ParserKinodom getList = new ParserKinodom(item, items -> itemAddRv(items, "kinodom"));
                    getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
            if (pref_base.contains("zombiefilm")) {
                ParserZombiefilm getZombiefilm = new ParserZombiefilm(item, items -> itemAddRv(items, "zombiefilm"));
                getZombiefilm.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (pref_base.contains("anidub") && !item.getUrl(0).contains(Statics.ANIDUB_URL)) {
                pbVisible();
                ParserAnidub getAnidub = new ParserAnidub(item, items -> itemAddRv(items, "anidub"));
                getAnidub.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (pref_base.contains("animevost") && !item.getUrl(0).contains(Statics.ANIMEVOST_URL)) {
                pbVisible();
                ParserVAnimevost getAnivost = new ParserVAnimevost(item, items -> itemAddRv(items, "animevost"));
                getAnivost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (pref_base.contains("animedia")) {
                ParserAnimedia getAnimedia = new ParserAnimedia(item, items -> itemAddRv(items, "animedia"));
                getAnimedia.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            vidBase = vidBase
                    .replace(", kinoxa", "")
                    .replace("kinoxa", "").trim();
            if (pref_base.isEmpty())
                pbGone();
        }
    }

    private void pbGone() {
        if (pb != null) {
            try {
                pb.animate()
                        .translationY(pb.getHeight())
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                pb.setVisibility(View.GONE);
                            }
                        });
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void pbVisible() {
        pb.setVisibility(View.VISIBLE);
        pb.animate()
                .translationY(0)
                .alpha(0.8f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                pb.setVisibility(View.VISIBLE);
            }
        });
    }
    private void preIntent(String q, String url, String translator, String id, String s, String e,
                             String action) {
        if (q.contains("ссылка")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
            pbGone();
        } else {
            url = url.replace("http:url:","").replace("url:","").trim();
            if (url.contains("cdn") || url.contains("[hdgo]")
                    || url.contains("/video.php?name=")) {
                if (url.contains("[hdgo]"))
                    url = url.replace("[hdgo]", "");
                pbVisible();
                if (!rekl.equals("farsihd")) {
                    GetLocation click = new GetLocation(url, location ->
                            videoIntent(q, location, translator, id, s, e, action));
                    click.execute();
                } else videoIntent(q, url, translator, id, s, e, action);
            } else
                videoIntent(q, url, translator, id, s, e, action);
            String qs = PreferenceManager.getDefaultSharedPreferences(getContext())
                    .getString("pref_quality", "select");
            if (qs.equals("hight") || qs.equals("low"))
                Toast.makeText(getContext(), q, Toast.LENGTH_SHORT).show();
            pbGone();
        }
    }

    private void videoIntent(String q, String url, String translator, String id, String s, String e,
                             String action) {
        if (rekl.equals("filmix") && PreferenceManager.getDefaultSharedPreferences(getContext())
                .getBoolean("sync_filmix_watch", false) &&
                !Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted") && !action.equals("trailer"))
            new ParserFilmixAddHist(translator, id, s, e).execute();
        Log.d("DetailVideo", "videoIntent: " + url);
        if (!url.trim().startsWith("http"))
            url = "http:" + url;
//        else url = url.trim().replace("https", "http");
        String subs = "error";
        if (url.contains("[subs]")){
            subs = url.split("\\[subs\\]")[1];
            url = url.split("\\[subs\\]")[0];
        }


        boolean pro = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getBoolean("pro_version", false);
        boolean seeit = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getBoolean("side_left", false);
        boolean setit = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getBoolean("side_video", false);
        boolean setet = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getBoolean("side_exist", false);

        String title;
        Statics.video = null;
        Statics.curAct = action;
        if (rekl != null)
            Statics.curReclam = rekl;
        if (rekl == null) rekl = "null";
        if (!rekl.contains("moonwalk"))
            Statics.adbWached = false;
        if (q.contains(" "))
            q = q.trim().split(" ")[0];
        if (item != null) {
            String t = item.getTitle(0).contains("(") ? item.getTitle(0).split("\\(")[0] :
                    item.getTitle(0);
            title = t.trim();
            if (s.contains("error") && e.contains("error")) {
                if (item.getUrl(0).contains(Statics.FANSERIALS_URL)) {
                    title = title + " s" + item.getSeason(0) + "e" + item.getSeries(0);
                } else if (!item.getDate(0).contains("error"))
                    title = title + " " + item.getDate(0);
            }
        } else if (getActivity() != null) {
            title = (String) getActivity().getTitle();
        } else title = "...";
        if (!s.contains("error") && !s.trim().equals("0"))
            title = title + " s" + s;
        if (!e.contains("error") && !e.trim().equals("0") && !e.trim().equals("X"))
            title = title + "e" + e;
        Intent reklIntent = new Intent(getContext(), ReclamActivity.class);
        reklIntent.putExtra("Source", rekl);
        boolean pt = setet && pro;
        //____________________________________________________________
        if (action.equals("play") || action.equals("trailer")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url.trim()));
            intent.putExtra("title", title);
            intent.putExtra("filename", title);
            intent.putExtra("forcename", title);
            intent.putExtra("headers", new String[]{
                    "User-Agent", "Mozilla compatible/1.0",
                    "Referer", Utils.getUrl(rekl)});
            if (!subs.isEmpty()) {
                intent.putExtra("subs", Uri.parse(subs.trim()));
                Log.e("sub", "videoIntent: " + subs);
                if (!subs.replace(",","").trim().isEmpty()) {
                    if (subs.contains(",")) {
                        intent.putExtra("subtitles_location", new Uri[]{Uri.parse(subs.split(",")[0]), Uri.parse(subs.split(",")[1])});
                        intent.putExtra("subs", new Uri[]{Uri.parse(subs.split(",")[0]), Uri.parse(subs.split(",")[1])});
                        intent.putExtra("forcedsrt", Uri.parse(subs.split(",")[0]));
                        intent.putExtra("subs.enable", new Uri[]{Uri.parse(subs.split(",")[0]), Uri.parse(subs.split(",")[1])});
                        intent.putExtra("subs.name", new String[]{subs.split(",")[0].split("/")[subs.split(",")[0].split("/").length - 1],
                                subs.split(",")[1].split("/")[subs.split(",")[1].split("/").length - 1]});
                        intent.putExtra("subs.filename ", new String[]{subs.split(",")[0].split("/")[subs.split(",")[0].split("/").length - 1],
                                subs.split(",")[1].split("/")[subs.split(",")[1].split("/").length - 1]});
                    } else {
                        intent.putExtra("subtitles_location", new Uri[]{Uri.parse(subs.trim())});
                        intent.putExtra("subs", new Uri[]{Uri.parse(subs.trim())});
                        intent.putExtra("forcedsrt", Uri.parse(subs.trim()));
                        intent.putExtra("subs.enable", new Uri[]{Uri.parse(subs.trim())});
                        intent.putExtra("subs.name", new String[]{subs.trim().split("/")[subs.trim().split("/").length - 1]});
                        intent.putExtra("subs.filename ", new String[]{subs.trim().split("/")[subs.trim().split("/").length - 1]});
                    }
                }
            }
            Intent chooser = Intent.createChooser(intent, title);

            ArrayList<Uri> uriList = new ArrayList<>();
            if (Statics.videoList != null) {
                if (Statics.videoList.length > 0) {
                    for (String u : Statics.videoList) {
                        String urlList = u
                                .replace("[720p,480,]", q)
                                .replace("[720,480,]", q).trim();

                        q = q.replace("(mp4)","")
                                .replace("(m3u8)","").trim();
                        if (urlList.contains("["+q) && (rekl.equals("filmix") || rekl.equals("animevost"))) {
                            urlList = urlList.replace("HD]","")
                                    .replace("p]","");
                            urlList = urlList.split("\\[" + q)[1].replace(" ","");
                            if (urlList.contains(",")){
                                urlList = urlList.split(",")[0].replace(" ","");
                                if (urlList.contains("]")){
                                    urlList = urlList.split("\\]")[1].trim();
                                }
                            }
                        } else if (urlList.contains("\"" + q) && rekl.equals("zombiefilm")) {
                            urlList = urlList.split("\"" + q)[1].replace("\":","")
                                    .replace("\"","");
                            if (urlList.contains(",")){
                                urlList = urlList.split(",")[0].replace(" ","");
                            }
                        }
                        uriList.add(Uri.parse(urlList));
                    }
                } else  Log.e("video", "videoList legth 0");
            } else Log.e("video", "videoList is empty");
            if (uriList.size() > 1) {
                String t = "";
                if (item != null) {
                    t = item.getTitle(0).contains("(") ?
                            item.getTitle(0).split("\\(")[0] :
                            item.getTitle(0) + " ";
                } else if (getActivity() != null)
                    t = getActivity().getTitle().toString().contains("(") ?
                            getActivity().getTitle().toString().split("\\(")[0] :
                            getActivity().getTitle().toString() + " ";
                ArrayList<String> nameList = new ArrayList<>();
                for (String n : Statics.videoListName) {
                    nameList.add(t + n);
                }
                if (rekl.contains("kinopub")) {
                    try {
                        uriList.set(Integer.parseInt(e)-1, Uri.parse(url));
                        Statics.videoList[Integer.parseInt(e)-1] = url;
                    } catch (Exception f){
                        f.printStackTrace();
                    }
                } else Log.e("video", "videoList2 "+rekl);
                intent.putExtra("video_list", uriList.toArray(new Uri[uriList.size()]));
                intent.putExtra("video_list.name", nameList.toArray(new String[nameList.size()]));
            }
            Log.e("video", "videoList all "+uriList.toString());
            String playV;
            try {
                playV = PreferenceManager.getDefaultSharedPreferences(getContext())
                        .getString("play_video_p", "default");
            } catch (Exception d) {
                playV = "default";
            }
            if (playV.equals("mxplayer") && !url.contains("youtube") && !url.contains("rutube"))
                intent.setDataAndType(Uri.parse(url.trim()),"application/com.mxtech.videoplayer.pro|application/com.mxtech.videoplayer.ad");
            else if (playV.equals("vlcplayer") && !url.contains("youtube") && !url.contains("rutube")) {
                intent.setPackage("org.videolan.vlc");
                intent.setDataAndType(Uri.parse(url.trim()), "video/*");
            } else if (playV.equals("vimuplayer") && !url.contains("youtube") && !url.contains("rutube")) {
                intent.setPackage("net.gtvbox.videoplayer");
                intent.setDataAndType(Uri.parse(url.trim()), "video/*");
            } else intent.setDataAndType(Uri.parse(url.trim()), "video/*");

            if (getContext() != null) {
                PackageManager packageManager = getContext().getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);

                Log.d("play", title + " " + url);
                if (playV.equals("other") || q.contains("(ссылка)")) {
                    Statics.video = chooser;
                } else {
                    if (activities.size() > 0) {
                        Statics.video = intent;
                    }else {
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url.trim()));
                        List<ResolveInfo> a = packageManager.queryIntentActivities(i, 0);
                        Log.d("download", title + " " + url);
                        if (a.size() > 0)
                            Statics.video = intent;
                    }
                }
                //____________________________________________________________
                if (!rekl.contains("moonwalk") && !Statics.adbWached && !pt)
                    getContext().startActivity(reklIntent);
                else {
                    Statics.adbWached = true;
                    onResume();
                }
            }
            if (action.equals("play"))
                addToDbVid(translator, s, e);
        } else if (action.equals("download")){
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

            String nameOfFile = title.trim().replace(" ", "_") + "" + q + "p.mp4";
            //set title for notification in status_bar
            request.setTitle(nameOfFile);
            //flag for if you want to show notification in status or not
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            String downV;
            try {
                downV = PreferenceManager.getDefaultSharedPreferences(getContext())
                        .getString("download_video_d", "default");
            } catch (Exception d) {
                downV = "default";
            }
            if (downV.equals("default") && getContext() != null) {
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
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
            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.trim()));
                if (downV.equals("dvget"))
                    intent.setDataAndType(Uri.parse(url.trim()), "application/com.dv.adm|application/com.dv.get");
                else intent.setDataAndType(Uri.parse(url.trim()), "application/*");
                intent.putExtra("title", title);
                // Create intent to show chooser
                Intent chooser = Intent.createChooser(intent, title);
                // Verify the intent will resolve to at least one activity
                if (getContext() != null) {
                    if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                        Statics.video = chooser;
                        if (!rekl.contains("moonwalk")
                                && !Statics.adbWached  && !pt)
                            getContext().startActivity(reklIntent);
                        else {
                            Statics.adbWached = true;
                            onResume();
                        }
                    }
                }
            }
            addToDbVid(translator, s, e);
        } else if (action.equals("copy") && getContext() != null) {
            if (!rekl.contains("moonwalk") &&  !Statics.adbWached  && !pt)
                getContext().startActivity(reklIntent);
            else {
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    @SuppressWarnings("deprecation")
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard != null) clipboard.setText(url);
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("TAG", url);
                    if (clipboard != null) clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(getContext(), "Ссылка скопирована", Toast.LENGTH_SHORT).show();
            }

        } else if (action.equals("share")){
            String share = "KinoTor" + " \n" + title + " \n" + url;


            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setType("text/html");
            intent.putExtra(Intent.EXTRA_SUBJECT, "sample");
            intent.putExtra(Intent.EXTRA_TEXT, share);
            Intent chooser = Intent.createChooser(intent, "Отправить");

            if (getContext() != null) {
                if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                    Statics.video = chooser;
                    if (!rekl.contains("moonwalk") && !Statics.adbWached  && !pt)
                        getContext().startActivity(reklIntent);
                    else {
                        Statics.adbWached = true;
                        getContext().startActivity(Statics.video);
                        Statics.video = null;
                    }
                }
            }
        }
    }

    private void addToDbVid(String translator, String s, String e){
        DBHelper dbHelper = new DBHelper(getContext());
        if (item!=null) {
            if (!dbHelper.getRepeatWatch(3, item.getTitle(0).trim(),
                    translator.trim(), s.trim(), e.trim())) {
                dbHelper.Write();
                dbHelper.insertWatch(item.getTitle(0).trim(), translator.trim(), s, e);
                rv.getRecycledViewPool().clear();
                rv.getAdapter().notifyDataSetChanged();
            }
        }
    }

    private void itemSetRv(ItemVideo items) {
        pbGone();
        if (rv.getAdapter() != null) {
            ((AdapterVideo) rv.getAdapter()).setItems(items);
            rv.getRecycledViewPool().clear();
            rv.getAdapter().notifyDataSetChanged();

            LinearLayoutManager layoutManager = (LinearLayoutManager) rv.getLayoutManager();
            layoutManager.scrollToPositionWithOffset(0, 0);
        }
    }

    private void itemAddRv(ItemVideo items, String source) {
        Log.e("rty", "itemAddRv0: "+vidBase );
        vidBase = vidBase.replace(source, "").replace(" ", "")
                .replace(",,", "").replace("[", "")
                .replace("]", "").trim();
        if (vidBase.startsWith(",")) vidBase = vidBase.substring(1);
        if (vidBase.endsWith(",")) vidBase = vidBase.substring(0, vidBase.length() - 1);
        pbText.setText("Поиск: " + (pref_base.size() - vidBase.split(",").length) +
                " из " + pref_base.size());

        Log.e("rty", "itemAddRv0: "+vidBase );
        if (vidBase.contains("hdgo") || vidBase.contains("moonwalk")
                || vidBase.contains("filmix")
                || vidBase.contains("kinosha") || vidBase.contains("kinodom")
                || vidBase.contains("kinohd") || vidBase.contains("kinolive")
                || vidBase.contains("anidub") || vidBase.contains("animedia")
                || vidBase.contains("zombiefilm") || vidBase.contains("kinopub")
                || (vidBase.contains("animevost") && !item.getUrl(0).contains(Statics.ANIMEVOST_URL))) {

//            Log.e("test", "itemAddRv3: "+vidBase);
            pbVisible();
        } else if (item != null){
            pbGone();
            pbText.setText("Подождите...");
            vidBase = Arrays.toString(vidBaseArr);
        } else vidBase = Arrays.toString(vidBaseArr);
        if (vidBase.trim().equals(Arrays.toString(vidBaseArr).trim())) {
            pbGone();
        }
        ((AdapterVideo) rv.getAdapter()).addItems(items);
        rv.getRecycledViewPool().clear();
        rv.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        pbGone();
        if (getActivity() != null) {
            if (getActivity().findViewById(R.id.detail_pb) != null)
                getActivity().findViewById(R.id.detail_pb).setVisibility(View.GONE);
        }
        if (Statics.backClick) {
            if (Statics.itemsVidSeason != null) {
                Statics.backClick = false;
                itemSetRv(Statics.itemsVidSeason);
                Statics.itemsVidSeason = null;
            } else if (Statics.itemsVidVoice != null) {
                Statics.backClick = false;
                itemSetRv(Statics.itemsVidVoice);
                Statics.itemsVidVoice = null;
            }
        }
        if (Statics.video != null && (!Statics.curReclam.contains("moonwalk") || Statics.adbWached)) {
            if (getContext() != null) {
                PackageManager packageManager = getContext().getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(Statics.video, 0);
                switch (Statics.curAct) {
                    case "play":
                        String playV = PreferenceManager.getDefaultSharedPreferences(getContext())
                                .getString("play_video_p", "default");
                        if (playV.equals("mxplayer")){
                            boolean started = false;
                            for (ResolveInfo info : activities) {
                                ActivityInfo activityInfo = info.activityInfo;
                                if (activityInfo.packageName.startsWith("com.mxtech.videoplayer.")) {
                                    getContext().startActivity(Statics.video.setClassName(activityInfo.packageName, activityInfo.name));
                                    started = true;
                                    break;
                                }
                            }
                            if (!started) {
                                try {
                                    Statics.video.putExtra("headers", new String[]{
                                            "User-Agent", "Mozilla compatible/1.0",
                                            "Referer", Utils.getUrl(rekl)});
                                    getContext().startActivity(Statics.video);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Mx плеер не найден", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (playV.equals("vlcplayer")){
                            boolean started = false;
                            for (ResolveInfo info : activities) {
                                ActivityInfo activityInfo = info.activityInfo;
                                Log.e("test", "onResume: "+activityInfo.packageName );
                                if (activityInfo.packageName.startsWith("org.videolan.vlc")) {
                                    getContext().startActivity(Statics.video.setClassName(activityInfo.packageName, activityInfo.name));
                                    started = true;
                                    break;
                                }
                            }
                            if (!started) {
                                try {
                                    Statics.video.putExtra("headers", new String[]{
                                            "User-Agent", "Mozilla compatible/1.0",
                                            "Referer", Utils.getUrl(rekl)});
                                    getContext().startActivity(Statics.video);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Vlc плеер не найден", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else if (playV.equals("vimuplayer")) {
                            boolean started = false;
                            for (ResolveInfo info : activities) {
                                ActivityInfo activityInfo = info.activityInfo;
                                Log.e("test", "onResume: "+activityInfo.packageName );
                                if (activityInfo.packageName.startsWith("net.gtvbox.videoplayer")) {
                                    getContext().startActivity(Statics.video.setClassName(activityInfo.packageName, activityInfo.name));
                                    started = true;
                                    break;
                                }
                            }
                            if (!started) {
                                try {
                                    Statics.video.putExtra("headers", new String[]{
                                            "User-Agent", "Mozilla compatible/1.0",
                                            "Referer", Utils.getUrl(rekl)});
                                    getContext().startActivity(Statics.video);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Vimu плеер не найден", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            getContext().startActivity(Statics.video);
                        }
                        break;
                    case "download":
                        String downV = PreferenceManager.getDefaultSharedPreferences(getContext())
                                .getString("download_video_d", "default");
                        if (downV.equals("dvget")){
                            boolean started = false;
                            for (ResolveInfo info : activities) {
                                ActivityInfo activityInfo = info.activityInfo;
                                if (activityInfo.packageName.startsWith("com.dv.")) {
                                    getContext().startActivity(Statics.video.setClassName(activityInfo.packageName, activityInfo.name));
                                    started = true;
                                    break;
                                }
                            }
                            if (!started) {
                                try {
                                    getContext().startActivity(Statics.video);
                                } catch (Exception e) {
                                    Toast.makeText(getContext(), "Dvget не найден", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } else {
                            getContext().startActivity(Statics.video);
                        }
                        break;
                    case "copy":
                        break;
                    default:
                        getContext().startActivity(Statics.video);
                        break;
                }
            }
            Statics.video = null;
        }
    }
}