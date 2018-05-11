package com.kinotor.tiar.kinotor.parser.hdgo;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.ui.DetailActivity;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class HdgoIframe extends AsyncTask<Void, Void, Void> {
    private String url, translator;
    private ArrayList<String> url_iframe;
    private final String TOKEN = "2c4lbb21dje7yo7aysht52fj&k";
    private boolean catalog;
    private ItemVideo items;
    private OnTaskVideoCallback callback;
    private ItemHtml item;

    public HdgoIframe(ItemHtml item, boolean catalog, OnTaskVideoCallback callback) {
        this.item = item;
        this.catalog = catalog;
        this.callback = callback;

        this.url = item.getIframe(0);
        this.items = new ItemVideo();
    }

    public HdgoIframe(ArrayList<String> url_iframe, String translator, ItemHtml item,
                      OnTaskVideoCallback callback) {
        this.url_iframe = url_iframe;
        this.translator = translator;
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
        if (catalog)
            iframeHdgo(GetData(url, DetailActivity.url));
        else iframeSeries(url_iframe);
        return null;
    }

    private void iframeSeries(ArrayList<String> url_iframe) {
        Log.d(TAG, "iframeSeries: " + DetailActivity.url);
        items.setTitle("site back");
        items.setType("hdgo on " + DetailActivity.url.split("/")[2]);
        items.setToken(TOKEN);
        items.setId("site");
        items.setId_trans("null");
        if (item.season.size() > 0)
            items.setSeason(String.valueOf(item.getSeason(0)));
        else items.setSeason("0");
        items.setUrlSite("error");
        items.setUrl("error");
        items.setEpisode("error");
        items.setTranslator(translator);

        if (url_iframe.size() > 1 && !DetailActivity.url.contains("coldfilm")){
            for (int i = 0; i < url_iframe.size(); i++){
                if (url_iframe.get(i).contains("error"))
                    break;
                else {
                    items.setTitle("series");
                    items.setType("hdgo on " + DetailActivity.url.split("/")[2]);
                    items.setToken(TOKEN);
                    items.setId_trans("null");
                    items.setId("site");
                    items.setUrlSite(url_iframe.get(i));
                    items.setUrl(url_iframe.get(i));
                    if (item.season.size() > 0)
                        items.setSeason(String.valueOf(item.getSeason(0)));
                    else items.setSeason("0");
                    items.setEpisode(String.valueOf(i + 1));
                    items.setTranslator(translator);
                }
            }
        } else {
            if (!url_iframe.get(0).contains("error")) {
                items.setTitle("series");
                items.setType("hdgo on " + DetailActivity.url.split("/")[2]);
                items.setToken(TOKEN);
                items.setId_trans("null");
                items.setId("site");
                items.setUrlSite(url_iframe.get(0));
                items.setUrl(url_iframe.get(0));
                if (item.season.size() > 0)
                    items.setSeason(String.valueOf(item.getSeason(0)));
                else items.setSeason("0");
                items.setEpisode(String.valueOf(DetailActivity.url.contains("coldfilm") ? item.getSeries(0) : 1));
                items.setTranslator(translator);
            }
        }
    }

    private void iframeHdgo(Document doc) {
        Log.d(TAG, "iframeHdgo: " + item.getUrl(0));
        if (doc != null)
            if (!doc.html().contains("Видео недоступно") && doc.html().contains("<iframe")){
                String src = doc.select("iframe").first().attr("src");
                Document iframe = GetData(src, DetailActivity.url);
                if (iframe != null) {
                    Log.d(TAG, "iframeHdgo: " + item.getType(0));
                    if (DetailActivity.url.contains("coldfilm")){
                        items.setTitle("catalog site");
                        items.setType("hdgo on coldfilm.ru");
                        items.setToken(TOKEN);
                        items.setId_trans("null");
                        items.setId("site");
                        items.setUrl(url);
                        items.setUrlSite(url);
                        items.setTranslator("Coldfilm");
                        if (item.season.size() > 0)
                            items.setSeason(String.valueOf(item.getSeason(0)));
                        else items.setSeason("0");
                        items.setEpisode(String.valueOf(item.getSeries(0)));
                        Log.d(TAG, "site coldfilm.ru: add");
                    } else if (iframe.html().contains("season_list[0] = [\"")) {
                        String season_list = iframe.html().split("season_list\\[0] = \\[\"")[1];
                        if (season_list.contains(",];")){
                            season_list = season_list.split(",];")[0]
                                    .replace("\"", "");
                            if (season_list.contains(",")){
                                String[] mp4 = season_list.split(",");
                                items.setTitle("catalog site serial");
                                items.setType("hdgo on " + DetailActivity.url.split("/")[2]);
                                items.setToken(TOKEN);
                                items.setId_trans("null");
                                items.setId("site");
                                if (item.season.size() > 0)
                                    items.setSeason(String.valueOf(item.getSeason(0)));
                                else items.setSeason("0");
                                items.setEpisode(String.valueOf(mp4.length));
                                items.setTranslator(item.getVoice(0).contains("error") ?
                                        item.getTitle(0) : item.getVoice(0));
                                for (String aMp4 : mp4) {
                                    items.setUrl("http://" + src.split("/")[2] + aMp4);
                                    items.setUrlSite("http://" + src.split("/")[2] + aMp4);
                                }
                                Log.d(TAG, "site " + DetailActivity.url.split("/")[2] + ": add");
                            } else {
                                items.setTitle("catalog site serial");
                                items.setType("hdgo on " + DetailActivity.url.split("/")[2]);
                                items.setToken(TOKEN);
                                items.setId_trans("null");
                                items.setId("site");
                                if (item.season.size() > 0)
                                    items.setSeason(String.valueOf(item.getSeason(0)));
                                else items.setSeason("0");
                                items.setEpisode("1");
                                items.setTranslator(item.getVoice(0).contains("error") ?
                                        item.getTitle(0) : item.getVoice(0));
                                items.setUrl("http://" + src.split("/")[2] + season_list);
                                items.setUrlSite("http://" + src.split("/")[2] + season_list);
                            }
                        }
                    } else if (item.getType(0).contains("movie")) {
                        items.setTitle("catalog site");
                        items.setType("hdgo on " + DetailActivity.url.split("/")[2]);
                        items.setToken(TOKEN);
                        items.setId_trans("null");
                        items.setId("site");
                        items.setUrl(src.startsWith("http") ? src : " http:" + src);
                        items.setUrlSite(src.startsWith("http") ? src : " http:" + src);
                        items.setSeason("error");
                        items.setEpisode("error");
                        items.setTranslator(item.getVoice(0).contains("error") ?
                                item.getTitle(0) : item.getVoice(0));
                        Log.d(TAG, "site " + DetailActivity.url.split("/")[2] + ": add " + src);
                    }
                    if (iframe.html().contains("The video file to be processed.")) {
                        Log.d(TAG, "The video file to be processed.");
                    }
                }
            }
    }

    private String checkUrl(String url) {
        url = url.replaceAll("\"", "");
        if (!url.contains("http://")) url = url.contains("//")?"http:" + url:"http://" + url;
        return url;
    }

    private Document GetData(String url, String referrer){
        try {
            Document htmlDoc = Jsoup.connect(checkUrl(url))
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .referrer(referrer)
                    .timeout(50000).ignoreContentType(true).get();
            Log.d(TAG, "GetdataHdgoIframe: connected to " + checkUrl(url));
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "GetdataHdgoIframe: connected false to " + checkUrl(url));
            e.printStackTrace();
            return null;
        }
    }
}
