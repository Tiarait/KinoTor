package com.kinotor.tiar.kinotor.parser.video.animedia;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;
import com.kinotor.tiar.kinotor.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Tiar on 02.2018.
 */

public class ParserAnimedia extends AsyncTask<Void, Void, Void> {
    private String search_title, type;
    private ItemHtml itempath;
    private OnTaskVideoCallback callback;
    private ItemVideo items;

    public ParserAnimedia(ItemHtml item, OnTaskVideoCallback callback){
        this.itempath = item;
        this.callback = callback;
        this.items = new ItemVideo();

        if (!item.getSubTitle(0).toLowerCase().contains("error")) {
            search_title = item.getSubTitle(0);
        } else {
            search_title = item.getTitle(0).trim();
            if (search_title.contains("("))
                search_title = search_title.split("\\(")[0].trim();
            if (search_title.contains("["))
                search_title = search_title.split("\\[")[0].trim();
        }
        search_title = search_title.trim().replace("\u00a0", " ");
        type = itempath.getType(0);
    }

    @Override
    protected void onPostExecute(Void aVoid) {callback.OnCompleted(items); }

    @Override
    protected Void doInBackground(Void... voids) {
        ParseHtml(Getdata(Statics.ANIMEDIA_URL + "/ajax/anime_list"));
        return null;
    }

    private void ParseHtml(Document data) {
        if (data != null) {
            String json = Utils.unicodeToString(data.text()).toLowerCase();
//            Log.e("test", "ParseHtml099: "+json );

            if (!search_title.trim().equals("error") && json.contains(search_title.toLowerCase())) {
                String title_m, title_en = "error", url, season = "error", episode = "error",
                        translator = "AniMedia", type_m;
//                String cur = json.split()[1];
                String cur = json.substring(json.indexOf(search_title.toLowerCase())+1).trim();
                title_m = search_title;
//                Log.e("test", "ParseHtml00: "+cur );
//                Log.e("test", "ParseHtml11: "+json.split(search_title.toLowerCase())[0] );

                if (cur.contains("\"url\":\"")) {
                    url = cur.split("\"url\":\"")[1].split("\"")[0]
                            .replace("\\","").replace("%/","/").trim();
                    Document doc = Getdata(url);
                    if (doc!=null){
                        if (doc.html().contains("data-entry_id=\"")){
                            Document ep = Getdata(Statics.ANIMEDIA_URL + "/ajax/episodes/" +
                                    doc.html().split("data-entry_id=\"")[1].split("\"")[0] +
                                    "/1/undefined");
                            if (ep!=null){
                                int e = ep.select("a").size();
                                if (!title_m.contains("error")) {
                                    items.setTitle("catalog serial");
                                    items.setType(title_m + "\nanimedia");
                                    items.setToken("");
                                    items.setId_trans("");
                                    items.setId("error");
                                    items.setUrl(ep.body().html());
                                    items.setUrlTrailer("error");
                                    items.setSeason("1");
                                    items.setEpisode(String.valueOf(e-2));
                                    items.setTranslator(translator);
                                }
                            }else Log.d("Animedia", "ParseHtml15: data search error");
                        }else Log.d("Animedia", "ParseHtml14: data search error");
                    }else Log.d("Animedia", "ParseHtml13: data search error");
                } else Log.d("Animedia", "ParseHtml1: data search error");
            } else Log.d("Animedia", "ParseHtml0: data search error");
        } else
            Log.d("Animedia", "ParseHtml2: data error");
    }

    private Document Getdata(String s) {
        try {
            return Jsoup.connect(s)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .header("X-Requested-With","XMLHttpRequest")
                    .referrer(Statics.ANIMEDIA_URL)
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
