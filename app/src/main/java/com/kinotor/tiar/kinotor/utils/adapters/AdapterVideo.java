package com.kinotor.tiar.kinotor.utils.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemNewVideo;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.parser.GetKpId;
import com.kinotor.tiar.kinotor.parser.url.ParserKinoliveUrl;
import com.kinotor.tiar.kinotor.parser.video.FanserialsIframe;
import com.kinotor.tiar.kinotor.parser.video.kinoxa.KinoxaIframe;
import com.kinotor.tiar.kinotor.parser.video.RufilmtvIframe;
import com.kinotor.tiar.kinotor.parser.video.anidub.AnidubList;
import com.kinotor.tiar.kinotor.parser.video.anidub.AnidubUrl;
import com.kinotor.tiar.kinotor.parser.video.anidub.ParserAnidub;
import com.kinotor.tiar.kinotor.parser.video.animedia.AnimediaList;
import com.kinotor.tiar.kinotor.parser.video.animedia.AnimediaUrl;
import com.kinotor.tiar.kinotor.parser.video.animedia.ParserAnimedia;
import com.kinotor.tiar.kinotor.parser.video.animevost.AnimevostSeries;
import com.kinotor.tiar.kinotor.parser.video.animevost.AnimevostUrl;
import com.kinotor.tiar.kinotor.parser.video.animevost.ParserVAnimevost;
import com.kinotor.tiar.kinotor.parser.video.farsihd.FarsihdIframe;
import com.kinotor.tiar.kinotor.parser.video.farsihd.FarsihdList;
import com.kinotor.tiar.kinotor.parser.video.farsihd.FarsihdUrl;
import com.kinotor.tiar.kinotor.parser.video.filmix.FilmixList;
import com.kinotor.tiar.kinotor.parser.video.filmix.FilmixUrl;
import com.kinotor.tiar.kinotor.parser.video.filmix.ParserFilmix;
import com.kinotor.tiar.kinotor.parser.video.hdgo.HdgoIframe;
import com.kinotor.tiar.kinotor.parser.video.hdgo.HdgoSeason;
import com.kinotor.tiar.kinotor.parser.video.hdgo.HdgoSeries;
import com.kinotor.tiar.kinotor.parser.video.hdgo.HdgoUrl;
import com.kinotor.tiar.kinotor.parser.video.hdgo.ParserHdgo;
import com.kinotor.tiar.kinotor.parser.video.kinodom.KinodomIframeUrl;
import com.kinotor.tiar.kinotor.parser.video.kinodom.KinodomList;
import com.kinotor.tiar.kinotor.parser.video.kinodom.KinodomUrl;
import com.kinotor.tiar.kinotor.parser.video.kinodom.ParserKinodom;
import com.kinotor.tiar.kinotor.parser.video.kinohd.KinohdIframe;
import com.kinotor.tiar.kinotor.parser.video.kinohd.KinohdList;
import com.kinotor.tiar.kinotor.parser.video.kinohd.KinohdUrl;
import com.kinotor.tiar.kinotor.parser.video.kinolive.KinoliveList;
import com.kinotor.tiar.kinotor.parser.video.kinolive.KinoliveUrl;
import com.kinotor.tiar.kinotor.parser.video.kinolive.ParserKinolive;
import com.kinotor.tiar.kinotor.parser.video.kinopub.KinopubIframeUrl;
import com.kinotor.tiar.kinotor.parser.video.kinopub.KinopubList;
import com.kinotor.tiar.kinotor.parser.video.kinopub.KinopubUrl;
import com.kinotor.tiar.kinotor.parser.video.kinopub.ParserKinopubSearch;
import com.kinotor.tiar.kinotor.parser.video.kinosha.KinoshaList;
import com.kinotor.tiar.kinotor.parser.video.kinosha.ParserKinosha;
import com.kinotor.tiar.kinotor.parser.video.moonwalk.MoonwalkSeason;
import com.kinotor.tiar.kinotor.parser.video.moonwalk.MoonwalkSeries;
import com.kinotor.tiar.kinotor.parser.video.moonwalk.MoonwalkUrl;
import com.kinotor.tiar.kinotor.parser.video.moonwalk.ParserMoonwalk;
import com.kinotor.tiar.kinotor.parser.video.zombiefilm.ParserZombiefilm;
import com.kinotor.tiar.kinotor.parser.video.zombiefilm.ZombiefilmList;
import com.kinotor.tiar.kinotor.parser.video.zombiefilm.ZombiefilmUrl;
import com.kinotor.tiar.kinotor.ui.DetailActivity;
import com.kinotor.tiar.kinotor.utils.DBHelper;
import com.kinotor.tiar.kinotor.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public abstract class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.ViewHolder> {
    private final String CATALOG = "catalog";
    private final String SEASON = "season";
    private final String SERIES = "series";
    private final String ERROR = "error";
    private final String BACK = "back";
    private View view;
    private ItemHtml item;
    private ItemVideo items;
    private ArrayList<ItemNewVideo> newItemList = new ArrayList<>();
    private Context context;
    private LinearLayout pb;
    private int lastFocussedPosition = -1;


    protected AdapterVideo(Context context, ItemHtml item, LinearLayout pb) {
        this.item = item;
        this.context = context;
        this.pb = pb;
    }

    @NonNull
    @Override
    public AdapterVideo.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_vid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterVideo.ViewHolder holder, int position) {
        final ItemNewVideo cur = newItemList.get(position);
//        if (cur == 0)
//            holder.mView.setFocusable(true);
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        DBHelper dbHelper = new DBHelper(context);
        int sizetext = Integer.parseInt(preference.getString("text_size_detail", "13"));

        if (cur.getTitle().contains(CATALOG)) {
            holder.name.setText(cur.getTranslator());
            holder.desc.setText(cur.getType());
            holder.description.setText(cur.getDescription());
            if (!cur.getSeasons_count().trim().contains(ERROR)) {
                if (cur.getType().contains("filmix") &&
                        preference.getBoolean("sync_filmix_watch", false) &&
                        !Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted")) {
                    if (Utils.checkFilmixHist(item.getUrl(0), cur.getTranslator(), "", ""))
                        holder.icon.setImageResource(R.drawable.ic_folder_view);
                    else holder.icon.setImageResource(R.drawable.ic_folder);
                } else if (dbHelper.getRepeatWatch(1, item.getTitle(0), cur.getTranslator(), "", ""))
                    holder.icon.setImageResource(R.drawable.ic_folder_view);
                else holder.icon.setImageResource(R.drawable.ic_folder);
                holder.size.setVisibility(View.VISIBLE);
                holder.size.setText("s" + cur.getSeasons_count().trim() + "e" + cur.getEpisodes_count()
                        .replace("\"","").trim());

                holder.more.setVisibility(Utils.boolToVisible(!cur.getUrlTrailer().contains("error")));
                holder.lmore.setVisibility(Utils.boolToVisible(!cur.getUrlTrailer().contains("error")));
            } else {
                if (cur.getType().contains("filmix") &&
                        preference.getBoolean("sync_filmix_watch", false) &&
                        !Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted")) {
                    if (Utils.checkFilmixHist(item.getUrl(0), cur.getTranslator(), "", ""))
                        holder.icon.setImageResource(R.drawable.ic_mp4_file_view);
                    else holder.icon.setImageResource(R.drawable.ic_mp4_file);
                } else if (dbHelper.getRepeatWatch(1, item.getTitle(0), cur.getTranslator(), "", ""))
                    holder.icon.setImageResource(R.drawable.ic_mp4_file_view);
                else holder.icon.setImageResource(R.drawable.ic_mp4_file);
                holder.size.setVisibility(View.GONE);
                holder.more.setVisibility(View.VISIBLE);
                holder.lmore.setVisibility(View.VISIBLE);
            }
        } else if (cur.getTitle().contains(SEASON)) {
            holder.more.setVisibility(View.GONE);
            holder.lmore.setVisibility(View.GONE);
            holder.desc.setVisibility(View.GONE);
            holder.size.setVisibility(View.VISIBLE);
            if (cur.getTitle().contains(BACK)){
                holder.name.setText(cur.getTranslator().contains(ERROR) ? "Неизвестный" : cur.getTranslator());
                holder.size.setText(cur.getType());
                holder.icon.setImageResource(R.drawable.ic_back_arrow);
            } else {
                if (cur.getType().contains("filmix") &&
                        preference.getBoolean("sync_filmix_watch", false) &&
                        !Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted")) {
                    if (Utils.checkFilmixHist(item.getUrl(0),cur.getTranslator(), cur.getSeasons_count().trim(), ""))
                        holder.icon.setImageResource(R.drawable.ic_folder_view);
                    else holder.icon.setImageResource(R.drawable.ic_folder);
                } else if (dbHelper.getRepeatWatch(2, item.getTitle(0), cur.getTranslator(),
                        cur.getSeasons_count().trim(), ""))
                    holder.icon.setImageResource(R.drawable.ic_folder_view);
                else holder.icon.setImageResource(R.drawable.ic_folder);
                holder.name.setText(cur.getSeasons_count().trim() + " сезон");
                holder.size.setText(cur.getEpisodes_count().trim() + seriesPatteg(cur.getEpisodes_count().trim()));
            }
        } else if (cur.getTitle().contains(SERIES)) {
            if (cur.getTitle().contains(BACK)){
                holder.more.setVisibility(View.GONE);
                holder.lmore.setVisibility(View.GONE);
                holder.size.setVisibility(View.VISIBLE);
                holder.desc.setVisibility(View.VISIBLE);
                holder.size.setText(cur.getType());
                holder.description.setText(cur.getDescription().replace("error", "Описание отсутствует"));
                holder.name.setText(cur.getSeasons_count().trim() + " сезон");
                holder.desc.setText(cur.getTranslator().contains(ERROR) ? "Неизвестный" : cur.getTranslator());
                holder.icon.setImageResource(R.drawable.ic_back_arrow);
            } else {
                if (cur.getType().contains("filmix") &&
                        preference.getBoolean("sync_filmix_watch", false) &&
                        !Statics.FILMIX_COOCKIE.contains("dle_user_id=deleted")) {
                    if (Utils.checkFilmixHist(item.getUrl(0),cur.getTranslator(), cur.getSeasons_count().trim(), cur.getEpisodes_count().trim()))
                        holder.icon.setImageResource(R.drawable.ic_mp4_file_view);
                    else holder.icon.setImageResource(R.drawable.ic_mp4_file);
                } else if (dbHelper.getRepeatWatch(3, item.getTitle(0), cur.getTranslator(),
                        cur.getSeasons_count().trim(), cur.getEpisodes_count().trim()))
                    holder.icon.setImageResource(R.drawable.ic_mp4_file_view);
                else holder.icon.setImageResource(R.drawable.ic_mp4_file);
                if (cur.getEpisodes_count().trim().length() > 5)
                    holder.name.setText(cur.getEpisodes_count().trim());
                else holder.name.setText(cur.getEpisodes_count().trim() + " серия");
                holder.more.setVisibility(View.VISIBLE);
                holder.lmore.setVisibility(View.VISIBLE);
                holder.desc.setVisibility(View.GONE);
                holder.size.setVisibility(View.GONE);
            }
        } else if (cur.getTitle().contains("site back")) {
            holder.more.setVisibility(View.GONE);
            holder.lmore.setVisibility(View.GONE);
            holder.size.setVisibility(View.VISIBLE);
            holder.desc.setVisibility(View.VISIBLE);
            holder.size.setText(cur.getSeasons_count().trim() + " сезон");
            holder.name.setText(cur.getTranslator().contains(ERROR) ? "Неизвестный" : cur.getTranslator());
            holder.desc.setText(cur.getType());
            holder.icon.setImageResource(R.drawable.ic_back_arrow);
        }
        if (cur.getTitle().contains(CATALOG))
            holder.desc.setVisibility(View.VISIBLE);
        holder.icon.setVisibility(View.VISIBLE);
        holder.pb.setVisibility(View.GONE);

        //text size
        holder.name.setTextSize(sizetext + 2);
        holder.desc.setTextSize(sizetext);
        holder.size.setTextSize(sizetext);

        holder.more.setFocusable(true);
        if (Build.VERSION.SDK_INT < 23){
            holder.more.setOnFocusChangeListener((view, b) -> {
                if (!view.isSelected()) {
                    holder.more.setBackgroundColor(view.getResources().getColor(R.color.colorAccentDark));
                }
                else holder.more.setBackgroundColor(view.getResources().getColor(R.color.colorGone));
                view.setSelected(b);
            });
        }
        holder.lsize.setVisibility(holder.size.getVisibility());


//        if (position == 0) {
//            holder.mView.requestFocus();
//        }
//        holder.mView.setFocusable(true);
        //holder.mView.setFocusableInTouchMode(true);
//        if (position == 0) holder.mView.requestFocus();
        if (preference.getBoolean("tv_activity_detail", true)) {
            holder.mView.setBackgroundColor(view.getResources().getColor(R.color.colorGone));
            holder.bg.setBackgroundColor(view.getResources().getColor(R.color.colorGone));
        }
        holder.mView.setOnFocusChangeListener((view, b) -> {
//            Log.d("qwe", "onFocusChange: " + holder.name.getText());
            if (b) {
                holder.description.setVisibility(View.VISIBLE);
                if (Build.VERSION.SDK_INT < 23){
                    holder.mView.setBackgroundColor(view.getResources().getColor(R.color.colorAccentDark));
                    holder.bg.setBackgroundColor(view.getResources().getColor(R.color.colorAccentDark));
                }
                if (lastFocussedPosition == -1 || lastFocussedPosition == position) {
                    lastFocussedPosition = position;
                    holder.mView.requestFocus();
                }
            } else {
                holder.description.setVisibility(View.GONE);
                lastFocussedPosition = -1;
                if (Build.VERSION.SDK_INT < 23){
                    holder.mView.setBackgroundColor(view.getResources().getColor(R.color.colorGone));
                    holder.bg.setBackgroundColor(view.getResources().getColor(R.color.colorGone));
                }
            }
//            view.setSelected(b);
        });
        holder.more.setOnClickListener(view -> clickMore(position));
        holder.mView.setOnClickListener(view -> {
//            Log.e("test", "onBindViewHolder u: "+cur.url.toString());
//            if (!cur.getUrl().contains("error"))
//                Log.e("test", "onBindViewHolder cur.getUrl(): "+cur.getUrl() +" "+cur );
//            else
//                Log.e("test", "onBindViewHolder: error url");

            if (!cur.getTitle().contains(SERIES) && !cur.getSeasons_count().trim().contains(ERROR)){
                holder.pb.setVisibility(View.VISIBLE);
                holder.icon.setVisibility(View.GONE);
            }
            clickPlay(position);
        });

        if (holder.more.getVisibility() == View.VISIBLE)
            holder.mView.setNextFocusRightId(holder.more.getId());
        holder.more.setNextFocusLeftId(holder.mView.getId());



        holder.mView.setOnLongClickListener(view -> {
            clickMore(position);
            return false;
        });
    }


    private void clickMore(int cur) {
        final ItemNewVideo itm = newItemList.get(cur);
        ArrayList<String> ctg = new ArrayList<>();
        DBHelper dbHelper = new DBHelper(context);
        if (itm.getTitle().contains(CATALOG)){
            if (!itm.getUrlTrailer().contains("error"))
                ctg.add("Трейлер");
            if (itm.getSeasons_count().trim().contains(ERROR)){
                ctg.add("Воспроизвести");
                ctg.add("Скачать");
                ctg.add("Скопировать");
                ctg.add("Поделиться");
            }
            if (dbHelper.getRepeatWatch(1, item.getTitle(0), itm.getTranslator(), "", "")) {
                ctg.add("Удалить из истории");
            }
        } else if (itm.getTitle().contains(SEASON)){
            if (dbHelper.getRepeatWatch(2, item.getTitle(0), itm.getTranslator(),
                    itm.getSeasons_count().trim(), "")) {
                ctg.add("Удалить из истории");
            }
        } else if (itm.getTitle().contains(SERIES)){
            ctg.add("Воспроизвести");
            ctg.add("Скачать");
            ctg.add("Скопировать");
            ctg.add("Поделиться");
            if (dbHelper.getRepeatWatch(3, item.getTitle(0), itm.getTranslator(),
                    itm.getSeasons_count().trim(), itm.getEpisodes_count().trim())) {
                ctg.add("Удалить из истории");
            }
        }
        final String[] list = ctg.toArray(new String[ctg.size()]);

        if (!itm.getTitle().contains("error") && !itm.getTitle().contains(BACK) && ctg.size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
            builder.setItems(list, (dialogInterface, i) -> {
                if (list[i].equals("Трейлер"))
                    getUrl(cur, "trailer");
                if (list[i].equals("Воспроизвести"))
                    getUrl(cur, "play");
                if (list[i].equals("Скачать"))
                    getUrl(cur, "download");
                if (list[i].equals("Скопировать"))
                    getUrl(cur, "copy");
                if (list[i].equals("Поделиться"))
                    getUrl(cur, "share");
                if (list[i].equals("Удалить из истории")) {
                    if (itm.getTitle().contains(CATALOG)){
                        dbHelper.deleteWatch(item.getTitle(0), itm.getTranslator());
                    } else if (itm.getTitle().contains(SEASON)){
                        dbHelper.deleteWatch(item.getTitle(0), itm.getTranslator(),
                                itm.getSeasons_count().trim());
                    } else {
                        dbHelper.deleteWatch(item.getTitle(0), itm.getTranslator(),
                                itm.getSeasons_count().trim(), itm.getEpisodes_count().trim());

                    }
                }
            });
            builder.create().show();
        }
    }

    private String seriesPatteg(String season) {
        String str = season.replace("[", "").replace("]", "")
                .replace("{", "").replace("}", "")
                .replace("(", "").replace(")", "");
        if (str.contains("-")) str = str.split("-")[1].trim();
        int s = 1;
        try {
            s = str.contains(ERROR) ? 0 : Integer.parseInt(str.trim());
        } catch (Exception ignored){}
        String series = " серий";
        if (season.toLowerCase().equals("all") || season.toLowerCase().equals("все")) {
            series = " серии";
        } else {
            if (s == 1 || s == 21 || s == 31 || s == 41 || s == 51) series = " серия";
            if ((s > 1 && s < 5) || (s > 21 && s < 25) || (s > 31 && s < 35)
                    || (s > 41 && s < 45) || (s > 51 && s < 55)) series = " серии";
        }
        return series;
    }

    private void clickPlay(final int cur){
        final ItemNewVideo itm = newItemList.get(cur);
        Log.d(TAG, "Click play: " + itm.getTitle());
        if (itm.getTitle().contains(CATALOG)) {
            if (!itm.getSeasons_count().trim().contains(ERROR)) {
                if (itm.getId().contains("site")) getSiteIframe(cur);
                else getSeason(cur);
            } else getUrl(cur, "play");
        } else if (itm.getTitle().contains(SEASON)) {
            if (itm.getTitle().contains(BACK)) getBase();
            else getSeries(cur);
        } else if (itm.getTitle().contains(SERIES)) {
            if (itm.getTitle().contains(BACK)) getSeason(cur);
            else getUrl(cur, "play");
        } else if (itm.getTitle().contains("site back")) getBase();
    }

    private void getUrl(final int cur, final String action) {
        pbVisible();
        final ItemNewVideo itm = newItemList.get(cur);
        Statics.itemsVideo = newToOld(itm);
        if (itm.getUrl() != null) {
            String urlIframe = action.equals("trailer") ? itm.getUrlTrailer() : itm.getUrl();
            Log.d("adapter video", "getUrl: " + itm.getType() + "|" + urlIframe + "|" + itm.getId());
            if (itm.getType().contains("kinosha")) {
                KinoshaList getMp4 = new KinoshaList(urlIframe, itm.getSeasons_count().trim(),
                        itm.getEpisodes_count().trim(), (quality, url) ->
                        play(quality, url, itm.getTranslator(), itm.getId(),  itm.getSeasons_count().trim(),
                                itm.getEpisodes_count().trim(), action, "kinosha"), true);
                getMp4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (itm.getType().contains("moonwalk")) {
                MoonwalkUrl getMp4 = new MoonwalkUrl(urlIframe, (quality, url) ->
                        play(quality, url, itm.getTranslator(), itm.getId(), itm.getSeasons_count().trim(),
                                itm.getEpisodes_count().trim(), action, "moonwalk"));
                getMp4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (itm.getType().contains("hdgo")) {
                HdgoUrl getMp4 = new HdgoUrl(urlIframe, (quality, url) ->
                        play(quality, url, itm.getTranslator(), itm.getId(),  itm.getSeasons_count().trim(),
                                itm.getEpisodes_count().trim(), action, "hdgo"));
                getMp4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (itm.getType().contains("animevost")) {
                AnimevostUrl getMp4 = new AnimevostUrl(itm.getUrl(), (quality, url) ->
                        play(quality, url, itm.getTranslator(), itm.getId(),  itm.getSeasons_count().trim(),
                                itm.getEpisodes_count().trim(), action, "animevost"));
                getMp4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (itm.getType().contains("filmix")) {
                FilmixUrl getMp4 = new FilmixUrl(urlIframe, (quality, url) ->
                        play(quality, url, itm.getTranslator(), itm.getId(),  itm.getSeasons_count().trim(),
                                itm.getEpisodes_count().trim(), action, "filmix"));
                getMp4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (itm.getType().contains("kinodom")) {
                KinodomUrl getMp4 = new KinodomUrl(urlIframe, (quality, url) ->
                        play(quality, url, itm.getTranslator(), itm.getId(),  itm.getSeasons_count().trim(),
                                itm.getEpisodes_count().trim(), action, "kinodom"));
                getMp4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (itm.getType().contains("farsihd")) {
                FarsihdUrl getMp4 = new FarsihdUrl(urlIframe, (quality, url) ->
                        play(quality, url, itm.getTranslator(), itm.getId(),  itm.getSeasons_count().trim(),
                                itm.getEpisodes_count().trim(), action, "farsihd"));
                getMp4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (itm.getType().contains("kinopub")) {
                KinopubUrl getMp4 = new KinopubUrl(urlIframe, (quality, url) ->
                        play(quality, url, itm.getTranslator(), itm.getId(),  itm.getSeasons_count().trim(),
                                itm.getEpisodes_count().trim(), action, "kinopub"));
                getMp4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (itm.getType().contains("zombiefilm")) {
                ZombiefilmUrl getMp4 = new ZombiefilmUrl(urlIframe, (quality, url) ->
                        play(quality, url, itm.getTranslator(), itm.getId(),  itm.getSeasons_count().trim(),
                                itm.getEpisodes_count().trim(), action, "zombiefilm"));
                getMp4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (itm.getType().contains("kinoxa")) {
                KinoxaIframe iframe = new KinoxaIframe(urlIframe, (quality, url) ->
                        play(quality, url, itm.getTranslator(), itm.getId(),  itm.getSeasons_count().trim(),
                                itm.getEpisodes_count().trim(), action, "kinoxa"));
                iframe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (itm.getType().contains("kinohd")) {
                if (itm.getTitle().equals("series")){
                    KinohdUrl getMp4 = new KinohdUrl(urlIframe, (quality, url) ->
                            play(quality, url, itm.getTranslator(), itm.getId(),  itm.getSeasons_count().trim(),
                                    itm.getEpisodes_count().trim(), action, "kinohd"));
                    getMp4.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    KinohdIframe iframe = new KinohdIframe(urlIframe, (quality, url) ->
                            play(quality, url, itm.getTranslator(), itm.getId(),  itm.getSeasons_count().trim(),
                                    itm.getEpisodes_count().trim(), action, "kinohd"));
                    iframe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            } else if (itm.getType().contains("rufilmtv")) {
                RufilmtvIframe iframe = new RufilmtvIframe(urlIframe, (quality, url) ->
                        play(quality, url, itm.getTranslator(), itm.getId(),  itm.getSeasons_count().trim(),
                                itm.getEpisodes_count().trim(), action, null));
                iframe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (itm.getType().contains("fanserials")) {
                FanserialsIframe iframe = new FanserialsIframe(urlIframe, (quality, url) ->
                        play(quality, url, itm.getTranslator(), itm.getId(),  itm.getSeasons_count().trim(),
                                itm.getEpisodes_count().trim(), action, null));
                iframe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (itm.getType().contains("kinolive")) {
                KinoliveUrl iframe = new KinoliveUrl(urlIframe, (quality, url) ->
                        play(quality, url, itm.getTranslator(), itm.getId(),  itm.getSeasons_count().trim(),
                                itm.getEpisodes_count().trim(), action, "kinolive"));
                iframe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (itm.getType().contains("anidub")) {
                AnidubUrl iframe = new AnidubUrl(urlIframe, (quality, url) ->
                        play(quality, url, itm.getTranslator(),  itm.getId(), itm.getSeasons_count().trim(),
                                itm.getEpisodes_count().trim(), action, "anidub"));
                iframe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (itm.getType().contains("animedia")) {
                AnimediaUrl iframe = new AnimediaUrl(urlIframe, (quality, url) ->
                        play(quality, url, itm.getTranslator(), itm.getId(), itm.getSeasons_count().trim(),
                                itm.getEpisodes_count().trim(), action, "animedia"));
                iframe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else
            Log.e(TAG, "getUrl: " + itm.getType() + " url not found");
    }

    private void getSeason (final int cur) {
        final ItemNewVideo itm = newItemList.get(cur);
//        Log.e("testvid", "getSeason: "+itm.getTitle() +"||"+itm.getType()+"||"+itm.getTranslator() );
        if (Statics.itemsVidVoice == null && itm.getTitle().contains("catalog"))
            Statics.itemsVidVoice = items;
        if (Statics.itemsVidSeason == null) {
            if (itm.getType().contains("moonwalk")) {
                MoonwalkSeason getSeason = new MoonwalkSeason(itm.getId(),
                        itm.getId_trans(), this::reload);
                getSeason.execute();
            } else if (itm.getType().contains("hdgo")) {
                HdgoSeason getSeason = new HdgoSeason(itm.getId(), this::reload);
                getSeason.execute();
            } else if (itm.getType().contains("kinosha")) {
                KinoshaList getSeason = new KinoshaList(itm.getUrl(), this::reload,
                        true, itm.getTranslator());
                getSeason.execute();
            } else if (itm.getType().contains("filmix")) {
                FilmixList getSeason = new FilmixList(itm.getUrl(), this::reload,
                        true, itm.getTranslator());
                getSeason.execute();
            } else if (itm.getType().contains("kinodom")) {
                KinodomList getSeason = new KinodomList(itm.getUrl(), this::reload,
                        true, itm.getTranslator());
                getSeason.execute();
            } else if (itm.getType().contains("farsihd")) {
                FarsihdList getSeason = new FarsihdList(itm.getUrl(), this::reload,
                        itm.getTranslator(), itm.getSeasons_count());
                getSeason.execute();
            } else if (itm.getType().contains("kinopub")) {
                KinopubList getSeason = new KinopubList(itm.getUrl(), this::reload,
                        true, itm.getTranslator());
                getSeason.execute();
            } else if (itm.getType().contains("zombiefilm")) {
                ZombiefilmList getSeason = new ZombiefilmList(itm.getUrl(), this::reload,
                        true, itm.getTranslator());
                getSeason.execute();
            } else if (itm.getType().contains("kinohd")) {
                KinohdList getSeason = new KinohdList(itm.getUrl(), this::reload,
                        true, itm.getTranslator());
                getSeason.execute();
            } else if (itm.getType().contains("kinolive")) {
                KinoliveList getSeason = new KinoliveList(itm.getUrl(), this::reload,
                        true, itm.getTranslator(), itm.getSeasons_count());
                getSeason.execute();
            } else if (itm.getType().contains("anidub")) {
                AnidubList getSeason = new AnidubList(itm.getUrl(), this::reload,
                        itm.getTranslator(), itm.getSeasons_count());
                getSeason.execute();
            } else if (itm.getType().contains("animedia")) {
                AnimediaList getSeason = new AnimediaList(itm.getUrl(), this::reload,
                        itm.getTranslator(), itm.getSeasons_count());
                getSeason.execute();
            }  else if (itm.getType().contains("animevost")) {
                AnimevostSeries getList = new AnimevostSeries(itm.getUrl(), item, this::reload);
                getList.execute();
            }

        } else reload(Statics.itemsVidSeason);
    }

    private void getSiteIframe (final int cur) {
        final ItemNewVideo itm = newItemList.get(cur);

        if (itm.getType().contains("hdgo")) {
            HdgoIframe getIframeSeries = new HdgoIframe(itm.getAllUrlSite(),
                    itm.getTranslator(), item, this::reload);
            getIframeSeries.execute();
        } else
            if (itm.getType().contains("animevost")) {
            AnimevostSeries getSeries = new AnimevostSeries(itm.getUrlSite(), item, this::reload);
            getSeries.execute();
        }
    }

    private void getSeries (final int cur) {
        final ItemNewVideo itm = newItemList.get(cur);
        Statics.itemsVidSeason = items;
        if (itm.getType().contains("moonwalk")) {
            MoonwalkSeries getSeries = new MoonwalkSeries(itm.getId(), itm.getId_trans(),
                    itm.getSeasons_count().trim(), this::reload);
            getSeries.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (itm.getType().contains("hdgo")) {
            HdgoSeries getSeries = new HdgoSeries(itm.getId(),
                    itm.getSeasons_count().trim(), this::reload);
            getSeries.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (itm.getType().contains("kinosha")) {
            KinoshaList getSeason = new KinoshaList(itm.getUrl(), this::reload,
                    true, itm.getTranslator(),itm.getSeasons_count().trim());
            getSeason.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (itm.getType().contains("filmix")) {
            FilmixList getSeason = new FilmixList(itm.getUrl(), this::reload,
                    true, itm.getTranslator(),itm.getSeasons_count().trim());
            getSeason.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (itm.getType().contains("kinodom")) {
            KinodomList getSeason = new KinodomList(itm.getUrl(), this::reload,
                    true, itm.getTranslator(),itm.getSeasons_count().trim());
            getSeason.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (itm.getType().contains("kinopub")) {
            KinopubList getSeason = new KinopubList(itm.getUrl(), this::reload,
                    true, itm.getTranslator(),itm.getSeasons_count().trim());
            getSeason.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (itm.getType().contains("zombiefilm")) {
            ZombiefilmList getSeason = new ZombiefilmList(itm.getUrl(), this::reload,
                    true, itm.getTranslator(),itm.getSeasons_count().trim());
            getSeason.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (itm.getType().contains("kinohd")) {
            KinohdList getSeason = new KinohdList(itm.getUrl(), this::reload,
                    true, itm.getTranslator(),itm.getSeasons_count().trim());
            getSeason.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (itm.getType().contains("kinolive")) {
            KinoliveList getSeason = new KinoliveList(itm.getUrl(), this::reload,
                    true, itm.getTranslator(),itm.getSeasons_count().trim());
            getSeason.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void getBase () {
        Statics.itemsVidSeason = null;
        if (Statics.itemsVidVoice == null) {
            items = new ItemVideo();
            HashSet<String> def = new HashSet<>(Arrays.asList("filmix", "hdgo", "moonwalk", "zombiefilm", "kinodom"));
            Set<String> pref_base = PreferenceManager.getDefaultSharedPreferences(context)
                    .getStringSet("base_video", def);
            pbVisible();

//            if (pref_base.contains("kinomania")) {
//                ParserTrailer parserTrailer = new ParserTrailer(item, items ->
//                        update(items, "kinomania"));
//                parserTrailer.execute();
//            }
            if (item.getIframe(0).contains("hdgo") || item.getIframe(0).contains("vio.to")) {
                HdgoIframe getIframe = new HdgoIframe(item, true, items ->
                        update(items, "iframe"));
                getIframe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (item.getIframe(0).contains("farsihd.")) {
                FarsihdIframe getIframe = new FarsihdIframe(item, items ->
                        update(items, "iframe"));
                getIframe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (DetailActivity.url.contains("animevost")) {
                AnimevostSeries getList = new AnimevostSeries(item, true, items ->
                        update(items, "animevost"));
                getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else if (pref_base.contains("animevost")) {
                ParserVAnimevost getAnivost = new ParserVAnimevost(item, items -> update(items, "animevost"));
                getAnivost.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (!item.getIframe(0).isEmpty() && !item.getIframe(0).contains("error") &&
                    DetailActivity.url.contains(Statics.KINOXA_URL)) {
                pbVisible();
                KinoxaIframe iframe = new KinoxaIframe(item, items ->
                        update(items, "iframe"));
                iframe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (!item.getIframe(0).isEmpty() && !item.getIframe(0).contains("error") &&
                    DetailActivity.url.contains(Statics.FANSERIALS_URL)) {
                pbVisible();
                FanserialsIframe iframe = new FanserialsIframe(item, items ->
                        update(items, "iframe"));
                iframe.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (pref_base.contains("kinosha")) {
                ParserKinosha getList = new ParserKinosha(item, items ->
                        update(items, "kinosha"));
                getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (!DetailActivity.url.contains(Statics.COLDFILM_URL) && !item.getSubTitle(0).trim().isEmpty()) {
                if ((Statics.KP_ID.contains("error") || Statics.KP_ID.isEmpty()) && Statics.MOON_ID.contains("error")) {
                    pbVisible();
                    GetKpId getList = new GetKpId(item, (n, m) -> {
                        if (pref_base.contains("moonwalk")) {
                            ParserMoonwalk get = new ParserMoonwalk(item, items ->
                                    update(items, "moonwalk"));
                            get.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                        if (pref_base.contains("hdgo")) {
                            ParserHdgo get = new ParserHdgo(item, items -> update(items, "hdgo"));
                            get.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                    });
                    getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    if (pref_base.contains("moonwalk")) {
                        pbVisible();
                        ParserMoonwalk get = new ParserMoonwalk(item, items -> update(items, "moonwalk"));
                        get.execute();
                    }
                    if (pref_base.contains("hdgo")) {
                        pbVisible();
                        ParserHdgo get = new ParserHdgo(item, items -> update(items, "hdgo"));
                        get.execute();
                    }
                }
            } else {
                if (pref_base.contains("moonwalk")) {
                    pbVisible();
                    ParserMoonwalk get = new ParserMoonwalk(item, items -> update(items, "moonwalk"));
                    get.execute();
                }
                if (pref_base.contains("hdgo")) {
                    pbVisible();
                    ParserHdgo get = new ParserHdgo(item, items -> update(items, "hdgo"));
                    get.execute();
                }
            }

            if (pref_base.contains("filmix")) {
                ParserFilmix getList = new ParserFilmix(item, items -> update(items, "filmix"));
                getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (pref_base.contains("kinohd")) {
                KinohdIframe getList = new KinohdIframe(item, items -> update(items, "kinohd"));
                getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (pref_base.contains("kinolive")) {
                if (item.getUrl(0).toLowerCase().contains("kino-live") || item.getUrl(0).contains(Statics.KINOLIVE_URL)) {
                    ParserKinoliveUrl getList = new ParserKinoliveUrl(item, items -> update(items, "kinolive"));
                    getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    ParserKinolive getList = new ParserKinolive(item, items -> update(items, "kinolive"));
                    getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
            if (pref_base.contains("kinopub")) {
                if (item.getUrl(0).toLowerCase().contains("kino.pub") || item.getUrl(0).contains(Statics.KINOPUB_URL)) {
                    KinopubIframeUrl getList = new KinopubIframeUrl(item, items -> update(items, "kinopub"));
                    getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    ParserKinopubSearch getList = new ParserKinopubSearch(item, items -> update(items, "kinopub"));
                    getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
            if (pref_base.contains("kinodom")) {
                pbVisible();
                if (item.getUrl(0).toLowerCase().contains("kino-dom.") || item.getUrl(0).contains(Statics.KINODOM_URL)) {
                    KinodomIframeUrl getList = new KinodomIframeUrl(item, items -> update(items, "kinodom"));
                    getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    ParserKinodom getList = new ParserKinodom(item, items -> update(items, "kinodom"));
                    getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
            if (pref_base.contains("zombiefilm")) {
                pbVisible();
                ParserZombiefilm getList = new ParserZombiefilm(item, items -> update(items, "zombiefilm"));
                getList.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (pref_base.contains("anidub")) {
                ParserAnidub getAnidub = new ParserAnidub(item, items -> update(items, "anidub"));
                getAnidub.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            if (pref_base.contains("animedia")) {
                ParserAnimedia getAnimedia = new ParserAnimedia(item, items -> update(items, "animedia"));
                getAnimedia.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        } else reload(Statics.itemsVidVoice);
    }

    private void pbVisible() {
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

    public abstract void update(ItemVideo items, String source);
    public abstract void reload(ItemVideo items);
    public abstract void play(String[] quality, String[] url, String translator,  String id, String s, String e, String action, String reklam);


    @Override
    public int getItemCount() {
        if (items != null)
            return newItemList.size();
        else return 0;
    }

    public void setItems (ItemVideo items) {
        this.items = items;
        addItemList();
    }

    public void addItems (ItemVideo items) {
        if (this.items != null) {
            if (this.items != items) {
                this.items.addItems(items);
                addItemList();
            }
        } else {
            this.items = items;
            addItemList();
        }
    }

    private ItemVideo newToOld(ItemNewVideo itm) {
        ItemVideo old = new ItemVideo();
        old.setUrlSite(itm.getUrlSite());
        old.setUrl(itm.getUrl());
        old.setUrlTrailer(itm.getUrlTrailer());
        old.setId(itm.getId());
        old.setSeason(itm.getSeasons_count());
        old.setEpisode(itm.getEpisodes_count());
        old.setType(itm.getType());
        old.setToken(itm.getToken());
        old.setId_trans(itm.getId_trans());
        old.setTranslator(itm.getTranslator());
        old.setTitle(itm.getTitle());
        return old;
    }

    private void addItemList() {
        newItemList = new ArrayList<>();
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        String vidFilter = preference.getString("filter_vid", "none");
        for (int i=0; i < items.translator.size(); i++) {
            ItemNewVideo newVideo = new ItemNewVideo(items.getUrl(i),
                    items.getUrlSite(i),
                    items.getAll_urlSite(),
                    items.getUrlTrailer(i),
                    items.getTitle(i),
                    items.getId(i),
                    items.getSeason(i),
                    items.getEpisode(i),
                    items.getType(i),
                    items.getToken(i),
                    items.getId_trans(i),
                    items.getTranslator(i),
                    items.getDescription(i));
            newItemList.add(newVideo);
        }
        if (vidFilter.equals("none")) {
        } else if (vidFilter.equals("new")) {
            try {
                Collections.sort(newItemList, (t2, t1) -> {
                    String tf = t1.getSeasons_count().trim();
                    String tt = t2.getSeasons_count().trim();
                    if (t1.getEpisodes_count().length() < 2)
                        tf = tf + "0" + t1.getEpisodes_count().trim();
                    else tf = tf + t1.getEpisodes_count().trim();

                    if (t2.getEpisodes_count().length() < 2)
                        tt = tt + "0" + t2.getEpisodes_count().trim();
                    else tt = tt + t2.getEpisodes_count().trim();

                    if (t1.getTitle().contains(BACK)){
                        tf = "1";
                        tt = "0";
                    }
                    Log.e("qwe", "tf: "+tf+" tt: " +tt);
                    try {
                        return (Integer.parseInt(tf) - Integer.parseInt(tt));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return t1.getSeasons_count().compareTo(t2.getSeasons_count());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (vidFilter.equals("name_up")) {
            Collections.sort(newItemList, (t1, t2) -> {
                if (t1.getTitle().contains(BACK)) {
                    return 1;
                } else if (t1.getTitle().contains(CATALOG)){
                    return t1.getTranslator().compareTo(t2.getTranslator());
                } else if (t1.getTitle().contains(SEASON)){
                    try {
                        return (Integer.parseInt(t1.getSeasons_count()) - Integer.parseInt(t2.getSeasons_count()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return t1.getSeasons_count().compareTo(t2.getSeasons_count());
                    }
                } else if (t1.getTitle().contains(SERIES)){
                    try {
                        return (Integer.parseInt(t1.getEpisodes_count()) - Integer.parseInt(t2.getEpisodes_count()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return t1.getSeasons_count().compareTo(t2.getSeasons_count());
                    }
                } else return 0;
            });
        } else if (vidFilter.equals("name_down")) {
            Collections.sort(newItemList, (t2, t1) -> {
                if (t1.getTitle().contains(BACK)) {
                    return 1;
                } else if (t1.getTitle().contains(CATALOG)){
                    return t1.getTranslator().compareTo(t2.getTranslator());
                } else if (t1.getTitle().contains(SEASON)){
                    try {
                        return (Integer.parseInt(t1.getSeasons_count()) - Integer.parseInt(t2.getSeasons_count()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return t1.getSeasons_count().compareTo(t2.getSeasons_count());
                    }
                } else if (t1.getTitle().contains(SERIES)){
                    try {
                        return (Integer.parseInt(t1.getEpisodes_count()) - Integer.parseInt(t2.getEpisodes_count()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return t1.getSeasons_count().compareTo(t2.getSeasons_count());
                    }
                } else return 0;
            });
        } else if (vidFilter.equals("history_up")) {
            DBHelper dbHelper = new DBHelper(context);
            try {
                Collections.sort(newItemList, (t2, t1) -> {
                    int tf = 0;
                    int tt = 0;
                    if (t2.getTitle().contains(SERIES)) {
                        if (t1.getTitle().contains(BACK) || dbHelper.getRepeatWatch(3, item.getTitle(0), t1.getTranslator(),
                                t1.getSeasons_count().trim(), t1.getEpisodes_count().trim()))
                            tf = 1;
                        if (t2.getTitle().contains(BACK) || dbHelper.getRepeatWatch(3, item.getTitle(0), t2.getTranslator(),
                                t2.getSeasons_count().trim(), t2.getEpisodes_count().trim()))
                            tt = 1;
                    } else if (t2.getTitle().contains(SEASON)) {
                        if (t1.getTitle().contains(BACK) || dbHelper.getRepeatWatch(2, item.getTitle(0), t1.getTranslator(),
                                t1.getSeasons_count().trim(), ""))
                            tf = 1;
                        if (t2.getTitle().contains(BACK) || dbHelper.getRepeatWatch(2, item.getTitle(0), t2.getTranslator(),
                                t2.getSeasons_count().trim(), ""))
                            tt = 1;
                    } else if (t2.getTitle().contains(CATALOG)) {
                        if (t1.getTitle().contains(BACK) || dbHelper.getRepeatWatch(1, item.getTitle(0), t1.getTranslator(),
                                "", ""))
                            tf = 1;
                        if (t2.getTitle().contains(BACK) || dbHelper.getRepeatWatch(1, item.getTitle(0), t2.getTranslator(),
                                "", ""))
                            tt = 1;
                    }

                    try {
                        return (tf - tt);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return t2.getTitle().compareTo(t1.getTitle());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (vidFilter.equals("history_down")) {
            DBHelper dbHelper = new DBHelper(context);
            try {
                Collections.sort(newItemList, (t2, t1) -> {
                    int tf = 1;
                    int tt = 1;
                    if (t2.getTitle().contains(SERIES)) {
                        if (dbHelper.getRepeatWatch(3, item.getTitle(0), t1.getTranslator(),
                                t1.getSeasons_count().trim(), t1.getEpisodes_count().trim()))
                            tf = 0;
                        if (dbHelper.getRepeatWatch(3, item.getTitle(0), t2.getTranslator(),
                                t2.getSeasons_count().trim(), t2.getEpisodes_count().trim()))
                            tt = 0;
                    } else if (t2.getTitle().contains(SEASON)) {
                        if (dbHelper.getRepeatWatch(2, item.getTitle(0), t1.getTranslator(),
                                t1.getSeasons_count().trim(), ""))
                            tf = 0;
                        if (dbHelper.getRepeatWatch(2, item.getTitle(0), t2.getTranslator(),
                                t2.getSeasons_count().trim(), ""))
                            tt = 0;
                    } else if (t2.getTitle().contains(CATALOG)) {
                        if (dbHelper.getRepeatWatch(1, item.getTitle(0), t1.getTranslator(),
                                "", ""))
                            tf = 0;
                        if (dbHelper.getRepeatWatch(1, item.getTitle(0), t2.getTranslator(),
                                "", ""))
                            tt = 0;
                    }
                    try {
                        return (tf - tt);
                    } catch (Exception e) {
                        e.printStackTrace();
                        return t2.getTitle().compareTo(t1.getTitle());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final LinearLayout bg, lsize, lmore;
        public final TextView name, size, desc, description;
        public final ImageView icon;
        public final ProgressBar pb;
        public final ImageButton more;

        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView.findViewById(R.id.mView);
            bg = itemView.findViewById(R.id.bgView);
            name = itemView.findViewById(R.id.name);
            size = itemView.findViewById(R.id.size);
            desc = itemView.findViewById(R.id.desc);
            description = itemView.findViewById(R.id.vid_description);
            more = itemView.findViewById(R.id.more);
            lsize = itemView.findViewById(R.id.l_size);
            lmore = itemView.findViewById(R.id.l_more);
            icon = itemView.findViewById(R.id.vid_ic);
            pb = itemView.findViewById(R.id.vid_pb);
        }
    }
}