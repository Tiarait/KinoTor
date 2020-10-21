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
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.ui.DetailActivity;
import com.kinotor.tiar.kinotor.ui.DetailActivityTv;
import com.kinotor.tiar.kinotor.utils.Utils;
import com.squareup.picasso.Picasso;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 04.2018.
 */
public class AdapterMore extends RecyclerView.Adapter<AdapterMore.CatalogViewHolder> {
    private Context context;
    private ItemHtml items;
    private int lastFocussedPosition = -1;
    private SharedPreferences preference;

    public AdapterMore(Context context) {
        this.context = context;
    }

    @Override
    public AdapterMore.CatalogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        preference = PreferenceManager.getDefaultSharedPreferences(context);
        View view;
        if (!preference.getBoolean("tv_focus_select", true))
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog_more_inverse_noselect, parent, false);
        else view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_catalog_more_inverse, parent, false);

        if (view.findViewById(R.id.large) != null) {
            RecyclerView.LayoutParams lp = (RecyclerView.LayoutParams) view.getLayoutParams();
            Utils utils = new Utils();
            lp.width = (int) (utils.dpToPixel(Statics.CATALOG_W, context));
            lp.height = (int) (utils.dpToPixel(Statics.CATALOG_H, context));
            view.setLayoutParams(lp);
        }

        return new AdapterMore.CatalogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterMore.CatalogViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.title.setText(items.getMoreTitle(position));
        holder.quality.setText(items.getMoreQu(position));
        holder.voice.setVisibility(View.GONE);

//        holder.cardView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
        if (preference.getString("theme_list", "gray").equals("gray")) {
            holder.voice.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
            holder.lTitle.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
            holder.poster.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryDark));
        } else if (preference.getString("theme_list", "gray").equals("black")) {
            holder.voice.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
            holder.lTitle.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
            holder.poster.setBackgroundColor(context.getResources().getColor(R.color.colorPrimaryLight));
        }
        if (preference.getBoolean("tv_activity_detail", true)) {
            holder.cardView.setRadius(10f);
        }

        if (items.getMoreVoice(position).contains("error"))
            holder.voice.setVisibility(View.GONE);
        if (items.getMoreQu(position).contains("error")) {
            holder.quality.setVisibility(View.GONE);
            holder.qualityRoud.setVisibility(View.GONE);
        }

//        String s, e;
//        if (large){
//            s = items.getMoreSeason(position)+"сезон ";
//            e = items.getMoreSeries(position)+" серия";
//        } else {
//            s = "s"+items.getMoreSeason(position);
//            e = "e"+items.getMoreSeries(position);
//        }

//        if (items.getMoreSeason(position).equals("0") && items.getMoreSeries(position).equals("0"))
//            holder.voice.setText(items.getMoreVoice(position));
//        else if (!items.getMoreSeason(position).equals("0") && items.getMoreSeries(position).equals("0")) {
//            holder.voice.setVisibility(View.VISIBLE);
//            holder.voice.setText(s);
//        } else {
//            holder.voice.setVisibility(View.VISIBLE);
//            holder.voice.setText(s + e);
//        }

        if (items.getMoreImg(position).contains(Statics.RUFILMTV_URL) ||
                items.getMoreImg(position).contains("fanserials.")) {
            Picasso.get()
                    .load(items.getMoreImg(position))
                    .fit().centerInside()
                    .into(holder.poster);
        } else if (items.getMoreImg(position).contains("error")) {
            Picasso.get()
                    .load("https://m.media-amazon.com/images/G/01/imdb/images/nopicture/medium/film-3385785534._CB483791896_.png")
                    .fit().centerCrop()
                    .into(holder.poster);
        } else {
            Picasso.get()
                    .load(items.getMoreImg(position))
                    .fit().centerCrop()
                    .into(holder.poster);
        }
        holder.cardView.setFocusable(true);

        if (preference.getBoolean("tv_activity_detail", false)) {
            if (position == 0) holder.cardView.setNextFocusLeftId(holder.cardView.getId());
            else if (position == getItemCount() - 1)
                holder.cardView.setNextFocusRightId(holder.cardView.getId());
        }

        if (preference.getBoolean("tv_focus_zoom", false)) {
            holder.itemView.setScaleX(0.9f);
            holder.itemView.setScaleY(0.9f);
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
                        holder.itemView.setScaleX(1.05f);
                        holder.itemView.setScaleY(1.05f);
                    }
                    holder.cardView.requestFocus();
                }
            } else {
                if (preference.getBoolean("tv_focus_zoom", false)) {
                    holder.itemView.setScaleX(0.9f);
                    holder.itemView.setScaleY(0.9f);
                }
                lastFocussedPosition = -1;
            }
        });

        holder.cardView.setOnClickListener(v -> {
            Intent intent;
            if (!preference.getBoolean("tv_activity_detail", true)) {
                intent = new Intent(context, DetailActivity.class);
            } else {
                intent = new Intent(context, DetailActivityTv.class);
            }
            intent.putExtra("Title", items.getMoreTitle(position));
            intent.putExtra("Url", items.getMoreUrl(position));
            intent.putExtra("Img", items.getMoreImg(position));
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation((Activity) context, holder.poster, "poster");
                context.startActivity(intent, options.toBundle());
            } else context.startActivity(intent);
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
        TextView title, quality, qualityRoud, voice;
        ImageView poster;
        CardView cardView;
        LinearLayout lTitle;

        CatalogViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            lTitle = itemView.findViewById(R.id.l_title);
            quality = itemView.findViewById(R.id.quality);
            qualityRoud = itemView.findViewById(R.id.quality_roud);
            poster = itemView.findViewById(R.id.imgPoster);
            cardView = itemView.findViewById(R.id.cardview);
            voice = itemView.findViewById(R.id.voice);
        }
    }
}
