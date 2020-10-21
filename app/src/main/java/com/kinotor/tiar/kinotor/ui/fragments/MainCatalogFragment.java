package com.kinotor.tiar.kinotor.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.parser.catalog.ParserAnidub;
import com.kinotor.tiar.kinotor.parser.catalog.ParserAnimevost;
import com.kinotor.tiar.kinotor.parser.catalog.ParserColdfilm;
import com.kinotor.tiar.kinotor.parser.catalog.ParserFanserials;
import com.kinotor.tiar.kinotor.parser.catalog.filmix.ParserFilmix;
import com.kinotor.tiar.kinotor.parser.catalog.ParserKinoFS;
import com.kinotor.tiar.kinotor.parser.catalog.ParserKinodom;
import com.kinotor.tiar.kinotor.parser.catalog.ParserKinolive;
import com.kinotor.tiar.kinotor.parser.catalog.ParserKinopub;
import com.kinotor.tiar.kinotor.parser.catalog.ParserKinoxa;
import com.kinotor.tiar.kinotor.parser.catalog.ParserKoshara;
import com.kinotor.tiar.kinotor.parser.catalog.ParserMyhit;
import com.kinotor.tiar.kinotor.parser.catalog.ParserRufilmtv;
import com.kinotor.tiar.kinotor.parser.catalog.ParserTopkino;
import com.kinotor.tiar.kinotor.ui.BDActivity;
import com.kinotor.tiar.kinotor.utils.Utils;
import com.kinotor.tiar.kinotor.utils.adapters.AdapterCatalog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static android.content.ContentValues.TAG;
import static com.kinotor.tiar.kinotor.items.ItemMain.cur_url;

public class MainCatalogFragment extends Fragment {
    private String curl = "null";
    private String category = "фильмы", catalog = "filmix";
    private String base = "";
    RecyclerView rv_catalog;
    private RelativeLayout pb;
    private ArrayList<ItemHtml> itemsCat = new ArrayList<>();
    private ItemHtml itemPathCat = new ItemHtml();
    private int cur_page = 1;
    private Context context;

    public static MainCatalogFragment newInstance(String url, String category, String catalog) {
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putString("category", category);
        args.putString("catalog", catalog);
        MainCatalogFragment f = new MainCatalogFragment();
        f.setArguments(args);
        return f;
    }

    public MainCatalogFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_catalog, container, false);
        rv_catalog = rootView.findViewById(R.id.rv_catalog);
        pb = rootView.findViewById(R.id.progresB);
        context = rootView.getContext();

        if (getArguments() != null) {
            curl = getArguments().getString("url");
            category = getArguments().getString("category");
            catalog = getArguments().getString("catalog");
        }

        final String dB;
        switch (category) {
            case "Избранное":
                if (BDActivity.show != null)
                    dB = "favor|" + BDActivity.show;
                else dB = "favor";
                break;
            case "История":
                if (BDActivity.show != null)
                    dB = "history|" + BDActivity.show;
                else dB = "history";
                break;
            case "Посмотреть позже":
                if (BDActivity.show != null)
                    dB = "later|" + BDActivity.show;
                else dB = "later";
                break;
            default:
                dB = "catalog";
                break;
        }

        GridLayoutManager gl = new GridLayoutManager(context,
                new Utils().calculateGrid(getContext()));
//        CenterZoomLayoutManager gl = new CenterZoomLayoutManager(context);
//        gl.onFocusSearchFailed()
        rv_catalog.setLayoutManager(gl);
        AdapterCatalog adapter = new AdapterCatalog(context, dB) {
            @Override
            public void load() {
                if (dB.equals("catalog") && !curl.equals("null")) {
                    cur_page++;
                    Log.d(TAG, "load: cur_page - " + cur_page);
                    onPage();
                }
            }

            @Override
            public void key(int keyCode, int position) {

            }
        };
        rv_catalog.setAdapter(adapter);
        rv_catalog.requestDisallowInterceptTouchEvent(false);

        if (curl != null)
        if (dB.equals("catalog") && !curl.equals("null")) {
            onPage();
        } else pbGone();
        return rootView;
    }

    public void onPage(){
        String url;
        pb.setVisibility(View.VISIBLE);
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);

        if (category.contains("filmix_fav")) {
            curl = Statics.FILMIX_URL + "/loader.php?do=favorites";
            if (cur_page > 1) url = curl + "&cstart=" + cur_page;
            else url = curl;
            ParserFilmix parserFilmix = new ParserFilmix(url, itemsCat, itemPathCat, this::updateRv);
            parserFilmix.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (category.contains("filmix_later")) {
            curl = Statics.FILMIX_URL + "/loader.php?do=watch_later";
            if (cur_page > 1) url = curl + "&cstart=" + cur_page;
            else url = curl;
            ParserFilmix parserFilmix = new ParserFilmix(url, itemsCat, itemPathCat, this::updateRv);
            parserFilmix.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (category.contains("Поиск") && catalog.equals("all")){
            if (cur_page == 1) {
                HashSet<String> def = new HashSet<>(Arrays.asList(context.getResources().getStringArray(R.array.pref_list_base)));
                Set<String> pref_base = preference.getStringSet("base_catalog", def);
                base = pref_base.toString();
//                Log.e(TAG, "onPage0: "+base );
                if (pref_base.contains("koshara")) {
                    String quer = ItemMain.xs_search;
                    try {
                        quer = URLEncoder.encode(quer, "windows-1251");
                    } catch (UnsupportedEncodingException e) {
                        quer = "error";
                    }
                    if (category.contains("ПоискАктер"))
                        cur_url = Statics.KOSHARA_URL + "/index.php?do=search&subaction=search&story=" + quer;
                    else
                        cur_url = Statics.KOSHARA_URL + "/index.php?do=search&subaction=search&titleonly=3&story=" + quer;
                    url = cur_url + "&search_start=1/";
                    ParserKoshara koshara = new ParserKoshara(url, itemsCat, itemPathCat, (items, itempath) -> {
                        base = base.replace("koshara", "");
                        updateRv(items, itempath);
                    });
                    koshara.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
//                if (pref_base.contains("amcet")) {
//                    ParserAmcet parserAmcet;
//                    if (category.contains("ПоискАктер"))
//                        url = Statics.AMCET_URL + "/xfsearch/actors/" +
//                                ItemMain.xs_search.replace(" ", "+") + "/" + "page/1/";
//                    else url = Statics.AMCET_URL + "/?subaction=search&do=search&story=" +
//                            ItemMain.xs_search + "&search_start=1";
//                    parserAmcet = new ParserAmcet(url, itemsCat, itemPathCat, (items, itempath) -> {
//                        base = base.replace("amcet", "");
//                        updateRv(items, itempath);
//                    });
//                    parserAmcet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                }
                if (pref_base.contains("kinofs")) {
                    ParserKinoFS parserKinoFS;
                    if (category.contains("ПоискАктер"))
                        cur_url = Statics.KINOFS_URL + "/search/" +
                                ItemMain.xs_search.replace(" ", "%20") + "/";
                    else cur_url = Statics.KINOFS_URL + "/load/поиск/";

                    parserKinoFS = new ParserKinoFS(cur_url, itemsCat, itemPathCat, (items, itempath) -> {
                        base = base.replace("kinofs", "");
                        updateRv(items, itempath);
                    });
                    parserKinoFS.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                if (pref_base.contains("kinolive")) {
                    ParserKinolive parserKinolive;
                    cur_url = Statics.KINOLIVE_URL + "/index.php?do=search";

                    parserKinolive = new ParserKinolive(cur_url, itemsCat, itemPathCat, (items, itempath) -> {
                        base = base.replace("kinolive", "");
                        updateRv(items, itempath);
                    });
                    parserKinolive.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                if (pref_base.contains("kinoxa")) {
                    cur_url = Statics.KINOXA_URL + "/index.php?do=search&subaction=search&titleonly=3&story=" + ItemMain.xs_search;
                    url = cur_url;
                    ParserKinoxa parser = new ParserKinoxa(url, itemsCat, itemPathCat, (items, itempath) -> {
                        base = base.replace("kinoxa", "");
                        updateRv(items, itempath);
                    });
                    parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                if (pref_base.contains("rufilmtv")) {
                    url = cur_url;
                    ParserRufilmtv parser = new ParserRufilmtv(url, itemsCat, itemPathCat, (items, itempath) -> {
                        base = base.replace("rufilmtv", "");
                        updateRv(items, itempath);
                    });
                    parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                if (pref_base.contains("topkino")) {
                    if (category.contains("ПоискАктер"))
                        cur_url = Statics.TOPKINO_URL + "/xfsearch/" +
                                ItemMain.xs_search.replace(" ", "+") + "/";
                    else cur_url = Statics.TOPKINO_URL +
                            "/index.php?do=search&subaction=search&full_search=1&titleonly=3&story=" + ItemMain.xs_search;
                    url = cur_url + "&search_start=1";

                    ParserTopkino parser = new ParserTopkino(url, itemsCat, itemPathCat, (items, itempath) -> {
                        base = base.replace("topkino", "");
                        updateRv(items, itempath);
                    });
                    parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                if (pref_base.contains("filmix")) {
                    ParserFilmix parserFilmix;
                    if (category.contains("ПоискАктер")) {
                        cur_url = Statics.FILMIX_URL + "/persons/search/" +
                                ItemMain.xs_search.replace(" ", "%20") + "/";
                        if (!curl.trim().endsWith("/")) curl = curl.trim() +"/";
                        if (cur_page == 1) url = curl;
                        else url = curl + "page/1/";
                    } else {
                        ItemMain.xs_value = "0";
                        url = Statics.FILMIX_URL + "/engine/ajax/sphinx_search.php";
                    }
                    parserFilmix = new ParserFilmix(url, itemsCat, itemPathCat, (items, itempath) -> {
                        base = base.replace("filmix", "");
                        updateRv(items, itempath);
                    });
                    parserFilmix.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                if (pref_base.contains("my-hit")) {
                    url = Statics.MYHIT_URL + "/search/?q=" + ItemMain.xs_search;
                    ParserMyhit parserMyhit = new ParserMyhit(url, itemsCat, itemPathCat, (items, itempath) -> {
                        base = base.replace("my-hit", "");
                        updateRv(items, itempath);
                    });
                    parserMyhit.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                if (pref_base.contains("kinopub")) {
                    url = Statics.KINOPUB_URL + "/item/search?query=" + ItemMain.xs_search;
                    ParserKinopub parserKinopub = new ParserKinopub(url, itemsCat, itemPathCat, (items, itempath) -> {
                        base = base.replace("kinopub", "");
                        updateRv(items, itempath);
                    });
                    parserKinopub.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            } else pbGone();
        } else {
            if (cur_url.contains(Statics.KOSHARA_URL)) {
                ParserKoshara parserKosara;
                if (category.equals("Мультфильмы")) url = cur_url + "&search_start=" + cur_page;
                else if (category.contains("Поиск"))
                    url = cur_url + "&search_start=" + cur_page + "/";
                else url = cur_url + "page/" + cur_page + "/";
                parserKosara = new ParserKoshara(url, itemsCat, itemPathCat, this::updateRv);
                parserKosara.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (cur_url.contains(Statics.COLDFILM_URL)) {
                if (category.contains("Поиск")) url = cur_url + "&m=news&t=0&p=" + cur_page;
                else url = cur_url + "?page" + cur_page;
                ParserColdfilm parserHtml = new ParserColdfilm(url, itemsCat, itemPathCat, this::updateRv);
                parserHtml.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (cur_url.contains(Statics.ANIMEVOST_URL)) {
                ParserAnimevost parserAnimevost;
                if (category.contains("Поиск")) url = cur_url + "'page'" + cur_page;
                else if (cur_page == 1) url = curl;
                else url = curl + "page/" + cur_page + "/";
                parserAnimevost = new ParserAnimevost(url, itemsCat, itemPathCat, this::updateRv);
                parserAnimevost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (cur_url.contains(Statics.ANIDUB_URL)) {
                ParserAnidub parserAnidub;
                if (category.contains("Поиск")) url = cur_url + "'page'" + cur_page;
                else if (cur_page == 1) url = curl;
                else url = curl + "page/" + cur_page + "/";
                parserAnidub = new ParserAnidub(url, itemsCat, itemPathCat, this::updateRv);
                parserAnidub.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (cur_url.contains(Statics.KINOFS_URL)) {
                ParserKinoFS parserKinoFS;
                if (category.contains("ПоискАктер")) {
                    if (cur_page == 1) url = curl;
                    else url = "error";
                } else if (category.contains("Поиск")) url = cur_url + cur_page;
                else if (curl.contains("top_100") && cur_page == 1) url = curl;
                else url = curl + "-" + cur_page;

                if (!ItemMain.xs_value.equals("") && !ItemMain.xs_value.equals("0"))
                    url = url + "-" + ItemMain.xs_value.trim();
                parserKinoFS = new ParserKinoFS(url, itemsCat, itemPathCat, this::updateRv);
                parserKinoFS.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (cur_url.contains(Statics.KINOXA_URL)) {
                if (cur_page == 1)
                    url = curl;
                else url = curl + "/page/" + cur_page + "/";
                ParserKinoxa parser = new ParserKinoxa(url, itemsCat, itemPathCat, this::updateRv);
                parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (cur_url.contains(Statics.RUFILMTV_URL)) {
                String search = "";
                if (curl.contains("?s=")) {
                    search = "?s=" + curl.split("\\?s=")[1];
                    curl = curl.split("\\?s=")[0];
                }
                if (cur_page == 1)
                    url = curl;
                else url = curl + "/page/" + cur_page;
                ParserRufilmtv parser = new ParserRufilmtv(url + search, itemsCat, itemPathCat, this::updateRv);
                parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (cur_url.contains(Statics.TOPKINO_URL)) {
                String search = "";
                if (curl.contains("index.php?do=search&subaction=search"))
                    url = curl + "&search_start=" + cur_page;
                else if (cur_page == 1)
                    url = curl;
                else url = curl + "/page/" + cur_page + "/";

                ParserTopkino parser = new ParserTopkino(url + search, itemsCat, itemPathCat, this::updateRv);
                parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (cur_url.contains(Statics.KINOLIVE_URL)) {
                if (curl.contains("index.php?do=search") || curl.contains("ajax/pages.php"))
                    url = curl + "&search_start=" + cur_page;
                else if (cur_page == 1)
                    url = curl;
                else {
                    url = curl + "/page/" + cur_page + "/";
                }

                ParserKinolive parser = new ParserKinolive(url, itemsCat, itemPathCat, this::updateRv);
                parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (cur_url.contains(Statics.FANSERIALS_URL)) {
                if (category.contains("Поиск")) url = cur_url + "&page=" + cur_page + "/";
                else url = cur_url + "page/" + cur_page + "/";
                ParserFanserials parserFanserials = new ParserFanserials(url, itemsCat, itemPathCat, (items, itempath) -> {
                    base = base.replace("fanserials", "");
                    updateRv(items, itempath);
                });
                parserFanserials.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (cur_url.contains(Statics.KINODOM_URL)) {
                if (category.contains("Поиск")) {
                    ItemMain.xs_value = String.valueOf(cur_page);
                    url = cur_url;
                } else if (cur_page > 1)
                    url = curl + "page/" + cur_page + "/";
                else url = curl;
                ParserKinodom parserKinodom = new ParserKinodom(url, itemsCat, itemPathCat, this::updateRv);
                parserKinodom.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (cur_url.contains(Statics.KINOPUB_URL)) {
                if (cur_page > 1) {
                    if (cur_url.contains("?"))
                        url = curl + "&page=" + cur_page;
                    else url = curl + "?page=" + cur_page;
                } else url = curl;
                ParserKinopub parserKinopub = new ParserKinopub(url, itemsCat, itemPathCat, this::updateRv);
                parserKinopub.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (cur_url.contains(Statics.FILMIX_URL)) {
                ParserFilmix parserFilmix;
                if (category.contains("ПоискАктер")) {
                    if (!curl.trim().endsWith("/")) curl = curl.trim() +"/";
                    if (cur_page == 1) url = curl;
                    else if (cur_page > 1) url = curl + "page/" + cur_page+"/";
                    else url = curl;
                } else {
                    if (curl.contains("loader.php")){
                        if (cur_page > 1) url = curl + "%2Fpage%2F" + cur_page+"%2F&cstart="+cur_page;
                        else url = curl;
                    } else if (curl.contains("sphinx_search.php")) {
                        if (cur_page == 1) ItemMain.xs_value = "0";
                        else ItemMain.xs_value = String.valueOf(cur_page);
                        url = curl;
                    } else if (curl.contains("viewing")) {
                        if (cur_page == 1) url = curl;
                        else url = curl + "page/" + cur_page + "/";
                    } else {
                        url = curl;
                    }
                }
                parserFilmix = new ParserFilmix(url, itemsCat, itemPathCat, this::updateRv);
                parserFilmix.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (cur_url.contains(Statics.MYHIT_URL)) {
                if (cur_page == 1)
                    url = curl;
                else {
                    if (curl.contains("?q"))
                        url = curl + "&p=" + cur_page;
                    else
                        url = curl + "?p=" + cur_page;
                }

                ParserMyhit parser = new ParserMyhit(url, itemsCat, itemPathCat, this::updateRv);
                parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
    }

    private void pbGone(){
        if (category.contains("Поиск") && catalog.equals("all")){
            if (!base.contains("kinoxa") &&
                    !base.contains("kinofs") &&
                    !base.contains("koshara") &&
                    !base.contains("rufilmtv") &&
                    !base.contains("topkino") &&
                    !base.contains("my-hit") &&
                    !base.contains("filmix")) {
                pb.setVisibility(View.GONE);
            } else pb.setVisibility(View.VISIBLE);
        } else
            pb.setVisibility(View.GONE);
    }

    private void updateRv(ArrayList<ItemHtml> items, ItemHtml itempath) {
        pbGone();
        if (itempath.url.size() > 0) {

//            SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
            boolean all = category.contains("Поиск") && catalog.equals("all");

//            Log.e("test", "updateRv: "+itempath.title.toString());
//            Log.e("test", "updateRv1: "+all);
//            Log.e("test", "updateRv2: "+itempath.title.toString().split(itempath.title.get(itempath.title.size() - 1)
//                    .replace("(","\\(")
//                    .replace(")","\\)")).length);

            int c = 3;
            if (itempath.url.size() < 3 || itempath.url.size() > 10)
                c = 4;

            if (itempath.url.toString().split(itempath.url.get(itempath.url.size() - 1)
                    .replace("(","\\(")
                    .replace(")","\\)")).length >= c && !all){
            } else
                updRv(items, itempath);
        }
    }
    private void updRv(ArrayList<ItemHtml> items, ItemHtml itempath) {
        if (!itempath.title.toString().contains("error")) {
            itemPathCat = itempath;
            itemsCat = items;

            ((AdapterCatalog) rv_catalog.getAdapter()).setHtmlItems(items);
            rv_catalog.getRecycledViewPool().clear();
            rv_catalog.getAdapter().notifyItemChanged(0);
        } else {
//            Log.e(TAG, "updateRv1: "+itempath.title.toString());
        }
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        rv_catalog.setLayoutManager(new GridLayoutManager(getContext(), new Utils().calculateGrid(getContext())));
//    }
}
