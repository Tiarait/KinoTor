package com.kinotor.tiar.kinotor.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
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
import com.kinotor.tiar.kinotor.ui.DetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 24.09.2017.
 */

public abstract class AdapterCatalog extends RecyclerView.Adapter<AdapterCatalog.CatalogViewHolder> {
    private Context context;
    private String category;
    private ArrayList<ItemHtml> htmlItems;

    protected AdapterCatalog(Context context, String category) {
        this.context = context;
        this.category = category;
        if (!category.equals("catalog")) {
            DBHelper dbHelper = new DBHelper(context);
            htmlItems = dbHelper.getDbItems(category);
        }
    }

    @Override
    public CatalogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        View view;
        if (preference.getString("grid_catalog", "2").equals("1"))
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog_line, parent, false);
        else view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog, parent, false);
        return new CatalogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CatalogViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        int sizetext = Integer.parseInt(preference.getString("text_size_main", "12"));
        int grid_size = Integer.parseInt(preference.getString("grid_catalog", "4")) + 1;
        final ItemHtml current = htmlItems.get(position);
        holder.title.setText(current.getTitle(position));
        holder.quality.setText(current.getQuality(position));

        if (current.getVoice(position).contains("error"))
            holder.voice.setVisibility(View.GONE);
        if (current.getQuality(position).contains("error"))
            holder.quality.setVisibility(View.GONE);

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
        holder.title.setTextSize(sizetext);
        holder.quality.setTextSize(sizetext + 2);
        holder.voice.setTextSize(sizetext);

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
                    holder.title.setBackgroundColor(view.getResources().getColor(R.color.colorBlack));
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
        TextView title, quality, voice;
        ImageView poster;
        CardView cardView;

        CatalogViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            quality = itemView.findViewById(R.id.quality);
            voice = itemView.findViewById(R.id.voice);
            poster = itemView.findViewById(R.id.imgPoster);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }
}
