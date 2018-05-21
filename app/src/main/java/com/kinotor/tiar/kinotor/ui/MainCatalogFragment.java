package com.kinotor.tiar.kinotor.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.parser.ParserAmcet;
import com.kinotor.tiar.kinotor.parser.ParserHtml;
import com.kinotor.tiar.kinotor.parser.animevost.ParserAnimevost;
import com.kinotor.tiar.kinotor.utils.AdapterCatalog;
import com.kinotor.tiar.kinotor.utils.OnTaskCallback;
import com.kinotor.tiar.kinotor.utils.Utils;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.kinotor.tiar.kinotor.items.ItemMain.cur_url;

public class MainCatalogFragment extends Fragment {
    private String url = "null";
    private String category = "фильмы";
    RecyclerView rv_catalog;
    private RelativeLayout pb;
    private ArrayList<ItemHtml> itemsCat = new ArrayList<>();
    private ItemHtml itemPathCat = new ItemHtml();
    private int cur_page = 1;

    public MainCatalogFragment() {
    }

    public MainCatalogFragment(String url, String category) {
        this.url = url;
        this.category = category;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_catalog, container, false);
        rv_catalog = rootView.findViewById(R.id.rv_catalog);
        pb = rootView.findViewById(R.id.progresB);
        final Context context = rootView.getContext();
        final String dB;
        switch (category) {
            case "Избранное":
                dB = "favor";
                break;
            case "История":
                dB = "history";
                break;
            default:
                dB = "catalog";
                break;
        }
        rv_catalog.setLayoutManager(new GridLayoutManager(context, new Utils().calculateGrid(getContext(), 175)));
        AdapterCatalog adapter = new AdapterCatalog(context, dB) {
            @Override
            public void load() {
                if (dB.equals("catalog")) {
                    cur_page++;
                    Log.d(TAG, "load: cur_page - " + cur_page);
                    onPage(context);
                }
            }
        };
        rv_catalog.setAdapter(adapter);

        if (dB.equals("catalog") && !url.equals("null")) {
            onPage(context);
        }
        return rootView;
    }

    public void onPage(Context context){
        ParserHtml parserHtml;
        String url;
        pb.setVisibility(View.VISIBLE);
        if (cur_url.contains("koshara")) {
            if (category.equals("Мультфильмы")) url = cur_url + "&search_start=" + cur_page;
            else if (category.contains("Поиск")) url = cur_url + "&search_start=" + cur_page + "/";
            else url = cur_url + "page/" + cur_page + "/";
            parserHtml = new ParserHtml(url, itemsCat, itemPathCat,
                    new OnTaskCallback() {
                        @Override
                        public void OnCompleted(ArrayList<ItemHtml> items, ItemHtml itempath) {
                            updateRv(items, itempath);
                        }
                    });
            parserHtml.execute();
        } else if (cur_url.contains("coldfilm")){
            if (category.contains("Поиск")) url = cur_url + "&m=news&t=0&p=" + cur_page;
            else url = cur_url + "?page" + cur_page;
            parserHtml = new ParserHtml(url, itemsCat, itemPathCat,
                    new OnTaskCallback() {
                        @Override
                        public void OnCompleted(ArrayList<ItemHtml> items, ItemHtml itempath) {
                            updateRv(items, itempath);
                        }
                    });
            parserHtml.execute();
        } else if (cur_url.contains("amcet")) {
            ParserAmcet parserAmcet;
            if (category.contains("ПоискАктер")) url = cur_url + "page/" + cur_page + "/";
            else if (category.contains("Поиск")) url = cur_url + "'page'" + cur_page;
            else url = cur_url + "page/" + cur_page + "/";
            parserAmcet = new ParserAmcet(url, itemsCat, itemPathCat,
                        new OnTaskCallback() {
                            @Override
                            public void OnCompleted(ArrayList<ItemHtml> items, ItemHtml itempath) {
                                updateRv(items, itempath);
                            }
                        });
            parserAmcet.execute();
        } else if (cur_url.contains("animevost")){
            ParserAnimevost parserAnimevost;
            if (category.contains("Поиск")) url = cur_url + "'page'" + cur_page;
            else url = cur_url + "page/" + cur_page + "/";
            parserAnimevost = new ParserAnimevost(url, itemsCat, itemPathCat,
                    new OnTaskCallback() {
                        @Override
                        public void OnCompleted(ArrayList<ItemHtml> items, ItemHtml itempath) {
                            updateRv(items, itempath);
                        }
                    });
            parserAnimevost.execute();
        }

        Toast.makeText(context,
                "стр. " + String.valueOf(cur_page),
                Toast.LENGTH_SHORT).show();
    }

    private void updateRv(ArrayList<ItemHtml> items, ItemHtml itempath) {
        pb.setVisibility(View.GONE);
        itemPathCat = itempath;
        itemsCat = items;
        ((AdapterCatalog) rv_catalog.getAdapter()).setHtmlItems(items);
        rv_catalog.getRecycledViewPool().clear();
        rv_catalog.getAdapter().notifyItemChanged(0);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        rv_catalog.setLayoutManager(new GridLayoutManager(getContext(), new Utils().calculateGrid(getContext(), 175)));
    }
}
