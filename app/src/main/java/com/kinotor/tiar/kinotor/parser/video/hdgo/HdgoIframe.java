package com.kinotor.tiar.kinotor.parser.video.hdgo;

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
        Log.e(TAG, "HdgoIframe: parse"+items.url.toString());
        callback.OnCompleted(items);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        Log.e(TAG, "HdgoIframe: parse"+url);
        if (catalog)
            iframeHdgo(GetData(url, item.getUrl(0)));
        else iframeSeries(url_iframe);
        return null;
    }

    private void iframeSeries(ArrayList<String> url_iframe) {
        Log.e("qweewq", "iframeSeries: " + url_iframe);
        items.setTitle("site back");
        items.setType("hdgo on site");
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

        if (url_iframe.size() > 1){
            for (int i = 0; i < url_iframe.size(); i++){
                if (url_iframe.get(i).contains("error"))
                    break;
                else {
                    items.setTitle("series");
                    items.setType("hdgo on site");
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
                items.setType("hdgo on site");
                items.setToken(TOKEN);
                items.setId_trans("null");
                items.setId("site");
                items.setUrlSite(url_iframe.get(0));
                items.setUrl(url_iframe.get(0));
                if (item.season.size() > 0)
                    items.setSeason(String.valueOf(item.getSeason(0)));
                else items.setSeason("0");
                items.setEpisode(String.valueOf(item.getSeries(0)));
                items.setTranslator(translator);
            }
        }
    }

    private void iframeHdgo(Document doc) {
//        Log.e(TAG, "iframeHdgo: 9");
        if (doc != null) {
            if (!doc.html().contains("Видео недоступно") && doc.html().contains("<iframe")) {
                if (doc.html().contains("'<iframe")) {
                    if (item.getType(0).contains("movie")) {
//                        Log.e(TAG, "iframeHdgo: 93");
                        items.setTitle("catalog site");
                        items.setType("hdgo on site");
                        items.setToken(TOKEN);
                        items.setId_trans("null");
                        items.setId("site");
                        items.setUrl(url);
                        items.setUrlSite(url);
                        items.setSeason("error");
                        items.setEpisode("error");
                        items.setTranslator(item.getVoice(0).contains("error") ?
                                item.getTitle(0).trim() : item.getVoice(0).trim());
                    } else {
//                        Log.e(TAG, "iframeHdgo: 94 " + item.getUrl(0));
                        String episodes = item.getSeries(0) +"";
                        Document iframe = GetData(url, item.getUrl(0));
                        if (iframe!= null) {
                            if (iframe.body().html().contains("id=\"episode\"")) {
//                                Log.e(TAG, "iframeHdgo: 01 " +url);
                                String eps = iframe.selectFirst("#episode").html();
                                if (eps.contains("value")) {
                                    episodes = String.valueOf(eps.split("value").length - 1);
                                    for (int i = 1; i < eps.split("value").length; i++) {
                                        if (eps.split("value")[i].contains("=\"")) {
                                            items.setUrlSite(url + "&e=" + eps.split("value")[i]
                                                    .split("=\"")[1].split("\"")[0]);
                                        }
                                    }
                                    items.setUrl(url);
                                    items.setTitle("catalog site");
                                    items.setType("hdgo on site");
                                    items.setToken(TOKEN);
                                    items.setId_trans("null");
                                    items.setId("site");
                                    items.setTranslator(item.getVoice(0).contains("error") ?
                                            item.getTitle(0).trim() : item.getVoice(0).trim());
                                    if (item.season.size() > 0)
                                        items.setSeason(String.valueOf(item.getSeason(0)));
                                    else items.setSeason("0");
                                    items.setEpisode(episodes);
                                }
//                                else Log.e(TAG, "iframeHdgo: 0");
                            } else {
//                                Log.e(TAG, "iframeHdgo: 02 " +url);
                                items.setTitle("catalog site");
                                items.setType("hdgo on site");
                                items.setToken(TOKEN);
                                items.setId_trans("null");
                                items.setId("site");
                                items.setTranslator(item.getVoice(0).contains("error") ?
                                        item.getTitle(0).trim() : item.getVoice(0).trim());
                                if (item.season.size() > 0)
                                    items.setSeason(String.valueOf(item.getSeason(0)));
                                else items.setSeason("0");
                                items.setEpisode(episodes);
                                items.setUrl(url);
                                items.setUrlSite(url);
                            }
                        }
//                        else Log.e(TAG, "iframeHdgo: 2");
                    }
                } else if (doc.html().contains("<iframe")) {
//                    Log.e(TAG, "iframeHdgo: " + doc.select("iframe").first().html());
                    String src = doc.select("iframe").first().attr("src");
                    Document iframe = GetData(src, DetailActivity.url);
                    if (iframe != null) {
                        if (iframe.html().contains("season_list[0] = [\"")) {
                            String season_list = iframe.html().split("season_list\\[0] = \\[\"")[1].replace(" ", "");
                            if (season_list.contains(",];")) {
                                season_list = season_list.split(",];")[0]
                                        .replace("\"", "");
                                if (season_list.contains(",")) {
//                                    Log.e(TAG, "iframeHdgo: 91");
                                    String[] mp4 = season_list.split(",");
                                    items.setTitle("catalog site serial");
                                    items.setType("hdgo on site");
                                    items.setToken(TOKEN);
                                    items.setId_trans("null");
                                    items.setId("site");
                                    if (item.season.size() > 0)
                                        items.setSeason(String.valueOf(item.getSeason(0)));
                                    else items.setSeason("0");
                                    items.setEpisode(String.valueOf(mp4.length));
                                    items.setTranslator(item.getVoice(0).contains("error") ?
                                            item.getTitle(0) : item.getVoice(0));
                                    items.setUrl(url);
                                    for (String aMp4 : mp4) {
                                        items.setUrlSite("http://" + src.split("/")[2] + aMp4);
                                    }
//                                    Log.d(TAG, "site site" + ": add");
                                } else {
//                                    Log.e(TAG, "iframeHdgo: 92");
                                    items.setTitle("catalog site serial");
                                    items.setType("hdgo on site");
                                    items.setToken(TOKEN);
                                    items.setId_trans("null");
                                    items.setId("site");
                                    if (item.season.size() > 0)
                                        items.setSeason(String.valueOf(item.getSeason(0)));
                                    else items.setSeason("0");
                                    items.setEpisode("1");
                                    items.setTranslator(item.getVoice(0).contains("error") ?
                                            item.getTitle(0) : item.getVoice(0));
                                    items.setUrl(url);
                                    items.setUrlSite("http://" + src.split("/")[2] + season_list);
                                }
                            } else {
//                                Log.d(TAG, "iframeHdgo: " + iframe.html());
                            }
                        } else if (item.getType(0).contains("movie")) {
//                            Log.e(TAG, "iframeHdgo: 933");
                            items.setTitle("catalog site");
                            if (item.getUrl(0).split("/").length > 1)
                                items.setType("hdgo on site");
                            else
                                items.setType("hdgo on site");
                            items.setToken(TOKEN);
                            items.setId_trans("null");
                            items.setId("site");
                            items.setUrl(src.startsWith("http") ? src : " http:" + src);
                            items.setUrlSite(src.startsWith("http") ? src : " http:" + src);
                            items.setSeason("error");
                            items.setEpisode("error");
                            items.setTranslator(item.getVoice(0).contains("error") ?
                                    item.getTitle(0).trim() : item.getVoice(0).trim());
//                            Log.d(TAG, "site site" + ": add " + src);
                        } else {
//                            Log.e(TAG, "iframeHdgo: 94");
                            items.setTitle("catalog site");
                            items.setType("hdgo on site");
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
                        }
                        if (iframe.html().contains("The video file to be processed.")) {
                            Log.d(TAG, "The video file to be processed.");
                        }
                    }
                }
            }
        }
    }

    private String checkUrl(String url) {
        url = url.replaceAll("\"", "");
        if (!url.contains("http://") && !url.contains("https://")) url = url.contains("//")?"http:" + url:"http://" + url;
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
