package com.kinotor.tiar.kinotor.utils.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemSearch;
import com.kinotor.tiar.kinotor.ui.DetailActivity;
import com.kinotor.tiar.kinotor.ui.DetailActivityTv;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 04.2018.
 */
public class AdapterSuggestion extends RecyclerView.Adapter<AdapterSuggestion.CatalogViewHolder> {
    private Context context;
    private List<ItemSearch> itemSearch;
    private int lastFocussedPosition = -1;

    public AdapterSuggestion(Context context, @NonNull List<ItemSearch> item) {
        this.context = context;
        itemSearch = new ArrayList<>(item);
    }

    @Override
    public AdapterSuggestion.CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new AdapterSuggestion.CatalogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterSuggestion.CatalogViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        ItemSearch itemCur = itemSearch.get(position);

        holder.title.setText(itemCur.getTitle());
        holder.subtitle.setText(itemCur.getSubtitle());

        int sizetext = Integer.parseInt(preference.getString("text_size_main", "12"));
        holder.title.setTextSize(sizetext);
        holder.subtitle.setTextSize(sizetext);

        if (itemCur.getSubtitle().contains("error"))
            holder.subtitle.setVisibility(View.GONE);
        if (itemCur.getImg().contains("error"))
            holder.poster.setVisibility(View.GONE);
        else {
            Picasso.get()
                    .load(itemCur.getImg())
                    .fit().centerInside()
                    .into(holder.poster);
        }
        holder.cardView.setFocusable(true);
        if (!preference.getBoolean("tv_focus_select", true)) {
            holder.cardView.setForeground(null);
        }

        if (preference.getBoolean("tv_focus_zoom", false)) {
            holder.itemView.setScaleY(0.9f);
            holder.itemView.setScaleX(0.9f);
        }

        if (preference.getString("theme_list", "gray").equals("gray")) {
            holder.v.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        } else if (preference.getString("theme_list", "gray").equals("black")) {
            holder.v.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
        }

        holder.cardView.setOnFocusChangeListener((view, b) -> {
            Log.d(TAG, "onFocusChange: "+position);
            if (b) {
                if (lastFocussedPosition == -1 || lastFocussedPosition == position) {
                    lastFocussedPosition = position;
                    if (preference.getBoolean("tv_focus_zoom", false)) {
                        holder.itemView.setScaleY(1.05f);
                        holder.itemView.setScaleX(1.05f);
                    }
                    holder.itemView.requestFocus();
                }
            } else {
                if (preference.getBoolean("tv_focus_zoom", false)) {
                    holder.itemView.setScaleY(0.9f);
                    holder.itemView.setScaleX(0.9f);
                }
                lastFocussedPosition = -1;
            }
        });
        holder.cardView.setOnClickListener(view -> {
            Intent intent;
            if (!preference.getBoolean("tv_activity_detail", true)) {
                intent = new Intent(context, DetailActivity.class);
            } else {
                intent = new Intent(context, DetailActivityTv.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("Title", itemCur.getTitle());
            intent.putExtra("Url", itemCur.getUrl());
            intent.putExtra("Img", itemCur.getImg());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return itemSearch.size();
    }

    class CatalogViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        ImageView poster;
        CardView cardView;
        LinearLayout v;

        CatalogViewHolder(View itemView) {
            super(itemView);
            v = itemView.findViewById(R.id.view);
            cardView = itemView.findViewById(R.id.cardview);
            title = itemView.findViewById(R.id.title);
            poster = itemView.findViewById(R.id.img);
            subtitle = itemView.findViewById(R.id.subtitle);
        }
    }
}
