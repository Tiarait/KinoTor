package com.kinotor.tiar.kinotor.parser.video.hdgo;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
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
        Log.e("test", "hdgo kp: "+Statics.KP_ID);
        if (Statics.KP_ID.contains("error"))
            AllList(GetDataTitle(search_title));
        else AllList(GetDataId(Statics.KP_ID));
        return null;
    }

    private void AllList(Document doc) {
        if (doc != null) {
            if (items == null) items = new ItemVideo();
            String[] array = doc.body().text().split("\\},");
            for (String anArray : array) {
                String title_m = "error", url = "error", season = "error", episode = "error",
                        translator = "error", id, id_trans = "error", type_m = "error",
                        trailer = "error";
                String q = "";
                if (anArray.contains("title") && !anArray.contains("title\":null"))
                    title_m = anArray.split("title\":\"")[1].split("\"")[0].trim();
                if (anArray.contains("quality\":") && !anArray.contains("quality\":null"))
                    q = " (" + anArray.split("quality\":\"")[1].split("\"")[0].trim() + ")";

                String sname = search_title.toLowerCase().replace("ё", "е").replace(".", "-").trim();
                if (sname.contains(":")) sname = sname.split(":")[0].trim();
                String stitle = title_m.toLowerCase().replace("ё", "е").replace(".", "-").trim();
                boolean tit = true;
                boolean prb = true;

                if (Statics.KP_ID.contains("error") || Statics.KP_ID.isEmpty() || Statics.KP_ID.equals("null")) {
                    tit = sname.toLowerCase().contains(stitle.toLowerCase()) ||
                            stitle.toLowerCase().contains(sname.toLowerCase());
                    if (sname.contains(" ") && stitle.contains(" "))
                        prb = true;
                    else if(!sname.contains(" ") && !stitle.contains(" "))
                        prb = true;
                    else prb = false;
                }
//                Log.e("GetdataHDGO", "prb: "+prb);
//                Log.e("GetdataHDGO", "tit: "+tit);
//                Log.e("GetdataHDGO", "Statics.KP_ID: "+Statics.KP_ID);

                if (anArray.contains("id_hdgo") && tit && prb) {
                    id = anArray.split("id_hdgo\":")[1].split(",")[0];
                    if (anArray.contains("seasons_count"))
                        season = anArray.split("seasons_count\":")[1].split(",")[0];
                    if (anArray.contains("episodes_count"))
                        episode = anArray.split("episodes_count\":")[1].split(",")[0];
                    if (anArray.contains("translator"))
                        translator = anArray.split("translator\":\"")[1].split("\"")[0];
                    if (anArray.contains("type"))
                        type_m = anArray.split("type\":\"")[1].split("\"")[0];
                    if (anArray.contains("iframe_url"))
                        url = anArray.split("iframe_url\":\"")[1].split("\"")[0];
                    if (anArray.contains("trailer\":") && !anArray.contains("trailer\":\"null"))
                        trailer = anArray.split("trailer\":\"")[1].split("\"")[0];
                    boolean types = true;
                    if (Statics.KP_ID.contains("error")) {
                        if (itempath.getKpId().contains("error") && !this.type.contains("error"))
                            types = this.type.contains(type_m);
                    }
                    String tr = trailer.contains("error") ? "" : " [+trailer]";

                    if (types) {
                        if (season.equals("error")) items.setTitle("catalog video");
                        else items.setTitle("catalog serial");
                        items.setType(title_m + q + "\nhdgo" + tr);
                        items.setToken(TOKEN);
                        items.setId_trans(id_trans);
                        items.setId(id);
                        items.setUrl(url);
//                        items.setUrlSite("error");
                        items.setUrlTrailer(trailer);
                        items.setSeason(season);
                        items.setEpisode(episode);
                        items.setTranslator(translator);
                        Log.d(TAG, "ParseHtmlHDGO: " + translator + " add");
                    }
                }
            }
        }
    }

    private Document GetDataTitle(String s){
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
    private Document GetDataId(String s){
        final String url = "http://hdgo.cc/api/video.json?token="+ TOKEN +"&kinopoisk_id=" + s.trim();
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