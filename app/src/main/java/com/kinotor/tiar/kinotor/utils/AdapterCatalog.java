package com.kinotor.tiar.kinotor.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.ui.DetailActivity;
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
    private ArrayList<ItemHtml> htmlItems;

    protected AdapterCatalog(Context context, String category) {
        dbHelper = new DBHelper(context);
        this.context = context;
        this.category = category;
        if (!category.equals("catalog")) {
            htmlItems = dbHelper.getDbItems(category);
        }
    }

    @Override
    public CatalogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        View view;
        if (preference.getString("grid_catalog", "2").equals("1"))
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog_line, parent, false);
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog, parent, false);

            GridLayoutManager.LayoutParams lp = (GridLayoutManager.LayoutParams) view.getLayoutParams();
            Utils utils = new Utils();
            int gridCount = utils.calculateGrid(context);

            if (!preference.getString("grid_count", "0").equals("0")) {
                lp.width = (int) (utils.dpToPixel(Statics.CATALOG_W, context) * utils.calculateScale(context, gridCount));
                lp.height = (int) (utils.dpToPixel(Statics.CATALOG_H, context) * utils.calculateScale(context, gridCount));
            } else {
                lp.width = (int) (utils.dpToPixel(Statics.CATALOG_W, context));
                lp.height = (int) (utils.dpToPixel(Statics.CATALOG_H, context));
            }
            view.setLayoutParams(lp);
        }

        return new CatalogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CatalogViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        int sizetext = Integer.parseInt(preference.getString("text_size_main", "12"));
        int grid_size = Integer.parseInt(preference.getString("grid_catalog", "2")) + 1;


        final ItemHtml current = htmlItems.get(position);
        holder.title.setText(current.getTitle(position));
        holder.quality.setText(current.getQuality(position));
        holder.rating.setText(current.getRating(position));
        holder.genre.setText(current.getGenre(position));

        String[] displayCat = {"title", "year", "poster", "quality", "extra"};
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
        if (pref_base.contains("quality"))
            holder.quality.setVisibility(View.VISIBLE);
        else holder.quality.setVisibility(View.GONE);
        if (pref_base.contains("rating"))
            holder.rating.setVisibility(View.VISIBLE);
        else holder.rating.setVisibility(View.GONE);
        if (pref_base.contains("extra"))
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

        if (current.getSeason(position) == 0 && current.getSeries(position) == 0)
            holder.voice.setText(current.getVoice(position));
        else if (current.getSeason(position) != 0 && current.getSeries(position) == 0) {
            holder.voice.setVisibility(View.VISIBLE);
            holder.voice.setText(current.getSeason(position) + " сезон ");
        } else {
            holder.voice.setVisibility(View.VISIBLE);
            holder.voice.setText(current.getSeason(position) + " сезон " +
                    current.getSeries(position) + " серия");
        }


        //text size
        if (preference.getString("grid_catalog", "2").equals("2")) {
            holder.title.setTextSize(sizetext);
            holder.quality.setTextSize(sizetext + 2);
            holder.rating.setTextSize(sizetext + 2);
            holder.voice.setTextSize(sizetext);
            holder.genre.setTextSize(sizetext - 1);
        } else {
            holder.title.setTextSize(sizetext + 3);
            holder.quality.setTextSize(sizetext);
            holder.rating.setTextSize(sizetext);
            holder.voice.setTextSize(sizetext);
            holder.genre.setTextSize(sizetext);
        }

        if (pref_base.contains("poster"))
            Picasso.with(context)
                    .load(current.getImg(position))
                    .into(holder.poster);
        holder.cardView.setFocusable(true);
        holder.cardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.d(TAG, "onFocusChange: "+position);
                if (!view.isSelected()) {
                    view.setBackgroundColor(view.getResources().getColor(R.color.colorAccent));
                    holder.title.setBackgroundColor(view.getResources().getColor(R.color.colorAccent));
                } else {
                    view.setBackgroundColor(view.getResources().getColor(R.color.colorGone));
                    holder.title.setBackgroundColor(view.getResources().getColor(R.color.colorPrimaryDark));
                }
                view.setSelected(b);
            }
        });
        if (position == 0)
            holder.cardView.requestFocus();

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("Title", current.getTitle(position));
                intent.putExtra("Season", String.valueOf(current.getSeason(position)));
                intent.putExtra("Serie", String.valueOf(current.getSeries(position)));
                intent.putExtra("Url", current.getUrl(position));
                intent.putExtra("Img", current.getImg(position));
                intent.putExtra("Voice", current.getVoice(position));
                intent.putExtra("Quality", current.getQuality(position));
                context.startActivity(intent);
            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final String title = current.getTitle(position);
                String a = dbHelper.getRepeat("history", title) ? "Удалить с Истории" : "Добавить в Историю";
                String b = dbHelper.getRepeat("favor", title) ? "Убрать из Избранного" : "Добавить в Избранное";
                final String[] list = {a, b};

                AlertDialog.Builder builder = new AlertDialog.Builder(context, 2);
                builder.setItems(list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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
                                } catch (Exception o){
                                    dbHelper.insert("history", title, current.getImg(position),
                                            current.getUrl(position), current.getVoice(position),
                                            current.getQuality(position), 0, 0);
                                }
                            }
                        } else {
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
                                } catch (Exception o){
                                    dbHelper.insert("favor", title, current.getImg(position),
                                            current.getUrl(position), current.getVoice(position),
                                            current.getQuality(position), 0, 0);
                                }
                            }

                        }
                        dbHelper.Write();
                    }
                });
                builder.create().show();
                return false;
            }
        });

        if ((position >= getItemCount() - grid_size) && ItemMain.cur_items < getItemCount() && category.equals("catalog")) {
            //для остановки бессконечной загрузки
            ItemMain.cur_items = getItemCount();
            load();
        }
    }
    public abstract void load();

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
        TextView title, quality, voice, rating, genre;
        ImageView poster;
        CardView cardView;

        CatalogViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            quality = itemView.findViewById(R.id.quality);
            voice = itemView.findViewById(R.id.voice);
            poster = itemView.findViewById(R.id.imgPoster);
            rating = itemView.findViewById(R.id.rating);
            genre = itemView.findViewById(R.id.genre);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }
}
