package com.kinotor.tiar.kinotor.ui.fragments;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.parser.GetQualBluRay;
import com.kinotor.tiar.kinotor.ui.DetailActivity;
import com.kinotor.tiar.kinotor.ui.ImgActivity;
import com.kinotor.tiar.kinotor.utils.DBHelper;
import com.kinotor.tiar.kinotor.utils.Utils;
import com.kinotor.tiar.kinotor.utils.adapters.AdapterMore;
import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 04.2018.
 */
public class DetailInfo extends Fragment {
    private static ItemHtml item;
    private RecyclerView rv_catalog;

    public static DetailInfo newInstance(ItemHtml items) {
        item = items;
        return new DetailInfo();
    }


    public DetailInfo() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        int sizetext = 16;
        Utils utils = new Utils();
        if (getActivity() != null) {
            sizetext = Integer.parseInt(PreferenceManager
                    .getDefaultSharedPreferences(getActivity())
                    .getString("text_size_detail", "16"));
            LinearLayout portret = rootView.findViewById(R.id.portret_info);
            if (!utils.isTablet(getActivity())||(utils.isTablet(getActivity()) &&
                    getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)) {
                portret.setVisibility(View.VISIBLE);
            } else {
                portret.setVisibility(View.GONE);
            }
            ImageView poster = rootView.findViewById(R.id.imgPoster_d);
            ImageView preimgFirst = rootView.findViewById(R.id.img_pre_1);
            ImageView preimgSecond = rootView.findViewById(R.id.img_pre_2);
            ImageView preimgThird = rootView.findViewById(R.id.img_pre_3);
            TextView title_portret = rootView.findViewById(R.id.title_portret);
            TextView title_desc = rootView.findViewById(R.id.title_desc);
            TextView t_year = rootView.findViewById(R.id.year);
            TextView t_quality = rootView.findViewById(R.id.quality);
            TextView t_country = rootView.findViewById(R.id.country);
            TextView t_genre = rootView.findViewById(R.id.genre);
            TextView t_time = rootView.findViewById(R.id.time);
            TextView t_translator = rootView.findViewById(R.id.translator);
            TextView t_description = rootView.findViewById(R.id.desc_inf);
            TextView t_rating = rootView.findViewById(R.id.rating_inf);
            TextView t_director = rootView.findViewById(R.id.director);
            TextView t_actor = rootView.findViewById(R.id.actor);
            TextView t_more = rootView.findViewById(R.id.more);

            LinearLayout l_year = rootView.findViewById(R.id.l_year);
            LinearLayout l_quality = rootView.findViewById(R.id.l_quality);
            LinearLayout l_translator = rootView.findViewById(R.id.l_translator);
            LinearLayout l_country = rootView.findViewById(R.id.l_country);
            LinearLayout l_genre = rootView.findViewById(R.id.l_genre);
            LinearLayout l_time = rootView.findViewById(R.id.l_time);
            LinearLayout l_description = rootView.findViewById(R.id.l_description);
            LinearLayout l_rating = rootView.findViewById(R.id.l_rating);
            LinearLayout l_rating2 = rootView.findViewById(R.id.l_rating2);
            LinearLayout l_director = rootView.findViewById(R.id.l_director);
            LinearLayout l_actor = rootView.findViewById(R.id.l_actor);


            TextView t_year_t = rootView.findViewById(R.id.year_t);
            TextView t_quality_t = rootView.findViewById(R.id.quality_t);
            TextView t_country_t = rootView.findViewById(R.id.country_t);
            TextView t_genre_t = rootView.findViewById(R.id.genre_t);
            TextView t_time_t = rootView.findViewById(R.id.time_t);
            TextView t_translator_t = rootView.findViewById(R.id.translator_t);
            TextView t_director_t = rootView.findViewById(R.id.director_s_t);
            TextView t_rating_t = rootView.findViewById(R.id.rating_s_t);
            TextView t_actor_t = rootView.findViewById(R.id.actor_s_t);
            TextView t_director_s = rootView.findViewById(R.id.director_s);
            TextView t_rating_s = rootView.findViewById(R.id.rating_s);
            TextView t_rating2 = rootView.findViewById(R.id.rating2);
            TextView t_actor_s = rootView.findViewById(R.id.actor_s);
            LinearLayout l_director_s = rootView.findViewById(R.id.l_director_s);
            LinearLayout l_rating_s = rootView.findViewById(R.id.l_rating_s);
            LinearLayout l_actor_s = rootView.findViewById(R.id.l_actor_s);

            //text size
            title_portret.setTextSize(sizetext + 2);
            title_desc.setTextSize(sizetext + 2);
            t_year.setTextSize(sizetext);
            t_country.setTextSize(sizetext);
            t_genre.setTextSize(sizetext);
            t_time.setTextSize(sizetext);
            t_quality.setTextSize(sizetext);
            t_translator.setTextSize(sizetext);
            t_description.setTextSize(sizetext);
            t_rating.setTextSize(sizetext);
            t_director.setTextSize(sizetext);
            t_actor.setTextSize(sizetext);
            if (t_actor_s != null) {
                t_year_t.setTextSize(sizetext);
                t_country_t.setTextSize(sizetext);
                t_genre_t.setTextSize(sizetext);
                t_time_t.setTextSize(sizetext);
                t_quality_t.setTextSize(sizetext);
                t_translator_t.setTextSize(sizetext);
                t_director_t.setTextSize(sizetext);
                t_director_s.setTextSize(sizetext);
                t_actor_t.setTextSize(sizetext);
                t_actor_s.setTextSize(sizetext);
                t_rating_s.setTextSize(sizetext);
                t_rating2.setTextSize(sizetext);
                t_rating_t.setTextSize(sizetext);
            }

            if (item != null) {
                if (!item.getTitle(0).contains("error")) {
                    String tt = item.getTitle(0).contains("(") ? item.getTitle(0).split("\\(")[0] :
                            item.getTitle(0);
                    title_portret.setText(tt);
                    if (item.getSubTitle(0) != null)
                        if (!item.getSubTitle(0).contains("error")) {
                            if (getActivity() != null)
                                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(item.getSubTitle(0));
                            title_portret.setText(tt + " / " + item.getSubTitle(0));
                        }
                    Log.d(TAG, "setInfo: " + item.getTitle(0));

                    String tab = "\t";
                    if (t_actor_s != null) {
                        t_year.setText(Html.fromHtml("<b>Год:</b> " + tab + item.getDate(0).trim().replace("\n", "/")
                                .replace("/"," / ")));
                        t_country.setText(Html.fromHtml("<b>Страна:</b> " + tab + item.getCountry(0)));
                        t_genre.setText(Html.fromHtml("<b>Жанр:</b> " + tab + item.getGenre(0)));
                        t_time.setText(Html.fromHtml("<b>Время:</b> " + tab + item.getTime(0)));
                        t_quality.setText(Html.fromHtml("<b>Качество:</b> " + tab + item.getQuality(0)));
                        if (PreferenceManager
                                .getDefaultSharedPreferences(getActivity()).getBoolean("check_qual", false)) {
                            GetQualBluRay getQualBluRay = new GetQualBluRay(item.getSubTitle(0), location -> {
                                if (!location.equals("null"))
                                    t_quality.setText(Html.fromHtml("<b>Качество:</b> " + tab + location));
                                else
                                    t_quality.setText(Html.fromHtml("<b>Качество:</b> " + tab + item.getQuality(0) + "!"));
                            });
                            getQualBluRay.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                        t_translator.setText(Html.fromHtml("<b>Звук:</b> " + tab + item.getVoice(0)));
                        t_description.setText(tab + item.getDescription(0).replace("<br>", tab));
                        t_director.setText(tab + item.getDirector(0));
                        t_rating.setText(tab + item.getRating(0));
                        t_actor.setText(tab + item.getActors(0).trim());


                        t_director_s.setText(Html.fromHtml("<b>Режиссёр:</b> " + tab + item.getDirector(0).trim()));
                        t_actor_s.setText(Html.fromHtml("<b>В Ролях:</b> " + tab + item.getActors(0).trim()));
                        t_rating_s.setText(Html.fromHtml("<b>Рейтинг:</b> " + tab + item.getRating(0).trim()));
                        t_rating2.setText(Html.fromHtml("<b>Рейтинг:</b> " + tab + item.getRating(0)));

                        l_director_s.setVisibility(Utils.boolToVisible(!item.getDirector(0).contains("error")));
                        l_actor_s.setVisibility(Utils.boolToVisible(!item.getActors(0).contains("error")));
                        l_rating_s.setVisibility(Utils.boolToVisible(!item.getRating(0).contains("error")));
                    } else {

                        t_year.setText(tab + item.getDate(0).trim().replace("\n", "/")
                                .replace("/", " / "));
                        t_country.setText(tab + item.getCountry(0));
                        t_genre.setText(tab + item.getGenre(0));
                        t_time.setText(tab + item.getTime(0));
                        t_quality.setText(tab + item.getQuality(0));
                        if (PreferenceManager
                                .getDefaultSharedPreferences(getActivity()).getBoolean("check_qual", false)) {
                            GetQualBluRay getQualBluRay = new GetQualBluRay(item.getSubTitle(0), location -> {
                                if (!location.equals("null")) t_quality.setText(tab + location);
                                else t_quality.setText(tab + item.getQuality(0) + "!");
                            });
                            getQualBluRay.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                        t_translator.setText(tab + item.getVoice(0));
                        t_description.setText(tab + item.getDescription(0).replace("<br>", tab));
                        t_rating.setText(tab + item.getRating(0));
                        t_director.setText(tab + item.getDirector(0));
                        t_actor.setText(tab + item.getActors(0).trim());
                    }
                    l_year.setVisibility(Utils.boolToVisible(!item.getDate(0).contains("error")));
                    l_country.setVisibility(Utils.boolToVisible(!item.getCountry(0).contains("error")));
                    l_genre.setVisibility(Utils.boolToVisible(!item.getGenre(0).contains("error")));
                    l_time.setVisibility(Utils.boolToVisible(!item.getTime(0).contains("error")));
                    l_quality.setVisibility(Utils.boolToVisible(!item.getQuality(0).contains("error")));
                    l_translator.setVisibility(Utils.boolToVisible(!item.getVoice(0).contains("error")));
                    l_description.setVisibility(Utils.boolToVisible(!item.getDescription(0).contains("error")));

                    if (portret.getVisibility() == View.VISIBLE) {
                        l_director.setVisibility(View.GONE);
                        l_actor.setVisibility(View.GONE);
                        l_rating.setVisibility(View.GONE);
                    } else {
                        l_director.setVisibility(Utils.boolToVisible(!item.getDirector(0).contains("error")));
                        l_actor.setVisibility(Utils.boolToVisible(!item.getActors(0).contains("error")));
                        l_rating.setVisibility(Utils.boolToVisible(!item.getRating(0).contains("error")));
                    }
                    if (t_actor_s != null) {
                        if (!item.getRating(0).contains("error") &&
                                (item.getVoice(0).contains("error") || item.getTime(0).contains("error") ||
                                        item.getCountry(0).contains("error") || item.getQuality(0).contains("error"))) {
                            l_rating2.setVisibility(View.VISIBLE);
                            l_rating_s.setVisibility(View.GONE);
                        } else l_rating2.setVisibility(View.GONE);
//                        if (!item.getRating(0).contains("error") &&
//                                (item.getCountry(0).contains("error") || item.getQuality(0).contains("error"))) {
//                            l_rating2.setVisibility(View.VISIBLE);
//                            l_rating_s.setVisibility(View.GONE);
//                        } else l_rating2.setVisibility(View.GONE);
                    }
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

                    Picasso.get()
                            .load(item.getImg(0))
                            .into(poster);


                    ((AdapterMore) rv_catalog.getAdapter()).setHtmlItems(item);
                    rv_catalog.getRecycledViewPool().clear();
                    rv_catalog.getAdapter().notifyItemChanged(0);

                    if (item.preimg.size() > 0)
                        Picasso.get()
                                .load(item.preimg.get(0).replace("_original.", "_small."))
                                .into(preimgFirst);
                    else
                        Picasso.get()
                                .load("http://leeford.in/wp-content/uploads/2017/09/image-not-found.jpg")
                                .into(preimgFirst);
                    if (item.preimg.size() > 1)
                        Picasso.get()
                                .load(item.preimg.get(1).replace("_original.", "_small."))
                                .into(preimgSecond);
                    else preimgSecond.setVisibility(View.GONE);
                    if (item.preimg.size() > 2)
                        Picasso.get()
                                .load(item.preimg.get(2).replace("_original.", "_small."))
                                .into(preimgThird);
                    else preimgThird.setVisibility(View.GONE);
                    if (item.preimg.size() < 2 || DetailActivity.url.toLowerCase().contains(Statics.KOSHARA_URL)) {
                        TextView preimgT = rootView.findViewById(R.id.preimgT);
                        preimgT.setVisibility(View.GONE);
                        LinearLayout preimgL = rootView.findViewById(R.id.preimgV);
                        preimgL.setVisibility(View.GONE);
                    }
                    if (portret.getVisibility() != View.GONE) {
                        rootView.findViewById(R.id.r_img_last).setVisibility(View.GONE);
                    } else {
                        l_rating.setVisibility(View.GONE);
                        rootView.findViewById(R.id.r_img_last).setVisibility(View.VISIBLE);
                    }


                    if (getContext() != null) {
                        String imgs;
//                        Log.e("test", "setInfo: "+item.preimg.toString() +"|"+item.preimg.size());

                        if (item.preimg.toString().equals("[]"))
                            imgs = item.getImg(0) + ",http://leeford.in/wp-content/uploads/2017/09/image-not-found.jpg";
                        else imgs = item.getImg(0) + "," + item.preimg.toString().replace("[","")
                                .replace("]","");
//                        Log.e("test", "setInfo: "+imgs);

                        preimgFirst.setFocusable(true);
                        preimgFirst.setOnFocusChangeListener((view, b) -> {
                            if (!view.isSelected()) {
                                rootView.findViewById(R.id.img_fg_pre_1).setVisibility(View.VISIBLE);
                            } else rootView.findViewById(R.id.img_fg_pre_1).setVisibility(View.GONE);
                            view.setSelected(b);
                        });
                        preimgFirst.setOnClickListener(view -> onPreImg(imgs, 1));
                        preimgSecond.setFocusable(true);
                        preimgSecond.setOnFocusChangeListener((view, b) -> {
                            if (!view.isSelected()) {
                                rootView.findViewById(R.id.img_fg_pre_2).setVisibility(View.VISIBLE);
                            }
                            else rootView.findViewById(R.id.img_fg_pre_2).setVisibility(View.GONE);
                            view.setSelected(b);
                        });
                        preimgSecond.setOnClickListener(view -> onPreImg(imgs, 2));
                        preimgThird.setFocusable(true);
                        preimgThird.setOnFocusChangeListener((view, b) -> {
                            if (!view.isSelected()) {
                                rootView.findViewById(R.id.img_fg_pre_3).setVisibility(View.VISIBLE);
                            }
                            else rootView.findViewById(R.id.img_fg_pre_3).setVisibility(View.GONE);
                            view.setSelected(b);
                        });
                        preimgThird.setOnClickListener(view -> onPreImg(imgs, 3));
                        poster.setOnClickListener(view -> onPreImg(imgs, 0));
                    }
//                    title_portret.setFocusable(true);
//                    title_portret.requestFocus();

                    if (item.moretitle.size() < 2)
                        t_more.setVisibility(View.GONE);
                    getActivity().findViewById(R.id.detail_pb).setVisibility(View.GONE);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogStyle);
                    builder.setMessage("Ошибка загрузки. Попробуйте поискать данное видео в другом каталоге.")
                            .setPositiveButton("Ок", (dialog, id) -> getActivity().finish());
                    builder.create().show();
                }
            } else {
                DBHelper dbHelper = new DBHelper(getContext());
                if (dbHelper.getRepeatCache(DetailActivity.url)) {
                    this.item = dbHelper.getDbItemsCache(DetailActivity.url);
                    setInfo(rootView);
                }
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null) {
            if (getActivity().findViewById(R.id.detail_pb) != null)
                getActivity().findViewById(R.id.detail_pb).setVisibility(View.GONE);
        }
    }

    private void onPreImg(String img, int pos){
        if (getContext() != null) {
            Intent intent = new Intent(getContext(), ImgActivity.class);
            intent.putExtra("Img", img);
            intent.putExtra("Position", pos);
            getContext().startActivity(intent);
        }
    }
}