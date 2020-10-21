package com.kinotor.tiar.kinotor.parser.video.animevost;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

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
    private ArrayList<String> videoList = new ArrayList<>();
    private ArrayList<String> videoListName = new ArrayList<>();

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
        Statics.videoList = videoList.toArray(new String[videoList.size()]);
        Statics.videoListName = videoListName.toArray(new String[videoListName.size()]);
        callback.OnCompleted(items);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (all.startsWith(Statics.ANIMEVOST_URL)){
            Document doc = GetData(all);
            if (doc != null){
                Log.e("test", "doInBackground: "+all );
                if (doc.html().contains("var data = {")) {
                    String iframe = doc.html().split("var data = \\{")[1].split("\\}")[0] + "}"
                            .replaceAll("\"", "");
                    if (iframe.endsWith(",}")) iframe = iframe.split(",\\}")[0];
                    else if (iframe.endsWith("}")) iframe = iframe.split("\\}")[0];
                    iframe = iframe.replace("\"", "");
                    if (iframe.isEmpty()) {
                        back();
                        Log.e("test", "doInBackgroundi: " + iframe);
                    } else iframeSeries(iframe);
                } else {
                    back();
                    Log.e("test", "doInBackground: "+doc.body().html() );
                }
            } else {
                back();
                Log.e("test", "doInBackground: "+doc.body().html() );
            }
        } else {
            if (catalog) AnimeVost(all);
            else iframeSeries(all);
        }
        return null;
    }

    private void back(){
        items.setTitle("season back");
        items.setType("animevost");
        items.setToken("error");
        items.setId("site");
        items.setId_trans("null");
        if (item.season.size() > 0)
            items.setSeason(String.valueOf(item.getSeason(0)));
        else items.setSeason("0");
        items.setUrlSite("error");
        items.setUrl("error");
        items.setUrlTrailer("error");
        items.setEpisode("error");
        items.setTranslator("AnimeVost");
    }

    private void iframeSeries(String all) {
        back();

//        Log.e("test", "iframeSeries: "+all );

        if (all.contains(",")){
            for (String epis: all.split(",")) {
                if (epis.contains(":")) {
                    String ur = epis.split(":")[1].trim();
//                    Log.e("test", "http://play.aniland.org/" + ur + "?player=7" );
                    Document doc = GetData("http://play.aniland.org/" + ur + "?player=7");
                    if (doc != null) {
                        if (doc.body().html().contains("knpki")) {
                            Elements allLink = doc.select("a");
                            String url = "";
                            for (Element link : allLink) {
                                if (link.attr("href").contains(".mp4")) {
                                    if (link.text().contains("720")) {
                                        url += "[720]"+link.attr("href") +",";
                                    } else {
                                        url += "[480]"+link.attr("href") +",";
                                    }
                                }
                            }
                            String e = epis.contains(" ") ? epis.split(" ")[0].trim() :
                                    epis.split(":")[0].trim();
                            String s = item.season.size() > 0 ? String.valueOf(item.getSeason(0)) : "0";
                            items.setTitle("series");
                            items.setType("animevost");
                            items.setToken("error");
                            items.setId_trans("null");
                            items.setId("site");
                            items.setUrlTrailer("error");
                            items.setUrlSite("error");
                            items.setUrl(url);
                            items.setSeason(s);
                            items.setEpisode(e);
                            items.setTranslator("AnimeVost");

                            videoList.add(url);
                            videoListName.add("s"+s+"e"+e);
                        }
                    }
                }
            }
//            String[] series = all.split(",");
//            for (int i = 0; i < series.length; i++){
//                series[i] = series[i].replace("'", "").trim();
//                items.setTitle("series");
//                items.setType("animevost");
//                items.setToken("error");
//                items.setId_trans("null");
//                items.setId("site");
//                items.setUrlTrailer("error");
//                items.setUrlSite(series[i].split(":")[1]);
//                items.setUrl(series[i].split(":")[1]);
//                if (item.season.size() > 0)
//                    items.setSeason(String.valueOf(item.getSeason(0)));
//                else items.setSeason("0");
//                items.setEpisode(series[i].contains(" ") ? series[i].split(" ")[0].trim() :
//                        series[i].split(":")[0].trim());
//                items.setTranslator("AnimeVost");
//            }
        } else {
            all = all.replace("'", "").trim();
            items.setTitle("series");
            items.setType("animevost");
            items.setToken("error");
            items.setId_trans("null");
            items.setId("site");
            items.setUrlTrailer("error");
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
            items.setUrlTrailer("error");
            items.setUrl(all);
            if (item.season.size() > 0)
                items.setSeason(String.valueOf(item.getSeason(0)));
            else items.setSeason("0");
            items.setEpisode(l_ep.trim());
            items.setTranslator("AnimeVost");
//            Log.d(TAG, "site animevost.org: add");
        } else {
            items.setTitle("catalog site");
            items.setType("animevost");
            items.setToken("error");
            items.setId_trans("null");
            items.setId("site");
            items.setUrl("error");
            items.setUrlTrailer("error");
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
//            Log.d(TAG, "site animevost.org: add");
        }
    }

    private Document GetData(String url){
        try {
            Document htmlDoc = Jsoup.connect(url)
                    .referrer("http://animevost.org/")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .ignoreContentType(true).get();
            Log.d(TAG, "Getdata: connected to " + url);
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "Getdata: connected false to " + url);
            e.printStackTrace();
            return null;
        }
    }
}
