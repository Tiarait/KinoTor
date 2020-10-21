package com.kinotor.tiar.kinotor.parser.video.farsihd;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class FarsihdIframe extends AsyncTask<Void, Void, Void> {
    private ArrayList<String> url_iframe;
    private final String TOKEN = "2c4lbb21dje7yo7aysht52fj&k";
    private boolean catalog = false;
    private ItemVideo items;
    private OnTaskVideoCallback callback;
    private ItemHtml item;

    public FarsihdIframe(ItemHtml item, OnTaskVideoCallback callback) {
        this.item = item;
        this.catalog = true;
        this.callback = callback;
        this.items = new ItemVideo();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(items);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (catalog)
            iframeFarsihd(GetData(item.getIframe(0)));
        return null;
    }

    private void iframeFarsihd(Document doc) {
        if (doc != null) {
            String dh = doc.body().html().replace("&quot;","\"").trim();
            String hls = "error";
            if (dh.contains("hls\":\"")) {
                hls = dh.split("hls\":\"")[1].split("\"")[0]
                        .replace("\\", "").trim();
                if (!hls.contains("http:"))
                    hls = "http:" + hls;
            }
            String site = item.getUrl(0);
            if (site.contains("//"))
                site = site.split("//")[1].trim();
            if (site.contains("/"))
                site = site.split("/")[0].trim();

            if (dh.contains("type\":\"movie")) {
                Log.e("FarsihdIframe", "doInBackground: "+hls);

                items.setTitle("catalog site");
                items.setType("farsihd on " + site);
                items.setToken("error");
                items.setId_trans("null");
                items.setId("site");
                items.setUrl(hls);
                items.setUrlSite("error");
                items.setSeason("error");
                items.setEpisode("error");
                items.setTranslator(item.getVoice(0).contains("error") ?
                        item.getTitle(0).trim() : item.getVoice(0).trim());
            } else if (dh.contains("type\":\"serial")) {
                Log.e("FarsihdIframe", "doInBackground: "+hls);

                String season = String.valueOf(item.getSeason(0));
                if (dh.contains("seasons\"><option value=\""))
                    season = dh.split("seasons\"><option value=\"")[1].split("\"")[0].trim();
                String episode = String.valueOf(item.getSeries(0));
                if (dh.contains("class=\"dropdown\" name=\"episodes\">"))
                    episode = dh.split("class=\"dropdown\" name=\"episodes\">")[1]
                            .split("</select>")[0].trim();
                if (episode.contains("<option"))
                    episode = String.valueOf(episode.split("<option").length-1);

                String idOctopus = "83e50473503411ac8d29b78a3506240f378910442d33104c77e5c2f1528cc91b";
                if (dh.contains("translation\"><option value=\""))
                    idOctopus = dh.split("translation\"><option value=\"")[1].split("\"")[0].trim();

                items.setTitle("catalog serial");
                items.setType("farsihd on " + site);
                items.setToken("error");
                items.setId_trans(idOctopus);
                items.setId("error");
                items.setUrl(item.getIframe(0));
                items.setUrlTrailer("error");
                items.setSeason(season);
                items.setEpisode(episode);
                items.setTranslator(item.getVoice(0).contains("error") ?
                        item.getTitle(0).trim() : item.getVoice(0).trim());
            } else Log.e("FarsihdIframe", "error "+doc.body().html());
        } else Log.e("FarsihdIframe", "error2 "+item.getIframe(0));
    }

    private Document GetData(String url){
        try {
            return Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(50000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
