package com.kinotor.tiar.kinotor.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.parser.ParseUrl;
import com.kinotor.tiar.kinotor.parser.ParserHtml;
import com.kinotor.tiar.kinotor.ui.DetailActivity;

import org.jsoup.Connection;

import java.util.concurrent.ExecutionException;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 30.09.2017.
 */

public class AdapterTorrents extends RecyclerView.Adapter<AdapterTorrents.ViewHolder> {

    @Override
    public AdapterTorrents.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_tor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdapterTorrents.ViewHolder holder, int position) {
        final int cur = position;
        holder.name.setText(ParserHtml.itemDetail.getTor_name(position));
        if (ParserHtml.itemDetail.getTor_size(position).contains("error"))
            holder.size.setVisibility(View.GONE);
        else {
            holder.size.setVisibility(View.VISIBLE);
            holder.size.setText(ParserHtml.itemDetail.getTor_size(position));
        }

        if (ParserHtml.itemDetail.getTor_sid(cur).equals("x"))
            holder.sl.setVisibility(View.GONE);
        else {
            holder.sl.setVisibility(View.VISIBLE);
            holder.lich.setText(ParserHtml.itemDetail.getTor_lich(cur));
            holder.sid.setText(ParserHtml.itemDetail.getTor_sid(cur));
        }

        if (!ParserHtml.itemDetail.getTor_magnet(position).contains("error")) {
            holder.magnet.setVisibility(View.VISIBLE);
            holder.magnet.setFocusable(true);
            holder.magnet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!view.isSelected()) {
                        view.setBackgroundColor(view.getResources().getColor(R.color.colorBlack));
                    }
                    else view.setBackgroundColor(view.getResources().getColor(R.color.colorGone));
                    view.setSelected(b);
                }
            });
        }
        if (!ParserHtml.itemDetail.getTor_content(position).contains("error")){
            holder.desc.setVisibility(View.VISIBLE);
            holder.desc.setText(ParserHtml.itemDetail.getTor_content(position));
        }
        holder.download.setFocusable(true);
        holder.download.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.isSelected()) {
                    view.setBackgroundColor(view.getResources().getColor(R.color.colorBlack));
                }
                else view.setBackgroundColor(view.getResources().getColor(R.color.colorGone));
                view.setSelected(b);
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
                    holder.magnet.setColorFilter(view.getResources().getColor(R.color.colorBlack));
                    holder.icon.setColorFilter(view.getResources().getColor(R.color.colorBlack));
                    holder.sid.setTextColor(view.getResources().getColor(R.color.colorBlack));
                    holder.lich.setTextColor(view.getResources().getColor(R.color.colorBlack));
                    YoYo.with(Techniques.FadeIn).playOn(holder.mView);
                }
                else {
                    holder.mView.setBackgroundColor(view.getResources().getColor(R.color.colorDarkGrey));
                    holder.name.setTextColor(view.getResources().getColor(R.color.colorWhite));
                    holder.desc.setTextColor(view.getResources().getColor(R.color.colorWhite));
                    holder.size.setTextColor(view.getResources().getColor(R.color.colorWhite));
                    holder.sid.setTextColor(view.getResources().getColor(R.color.colorWhite));
                    holder.lich.setTextColor(view.getResources().getColor(R.color.colorWhite));
                    holder.download.clearColorFilter();
                    holder.magnet.clearColorFilter();
                    holder.icon.clearColorFilter();
                }
                view.setSelected(b);
            }
        });

        holder.magnet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickMagnet(view, cur);
            }
        });
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(ParserHtml.itemDetail.getTorrents(cur)));
                DetailActivity.fragm_vid.getContext().startActivity(intent);
            }
        });
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ParserHtml.itemDetail.getTorrents(cur).contains("koshara") ||
                        ParserHtml.itemDetail.getTorrents(cur).contains("coldfilm")) {
                    ParserHtml.itemDetail.setCur(cur);
                    ParserHtml parse = new ParserHtml(ParserHtml.itemDetail.getTorrents(cur), "torrent down play");
                    parse.execute();
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(ParserHtml.itemDetail.getTorrents(cur)));
                    DetailActivity.fragm_vid.getContext().startActivity(intent);
                }
            }
        });
    }

    private void ClickMagnet (View view, int cur) {
        String url = ParserHtml.itemDetail.getTor_magnet(cur).replace("'", "")
                .replace("}", "");
        if (!url.startsWith("magnet")) {
            Connection.Response s = null;
            try {
                s = new ParseUrl(url, null).execute().get();
            }  catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (s != null) url = s.header("Location");
            else url = "error";
        }
        ClipboardManager clipboard = (ClipboardManager) view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", url);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(view.getContext(), "Magnet ссылка скопирована в буфер обмена", Toast.LENGTH_LONG).show();

        Log.d(TAG, "onClick: " + url);
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
//                    intent.setType("application/x-bittorrent");
            DetailActivity.fragm_tor.getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (ParserHtml.itemDetail != null)
            return ParserHtml.itemDetail.torrents.size();
        else return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final LinearLayout sl;
        public final TextView name, size, desc, sid, lich;
        public final ImageView icon;
        public final ImageButton magnet, download;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            name = (TextView) itemView.findViewById(R.id.name);
            size = (TextView) itemView.findViewById(R.id.size);
            desc = (TextView) itemView.findViewById(R.id.desc);
            sid = (TextView) itemView.findViewById(R.id.sid);
            lich = (TextView) itemView.findViewById(R.id.lich);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            magnet = (ImageButton) itemView.findViewById(R.id.magnet);
            download = (ImageButton) itemView.findViewById(R.id.save);
            sl = (LinearLayout) itemView.findViewById(R.id.sl);

        }
    }
}
