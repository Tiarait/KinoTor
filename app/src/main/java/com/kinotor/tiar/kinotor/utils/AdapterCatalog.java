package com.kinotor.tiar.kinotor.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.ui.DetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Tiar on 24.09.2017.
 */

public abstract class AdapterCatalog  extends RecyclerView.Adapter<AdapterCatalog.CatalogViewHolder> {
    Context context;
    String category;
    DBHelper dbHelper;
    ArrayList<ItemHtml> htmlItems;

    public AdapterCatalog (Context context, String category) {
        this.context = context;
        this.category = category;
        if (!category.equals("catalog")) {
            dbHelper = new DBHelper(context);
            htmlItems = dbHelper.getDbItems(category);
        }
    }

    @Override
    public CatalogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        View view;
        if (preference.getString("grid_catalog", "5").equals("1"))
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog_line, parent, false);
        else view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog, parent, false);
        CatalogViewHolder holder = new CatalogViewHolder(view);
        return holder;
    }

    private void updateSelect(View view) {
        if (!view.isSelected()) {
            view.setBackgroundColor(view.getResources().getColor(R.color.colorAccent));
            YoYo.with(Techniques.FadeIn).playOn(view);
        }
        else {
            view.setBackgroundColor(view.getResources().getColor(R.color.colorGone));
        }
    }

    @Override
    public void onBindViewHolder(CatalogViewHolder holder, final int position) {
        final ItemHtml current = htmlItems.get(position);
        holder.title.setText(current.getTitle(position));
        holder.quality.setText(current.getQuality(position));

        if (current.getVoice(position).contains("error"))
            holder.voice.setVisibility(View.GONE);
        if (current.getQuality(position).contains("error"))
            holder.quality.setVisibility(View.GONE);

        if (current.getSeason(position) == 0 && current.getSeries(position) == 0)
            holder.voice.setText(current.getVoice(position));
        else if (current.getSeason(position) != 0 && current.getSeries(position) == 0)
            holder.voice.setText(current.getSeason(position) + " сезон ");
        else holder.voice.setText(current.getSeason(position) + " сезон " +
                    current.getSeries(position) + " серия");

        Picasso.with(context)
                .load(current.getImg(position))
                .placeholder(R.drawable.loading)
                .error(R.drawable.error)
                .into(holder.poster);
        holder.cardView.setFocusable(true);
//        holder.cardView.setFocusableInTouchMode(true);
        holder.cardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                updateSelect(view);
                view.setSelected(b);
            }
        });
        holder.cardView.requestFocus();

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ItemMain.isLoading) {
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
//                updateSelect(v);
            }
        });

        if ((position >= getItemCount() - 4) && ItemMain.cur_items < getItemCount() && category.equals("catalog")) {
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
        if (htmlItems != null)
            return htmlItems.size();
        else return 0;
    }

    public class CatalogViewHolder extends RecyclerView.ViewHolder {
        TextView title, quality, voice;
        ImageView poster;
        CardView cardView;

        public CatalogViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            quality = (TextView) itemView.findViewById(R.id.quality);
            voice = (TextView) itemView.findViewById(R.id.voice);
            poster = (ImageView) itemView.findViewById(R.id.imgPoster);
            cardView = (CardView) itemView.findViewById(R.id.cardview);
        }
    }
}
