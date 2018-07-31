package com.kinotor.tiar.kinotor.parser.video.hdgo;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;
import com.kinotor.tiar.kinotor.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class ParserHdgo extends AsyncTask<Void, Void, Void> {
    private String search_title, type;
    private final String TOKEN = "2c4lbb21dje7yo7aysht52fj&k";
    private ItemHtml itempath;
    private OnTaskVideoCallback callback;
    private ItemVideo items;

    public ParserHdgo(ItemHtml item, OnTaskVideoCallback callback) {
        this.itempath = item;
        this.callback = callback;
        this.items = new ItemVideo();

        if (itempath.getTitle(0).contains("("))
            search_title = new Utils().replaceTitle(itempath.getTitle(0).split("\\(")[0]);
        else search_title = new Utils().replaceTitle(itempath.getTitle(0));
        search_title = search_title.trim().replace("\u00a0", " ");
        type = itempath.getType(0);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(items);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        AllList(GetData(search_title));
        return null;
    }

    private void AllList(Document doc) {
        if (doc != null) {
            if (items == null) items = new ItemVideo();
            String[] array = doc.body().text().split("\\},");
            for (int i = 0; i < array.length; i ++){
                String title_m = "error", url = "error", season = "error", episode = "error",
                        translator = "error", id = "error", id_trans = "error", type_m = "error";
                String q = "";
                if (array[i].contains("title") && !array[i].contains("title\":null"))
                    title_m = array[i].split("title\":\"")[1].split("\"")[0].trim();
                if (array[i].contains("quality\":") && !array[i].contains("quality\":null"))
                    q = " (" + array[i].split("quality\":\"")[1].split("\"")[0].trim() + ")";

                String sname = search_title.toLowerCase().replace("ё", "е").replace(".", "-").trim();
                String stitle = title_m.toLowerCase().replace("ё", "е").replace(".", "-").trim();
                if (array[i].contains("id_hdgo") && (sname.equals(stitle) || stitle.equals(sname))) {
                    id = array[i].split("id_hdgo\":")[1].split(",")[0];
                    if (array[i].contains("seasons_count"))
                        season = array[i].split("seasons_count\":")[1].split(",")[0];
                    if (array[i].contains("episodes_count"))
                        episode = array[i].split("episodes_count\":")[1].split(",")[0];
                    if (array[i].contains("translator"))
                        translator = array[i].split("translator\":\"")[1].split("\"")[0];
                    if (array[i].contains("type"))
                        type_m = array[i].split("type\":\"")[1].split("\"")[0];
                    if (array[i].contains("iframe_url"))
                        url = array[i].split("iframe_url\":\"")[1].split("\"")[0];
                    if (this.type.contains(type_m)) {
                        if (season.equals("error")) items.setTitle("catalog video");
                        else items.setTitle("catalog serial");
                        items.setType(title_m + q +"\nhdgo");
                        items.setToken(TOKEN);
                        items.setId_trans(id_trans);
                        items.setId(id);
                        items.setUrl(url);
                        items.setSeason(season);
                        items.setEpisode(episode);
                        items.setTranslator(translator);
                        Log.d(TAG, "ParseHtmlHDGO: " + translator + " add");
                    }
                }
            }
        }
    }

    private Document GetData(String s){
        String name = s.trim().replace(" ", "%20").replace("\u00a0", "%20");
        final String url = "http://hdgo.cc/api/video.json?token="+ TOKEN +"&title=" + name;
        try {
            Document htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(5000).ignoreContentType(true).referrer("hdgo.cc").get();
            Log.d(TAG, "GetdataHDGO: get connected to " + url);
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "GetdataHDGO: connected false to " + url);
            return null;
        }
    }
}