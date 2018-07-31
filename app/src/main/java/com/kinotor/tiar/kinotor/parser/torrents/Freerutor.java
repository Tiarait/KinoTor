package com.kinotor.tiar.kinotor.parser.torrents;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemTorrent;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskTorrentCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class Freerutor extends AsyncTask<Void, Void, Void> {
    private ItemHtml item;
    private ItemTorrent torrent;
    private OnTaskTorrentCallback callback;

    public Freerutor(ItemHtml item, OnTaskTorrentCallback callback) {
        this.item = item;
        this.callback = callback;

        torrent = new ItemTorrent();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(torrent);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        FreeRutorMe(getData(Statics.FREERUTOR_URL + "/?do=search&subaction=search&story=" + item.getTitle(0)));
        return null;
    }

    private void FreeRutorMe(Document data) {
        if (data != null) {
            Elements allEntries = data.select(".fr_viewn_in.fr_viewn_new");
            for (Element entry : allEntries) {
                String category = "error";
                if (entry.html().contains("lastcat fr_bor"))
                    category = entry.select(".lastcat.fr_bor a").first().attr("href");
                if (category.contains("filmy") || category.contains("mult") || category.contains("serialy") ||
                        category.contains("tv")){
                    if (entry.html().contains("titlelast fr_bor fr_borleft")){
                        String name = entry.select(".titlelast a").attr("title");
                        String sid = entry.select(".frsl_s").text().trim();
                        Log.d(TAG, "FreeRutorMe: "+ item.getTitle(0) + "|" + name + " " + sid);
                        if (name.toLowerCase().contains(item.getTitle(0).toLowerCase())
                                && !sid.equals("0")) {
                            torrent.setTorTitle(entry.select(".titlelast a").attr("title"));
                            torrent.setTorSize(entry.select(".frs.fr_bor.fr_borleft").text());
                            torrent.setTorSid(sid);
                            torrent.setTorLich(entry.select(".frsl_p").text());
                            torrent.setTorContent("freerutor.me");
                            torrent.setTorUrl(entry.select(".titlelast a").attr("href"));
                            torrent.setTorMagnet(entry.select(".titlelast a").attr("href"));
                        }
                    }
                }
            }
        }
    }

    private Document getData(String url) {
        try {
            Document htmlDoc = Jsoup.connect(url.replace(" ", "%20"))
                    .validateTLSCertificates(false)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(10000).ignoreContentType(true).get();
            Log.d(TAG, "Getdata: FreeRutorMe " + url);
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "Getdata: FreeRutorMe error " + url);
            e.printStackTrace();
            return null;
        }
    }
}
