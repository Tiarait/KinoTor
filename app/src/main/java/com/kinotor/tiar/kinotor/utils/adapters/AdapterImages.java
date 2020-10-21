package com.kinotor.tiar.kinotor.utils.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.ui.ImgActivity;
import com.kinotor.tiar.kinotor.utils.RoundedTransformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 04.2018.
 */
public class AdapterImages extends RecyclerView.Adapter<AdapterImages.CatalogViewHolder> {
    private Context context;
    private ArrayList<String> items;
    private int lastFocussedPosition = -1;

    public AdapterImages(Context context) {
        this.context = context;
    }

    @Override
    public AdapterImages.CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        View view;
        if (!preference.getBoolean("tv_focus_select", true))
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog_images_noselect, parent, false);
        else view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog_images, parent, false);

        return new AdapterImages.CatalogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterImages.CatalogViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        Picasso.get()
                .load(items.get(position))
                .transform(new RoundedTransformation(10, 4))
                .into(holder.image);
        holder.cardView.setFocusable(true);

        if (preference.getBoolean("tv_activity_detail", false)) {
            if (position == 0) holder.image.setNextFocusLeftId(holder.image.getId());
            else if (position == items.size() - 1) holder.image.setNextFocusRightId(holder.image.getId());
        }
        if (preference.getBoolean("tv_focus_zoom", false)) {
            holder.itemView.setScaleX((float) 0.85);
            holder.itemView.setScaleY((float) 0.85);
        }
        if (!preference.getBoolean("tv_focus_select", true)) {
            holder.cardView.setForeground(null);
        }
        holder.cardView.setOnFocusChangeListener((view, b) -> {
            Log.d(TAG, "onFocusChange: "+position);
            if (b) {
                if (lastFocussedPosition == -1 || lastFocussedPosition == position) {
                    lastFocussedPosition = position;
                    if (preference.getBoolean("tv_focus_zoom", false)) {
                        holder.itemView.setScaleX((float) 1.05);
                        holder.itemView.setScaleY((float) 1.05);
                    }
                    holder.cardView.requestFocus();
                }
            } else {
                if (preference.getBoolean("tv_focus_zoom", false)) {
                    holder.itemView.setScaleX((float) 0.85);
                    holder.itemView.setScaleY((float) 0.85);
                }
                lastFocussedPosition = -1;
            }
        });


        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ImgActivity.class);
            intent.putExtra("Img", items.toString() + ",http://leeford.in/wp-content/uploads/2017/09/image-not-found.jpg");
            intent.putExtra("Position", position);
            context.startActivity(intent);
        });
    }

    public void setItems (ArrayList<String> items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        } else return 0;
    }

    class CatalogViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        CardView cardView;

        CatalogViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }
}
