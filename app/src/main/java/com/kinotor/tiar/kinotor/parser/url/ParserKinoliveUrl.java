package com.kinotor.tiar.kinotor.parser.url;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by Tiar on 02.2018.
 */

public class ParserKinoliveUrl extends AsyncTask<Void, Void, Void> {
    String title_m = "error", title_en = "error", url = "error", season = "error", episode = "error",
            translator = "Неизвестный", year = "", q = "", type_m = "movie";
    private ItemHtml itempath;
    private OnTaskVideoCallback callback;
    private ItemVideo items;

    public ParserKinoliveUrl(ItemHtml item, OnTaskVideoCallback callback){
        this.itempath = item;
        this.callback = callback;
        this.items = new ItemVideo();
    }

    @Override
    protected void onPostExecute(Void aVoid) {callback.OnCompleted(items); }

    @Override
    protected Void doInBackground(Void... voids) {
        ParseHtml(Getdata());
        return null;
    }

    private void ParseHtml(Document data) {
        if (data != null) {
            if (data.html().contains("id=\"dle-content\"")) {
                Element entry = data.selectFirst("#dle-content");
                String dataHtml = entry.html();
                if (dataHtml.contains("<h1")) {
                    title_m = data.select("h1").text().replace("HD","").trim();
                }
                if (dataHtml.contains("<a")) {
                    year = data.selectFirst("a").text().trim();
                }
                if (dataHtml.contains("class=\"tezt\"")) {
                    Element all = data.selectFirst(".tezt");
                    String allHtml = all.html();
                    if (allHtml.contains("Оригинальное название: ")) {
                        title_en = allHtml.split("Оригинальное название: ")[1].trim();
                        if (title_en.contains("<br")) {
                            title_en = title_en.split("<br")[0].trim();
                        }
                    }
                    if (allHtml.contains("Перевод: ")) {
                        translator = allHtml.split("Перевод: ")[1].trim();
                        if (translator.contains("<br")) {
                            translator = translator.split("<br")[0].trim();
                        }
                    }
                    
                }
                parseSeason(dataHtml);

                if (year.contains("-"))
                    year = year.split("-")[0];
                if (title_m.contains("("))
                    title_m = title_m.split("\\(")[0].trim();

                if (!year.contains("error"))
                    title_m += " (" + year + ")";

                if (dataHtml.contains("<iframe")) {
                    String ifrm = data.selectFirst("iframe").attr("src").trim();
                    if (ifrm.contains("?file="))
                        ifrm = ifrm.split("\\?file=")[1].trim();
                    else if (ifrm.startsWith("//"))
                        ifrm = "http" + ifrm.trim();
                    if (ifrm.startsWith("/player"))
                        ifrm = Statics.KINOLIVE_URL + ifrm.trim();
                    if (translator.contains("<!--colorend-->"))
                        translator = translator.split("<!--colorend-->")[0].trim();
                    if (type_m.equals("movie")) items.setTitle("catalog video");
                    else items.setTitle("catalog serial");
                    if (episode.length() > 4 || episode.contains("error")) episode = "X";
                    items.setType(title_m.replace("()","") + " " + " " + q + "\nkinolive");
                    items.setToken("");
                    items.setId_trans("");
                    items.setId("error");
                    items.setUrl(ifrm);
                    items.setUrlTrailer("error");
                    items.setSeason(season);
                    items.setEpisode(episode.trim());
                    items.setTranslator(translator.replace("&amp;", "")
                            .replace("  "," ").trim());
                }
            }
        } else
            Log.d("Kinolive", "ParseHtml: data error");
    }

    private void parseSeason(String entry) {
        if (title_m.contains(" сезон")) {
            type_m = "serial";
            season = title_m.split(" сезон")[0].trim().split(" ")[
                    title_m.split(" сезон")[0].trim().split(" ").length-1].trim();
            season = season.replace("(","").trim();
            episode = "0";
            if (title_m.contains(" серия") || title_m.contains(" серии") || title_m.contains(" серий")) {
                episode = title_m.split(" сери")[0].trim().split(" ")[
                        title_m.split(" сери")[0].trim().split(" ").length-1].trim();
            } else if (entry.contains("class=\"tezt\"")) {
                String tezt = entry.split("class=\"tezt\"")[1]
                        .replace("<!--/colorstart-->"," ")
                        .replace("<!--dle_image_end-->"," ").trim();
                if (tezt.contains(" серия") || tezt.contains(" серии") || tezt.contains(" серий")) {
                    Log.e("kl2", "parseMain: 8");
                    episode = tezt.split(" сери")[0].trim().split(" ")[
                            tezt.split(" сери")[0].trim().split(" ").length - 1].trim();
                }
            }
            title_m = title_m.replace("1-"+season,"")
                    .replace(season,"")
                    .replace("сезон","")
                    .replace("( )","")
                    .replace("()","").trim();
            if (season.contains("-"))
                season = season.split("-")[1];
        } else if (title_m.contains(" серия") || title_m.contains(" серии") || title_m.contains(" серий")) {
            season = "1";
            type_m = "serial";
            episode = title_m.split(" сери")[0].trim().split(" ")[
                    title_m.split(" сери")[0].trim().split(" ").length-1].trim();
        } else if (entry.contains("class=\"tezt\"")) {
            String tezt = entry.split("class=\"tezt\"")[1]
                    .replace("<!--/colorstart-->"," ")
                    .replace("<!--dle_image_end-->"," ").trim();
            if (tezt.contains(" серия") || tezt.contains(" серии") || tezt.contains(" серий")) {
                season = "1";
                type_m = "serial";
                episode = tezt.split(" сери")[0].trim().split(" ")[
                        tezt.split(" сери")[0].trim().split(" ").length - 1].trim();
            }
        }

        episode = episode.replace("(","").replace("<!--/colorstart-->","");
        if (episode.contains("-"))
            episode = episode.split("-")[1].trim();
    }

    private Document Getdata() {
        try {
            String url = itempath.getUrl(0);
            Document htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .ignoreContentType(true).get();
            return htmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
