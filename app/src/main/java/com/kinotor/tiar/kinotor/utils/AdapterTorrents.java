package com.kinotor.tiar.kinotor.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
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
import android.widget.Toast;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemTorrent;
import com.kinotor.tiar.kinotor.parser.GetLocation;
import com.kinotor.tiar.kinotor.parser.torrents.FreerutorLocation;
import com.kinotor.tiar.kinotor.parser.torrents.FreerutorUrl;

import java.util.List;

/**
 * Created by Tiar on 30.09.2017.
 */

public class AdapterTorrents extends RecyclerView.Adapter<AdapterTorrents.ViewHolder> {
    private Context context;
    private ItemTorrent item;

    public AdapterTorrents(Context context, ItemTorrent item) {
        this.item = item;
        this.context = context;
    }

    @Override
    public AdapterTorrents.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_tor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdapterTorrents.ViewHolder holder, int position) {
        SharedPreferences preference = PreferenceManager.getDefaultSharedPreferences(context);
        int sizetext = Integer.parseInt(preference.getString("text_size_detail", "13"));
        final int cur = position;
        holder.name.setText(item.getTorTitle(position));
        if (item.getTorSize(position).contains("error"))
            holder.size.setVisibility(View.GONE);
        else {
            holder.size.setVisibility(View.VISIBLE);
            holder.size.setText(item.getTorSize(position));
        }

        if (item.getTorSid(cur).equals("x"))
            holder.sl.setVisibility(View.GONE);
        else {
            holder.sl.setVisibility(View.VISIBLE);
            holder.lich.setText(item.getTorLich(cur));
            holder.sid.setText(item.getTorSid(cur));
        }

        if (!item.getTorMagnet(position).contains("error")) {
            holder.magnet.setVisibility(View.VISIBLE);
            holder.magnet.setFocusable(true);
            holder.magnet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {
                    if (!view.isSelected()) {
                        view.setBackgroundColor(view.getResources().getColor(R.color.colorAccentDark));
                    }
                    else view.setBackgroundColor(view.getResources().getColor(R.color.colorGone));
                    view.setSelected(b);
                }
            });
        } else holder.magnet.setVisibility(View.GONE);
        if (!item.getTorContent(position).contains("error")){
            holder.desc.setVisibility(View.VISIBLE);
            holder.desc.setText(item.getTorContent(position));
        }
        holder.download.setFocusable(true);
        holder.download.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!view.isSelected()) {
                    view.setBackgroundColor(view.getResources().getColor(R.color.colorAccentDark));
                }
                else view.setBackgroundColor(view.getResources().getColor(R.color.colorGone));
                view.setSelected(b);
            }
        });

        //text size
        holder.name.setTextSize(sizetext + 4);
        holder.desc.setTextSize(sizetext);
        holder.lich.setTextSize(sizetext);
        holder.sid.setTextSize(sizetext);
        holder.size.setTextSize(sizetext);


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
                }
                else {
                    holder.mView.setBackgroundColor(view.getResources().getColor(R.color.colorPrimaryLight));
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
                ClickMagnet(cur);
            }
        });
        holder.download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(item.getTorUrl(cur)));

                PackageManager packageManager = context.getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
                if (activities.size() > 0)
                    context.startActivity(Intent.createChooser(intent, "Скачать с помощью.."));
            }
        });
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("torrent", "onClick: " + item.getTorTitle(0));
                Log.d("torrent", "onClick: " + item.getTorUrl(0));
                if (item.getTorUrl(cur).contains("freerutor.me")) {
                    FreerutorUrl getTor = new FreerutorUrl(item.getTorUrl(cur), "play", new OnTaskLocationCallback() {
                        @Override
                        public void OnCompleted(String location) {
                            onMagnet(location);
                        }
                    });
                    getTor.execute();
                }else {
                    onMagnet(item.getTorUrl(cur));
                }
            }
        });
    }

    private void startIntent(Uri uri) {
//            intent.setDataAndType(uri, "application/x-bittorrent");
//            intent.setDataAndType(uri, "application/*");
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setDataAndType(uri, "application/x-bittorrent");

//        PackageManager packageManager = context.getPackageManager();
//        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
////        List<ApplicationInfo> activities = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
//        Log.d("adaptertorrent", "startIntent: " + uri + " " + activities.size());
//        if (activities.size() > 0) {
//            context.startActivity(intent);
//            List<Intent> targetedShareIntents = new ArrayList<>();
//            for (ResolveInfo r : activities) {
//                Intent progIntent = (Intent)intent.clone();
//                String packageName = r.activityInfo.packageName;
//                progIntent.setPackage(packageName);
//                if (packageName.contains("torrent")
//                        || packageName.contains("mediaget")) {
//                    targetedShareIntents.add(progIntent);
//                }
//            }
//            if (targetedShareIntents.size() > 0) {
//                Intent chooserIntent = Intent.createChooser(targetedShareIntents.remove(0),
//                        "Открыть с помощью...");
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
//                        targetedShareIntents.toArray(new Parcelable[] {}));
//                context.startActivity(chooserIntent);
//            } else {
//                intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(uri, "application/*");
//
//                packageManager = context.getPackageManager();
//                activities = packageManager.queryIntentActivities(intent, 0);
//                if (activities.size() > 0)
//                    CIntent.createChooser(intent, "Торрент клиент не найден.."));
//            }
//        } else {
//            intent = new Intent(Intent.ACTION_VIEW);
//            intent.setData(uri);
//            context.startActivity(intent);
//        }
        if (uri.toString().contains("magnet") || uri.toString().contains("rutracker") ||
                uri.toString().contains("underverse") || uri.toString().contains("kinozal")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            context.startActivity(intent);
        } else {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/x-bittorrent");
                context.startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                context.startActivity(intent);
            }
        }
    }

    private void ClickMagnet (int cur) {
        Log.d("adaptertorrent", "ClickMagnet: " + cur);
        String url = item.getTorMagnet(cur).replace("'", "")
                .replace("}", "");
        if (url.startsWith("http://freerutor.me/")){
            FreerutorUrl getMagnet = new FreerutorUrl(url, "magnet", new OnTaskLocationCallback() {
                @Override
                public void OnCompleted(String location) {
                    onMagnet(location);
                }
            });
            getMagnet.execute();
        } else {
            if (url.contains("tparser")) {
                url = url.replace("'", "").replace("}", "");
                GetLocation getLocation = new GetLocation(url, new OnTaskLocationCallback() {
                    @Override
                    public void OnCompleted(String location) {
                        onMagnet(location);
                    }
                });
                getLocation.execute();
            } else if (!url.startsWith("magnet")) {
                FreerutorLocation getLocation = new FreerutorLocation(url, new OnTaskLocationCallback() {
                    @Override
                    public void OnCompleted(String location) {
                        onMagnet(location);
                    }
                });
                getLocation.execute();
            } else onMagnet(url);
        }
    }

    private void onMagnet (String location) {
        if (!location.isEmpty() && !location.contains("error")) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("", location);
            if (clipboard != null) clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Cсылка скопирована в буфер обмена",
                    Toast.LENGTH_LONG).show();
            Log.d("adaptertorrent", "onMagnet: " + location);
            startIntent(Uri.parse(location));
        } else
            Toast.makeText(context, "Ошибка magnet ссылки.",
                    Toast.LENGTH_LONG).show();
    }

    @Override
    public int getItemCount() {
        if (item != null)
            return item.tortitle.size();
        else
            return 0;
    }

    public void addItems(ItemTorrent item) {
        if (this.item != null)
            this.item.addItems(item);
        else this.item = item;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final LinearLayout sl;
        public final TextView name, size, desc, sid, lich;
        public final ImageView icon;
        public final ImageButton magnet, download;

        ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            name = itemView.findViewById(R.id.name);
            size = itemView.findViewById(R.id.size);
            desc = itemView.findViewById(R.id.desc);
            sid = itemView.findViewById(R.id.sid);
            lich = itemView.findViewById(R.id.lich);
            icon = itemView.findViewById(R.id.icon);
            magnet = itemView.findViewById(R.id.magnet);
            download = itemView.findViewById(R.id.save);
            sl = itemView.findViewById(R.id.sl);

        }
    }
}
