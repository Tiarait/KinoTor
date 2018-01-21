package com.kinotor.tiar.kinotor.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemBase;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.parser.ParserBase;
import com.kinotor.tiar.kinotor.ui.DetailActivity;

/**
 * Created by Tiar on 30.09.2017.
 */

public class AdapterVideo extends RecyclerView.Adapter<AdapterVideo.ViewHolder> {
    private ItemBase itemBase;
    private RelativeLayout pb;

    public AdapterVideo(ItemBase itemBase) {
        this.itemBase = itemBase;
        this.pb = pb;
    }

    @Override
    public AdapterVideo.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_vid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdapterVideo.ViewHolder holder, int position) {
        final int cur = position;

        if (ItemMain.stat.equals("catalog"))
            holder.name.setText(itemBase.getTranslator(position));
        else if (ItemMain.stat.equals("season"))
            holder.name.setText(itemBase.getTitle(position) + " сезон");
        else if (ItemMain.stat.equals("series"))
            holder.name.setText(itemBase.getTitle(position) + " серия");

        if (ItemMain.stat.equals("series"))
            holder.desc.setText(itemBase.getSeason(position)+ " сезон");
        else if (ItemMain.stat.equals("season"))
            holder.desc.setText(itemBase.getSeason(position) + " " + seriesPatteg(itemBase.getSeason(position)));
        else holder.desc.setText(itemBase.getType(position));

        if (!itemBase.getSeason(position).contains("error") && ItemMain.stat.equals("catalog")) {
            holder.size.setVisibility(View.VISIBLE);
            holder.size.setText("s" + itemBase.getSeason(position) + "e" + itemBase.getEpisode(position));
        }

        if (itemBase.getTitle(position).contains("back")) {
            holder.desc.setText(itemBase.getType(position));
            holder.name.setText(itemBase.getTranslator(position));
            holder.icon.setImageResource(R.drawable.ic_back_arrow);
        } else if ((ItemMain.stat.equals("catalog") || ItemMain.stat.equals("season")) && !DetailActivity.type.equals("movie") )
            holder.icon.setImageResource(R.drawable.ic_folder);
        else holder.icon.setImageResource(R.drawable.ic_mp4_file);

        if ((ItemMain.stat.equals("series") && !itemBase.getTitle(position).contains("back")) ||
                DetailActivity.type.equals("movie"))
            holder.download.setVisibility(View.VISIBLE);
        else holder.download.setVisibility(View.GONE);

        if (itemBase.getUrl(position).contains("error")){
            holder.download.setVisibility(View.GONE);
        }

        holder.download.setFocusable(true);
        holder.download.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.isSelected()) {
                    holder.download.setBackgroundColor(view.getResources().getColor(R.color.colorBlack));
                }
                else holder.download.setBackgroundColor(view.getResources().getColor(R.color.colorGone));
                view.setSelected(b);
            }
        });

        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParserBase parserBase = new ParserBase("url.download name= film", itemBase.getUrl(cur));
                parserBase.execute();
            }
        });

        holder.name.setFocusable(true);
//        holder.name.setFocusableInTouchMode(true);
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
                    YoYo.with(Techniques.FadeIn).playOn(holder.mView);
                }
                else {
                    holder.mView.setBackgroundColor(view.getResources().getColor(R.color.colorDarkGrey));
                    holder.name.setTextColor(view.getResources().getColor(R.color.colorWhite));
                    holder.desc.setTextColor(view.getResources().getColor(R.color.colorWhite));
                    holder.size.setTextColor(view.getResources().getColor(R.color.colorWhite));
                    holder.download.clearColorFilter();
                    holder.icon.clearColorFilter();
                }
                view.setSelected(b);
            }
        });
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Click(view, cur);
            }
        });
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Click(view, cur);
            }
            });
    }

    public void Click(View view, final int cur){
        if (ItemMain.stat.contains("catalog")) {
            if (DetailActivity.type.equals("movie")) {
                ParserBase parserBase = new ParserBase("url.play name= film", itemBase.getUrl(cur));
                parserBase.execute();
            } else if (DetailActivity.type.contains("serial")) {
                if (itemBase.getId(cur) != null && itemBase.getId(cur).contains("site")) {
                    ParserBase parserBase = new ParserBase(itemBase.getAll_url(), "series", itemBase.getTranslator(cur),
                            itemBase.getType(cur));
                    parserBase.execute();
                } else {
                    ParserBase parserBase = new ParserBase("season", itemBase.getType(cur),
                            itemBase.getId(cur), itemBase.getId_trans(cur));
                    parserBase.execute();
                }
            }
        } else if (ItemMain.stat.contains("season")) {
            if (itemBase.getTitle(cur).equals("back")) goBack("", cur);
            else {
                ParserBase parserBase = new ParserBase("series", itemBase.getTitle(cur),
                        itemBase.getType(cur), itemBase.getId(cur), itemBase.getId_trans(cur));
                parserBase.execute();
            }
        } else if (ItemMain.stat.contains("series")) {
            if (itemBase.getTitle(cur).equals("back")) goBack("season", cur);
            else if (itemBase.getTitle(cur).equals("superback")) goBack("", cur);
            else {
                ParserBase parserBase = new ParserBase("url.play name=" + itemBase.getTitle(cur),
                        itemBase.getUrl(cur));
                parserBase.execute();
            }
        }
    }

    public void goBack(String way, int cur){
        if (way.equals("season")){
            ParserBase parserBase = new ParserBase(way, itemBase.getType(cur),
                    itemBase.getId(cur), itemBase.getId_trans(cur));
            parserBase.execute();
        } else {
            ParserBase parserBase = new ParserBase(DetailActivity.title.split(" \\(")[0], DetailActivity.type, "catalog");
            parserBase.execute();
        }
    }

    @Override
    public int getItemCount() {
        if (itemBase != null)
            return itemBase.translator.size();
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView name, size, desc;
        public final ImageButton download;
        public final ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            name = (TextView) itemView.findViewById(R.id.name);
            size = (TextView) itemView.findViewById(R.id.size);
            desc = (TextView) itemView.findViewById(R.id.desc);
            download = (ImageButton) itemView.findViewById(R.id.save);
            icon = (ImageView) itemView.findViewById(R.id.vid_ic);
        }
    }

    private String seriesPatteg(String season) {
        int s = season.contains("error") ? 0 : Integer.parseInt(season.trim());
        String series = "серий";
        if (s == 1 || s == 21 || s == 31 || s == 41 || s == 51) series = "серия";
        if ((s > 1 && s < 5) || (s > 21 && s < 25) || (s > 31 && s < 35)
                || (s > 41 && s < 45) || (s > 51 && s < 55)) series = "серии";
        return series;
    }
}
