package com.kinotor.tiar.kinotor.parser.trailer;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class ParserTrailer extends AsyncTask<Void, Void, Void> {
    private OnTaskVideoCallback callback;
    private ItemHtml item;
    private ItemVideo items;
    private boolean stop = false;

    public ParserTrailer(ItemHtml item, OnTaskVideoCallback callback) {
        this.item = item;
        this.callback = callback;

        this.items = new ItemVideo();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(items);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        getTrailer(getData(
                item.getSubTitle(0).contains("error") ? item.getTitle(0).trim() :
                        item.getSubTitle(0).trim()));
        return null;
    }

    private void getTrailer(Document data) {
        String title = "error", subtitle = "error", year = "error", url = "error";
        String cursubtitle = "error", curyear = "error";
        if (data != null) {
            Elements allEntries = data.select(".section-result-item");
            for (Element entry : allEntries) {
                if (entry.html().contains("name")) {
                    title = entry.select(".name").first().text().trim()
                            .replaceAll("\\u00a0", "")
                            .replace("'", "");
                    url = "http://www.kinomania.ru" +
                            entry.select(".name a").attr("href").trim();
                }
                if (entry.html().contains("name__eng"))
                    subtitle = entry.select(".name__eng").first().text().trim()
                            .replaceAll("\\u00a0", "")
                            .replace("'", "");
                if (entry.html().contains("place"))
                    year = entry.select(".place").first().text().trim();


                if (!item.getSubTitle(0).contains("error"))
                    cursubtitle = item.getSubTitle(0).trim().replace("'", "");
                else if (!subtitle.contains("error"))
                    cursubtitle = subtitle;
                else cursubtitle = "null";

                if (!item.getDate(0).contains("error"))
                    curyear = item.getDate(0).trim();
                else curyear = year;
                if (item.getUrl(0).contains("coldfilm")) curyear = year;
//                Log.d(TAG, "ParseTrailer 1: " +subtitle + "|" + year);
//                Log.d(TAG, "ParseTrailer 2: " +cursubtitle + "|" + curyear);

                if (curyear.contains(year) && (cursubtitle.contains(subtitle) || cursubtitle.contains(title))) {
                    items.setTitle("catalog video");
                    items.setType(subtitle.contains("error") ? title : subtitle
                            + " " + year + "\nkinomania");
                    items.setToken("error");
                    items.setId_trans(title);
                    items.setId("error");
                    items.setUrl(url);
                    items.setSeason("error");
                    items.setEpisode("error");
                    items.setTranslator(title + " (Трейлер)");
                    Log.d(TAG, "ParseTrailer add: " + title + " " + subtitle + " " + year);
                    break;
                }
            }

            if (items.translator.size() == 0 && !item.getSubTitle(0).contains("error") && !stop) {
                stop = true;
                getTrailer(getData(item.getTitle(0)));
            }
        }
    }

    private Document getData(String title){
        if (title.startsWith("The ")) title = title.replace("The ", "");
        String url = "http://www.kinomania.ru/search?q=" + title.replace(" ", "%20");
        try {
            Document htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(5000).ignoreContentType(true).get();
            Log.d(TAG, "Getdata: connected to " + url);
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "Getdata: connected false to " + url);
            e.printStackTrace();
            return null;
        }
    }

}
