package com.kinotor.tiar.kinotor.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.parser.animevost.AnimevostSeries;
import com.kinotor.tiar.kinotor.parser.animevost.AnimevostUrl;
import com.kinotor.tiar.kinotor.parser.hdgo.HdgoIframe;
import com.kinotor.tiar.kinotor.parser.hdgo.HdgoSeason;
import com.kinotor.tiar.kinotor.parser.hdgo.HdgoSeries;
import com.kinotor.tiar.kinotor.parser.hdgo.HdgoUrl;
import com.kinotor.tiar.kinotor.parser.hdgo.ParserHdgo;
import com.kinotor.tiar.kinotor.parser.moonwalk.MoonwalkSeason;
import com.kinotor.tiar.kinotor.parser.moonwalk.MoonwalkSeries;
import com.kinotor.tiar.kinotor.parser.moonwalk.MoonwalkUrl;
import com.kinotor.tiar.kinotor.parser.moonwalk.ParserMoonwalk;
import com.kinotor.tiar.kinotor.parser.trailer.ParserTrailer;
import com.kinotor.tiar.kinotor.parser.trailer.TrailerUrl;
import com.kinotor.tiar.kinotor.ui.DetailActivity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public abstract class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.ViewHolder> {
    private View view;
    private ItemVideo items;
    private ItemHtml item;
    private Context context;
    private LinearLayout pb;
    private final String CATALOG = "catalog";
    private final String SEASON = "season";
    private final String SERIES = "series";
    private final String ERROR = "error";
    private final String BACK = "back";


    protected AdapterVideo(Context context, ItemHtml item, LinearLayout pb) {
        this.item = item;
        this.context = context;
        this.pb = pb;
    }

    @Override
    public AdapterVideo.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_vid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdapterVideo.ViewHolder holder, int position) {
        final int cur = position;
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        int sizetext = Integer.parseInt(preference.getString("text_size_detail", "13"));

        if (items.getTitle(cur).contains(CATALOG)) {
            holder.name.setText(items.getTranslator(cur));
            holder.desc.setText(items.getType(cur));
            if (!items.getSeason(cur).contains(ERROR)) {
                holder.size.setVisibility(View.VISIBLE);
                holder.size.setText("s" + items.getSeason(cur) + "e" + items.getEpisode(cur));
                holder.icon.setImageResource(R.drawable.ic_folder);
                holder.download.setVisibility(View.GONE);
            } else {
                holder.icon.setImageResource(R.drawable.ic_mp4_file);
                holder.size.setVisibility(View.GONE);
                holder.download.setVisibility(View.VISIBLE);
            }
        } else if (items.getTitle(cur).contains(SEASON)) {
            holder.download.setVisibility(View.GONE);
            holder.desc.setVisibility(View.GONE);
            holder.size.setVisibility(View.VISIBLE);
            if (items.getTitle(cur).contains(BACK)){
                holder.name.setText(items.getTranslator(cur).contains(ERROR) ? "Неизвестный" : items.getTranslator(cur));
                holder.size.setText(items.getType(cur));
                holder.icon.setImageResource(R.drawable.ic_back_arrow);
            } else {
                holder.icon.setImageResource(R.drawable.ic_folder);
                holder.name.setText(items.getSeason(cur) + " сезон");
                holder.size.setText(items.getEpisode(cur) + seriesPatteg(items.getEpisode(cur)));
            }

        } else if (items.getTitle(cur).contains(SERIES)) {
            if (items.getTitle(cur).contains(BACK)){
                holder.download.setVisibility(View.GONE);
                holder.size.setVisibility(View.VISIBLE);
                holder.desc.setVisibility(View.VISIBLE);
                holder.size.setText(items.getType(cur));
                holder.name.setText(items.getSeason(cur) + " сезон");
                holder.desc.setText(items.getTranslator(cur).contains(ERROR) ? "Неизвестный" : items.getTranslator(cur));
                holder.icon.setImageResource(R.drawable.ic_back_arrow);
            } else {
                holder.icon.setImageResource(R.drawable.ic_mp4_file);
                holder.name.setText(items.getEpisode(cur) + " серия");
                holder.download.setVisibility(View.VISIBLE);
                holder.desc.setVisibility(View.GONE);
                holder.size.setVisibility(View.GONE);
            }
        } else if (items.getTitle(cur).contains("site back")) {
            holder.download.setVisibility(View.GONE);
            holder.size.setVisibility(View.VISIBLE);
            holder.desc.setVisibility(View.VISIBLE);
            holder.size.setText(items.getSeason(cur) + " сезон");
            holder.name.setText(items.getTranslator(cur).contains(ERROR) ? "Неизвестный" : items.getTranslator(cur));
            holder.desc.setText(items.getType(cur));
            holder.icon.setImageResource(R.drawable.ic_back_arrow);
        }
        if (items.getTitle(cur).contains(CATALOG))
            holder.desc.setVisibility(View.VISIBLE);

        //text size
        holder.name.setTextSize(sizetext + 4);
        holder.desc.setTextSize(sizetext);
        holder.size.setTextSize(sizetext);

        holder.download.setFocusable(true);
        holder.download.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.isSelected()) {
                    holder.download.setBackgroundColor(view.getResources().getColor(R.color.colorAccentDark));
                }
                else holder.download.setBackgroundColor(view.getResources().getColor(R.color.colorGone));
                view.setSelected(b);
            }
        });
        holder.name.setFocusable(true);
        holder.name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.isSelected()) {
                    holder.mView.setBackgroundColor(view.getResources().getColor(R.color.colorAccent));
                    holder.name.setTextColor(view.getResources().getColor(R.color.colorBlack));
                    holder.desc.setTextColor(view.getResources().getColor(R.color.colorBlack));
                    holder.size.setTextColor(view.getResources().getColor(R.color.colorBlack));
                    holder.download.setColorFilter(view.getResources().getColor(R.color.colorBlack));
                    holder.icon.setColorFilter(view.getResources().getColor(R.color.colorBlack));
                }
                else {
                    holder.mView.setBackgroundColor(view.getResources().getColor(R.color.colorPrimaryLight));
                    holder.name.setTextColor(view.getResources().getColor(R.color.colorWhite));
                    holder.desc.setTextColor(view.getResources().getColor(R.color.colorWhite));
                    holder.size.setTextColor(view.getResources().getColor(R.color.colorWhite));
                    holder.download.clearColorFilter();
                    holder.icon.clearColorFilter();
                }
                view.setSelected(b);
            }
        });
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUrl(cur, false);
            }
        });
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPlay(cur);
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickPlay(cur);
            }
        });
    }

    private String seriesPatteg(String season) {
        int s = season.contains(ERROR) ? 0 : Integer.parseInt(season.trim());
        String series = " серий";
        if (s == 1 || s == 21 || s == 31 || s == 41 || s == 51) series = " серия";
        if ((s > 1 && s < 5) || (s > 21 && s < 25) || (s > 31 && s < 35)
                || (s > 41 && s < 45) || (s > 51 && s < 55)) series = " серии";
        return series;
    }

    private void clickPlay(final int cur){
        pb.setVisibility(View.VISIBLE);
        Log.d(TAG, "Click play: " + items.getTitle(cur));
        if (items.getTitle(cur).contains(CATALOG)) {
            if (!items.getSeason(cur).contains(ERROR)) {
                if (items.getId(cur).contains("site")) getSiteIframe(cur);
                else getSeason(cur);
            } else getUrl(cur, true);
        } else if (items.getTitle(cur).contains(SEASON)) {
            if (items.getTitle(cur).contains(BACK)) getBase();
            else getSeries(cur);
        } else if (items.getTitle(cur).contains(SERIES)) {
            if (items.getTitle(cur).contains(BACK)) getSeason(cur);
            else getUrl(cur, true);
        } else if (items.getTitle(cur).contains("site back")) getBase();
    }

    private void getUrl(final int cur, final boolean play) {
        pb.setVisibility(View.VISIBLE);
        if (items.url.size() > 0) {
            Log.d(TAG, "getUrl: " + items.getType(cur) + " " + items.getUrl(cur));
            if (items.getType(cur).contains("moonwalk")) {
                MoonwalkUrl getMp4 = new MoonwalkUrl(items.getUrl(cur), new OnTaskUrlCallback() {
                    @Override
                    public void OnCompleted(String[] quality, String[] url) {
                        play(quality, url, items.getSeason(cur), items.getEpisode(cur), play);
                    }
                });
                getMp4.execute();
            } else if (items.getType(cur).contains("hdgo")) {
                HdgoUrl getMp4 = new HdgoUrl(items.getUrl(cur), new OnTaskUrlCallback() {
                    @Override
                    public void OnCompleted(String[] quality, String[] url) {
                        play(quality, url, items.getSeason(cur), items.getEpisode(cur), play);
                    }
                });
                getMp4.execute();
            } else if (items.getType(cur).contains("animevost")) {
                AnimevostUrl getMp4 = new AnimevostUrl(items.getUrlSite(cur), new OnTaskUrlCallback() {
                    @Override
                    public void OnCompleted(String[] quality, String[] url) {
                        play(quality, url, items.getSeason(cur), items.getEpisode(cur), play);
                    }
                });
                getMp4.execute();
            } else if (items.getType(cur).contains("kinomania") ||
                    items.getUrl(cur).contains("kinomania")) {
                TrailerUrl getMp4 = new TrailerUrl(items.getUrl(cur), new OnTaskUrlCallback() {
                    @Override
                    public void OnCompleted(String[] quality, String[] url) {
                        play(quality, url, items.getSeason(cur), items.getEpisode(cur), play);
                    }
                });
                getMp4.execute();
            }
        } else
            Log.d(TAG, "getUrl: " + items.getType(cur) + " url not found");
    }

    private void getSeason (final int cur) {
        if (items.getType(cur).contains("moonwalk")) {
            MoonwalkSeason getSeason = new MoonwalkSeason(items.getId(cur),
                    items.getId_trans(cur), new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    reload(items);
                }
            });
            getSeason.execute();
        } else if (items.getType(cur).contains("hdgo")) {
            HdgoSeason getSeason = new HdgoSeason(items.getId(cur), new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    reload(items);
                }
            });
            getSeason.execute();
        }
    }

    private void getSiteIframe (final int cur) {
        if (items.getType(cur).contains("hdgo")) {
            HdgoIframe getIframeSeries = new HdgoIframe(items.getAll_urlSite(),
                    items.getTranslator(cur), item, new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    reload(items);
                }
            });
            getIframeSeries.execute();
        } else if (items.getType(cur).contains("animevost")) {
            AnimevostSeries getSeries = new AnimevostSeries(items.getUrlSite(cur), item, new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    reload(items);
                }
            });
            getSeries.execute();
        }
    }

    private void getSeries (final int cur) {
        if (items.getType(cur).contains("moonwalk")) {
            MoonwalkSeries getSeries = new MoonwalkSeries(items.getId(cur), items.getId_trans(cur),
                    items.getSeason(cur), new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    reload(items);
                }
            });
            getSeries.execute();
        } else if (items.getType(cur).contains("hdgo")) {
            HdgoSeries getSeries = new HdgoSeries(items.getId(cur),
                    items.getSeason(cur), new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    reload(items);
                }
            });
            getSeries.execute();
        }
    }

    private void getBase () {
        items = new ItemVideo();
        HashSet<String> def = new HashSet<>(Arrays.asList("hdgo", "moonwalk"));
        Set<String> pref_base = PreferenceManager.getDefaultSharedPreferences(context)
                .getStringSet("base_video", def);

        ParserTrailer parserTrailer = new ParserTrailer(item, new OnTaskVideoCallback() {
            @Override
            public void OnCompleted(ItemVideo items) {
                update(items);
            }
        });
        parserTrailer.execute();
        if (item.getIframe(0).contains("hdgo")){
            HdgoIframe getIframe = new HdgoIframe(item, true, new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    update(items);
                }
            });
            getIframe.execute();
        } else if (DetailActivity.url.contains("animevost")){
            AnimevostSeries getList = new AnimevostSeries(item, true, new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    update(items);
                }
            });
            getList.execute();
        }
        if (pref_base.contains("moonwalk")) {
            ParserMoonwalk getList = new ParserMoonwalk(item, new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    update(items);
                }
            });
            getList.execute();
        }
        if (pref_base.contains("hdgo")) {
            ParserHdgo getList = new ParserHdgo(item, new OnTaskVideoCallback() {
                @Override
                public void OnCompleted(ItemVideo items) {
                    update(items);
                }
            });
            getList.execute();
        }
    }

    public abstract void update(ItemVideo items);
    public abstract void reload(ItemVideo items);
    public abstract void play(String[] quality, String[] url, String s, String e, boolean play);


    @Override
    public int getItemCount() {
        if (items != null)
            return items.translator.size();
        else return 0;
    }

    public void setItems (ItemVideo items) {
        this.items = items;
    }

    public void addItems (ItemVideo items) {
        if (this.items != null)
            this.items.addItems(items);
        else this.items = items;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView name, size, desc;
        public final ImageButton download;
        public final ImageView icon;

        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            name = itemView.findViewById(R.id.name);
            size = itemView.findViewById(R.id.size);
            desc = itemView.findViewById(R.id.desc);
            download = itemView.findViewById(R.id.save);
            icon = itemView.findViewById(R.id.vid_ic);
        }
    }
}