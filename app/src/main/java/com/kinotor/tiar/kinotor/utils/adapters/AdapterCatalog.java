package com.kinotor.tiar.kinotor.utils.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.ui.DetailActivity;
import com.kinotor.tiar.kinotor.ui.DetailActivityTv;
import com.kinotor.tiar.kinotor.ui.SearchActivity;
import com.kinotor.tiar.kinotor.utils.DBHelper;
import com.kinotor.tiar.kinotor.utils.Utils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 24.09.2017.
 */

public abstract class AdapterCatalog extends RecyclerView.Adapter<AdapterCatalog.CatalogViewHolder> {
    private Context context;
    private String category;
    private DBHelper dbHelper;
    private boolean req = true;
    private ArrayList<ItemHtml> htmlItems;
    private int lastFocussedPosition = -1;
    private boolean newtv;

    protected AdapterCatalog(Context context, String category) {
        dbHelper = new DBHelper(context);
        req = true;
        this.context = context;
        newtv = category.contains("newtv");
        this.category = category.replace(" newtv", "").trim();

        if (!this.category.equals("catalog")) {
            htmlItems = dbHelper.getDbItems(this.category);
        }
    }

    @NonNull
    @Override
    public CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        View view;
//        Log.e(TAG, "wtf "+category );
        int height = category.contains("catalog") ? Statics.CATALOG_H() : Statics.CATALOG_H;
//        && !preference.getBoolean("tv_activity_main", true)
        if (preference.getString("grid_catalog", "2").equals("1"))
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog_line, parent, false);
        else {
//            preference.getBoolean("tv_activity_main", true)
            if (preference.getString("card_main", "2").equals("2")) {
                if (!preference.getBoolean("tv_focus_select", true))
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog_inverse_noselect_tv, parent, false);
                else
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog_inverse_tv, parent, false);
            } else {
                if (!preference.getBoolean("tv_focus_select", true))
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog_inverse_noselect, parent, false);
                else
                    view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog_inverse, parent, false);
            }
//            GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            ViewGroup.LayoutParams lp = view.getLayoutParams();
            Utils utils = new Utils();
            int gridCount = utils.calculateGrid(context);

//            if (preference.getBoolean("tv_activity_main", true) && newtv) {
//                lp.width = (int) (utils.dpToPixel(135, context));
//                lp.height = (int) (utils.dpToPixel(235, context));
//            } else
            if (!preference.getString("grid_count", "0").equals("0")) {
                lp.width = (int) (utils.dpToPixel(Statics.CATALOG_W, context) * utils.calculateScale(context, gridCount));
                lp.height = (int) (utils.dpToPixel(height, context) * utils.calculateScale(context, gridCount));
            } else {
                lp.width = (int) (utils.dpToPixel(Statics.CATALOG_W, context));
                lp.height = (int) (utils.dpToPixel(height, context));
            }
            view.setLayoutParams(lp);
        }

        return new CatalogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CatalogViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        int sizetext = Integer.parseInt(preference.getString("text_size_main", "15"));
        int grid_size = Integer.parseInt(preference.getString("grid_catalog", "2")) + 1;

        final ItemHtml current = htmlItems.get(position);
        if (current.getTitle(position).trim().equals("error")) {

        } else {
            String q = current.getQuality(position).trim();
            if (!q.equals("360p") || !q.equals("480p") || !q.equals("720p") || !q.equals("1080p") || !q.equals("2160p")) {
                q = q.replace("360p", "").replace("480p", "")
                        .replace("720p", "").replace("1080p", "")
                        .replace("2160p", "")
                        .replace("BDRip/", "").replace("-DL", "")
                        .replace("Rip", "").replace(" UHD", "").trim();
            }
            holder.title.setText(current.getTitle(position));
            holder.quality.setText(q);
//            quality_color
            if (preference.getBoolean("quality_color", true)) {
                if (q.contains("WEB") || q.contains("DVD")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        holder.quality.setBackground(ContextCompat.getDrawable(context, R.drawable.rounding_right_orange));
                    } else {
                        holder.quality.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounding_right_orange));
                    }
                    holder.quality.setTextColor(context.getResources().getColor(R.color.colorWhite));
                } else if (q.contains("TS") || q.contains("TC") || q.contains("ТС") || q.contains("ТV") || q.contains("SAT")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        holder.quality.setBackground(ContextCompat.getDrawable(context, R.drawable.rounding_right_red));
                    } else {
                        holder.quality.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounding_right_red));
                        holder.quality.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    }
                } else if (q.contains("4K") || q.contains("HD") || q.contains("BD")) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        holder.quality.setBackground(ContextCompat.getDrawable(context, R.drawable.rounding_right_acent));
                    } else {
                        holder.quality.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounding_right_acent));
                    }
                    holder.quality.setTextColor(context.getResources().getColor(R.color.colorWhite));
                }
            }
            holder.rating.setText(current.getRating(position).replace("SITE[","")
                    .replace("IMDB[","").replace("KP[","")
                    .replace("]","").replace("00","0"));
            holder.genre.setText(current.getGenre(position));

            String[] displayCat = {"title", "year", "poster", "quality", "rating", "series"};
            HashSet<String> def = new HashSet<>(Arrays.asList(displayCat));
            Set<String> pref_base = PreferenceManager.getDefaultSharedPreferences(context)
                    .getStringSet("display_content", def);
            if (pref_base.contains("title"))
                holder.title.setVisibility(View.VISIBLE);
            else holder.title.setVisibility(View.GONE);
            if (!pref_base.contains("year") && current.getTitle(position).contains("("))
                holder.title.setText(current.getTitle(position).split("\\(")[0]);
            if (pref_base.contains("poster"))
                holder.poster.setVisibility(View.VISIBLE);
            else holder.poster.setVisibility(View.GONE);
            if (pref_base.contains("quality") && !current.getQuality(position).contains("error") &&
                    !current.getQuality(position).isEmpty()) {
                holder.quality.setVisibility(View.VISIBLE);
                holder.qualityRoud.setVisibility(View.VISIBLE);
            } else {
                holder.quality.setVisibility(View.GONE);
                holder.qualityRoud.setVisibility(View.GONE);
            }
            if (pref_base.contains("rating") && !current.getRating(position).contains("error") &&
                    !current.getRating(position).isEmpty()) {
                holder.rating.setVisibility(View.VISIBLE);
                holder.ratingRoud.setVisibility(View.VISIBLE);
            } else {
                holder.rating.setVisibility(View.GONE);
                holder.ratingRoud.setVisibility(View.GONE);
            }
            if (pref_base.contains("series") && (current.getSeason(position) != 0 || current.getSeries(position) != 0))
                holder.voice.setVisibility(View.VISIBLE);
            else holder.voice.setVisibility(View.GONE);
            if (pref_base.contains("trans"))
                holder.voice.setVisibility(View.VISIBLE);
            else holder.voice.setVisibility(View.GONE);
            if (pref_base.contains("genre"))
                holder.genre.setVisibility(View.VISIBLE);
            else holder.genre.setVisibility(View.GONE);

            if (current.getVoice(position).contains("error"))
                holder.voice.setVisibility(View.GONE);
            if (current.getQuality(position).contains("error"))
                holder.quality.setVisibility(View.GONE);
            if (current.getRating(position).contains("error"))
                holder.rating.setVisibility(View.GONE);
            if (current.getGenre(position).contains("error"))
                holder.genre.setVisibility(View.GONE);

            if (pref_base.contains("series")) {
                if (current.getSeason(position) != 0 && current.getSeries(position) == 0) {
                    holder.voice.setVisibility(View.VISIBLE);
                    holder.voice.setText(current.getSeason(position) + " сезон ");
                } else if (current.getSeason(position) == 0 && current.getSeries(position) != 0) {
                    holder.voice.setVisibility(View.VISIBLE);
                    holder.voice.setText(current.getSeries(position) + " серия");
                } else if (current.getSeason(position) != 0 && current.getSeries(position) != 0){
                    holder.voice.setVisibility(View.VISIBLE);
                    holder.voice.setText(current.getSeason(position) + " сезон " +
                            current.getSeries(position) + " серия");
                } else {
                    holder.voice.setVisibility(View.GONE);
                }
            } else if (pref_base.contains("trans")){
                holder.voice.setVisibility(View.VISIBLE);
                if (!current.getVoice(position).contains("error")) {
                    holder.voice.setText(current.getVoice(position));
                } else {
                    holder.voice.setVisibility(View.GONE);
                }
            } else {
                holder.voice.setVisibility(View.GONE);
            }

            updDB(holder, current.getTitle(position));

            //text size
            if (preference.getString("grid_catalog", "2").equals("2")) {
                holder.title.setTextSize(sizetext);
//                holder.ic.setTextSize(sizetext);
                holder.quality.setTextSize(sizetext + 2);
                holder.qualityRoud.setTextSize(sizetext + 2);
                holder.rating.setTextSize(sizetext + 2);
                holder.ratingRoud.setTextSize(sizetext + 2);
                holder.voice.setTextSize(sizetext);
                holder.genre.setTextSize(sizetext - 1);
            } else {
                holder.title.setTextSize(sizetext + 3);
//                holder.ic.setTextSize(sizetext + 3);
                holder.quality.setTextSize(sizetext);
                holder.qualityRoud.setTextSize(sizetext);
                holder.rating.setTextSize(sizetext);
                holder.ratingRoud.setTextSize(sizetext);
                holder.voice.setTextSize(sizetext);
                holder.genre.setTextSize(sizetext);
            }


            if (pref_base.contains("poster")) {
                if (current.getImg(position) != null) {
                    if (preference.getBoolean("poster_crop", true))
                        Picasso.get()
                                .load(current.getImg(position))
                                .fit().centerCrop()
                                .into(holder.poster);
                    else Picasso.get()
                            .load(current.getImg(position))
                            .fit().centerInside()
                            .into(holder.poster);
                } else if (preference.getBoolean("poster_crop", true))
                    Picasso.get()
                            .load("https://m.media-amazon.com/images/G/01/imdb/images/nopicture/medium/film-3385785534._CB483791896_.png")
                            .fit().centerCrop()
                            .into(holder.poster);
                else Picasso.get()
                            .load("https://m.media-amazon.com/images/G/01/imdb/images/nopicture/medium/film-3385785534._CB483791896_.png")
                            .fit().centerInside()
                            .into(holder.poster);
            }
//            Log.e(TAG, "onBindViewHolder: "+current.getImg(position) );
//             || preference.getBoolean("tv_activity_main", true)

            if (preference.getBoolean("radius_card", false)) {
                holder.cardView.setRadius(10f);
                if (holder.cardImg != null)
                    holder.cardImg.setRadius(25f);
            } else {
                holder.cardView.setRadius(0f);
                if (holder.cardImg != null)
                    holder.cardImg.setRadius(0f);
            }

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.colorGone));
            }
            if (preference.getString("theme_list", "gray").equals("gray")) {
                holder.voice.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
//                if (!preference.getBoolean("tv_activity_main", true))
//                    holder.lTitle.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
            } else if (preference.getString("theme_list", "gray").equals("black")) {
                holder.voice.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
//                if (!preference.getBoolean("tv_activity_main", true))
//                    holder.lTitle.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
            }
            if (preference.getBoolean("tv_focus_zoom", false)) {
                holder.itemView.setScaleX((float) 0.95);
                holder.itemView.setScaleY((float) 0.95);
            }
            if (!preference.getBoolean("tv_focus_select", true)) {
                holder.cardView.setForeground(null);
            }
            holder.cardView.setFocusable(true);
//            if (preference.getBoolean("tv_activity_main", true) && newtv) {
//                holder.cardView.setNextFocusDownId(holder.cardView.getId());
//                holder.cardView.setNextFocusUpId(holder.cardView.getId());
////                holder.cardView.setNextFocusLeftId(holder.cardView.getId());
//                holder.title.setTextColor(context.getResources().getColor(R.color.colorDarkWhite));

//            }

            holder.cardView.setOnKeyListener((view, i, keyEvent) -> {
                switch (keyEvent.getKeyCode()) {
//                    case KeyEvent.KEYCODE_DPAD_DOWN:
//                        key(keyEvent.getKeyCode(), position);
//                        return true;
//                    case KeyEvent.KEYCODE_DPAD_UP:
//                        key(keyEvent.getKeyCode(), position);
//                        return true;
                    case KeyEvent.KEYCODE_SEARCH:
                        if (!SearchActivity.activeSearch) {
                            Intent intent = new Intent(context, SearchActivity.class);
                            context.startActivity(intent);
                            return true;
                        } else return false;
                }
                return false;
            });
            holder.cardView.setOnFocusChangeListener((view, b) -> {
                Log.d(TAG, "onFocusChange: " + position);
                if (!preference.getBoolean("tv_focus_select", true)) {
                    holder.cardView.setForeground(null);
                }
                if (b) {
                    if (preference.getBoolean("tv_focus_zoom", false)) {
                        holder.itemView.setScaleX((float) 1.1);
                        holder.itemView.setScaleY((float) 1.1);
                    }
//                    if (preference.getBoolean("tv_activity_main", true))
//                        holder.title.setTextColor(context.getResources().getColor(R.color.colorWhite));
                    if (lastFocussedPosition == -1 || lastFocussedPosition == position) {
                        lastFocussedPosition = position;
                        holder.cardView.requestFocus();
                    }
                } else {
                    if (preference.getBoolean("tv_focus_zoom", false)) {
                        holder.itemView.setScaleX((float) 0.95);
                        holder.itemView.setScaleY((float) 0.95);
                    }
//                    if (preference.getBoolean("tv_activity_main", true))
//                        holder.title.setTextColor(context.getResources().getColor(R.color.colorDarkWhite));
                    lastFocussedPosition = -1;
                }
            });
//            && !preference.getBoolean("tv_activity_main", true)
            //выставляем фокус автоматом
            if (preference.getBoolean("focus_on_video", true) &&
                    position == 0 && req) {
                req = false;
                holder.cardView.requestFocus();
            }

            holder.cardView.setOnClickListener(v -> {
                Intent intent;
                if (!preference.getBoolean("tv_activity_detail", true)) {
                    intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("Title", current.getTitle(position));
                    intent.putExtra("Season", String.valueOf(current.getSeason(position)));
                    intent.putExtra("Serie", String.valueOf(current.getSeries(position)));
                    intent.putExtra("Url", current.getUrl(position));
                    intent.putExtra("Img", current.getImg(position));
                    intent.putExtra("Voice", current.getVoice(position));
                    intent.putExtra("Quality", current.getQuality(position));
                } else {
                    intent = new Intent(context, DetailActivityTv.class);
                    intent.putExtra("Title", current.getTitle(position));
                    intent.putExtra("Url", current.getUrl(position));
                    intent.putExtra("Img", current.getImg(position));
                }
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) context, holder.poster, "poster");
                    context.startActivity(intent, options.toBundle());
                } else context.startActivity(intent);
            });

            holder.cardView.setOnLongClickListener(view -> {
                final String title = current.getTitle(position);
                String a = dbHelper.getRepeat("history", title) ? "Удалить с Истории" : "Добавить в Историю";
                String b = dbHelper.getRepeat("favor", title) ? "Убрать из Избранного" : "Добавить в Избранное";
                String с = dbHelper.getRepeat("later", title) ? "Убрать из Посмотреть позже" : "Добавить в Посмотреть позже";
                final String[] list = {a, b, с};

                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.MyAlertDialogStyle);
                builder.setTitle(current.getTitle(position)).setItems(list, (dialogInterface, i) -> {
                    if (i == 0) {
                        if (dbHelper.getRepeat("history", title)) {
                            dbHelper.delete("history", title);
                            if (!category.equals("catalog")) {
                                htmlItems = dbHelper.getDbItems(category);
                                notifyDataSetChanged();
                            }
                        } else {
                            try {
                                dbHelper.insert("history", title, current.getImg(position),
                                        current.getUrl(position), current.getVoice(position),
                                        current.getQuality(position), current.getSeason(position),
                                        current.getSeries(position));
                            } catch (Exception o) {
                                dbHelper.insert("history", title, current.getImg(position),
                                        current.getUrl(position), current.getVoice(position),
                                        current.getQuality(position), 0, 0);
                            }
                        }
                        updDB(holder, current.getTitle(position));
                    } else if (i == 1) {
                        if (dbHelper.getRepeat("favor", title)) {
                            dbHelper.delete("favor", title);
                            if (!category.equals("catalog")) {
                                htmlItems = dbHelper.getDbItems(category);
                                notifyDataSetChanged();
                            }
                        } else {
                            try {
                                dbHelper.insert("favor", title, current.getImg(position),
                                        current.getUrl(position), current.getVoice(position),
                                        current.getQuality(position), current.getSeason(position),
                                        current.getSeries(position));
                            } catch (Exception o) {
                                dbHelper.insert("favor", title, current.getImg(position),
                                        current.getUrl(position), current.getVoice(position),
                                        current.getQuality(position), 0, 0);
                            }
                        }
                        updDB(holder, current.getTitle(position));
                    } else {
                        if (dbHelper.getRepeat("later", title)) {
                            dbHelper.delete("later", title);
                            if (!category.equals("catalog")) {
                                htmlItems = dbHelper.getDbItems(category);
                                notifyDataSetChanged();
                            }
                        } else {
                            try {
                                dbHelper.insert("later", title, current.getImg(position),
                                        current.getUrl(position), current.getVoice(position),
                                        current.getQuality(position), current.getSeason(position),
                                        current.getSeries(position));
                            } catch (Exception o) {
                                dbHelper.insert("later", title, current.getImg(position),
                                        current.getUrl(position), current.getVoice(position),
                                        current.getQuality(position), 0, 0);
                            }
                        }
                    }
                    dbHelper.Write();
                });
                builder.create().show();
                return false;
            });
        }

        boolean load = (position >= getItemCount() - grid_size) &&
                ItemMain.cur_items < getItemCount() && category.equals("catalog");

        if (load) {
            //для остановки бессконечной загрузки
            ItemMain.cur_items = getItemCount();
            load();
        }
    }

    private void updDB(CatalogViewHolder holder, String title){
        if (dbHelper.getRepeat("favor", title)) {
            holder.state.setVisibility(View.VISIBLE);
            holder.state.setImageResource(R.drawable.ic_menu_fav );
        } else if (dbHelper.getRepeat("history", title)) {
            holder.state.setVisibility(View.VISIBLE);
            holder.state.setImageResource(R.drawable.ic_menu_hist);
        } else holder.state.setVisibility(View.GONE);
    }
    public abstract void load();
    public abstract void key(int keyCode, int position);


    public void setHtmlItems (ArrayList<ItemHtml> htmlItems) {
        this.htmlItems = htmlItems;
    }

    @Override
    public int getItemCount() {
        if (htmlItems != null) {
            return htmlItems.size();
        } else return 0;
    }

    class CatalogViewHolder extends RecyclerView.ViewHolder {
        TextView title, quality, qualityRoud, voice, rating, ratingRoud, genre;
        ImageView poster, state;
        CardView cardView, cardImg;
        LinearLayout lTitle;

        CatalogViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            lTitle = itemView.findViewById(R.id.l_title);
            quality = itemView.findViewById(R.id.quality);
            qualityRoud = itemView.findViewById(R.id.quality_roud);
            voice = itemView.findViewById(R.id.voice);
            poster = itemView.findViewById(R.id.imgPoster);
            rating = itemView.findViewById(R.id.rating);
            ratingRoud = itemView.findViewById(R.id.rating_roud);
            genre = itemView.findViewById(R.id.genre);
            cardView = itemView.findViewById(R.id.cardview);
            cardImg = itemView.findViewById(R.id.card_images);
            state = itemView.findViewById(R.id.state);
        }
    }
}
