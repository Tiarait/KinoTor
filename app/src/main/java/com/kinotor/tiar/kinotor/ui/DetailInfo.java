package com.kinotor.tiar.kinotor.ui;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.parser.ParserAmcet;
import com.kinotor.tiar.kinotor.parser.ParserHtml;
import com.kinotor.tiar.kinotor.parser.animevost.ParserAnimevost;
import com.kinotor.tiar.kinotor.utils.AdapterMore;
import com.kinotor.tiar.kinotor.utils.OnTaskCallback;
import com.kinotor.tiar.kinotor.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 04.2018.
 */
public class DetailInfo extends Fragment {
    private ItemHtml item;
    private RecyclerView rv_catalog;

    public DetailInfo() {
    }

    public DetailInfo(ItemHtml item) {
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
        final View view = inflater.inflate(R.layout.fragment_detail_inf, container, false);
        rv_catalog = view.findViewById(R.id.rv_more_d);
        rv_catalog.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        AdapterMore adapter = new AdapterMore(getContext());
        rv_catalog.setNestedScrollingEnabled(false);
        rv_catalog.setAdapter(adapter);
        Log.d("DetailActivity", "onCreateView: orientation "+ getResources().getConfiguration().orientation);
        setInfo(view);
        return view;
    }

    private void setInfo(final View rootView) {
        int sizetext = Integer.parseInt(PreferenceManager
                .getDefaultSharedPreferences(getContext())
                .getString("text_size_detail", "13"));
        Utils utils = new Utils();
        LinearLayout portret = rootView.findViewById(R.id.portret_info);
        if (utils.isTablet(getContext()) && getResources().getConfiguration().orientation == 2) {
            portret.setVisibility(View.GONE);
        } else portret.setVisibility(View.VISIBLE);
        ImageView poster = rootView.findViewById(R.id.imgPoster_d);
        ImageView posterBG = rootView.findViewById(R.id.imgPoster_bg);
        ImageView preimgFirst = rootView.findViewById(R.id.img_pre_1);
        ImageView preimgSecond = rootView.findViewById(R.id.img_pre_2);
        ImageView preimgThird = rootView.findViewById(R.id.img_pre_3);
        TextView title_portret = rootView.findViewById(R.id.title_portret);
        TextView title_desc = rootView.findViewById(R.id.title_desc);
        TextView t_year = rootView.findViewById(R.id.year);
        TextView t_country = rootView.findViewById(R.id.country);
        TextView t_genre = rootView.findViewById(R.id.genre);
        TextView t_time = rootView.findViewById(R.id.time);
        TextView t_quality = rootView.findViewById(R.id.quality);
        TextView t_tranlator = rootView.findViewById(R.id.translator);
        TextView t_description = rootView.findViewById(R.id.desc_inf);
        TextView t_director = rootView.findViewById(R.id.director);
        TextView t_actor = rootView.findViewById(R.id.actor);
        TextView t_more = rootView.findViewById(R.id.more);

        LinearLayout l_year = rootView.findViewById(R.id.l_year);
        LinearLayout l_country = rootView.findViewById(R.id.l_country);
        LinearLayout l_genre = rootView.findViewById(R.id.l_genre);
        LinearLayout l_time = rootView.findViewById(R.id.l_time);
        LinearLayout l_director = rootView.findViewById(R.id.l_director);
        LinearLayout l_description = rootView.findViewById(R.id.l_description);
        LinearLayout l_actor = rootView.findViewById(R.id.l_actor);

        //text size
        title_portret.setTextSize(sizetext + 2);
        title_desc.setTextSize(sizetext + 2);
        t_year.setTextSize(sizetext);
        t_country.setTextSize(sizetext);
        t_genre.setTextSize(sizetext);
        t_time.setTextSize(sizetext);
        t_quality.setTextSize(sizetext);
        t_tranlator.setTextSize(sizetext);
        t_description.setTextSize(sizetext);
        t_director.setTextSize(sizetext);
        t_actor.setTextSize(sizetext);

        if (item != null) {
            title_portret.setText(item.getTitle(0));
            if (item.getSubTitle(0) != null)
                if (!item.getSubTitle(0).contains("error")) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(item.getSubTitle(0));
                    title_portret.setText(item.getTitle(0) + " / " + item.getSubTitle(0));
                }
            Log.d(TAG, "setInfo: "+ item.getTitle(0));
            if (item.getDate(0).contains("error"))
                l_year.setVisibility(View.GONE);
            else t_year.setText(item.getDate(0));
            if (item.getCountry(0).contains("error"))
                l_country.setVisibility(View.GONE);
            else t_country.setText(item.getCountry(0));
            if (item.getGenre(0).contains("error"))
                l_genre.setVisibility(View.GONE);
            else t_genre.setText(item.getGenre(0));
            if (item.getTime(0).contains("error"))
                l_time.setVisibility(View.GONE);
            else t_time.setText(item.getTime(0));
            if (item.getQuality(0).contains("error"))
                t_quality.setVisibility(View.GONE);
            else t_quality.setText(item.getQuality(0));
            if (item.getVoice(0).contains("error"))
                t_tranlator.setVisibility(View.GONE);
            else t_tranlator.setText(item.getVoice(0));
            if (item.getDescription(0).contains("error"))
                l_description.setVisibility(View.GONE);
            else t_description.setText("\t" + item.getDescription(0));
            if (item.getDirector(0).contains("error"))
                l_director.setVisibility(View.GONE);
            else t_director.setText("\t" + item.getDirector(0));
            if (item.getActors(0).contains("error"))
                l_actor.setVisibility(View.GONE);
            else t_actor.setText("\t" + item.getActors(0));
            if (item.season.size() > 0 && item.series.size() > 0) {
                if (item.getSeason(0) == 0 && item.getSeries(0) == 0)
                    title_desc.setVisibility(View.GONE);
                else {
                    String curS = "";
                    if (item.getSeason(0) > 0)
                        curS += item.getSeason(0) + " сезон ";
                    if (item.getSeries(0) > 0)
                        curS += item.getSeries(0) + " серия ";
                    title_desc.setText(curS);
                }
            } else title_desc.setVisibility(View.GONE);

            Picasso.with(rootView.getContext())
                    .load(item.getImg(0))
                    .into(poster);
            Picasso.with(rootView.getContext())
                    .load(item.getImg(0))
                    .into(posterBG);


            ((AdapterMore) rv_catalog.getAdapter()).setHtmlItems(item);
            rv_catalog.getRecycledViewPool().clear();
            rv_catalog.getAdapter().notifyItemChanged(0);

            final String[] preimg = {"http://leeford.in/wp-content/uploads/2017/09/image-not-found.jpg"
                    , "http://leeford.in/wp-content/uploads/2017/09/image-not-found.jpg"
                    , "http://leeford.in/wp-content/uploads/2017/09/image-not-found.jpg"};
            if (item.preimg.size() > 0)
                if (!item.getPreImg(0).contains("error")) preimg[0] = item.getPreImg(0);
            if (item.preimg.size() > 1)
                if (!item.getPreImg(1).contains("error")) preimg[1] = item.getPreImg(1);
            if (item.preimg.size() > 2)
                if (!item.getPreImg(2).contains("error")) preimg[2] = item.getPreImg(2);
            Picasso.with(rootView.getContext())
                    .load(preimg[0])
                    .into(preimgFirst);
            Picasso.with(rootView.getContext())
                    .load(preimg[1])
                    .into(preimgSecond);
            Picasso.with(rootView.getContext())
                    .load(preimg[2])
                    .into(preimgThird);

            preimgFirst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ImageActivity.class);
                    intent.putExtra("Url", preimg[0]);
                    getContext().startActivity(intent);
                }
            });
            preimgSecond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ImageActivity.class);
                    intent.putExtra("Url", preimg[1]);
                    getContext().startActivity(intent);
                }
            });
            preimgThird.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ImageActivity.class);
                    intent.putExtra("Url", preimg[2]);
                    getContext().startActivity(intent);
                }
            });

            if (item.moretitle.size() < 2)
                t_more.setVisibility(View.GONE);

        } else {
            if (DetailActivity.url.contains("amcet")) {
                ParserAmcet parserAmcet = new ParserAmcet(DetailActivity.url, null, new ItemHtml(),
                        new OnTaskCallback() {
                            @Override
                            public void OnCompleted(ArrayList<ItemHtml> items, ItemHtml itempath) {
                                taskDone(itempath, rootView);
                            }
                        });
                parserAmcet.execute();
            } else if (DetailActivity.url.contains("animevost")) {
                ParserAnimevost parserAnimevost = new ParserAnimevost(DetailActivity.url, null, new ItemHtml(),
                        new OnTaskCallback() {
                            @Override
                            public void OnCompleted(ArrayList<ItemHtml> items, ItemHtml itempath) {
                                taskDone(itempath, rootView);
                            }
                        });
                parserAnimevost.execute();
            } else {
                ParserHtml parserHtml = new ParserHtml(DetailActivity.url, null, new ItemHtml(),
                        new OnTaskCallback() {
                            @Override
                            public void OnCompleted(ArrayList<ItemHtml> items, ItemHtml itempath) {
                                taskDone(itempath, rootView);
                            }
                        });
                parserHtml.execute();
            }
        }
    }

    private void taskDone(ItemHtml itempath, View rootView) {
        this.item = itempath;
        setInfo(rootView);
    }
}