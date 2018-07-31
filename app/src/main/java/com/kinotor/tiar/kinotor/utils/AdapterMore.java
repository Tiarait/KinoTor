package com.kinotor.tiar.kinotor.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.ui.DetailActivity;
import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 04.2018.
 */
public class AdapterMore extends RecyclerView.Adapter<AdapterMore.CatalogViewHolder> {
    private Context context;
    private ItemHtml items;

    public AdapterMore(Context context) {
        this.context = context;
    }

    @Override
    public AdapterMore.CatalogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog, parent, false);

        RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
        Utils utils = new Utils();
        lp.height = (int) (utils.dpToPixel(Statics.CATALOG_H, context));
        lp.width = (int) (utils.dpToPixel(Statics.CATALOG_W, context));
        view.setLayoutParams(lp);

        return new AdapterMore.CatalogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdapterMore.CatalogViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.title.setText(items.getMoreTitle(position));
        holder.quality.setText(items.getMoreQu(position));
        holder.rating.setVisibility(View.GONE);
        holder.genre.setVisibility(View.GONE);

        if (items.getMoreVoice(position).contains("error"))
            holder.voice.setVisibility(View.GONE);
        if (items.getMoreQu(position).contains("error"))
            holder.quality.setVisibility(View.GONE);

        if (items.getMoreSeason(position).equals("0") && items.getMoreSeries(position).equals("0"))
            holder.voice.setText(items.getMoreVoice(position));
        else if (!items.getMoreSeason(position).equals("0") && items.getMoreSeries(position).equals("0")) {
            holder.voice.setVisibility(View.VISIBLE);
            holder.voice.setText(items.getMoreSeason(position) + " сезон ");
        } else {
            holder.voice.setVisibility(View.VISIBLE);
            holder.voice.setText(items.getMoreSeason(position) + " сезон " +
                    items.getMoreSeries(position) + " серия");
        }

        Picasso.with(context)
                .load(items.getMoreImg(position))
                .into(holder.poster);
        holder.cardView.setFocusable(true);
        holder.cardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Log.d(TAG, "onFocusChange: "+ position);
                if (!view.isSelected()) {
                    view.setBackgroundColor(view.getResources().getColor(R.color.colorAccent));
                    holder.title.setBackgroundColor(view.getResources().getColor(R.color.colorAccent));
                }
                else {
                    view.setBackgroundColor(view.getResources().getColor(R.color.colorGone));
                    holder.title.setBackgroundColor(view.getResources().getColor(R.color.colorPrimary));
                }
                view.setSelected(b);
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra("Title", items.getMoreTitle(position));
                intent.putExtra("Season", items.getMoreSeason(position));
                intent.putExtra("Serie", items.getMoreSeries(position));
                intent.putExtra("Url", items.getMoreUrl(position));
                intent.putExtra("Img", items.getMoreImg(position));
                intent.putExtra("Voice", items.getMoreVoice(position));
                intent.putExtra("Quality", items.getMoreQu(position));
                context.startActivity(intent);
            }
        });
    }

    public void setHtmlItems (ItemHtml items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            if (items.moretitle.size() > 1)
                return items.moretitle.size();
            else return 0;
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
            rating = itemView.findViewById(R.id.rating);
            voice = itemView.findViewById(R.id.voice);
            genre = itemView.findViewById(R.id.genre);
            poster = itemView.findViewById(R.id.imgPoster);
            cardView = itemView.findViewById(R.id.cardview);
        }
    }
}
