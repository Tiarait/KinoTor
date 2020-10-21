package com.kinotor.tiar.kinotor.parser.video.kinopub;

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

/**
 * Created by Tiar on 02.2018.
 */

public class ParserKinopubSearch extends AsyncTask<Void, Void, Void> {
    private String search_title = "";
    private ItemHtml itempath;
    private OnTaskVideoCallback callback;
    private ItemVideo items;

    public ParserKinopubSearch(ItemHtml item, OnTaskVideoCallback callback){
        this.itempath = item;
        this.callback = callback;
        this.items = new ItemVideo();

        if (item.getSubTitle(0).toLowerCase().contains("error"))
            search_title = item.getTitle(0).trim();
        else search_title = item.getSubTitle(0).trim();

        if (search_title.contains("("))
            search_title = search_title.split("\\(")[0].trim();
        if (search_title.contains("["))
            search_title = search_title.split("\\[")[0].trim();
        search_title = search_title.trim().replace("\u00a0", " ");
    }

    @Override
    protected void onPostExecute(Void aVoid) {callback.OnCompleted(items); }

    @Override
    protected Void doInBackground(Void... voids) {
        if (search_title != null)
            ParseHtml(getDataSearch(search_title));
        return null;
    }

    private void ParseHtml(Document data) {
        if (data != null) {
            if (data.html().contains("\"id\":")) {
                String[] allEntries = data.text().split("\"id\":");
                for (String entry : allEntries) {

                    entry = entry.replace("\\\"", "'");
                    String title = "error", url = "error";
                    if (entry.contains(","))
                        url = Statics.KINOPUB_URL + "/item/view/" + entry.split(",")[0].trim();

                    if (entry.contains("value\":\"")) {
                        title = entry.split("value\":\"")[1].trim();
                        if (title.contains("\"}"))
                            title = title.split("\"\\}")[0].trim();
                    }
                    Log.d("Kinopub", "title: " + title);
                    boolean tit =
                            (title.toLowerCase().trim().contains(itempath.getSubTitle(0).toLowerCase().trim())
                                    && title.toLowerCase().trim().contains(itempath.getTitle(0).toLowerCase().trim())
                                    && !itempath.getSubTitle(0).contains("error")) ||
                                    title.toLowerCase().trim().contains(search_title.toLowerCase().trim());
                    Log.d("Kinopub", "title: " + tit);
                    if (tit) {
                        Document doc = getData(url);
                        if (doc != null) {
                            String d = doc.html();
                            String t = "";
                            String season = "error", episode = "error";
                            if (d.contains("og:title\" content=\"")){
                                t = d.split("og:title\" content=\"")[1].split("\"")[0];
                                if (t.contains("/")) {
                                    t = t.split("/")[0].trim();
                                }
                            } else if (doc.html().contains("<h3"))
                                t = doc.selectFirst("h3").text();
                            if (t.contains("/"))
                                t = t.split("/")[0].trim();
                            if (d.contains("table table-striped")) {
                                Elements allLines = doc.select(".table.table-striped tr");
                                for (Element line : allLines) {
                                    if (line.text().contains("Добавлен")) {
                                        String l = line.text().replace("Добавлен","").trim();
                                        if (l.contains(" сезон")) {
                                            season = l.split(" сезон")[0].trim();
                                        }
                                        if (l.contains(" эпизод")) {
                                            episode = l.split(" эпизод")[0].trim();
                                            if (episode.contains("сезон ")) {
                                                episode = episode.split("сезон ")[1].trim();
                                            }
                                        }
                                    }
                                }
                            }
                            if (!season.contains("error") || !episode.contains("error")) {
                                items.setTitle("catalog serial");
                                items.setType(t + "\nkinopub");
                                items.setToken("");
                                items.setId_trans("");
                                items.setId("error");
                                items.setUrl(url);
                                items.setUrlTrailer("error");
                                items.setSeason(season);
                                items.setEpisode(episode);
                                items.setTranslator("Неизвестный");
                            } else if (d.contains("class=\"item episode-thumbnail\"")) {
                                Element vv = data.selectFirst(".item.episode-thumbnail");
                                String ss = vv.attr("data-url");
                                if (!ss.contains("/s")) {
                                    for (Element v : data.select(".item.episode-thumbnail")) {
                                        if (v.html().contains("item-title text-ellipsis")) {
                                            String u = Statics.KINOPUB_URL + v.select(".item-title.text-ellipsis a").attr("href");
                                            t = v.select(".item-title.text-ellipsis a").text();
                                            items.setTitle("catalog video");
                                            items.setType(t + "\nkinopub");
                                            items.setToken("");
                                            items.setId_trans("");
                                            items.setId("error");
                                            items.setUrl(u);
                                            items.setUrlTrailer("error");
                                            items.setSeason(season);
                                            items.setEpisode(episode);
                                            items.setTranslator("Неизвестный");
                                        } else Log.d("Kinopub", "ParseHtml: no item-title");
                                    }
                                } else {
                                    Element v = data.select(".item.episode-thumbnail").last();
                                    String sv = v.attr("data-url");
                                    if (sv.contains("/s")) {
                                        String s = sv.split("/s")[sv.split("/s").length-1];
                                        String e = s.split("e")[1];
                                        s = s.split("e")[0];
                                        items.setTitle("catalog serial");
                                        items.setType(t + "\nkinopub");
                                        items.setToken("");
                                        items.setId_trans("");
                                        items.setId("error");
                                        items.setUrl(Statics.KINOPUB_URL + sv);
                                        items.setUrlTrailer("error");
                                        items.setSeason(s);
                                        items.setEpisode(e);
                                        items.setTranslator("Неизвестный");
                                    }
                                }
                            } else if (d.contains("class=\"dropdown-menu")) {
                                for (Element v : doc.select(".dropdown-menu")) {
                                    if (v.html().contains("Файл mp4") || v.html().contains("HLS плейлист")) {
                                        String q = "";
                                        if (v.html().contains("4K"))
                                            q = "4K";
                                        else if (v.html().contains("1080p"))
                                            q = "1080p";
                                        else if (v.html().contains("720p"))
                                            q = "720p";
                                        else if (v.html().contains("480p"))
                                            q = "480p";

                                        items.setTitle("catalog video");
                                        items.setType(t + " "+ q + "\nkinopub");
                                        items.setToken("");
                                        items.setId_trans("");
                                        items.setId("error");
                                        items.setUrl(v.html());
                                        items.setUrlTrailer("error");
                                        items.setSeason(season);
                                        items.setEpisode(episode);
                                        items.setTranslator("Неизвестный");
                                    }  else Log.d("Kinopub", "ParseHtml: no item-title");
                                }
                            } else Log.d("Kinopub", "ParseHtml: no item episode-thumbnail && mp4");
                            break;
                        } else Log.d("Kinopub", "ParseHtml: data error 2");
                    } else Log.d("Kinopub", "ParseHtml: !tit");
                }
            } else Log.d("Kinopub", "ParseHtml: data search not found");
        } else Log.d("Kinopub", "ParseHtml: data error wtf");
    }

    private Document getData(String url) {
        try {
            return Jsoup.connect(url)
                    .header("Cookie", Statics.KINOPUB_COOCKIE
                            .replace("{","")
                            .replace("}","")
                            .replace(" , ",";")
                            .replace(",",";"))
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Document getDataSearch(String s) {
        String n = s.trim().replace("\u00a0", " ").trim();
        String url = Statics.KINOPUB_URL + "/item/autocomplete?query=" + n.trim();
        try {
            return Jsoup.connect(url)
                    .header("Cookie", Statics.KINOPUB_COOCKIE
                            .replace("{","")
                            .replace("}","")
                            .replace(" , ",";")
                            .replace(",",";"))
                    .timeout(10000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
