package com.kinotor.tiar.kinotor.parser.animevost;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class AnimevostSeries extends AsyncTask<Void, Void, Void> {
    private String all;
    private boolean catalog;
    private OnTaskVideoCallback callback;
    private ItemHtml item;
    private ItemVideo items;

    public AnimevostSeries(ItemHtml item, boolean catalog, OnTaskVideoCallback callback) {
        this.item = item;
        this.catalog = catalog;
        this.callback = callback;

        this.all = item.getIframe(0);
        this.items = new ItemVideo();
    }

    public AnimevostSeries(String all, ItemHtml item, OnTaskVideoCallback callback) {
        this.all = all;
        this.item = item;
        this.callback = callback;

        items = new ItemVideo();
        catalog = false;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(items);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (catalog) AnimeVost(all);
        else iframeSeries(all);
        return null;
    }

    private void iframeSeries(String all) {
        items.setTitle("site back");
        items.setType("animevost");
        items.setToken("error");
        items.setId("site");
        items.setId_trans("null");
        if (item.season.size() > 0)
            items.setSeason(String.valueOf(item.getSeason(0)));
        else items.setSeason("0");
        items.setUrlSite("error");
        items.setUrl("error");
        items.setEpisode("error");
        items.setTranslator("AnimeVost");

        if (all.contains(",")){
            String[] series = all.split(",");
            for (int i = 0; i < series.length; i++){
                series[i] = series[i].replace("'", "").trim();
                items.setTitle("series");
                items.setType("animevost");
                items.setToken("error");
                items.setId_trans("null");
                items.setId("site");
                items.setUrlSite(series[i].split(":")[1]);
                items.setUrl(series[i].split(":")[1]);
                if (item.season.size() > 0)
                    items.setSeason(String.valueOf(item.getSeason(0)));
                else items.setSeason("0");
                items.setEpisode(series[i].contains(" ") ? series[i].split(" ")[0].trim() :
                        series[i].split(":")[0].trim());
                items.setTranslator("AnimeVost");
            }
        } else {
            all = all.replace("'", "").trim();
            items.setTitle("series");
            items.setType("animevost");
            items.setToken("error");
            items.setId_trans("null");
            items.setId("site");
            items.setUrlSite(all.split(":")[1]);
            items.setUrl(all.split(":")[1]);
            if (item.season.size() > 0)
                items.setSeason(String.valueOf(item.getSeason(0)));
            else items.setSeason("0");
            items.setEpisode(all.split(":")[0].trim());
            items.setTranslator("AnimeVost");
        }
    }

    private void AnimeVost(String all) {

        if (all.contains("2 серия") || all.contains(",")){
            String l_ep = all.split(",")[all.split(",").length -1];
            l_ep = String.valueOf(l_ep.contains(" серия") ? l_ep.split(" серия")[0]
                    .replace("'", "").trim() : item.getSeries(0));
            items.setTitle("catalog site serial");
            items.setType("animevost");
            items.setToken("error");
            items.setId_trans("null");
            items.setId("site");
            items.setUrlSite(all);
            items.setUrl(all);
            if (item.season.size() > 0)
                items.setSeason(String.valueOf(item.getSeason(0)));
            else items.setSeason("0");
            items.setEpisode(l_ep.trim());
            items.setTranslator("AnimeVost");
            Log.d(TAG, "site animevost.org: add");
        } else {
            items.setTitle("catalog site");
            items.setType("animevost");
            items.setToken("error");
            items.setId_trans("null");
            items.setId("site");
            items.setUrl("error");
            if (all.contains(":")) {
                items.setUrlSite(all.split(":")[1].replace("'", ""));
                items.setUrl(all.split(":")[1].replace("'", ""));
            } else {
                items.setUrlSite(all);
                items.setUrl(all);
            }
            items.setSeason("error");
            items.setEpisode("error");
            items.setTranslator("AnimeVost");
            Log.d(TAG, "site animevost.org: add");
        }
    }
}
