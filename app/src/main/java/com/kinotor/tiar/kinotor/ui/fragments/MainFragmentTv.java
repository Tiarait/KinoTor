package com.kinotor.tiar.kinotor.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
import com.kinotor.tiar.kinotor.parser.catalog.ParserKinoxa;
import com.kinotor.tiar.kinotor.parser.catalog.ParserKoshara;
import com.kinotor.tiar.kinotor.parser.catalog.ParserMyhit;
import com.kinotor.tiar.kinotor.parser.catalog.ParserRufilmtv;
import com.kinotor.tiar.kinotor.parser.catalog.ParserTopkino;
import com.kinotor.tiar.kinotor.ui.BDActivity;
import com.kinotor.tiar.kinotor.ui.MainActivityTvCat;
import com.kinotor.tiar.kinotor.utils.adapters.AdapterCatalog;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class MainFragmentTv extends Fragment {
    private String curl = "null";
    private String category = "фильмы", catalog = "filmix";
    private ArrayList<ItemHtml> itemsCat = new ArrayList<>();
    private ItemHtml itemPathCat = new ItemHtml();
    private int cur_page = 1;
    private Context context;


    private RecyclerView rv_catalog;
    private LinearLayout pb, categoryView, categoryRefresh;
    private TextView title;
    private ImageView icon;


    public static MainFragmentTv newInstance(String url, String category, String catalog) {
        Bundle args = new Bundle();
        args.putString("url", url);
        args.putString("category", category);
        args.putString("catalog", catalog);
        MainFragmentTv f = new MainFragmentTv();
        f.setArguments(args);
        return f;
    }

    public MainFragmentTv() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_list_content_tv, container, false);
        rv_catalog = rootView.findViewById(R.id.rv_catalog);
        pb = rootView.findViewById(R.id.category_pb);
        title = rootView.findViewById(R.id.category_title);
        icon = rootView.findViewById(R.id.icon_list);
        categoryView = rootView.findViewById(R.id.category_view);
        categoryRefresh = rootView.findViewById(R.id.category_refresh);

        context = rootView.getContext();
        switch (category) {
            case "Фильмы":
                icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flat_filmclap));
                categoryView.setId(R.id.category_films);
                break;
            case "Сериалы":
                icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flat_serialvideo));
                categoryView.setId(R.id.category_serials);
                break;
            case "Мультфильмы":
                icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flat_mult));
                categoryView.setId(R.id.category_mults);
                break;
            case "Аниме":
                icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flat_animate));
                categoryView.setId(R.id.category_anime);
                break;
            case "ТВ Передачи":
                icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flat_tv));
                categoryView.setId(R.id.category_tv);
                break;
            case "Избранное":
                icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flat_favor));
                categoryView.setId(R.id.category_favor);
                break;
            case "История":
                icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flat_hist));
                categoryView.setId(R.id.category_history);
                break;
            case "ANIMEVOST":
                icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flat_serialvideo));
                categoryView.setId(R.id.category_animevost);
                break;
            case "ANIDUB":
                icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flat_serialvideo));
                categoryView.setId(R.id.category_anidub);
                break;
            case "COLDFILM":
                icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flat_serialvideo));
                categoryView.setId(R.id.category_coldfim);
                break;
            case "FANSERIALS":
                icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flat_serialvideo));
                categoryView.setId(R.id.category_fanserials);
                break;
            case "KINODOM":
                icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flat_serialvideo));
                categoryView.setId(R.id.category_kinodom);
                break;
            default:
                icon.setImageDrawable(getResources().getDrawable(R.drawable.ic_flat_serialvideo));
                break;
        }

        title.setText(category.toUpperCase());
        categoryView.setFocusable(true);
        categoryRefresh.setFocusable(true);
        title.setTextColor(getResources().getColor(R.color.colorWhiteTr));
        categoryView.setOnLongClickListener(view -> {
            categoryRefresh.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);
            cur_page = 1;
            onPage();
            return true;
        });
        categoryRefresh.setOnClickListener(view -> {
            categoryRefresh.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);
            cur_page = 1;
            onPage();
        });
        categoryRefresh.setOnFocusChangeListener((view, b) -> {
            if (!view.isSelected()) {
                view.setScaleX(1.2f);
                view.setScaleY(1.2f);
            } else {
                view.setScaleX(1f);
                view.setScaleY(1f);
            }
            view.setSelected(b);
        });
        categoryView.setOnClickListener(view -> {
            switch (category) {
                case "Избранное": {
                    Intent intent = new Intent(context, BDActivity.class);
                    intent.putExtra("Status", "favor");
                    startActivity(intent);
                    break;
                }
                case "История": {
                    Intent intent = new Intent(context, BDActivity.class);
                    intent.putExtra("Status", "history");
                    startActivity(intent);
                    break;
                }
                default: {
                    ItemMain.cur_items = 0;
                    Intent intent = new Intent(context, MainActivityTvCat.class);
                    intent.putExtra("Url", curl);
                    intent.putExtra("Category", category);
                    intent.putExtra("Catalog", catalog);
                    startActivity(intent);
                    break;
                }
            }
        });
        categoryView.setOnFocusChangeListener((view, b) -> {
            if (!view.isSelected()) {
                if (category.equals("Фильмы") && getActivity() != null) {
                    ScrollView scroll = getActivity().findViewById(R.id.scrol);
                    scroll.scrollTo(0,0);
                }
                view.setScaleX(1.2f);
                view.setScaleY(1.2f);
                title.setTextColor(getResources().getColor(R.color.colorWhite));
            } else {
                view.setScaleX(1f);
                view.setScaleY(1f);
                title.setTextColor(getResources().getColor(R.color.colorWhiteTr));
            }
            view.setSelected(b);
        });
        categoryView.setNextFocusRightId(R.id.rv_catalog);
        rv_catalog.setNextFocusRightId(R.id.category_view);
        final String dB;
        switch (category) {
            case "Фильмы":
                dB = "catalog newtv";
                categoryView.requestFocus();
                break;
            case "Избранное":
                pb.setVisibility(View.GONE);
                if (BDActivity.show != null)
                    dB = "favor newtv|" + BDActivity.show;
                else dB = "favor newtv";
                break;
            case "История":
                pb.setVisibility(View.GONE);
                if (BDActivity.show != null)
                    dB = "history newtv|" + BDActivity.show;
                else dB = "history newtv";
                break;
            case "Посмотреть позже":
                if (BDActivity.show != null)
                    dB = "later newtv|" + BDActivity.show;
                else dB = "later newtv";
                break;
            default:
                dB = "catalog newtv";
                break;
        }

        LinearLayoutManager gl = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        rv_catalog.setLayoutManager(gl);
        AdapterCatalog adapter = new AdapterCatalog(context, dB) {
            @Override
            public void load() {
                if (dB.contains("catalog") && !curl.equals("null")) {
                    cur_page++;
                    Log.d(TAG, "load: cur_page - " + cur_page);
                    onPage();
                }
            }

            @Override
            public void key(int keyCode, int position) {
//                position = position + 1;
                Log.e(TAG, "key: "+rv_catalog.getAdapter().getItemCount());
                Log.e(TAG, "key: position -"+position);
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        focusDown(category);
                        break;
                    case KeyEvent.KEYCODE_DPAD_UP:
                        focusUp(category);
                        break;
                }
            }
        };
        rv_catalog.setAdapter(adapter);
        rv_catalog.requestDisallowInterceptTouchEvent(false);

        if (curl != null)
            if (dB.contains("catalog") && !curl.equals("null")) {
                onPage();
            }
        return rootView;
    }

    private void focusUp(String category) {
        if (getActivity() != null) {
            switch (category) {
                case "Фильмы":
                    categoryView.requestFocus();
                    break;
                case "Сериалы":
                    if (getActivity().findViewById(R.id.category_films) != null)
                        getActivity().findViewById(R.id.category_films).requestFocus();
                    else focusUp("Фильмы");
                    break;
                case "Мультфильмы":
                    if (getActivity().findViewById(R.id.category_serials) != null)
                        getActivity().findViewById(R.id.category_serials).requestFocus();
                    else focusUp("Сериалы");
                    break;
                case "Аниме":
                    if (getActivity().findViewById(R.id.category_mults) != null)
                        getActivity().findViewById(R.id.category_mults).requestFocus();
                    else focusUp("Мультфильмы");
                    break;
                case "ТВ Передачи":
                    if (getActivity().findViewById(R.id.category_anime) != null)
                        getActivity().findViewById(R.id.category_anime).requestFocus();
                    else focusUp("Аниме");
                    break;
                case "Избранное":
                    if (getActivity().findViewById(R.id.category_tv) != null)
                        getActivity().findViewById(R.id.category_tv).requestFocus();
                    else focusUp("ТВ Передачи");
                    break;
                case "История":
                    if (getActivity().findViewById(R.id.category_favor) != null)
                        getActivity().findViewById(R.id.category_favor).requestFocus();
                    else focusUp("Избранное");
                    break;
                case "ANIMEVOST":
                    if (getActivity().findViewById(R.id.category_history) != null)
                        getActivity().findViewById(R.id.category_history).requestFocus();
                    else focusUp("История");
                    break;
                case "ANIDUB":
                    if (getActivity().findViewById(R.id.category_animevost) != null)
                        getActivity().findViewById(R.id.category_animevost).requestFocus();
                    else focusUp("ANIMEVOST");
                    break;
                case "COLDFILM":
                    if (getActivity().findViewById(R.id.category_anidub) != null)
                        getActivity().findViewById(R.id.category_anidub).requestFocus();
                    else focusUp("ANIDUB");
                    break;
                case "FANSERIALS":
                    if (getActivity().findViewById(R.id.category_coldfim) != null)
                        getActivity().findViewById(R.id.category_coldfim).requestFocus();
                    else focusUp("COLDFILM");
                    break;
                case "KINODOM":
                    if (getActivity().findViewById(R.id.category_fanserials) != null)
                        getActivity().findViewById(R.id.category_fanserials).requestFocus();
                    else focusUp("FANSERIALS");
                    break;
                default:
                    categoryView.requestFocus();
                    break;
            }
        } else categoryView.requestFocus();
    }

    private void focusDown(String category) {
        if (getActivity() != null) {
            switch (category) {
                case "Фильмы":
                    if (getActivity().findViewById(R.id.category_serials) != null)
                        getActivity().findViewById(R.id.category_serials).requestFocus();
                    else focusDown("Сериалы");
                    break;
                case "Сериалы":
                    if (getActivity().findViewById(R.id.category_mults) != null)
                        getActivity().findViewById(R.id.category_mults).requestFocus();
                    else focusDown("Мультфильмы");
                    break;
                case "Мультфильмы":
                    if (getActivity().findViewById(R.id.category_anime) != null)
                        getActivity().findViewById(R.id.category_anime).requestFocus();
                    else focusDown("Аниме");
                    break;
                case "Аниме":
                    if (getActivity().findViewById(R.id.category_tv) != null)
                        getActivity().findViewById(R.id.category_tv).requestFocus();
                    else focusDown("ТВ Передачи");
                    break;
                case "ТВ Передачи":
                    if (getActivity().findViewById(R.id.category_favor) != null)
                        getActivity().findViewById(R.id.category_favor).requestFocus();
                    else focusDown("Избранное");
                    break;
                case "Избранное":
                    if (getActivity().findViewById(R.id.category_history) != null)
                        getActivity().findViewById(R.id.category_history).requestFocus();
                    else focusDown("История");
                    break;
                case "История":
                    if (getActivity().findViewById(R.id.category_animevost) != null) {
                        getActivity().findViewById(R.id.category_animevost).requestFocus();
                    }
                    else {
                        focusDown("ANIMEVOST");
                    }
                    break;
                case "ANIMEVOST":
                    if (getActivity().findViewById(R.id.category_anidub) != null)
                        getActivity().findViewById(R.id.category_anidub).requestFocus();
                    else focusDown("ANIDUB");
                    break;
                case "ANIDUB":
                    if (getActivity().findViewById(R.id.category_coldfim) != null)
                        getActivity().findViewById(R.id.category_coldfim).requestFocus();
                    else focusDown("COLDFILM");
                    break;
                case "COLDFILM":
                    if (getActivity().findViewById(R.id.category_fanserials) != null)
                        getActivity().findViewById(R.id.category_fanserials).requestFocus();
                    else focusDown("FANSERIALS");
                    break;
                case "FANSERIALS":
                    if (getActivity().findViewById(R.id.category_kinodom) != null)
                        getActivity().findViewById(R.id.category_kinodom).requestFocus();
                    else focusDown("KINODOM");
                    break;
                case "KINODOM":
                    categoryView.requestFocus();
                    break;
                default:
                    categoryView.requestFocus();
                    break;
            }
        } else categoryView.requestFocus();
    }

    public void onPage(){
        String url;
        if (category.equals("Избранное") || category.equals("История"))
            pb.setVisibility(View.GONE);
        else pb.setVisibility(View.VISIBLE);

        if (category.contains("filmix_fav")) {
            curl = Statics.FILMIX_URL + "/favorites";
            if (cur_page > 1) url = curl + "/page/" + cur_page;
            else url = curl;
            ParserFilmix parserFilmix = new ParserFilmix(url, itemsCat, itemPathCat, this::updateRv);
            parserFilmix.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (category.contains("filmix_later")) {
            curl = Statics.FILMIX_URL + "/watch_later";
            if (cur_page > 1) url = curl + "/page/" + cur_page;
            else url = curl;
            ParserFilmix parserFilmix = new ParserFilmix(url, itemsCat, itemPathCat, this::updateRv);
            parserFilmix.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            Log.d(TAG, "test onPage: "+ Statics.CATALOG);
            switch (catalog) {
                case "koshara":
                    ParserKoshara parserKosara;
                    if (category.equals("Мультфильмы")) url = curl + "&search_start=" + cur_page;
                    else url = curl + "page/" + cur_page + "/";
                    parserKosara = new ParserKoshara(url, itemsCat, itemPathCat, this::updateRv);
                    parserKosara.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                case "coldfilm":
                    url = curl + "?page" + cur_page;
                    ParserColdfilm parserHtml = new ParserColdfilm(url, itemsCat, itemPathCat, this::updateRv);
                    parserHtml.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
//                case "amcet":
//                    ParserAmcet parserAmcet;
//                    url = curl + "page/" + cur_page + "/";
//                    parserAmcet = new ParserAmcet(url, itemsCat, itemPathCat, this::updateRv);
//                    parserAmcet.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                    break;
                case "animevost":
                    ParserAnimevost parserAnimevost;
                    if (cur_page == 1) url = curl;
                    else url = curl + "page/" + cur_page + "/";
                    parserAnimevost = new ParserAnimevost(url, itemsCat, itemPathCat, this::updateRv);
                    parserAnimevost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                case "anidub":
                    ParserAnidub parserAnidub;
                    if (cur_page == 1) url = curl;
                    else url = curl + "page/" + cur_page + "/";
                    parserAnidub = new ParserAnidub(url, itemsCat, itemPathCat, this::updateRv);
                    parserAnidub.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                case "kinofs": {
                    ParserKinoFS parserKinoFS;
                    if (curl.contains("top_100") && cur_page == 1) url = curl;
                    else url = curl + "-" + cur_page;

                    if (!ItemMain.xs_value.equals("") && !ItemMain.xs_value.equals("0"))
                        url = url + "-" + ItemMain.xs_value.trim();
                    parserKinoFS = new ParserKinoFS(url, itemsCat, itemPathCat, this::updateRv);
                    parserKinoFS.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                }
                case "kinoxa": {
                    if (cur_page == 1)
                        url = curl;
                    else url = curl + "/page/" + cur_page + "/";
                    ParserKinoxa parser = new ParserKinoxa(url, itemsCat, itemPathCat, this::updateRv);
                    parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                }
                case "rufilmtv": {
                    String search = "";
                    if (cur_page == 1)
                        url = curl;
                    else url = curl + "/page/" + cur_page;
                    ParserRufilmtv parser = new ParserRufilmtv(url + search, itemsCat, itemPathCat, this::updateRv);
                    parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                }
                case "topkino": {
                    String search = "";
                    if (cur_page == 1)
                        url = curl;
                    else url = curl + "/page/" + cur_page + "/";

                    ParserTopkino parser = new ParserTopkino(url + search, itemsCat, itemPathCat, this::updateRv);
                    parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                }
                case "fanserials": {
                    url = curl + "page/" + cur_page + "/";
                    ParserFanserials parserFanserials = new ParserFanserials(url, itemsCat, itemPathCat, this::updateRv);
                    parserFanserials.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                }
                case "kinodom": {
                    if (cur_page > 1)
                        url = curl + "page/" + cur_page + "/";
                    else url = curl;
                    ParserKinodom parserKinodom = new ParserKinodom(url, itemsCat, itemPathCat, this::updateRv);
                    parserKinodom.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                }
                case "filmix":
                    ParserFilmix parserFilmix;
                    if (curl.contains("loader.php")){
                        if (cur_page > 1) url = curl + "%2Fpage%2F" + cur_page+"%2F&cstart="+cur_page;
                        else url = curl;
                    } else {
                        url = curl;
                    }
                    parserFilmix = new ParserFilmix(url, itemsCat, itemPathCat, this::updateRv);
                    parserFilmix.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                case "my-hit": {
                    if (cur_page == 1)
                        url = curl;
                    else {
                        url = curl + "?p=" + cur_page;
                    }

                    ParserMyhit parser = new ParserMyhit(url, itemsCat, itemPathCat, this::updateRv);
                    parser.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    break;
                }
            }
        }
    }

    private void updateRv(ArrayList<ItemHtml> items, ItemHtml itempath) {
        pb.setVisibility(View.GONE);
        if (itempath.url.size() > 0) {
            int c = 3;
            if (itempath.url.size() < 3 || itempath.url.size() > 10)
                c = 4;

            SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
            boolean all = preference.getBoolean("search_all", false);
            if (itempath.url.toString().split(itempath.url.get(itempath.url.size() - 1)
                    .replace("(","\\(")
                    .replace(")","\\)")).length >= c && !all){
            } else
                updRv(items, itempath);
        } else categoryRefresh.setVisibility(View.VISIBLE);
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
}
