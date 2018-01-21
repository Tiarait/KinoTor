package com.kinotor.tiar.kinotor.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.parser.ParserHtml;
import com.kinotor.tiar.kinotor.utils.AdapterCatalog;

import static com.kinotor.tiar.kinotor.items.ItemMain.cur_page;
import static com.kinotor.tiar.kinotor.items.ItemMain.cur_url;
import static com.kinotor.tiar.kinotor.items.ItemMain.isLoading;

public class MainCatalogFragment extends Fragment {
    private String url = "null";
    private String category = "фильмы";
    private RecyclerView rv_catalog;
    private ParserHtml parserHtml;
    private RelativeLayout pb;

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
        rv_catalog = (RecyclerView) rootView.findViewById(R.id.rv_catalog);
        pb = (RelativeLayout) rootView.findViewById(R.id.progresB);
        final Context context = rootView.getContext();
        final GridLayoutManager mLayoutManager;
        final String dB;
        if (category.equals("Избранное")) dB = "favor";
        else if  (category.equals("История")) dB = "history";
        else dB = "catalog";

        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(rv_catalog.getContext());
        int grid_size = Integer.parseInt(preference.getString("grid_catalog", "5"));
        if (grid_size == 0) grid_size = 5;

        mLayoutManager = new GridLayoutManager(context, grid_size);
        rv_catalog.setLayoutManager(mLayoutManager);
        AdapterCatalog adapter = new AdapterCatalog(context, dB) {
            @Override
            public void load() {
                if (!isLoading && dB.equals("catalog")) {
                    cur_page++;
                    onPage(context);
                }
            }
        };
        rv_catalog.setAdapter(adapter);

        if (!isLoading && dB.equals("catalog") && !url.equals("null")) {
            parserHtml = new ParserHtml(url, "catalog", rv_catalog, pb);
            parserHtml.execute();
        }
        return rootView;
    }

    public void onPage(Context context){
        if (cur_url.contains("koshara.co")) {
            if (!category.equals("Мультфильмы"))
                parserHtml = new ParserHtml(cur_url + "page/" + cur_page + "/", "catalog", rv_catalog, pb);
            else
                parserHtml = new ParserHtml(cur_url + "&search_start=" + cur_page, "catalog", rv_catalog, pb);
            parserHtml.execute();
        } else if (cur_url.contains("coldfilm.ru")){
            if (category.contains("Поиск")) {
                parserHtml = new ParserHtml(cur_url + "&m=news&t=0&p=" + cur_page, "catalog", rv_catalog, pb);
                parserHtml.execute();
            } else {
                parserHtml = new ParserHtml(cur_url + "?page" + cur_page, "catalog", rv_catalog, pb);
                parserHtml.execute();
            }
        }

        Toast.makeText(context,
                "стр. " + String.valueOf(cur_page),
                Toast.LENGTH_LONG).show();
    }
}
