package com.kinotor.tiar.kinotor.parser.catalog;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskCallback;
import com.kinotor.tiar.kinotor.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Tiar on 08.2018.
 */
public class ParserKinoxa extends AsyncTask<Void, Void, Void> {
    private String url;
    private ArrayList<ItemHtml> items;
    private ItemHtml itempath;
    private OnTaskCallback callback;

    private String url_entry = "error ", img = "error ", kpId = "error",
            quality = "error ", rating = "error ", trailer;
    private String name = "error", subname = "error", year = "error ", country = "error ",
            genre = "error ", time = "error ", translator = "error ",
            director = "error ", actors = "error ", description_t = "error ",
            iframe = "error", type = "error";
    private String moretitle = "error", moreurl = "error", moreimg = "error", moreseason = "0",
            moreseries = "0", morequality = "error";
    private String season = "0", series = "0";


    public ParserKinoxa(String url, ArrayList<ItemHtml> items, ItemHtml itempath, OnTaskCallback callback) {
        this.url = url;
        if (items != null) this.items = items;
        else this.items = new ArrayList<>();
        if (itempath != null) this.itempath = itempath;
        else this.itempath = new ItemHtml();
        this.callback = callback;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(items, itempath);
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (!ItemMain.xs_field.trim().isEmpty() && !ItemMain.xs_field.equals("error"))
            ParseHtml(Postdata(url));
        else ParseHtml(Getdata(url));
        return null;
    }

    private void defVal(){
        url_entry = "error ";
        img = "error ";
        quality = "error ";
        rating = "error ";
        trailer = "error ";
        name = "error";
        subname = "error";
        year = "error ";
        kpId = "error";
        country = "error ";
        genre = "error ";
        time = "error ";
        translator = "error ";
        director = "error ";
        actors = "error ";
        description_t = "error ";
        iframe = "error";
        type = "error";
        season = "0";
        series = "0";
    }
    private void defMore(){
        moretitle = "error";
        moreurl = "error";
        moreimg = "error";
        moreseason = "0";
        moreseries = "0";
        morequality = "error";
    }

    private void itemSet(){
        itempath.setUrl(url_entry);
        itempath.setTitle(name);
        itempath.setImg(img);
        itempath.setTrailer(trailer);
        itempath.setSubTitle(subname);
        itempath.setQuality(quality);
        itempath.setVoice(translator);
        itempath.setRating(rating);
        itempath.setDescription(description_t);
        itempath.setDate(year);
        itempath.setKpId(kpId);
        itempath.setCountry(country);
        itempath.setGenre(Utils.renGenre(genre));
        itempath.setDirector(director);
        itempath.setActors(actors);
        itempath.setTime(time);
        itempath.setIframe(iframe);
        itempath.setType(type);
        try {
            itempath.setSeason(Integer.parseInt(season.replace(" ", "")));
            itempath.setSeries(Integer.parseInt(series.replace(" ", "")));
        } catch (Exception e){
            itempath.setSeason(0);
            itempath.setSeries(0);
        }
        items.add(itempath);
    }

    private void ParseHtml(Document data) {
        if (data != null) {
            Log.i("i", "parse");
            if (data.html().contains("class=\"short fx-row")) {
                Elements allEntries = data.select(".short.fx-row");
                for (Element entry : allEntries) {
                    defVal();
                    if (entry.html().contains("class=\"short-top-left fx-1\"")) {
                        name = entry.select(".short-top-left.fx-1").text().trim();
                        url_entry = entry.select(".short-top-left.fx-1 a").attr("href").trim();
                    }
                    Elements allLines = entry.select(".short-info");
                    for (Element line : allLines) {
                        if (line.text().contains("Год:"))
                            year = line.text().replace("Год:", "").trim();
                        if (line.text().contains("Страна:"))
                            country = line.text().replace("Страна:", "").trim();
                        if (line.text().contains("Жанр:"))
                            genre = line.text().replace("Жанр:", "").trim();
                        if (line.text().contains("Перевод:")) {
                            translator = line.text().replace("Перевод:", "").trim();
                        }
                        if (line.text().contains("Прод-сть:"))
                            time = line.text().replace("Прод-сть:", "").trim();
                    }
                    if (translator.equals("-"))
                        translator = "error";

                    if (entry.html().contains("class=\"mrate-imdb\"")) {
                        rating = entry.select(".mrate-imdb").text().trim();
                    } else if (entry.html().contains("class=\"mrate-kp\"")) {
                        rating = entry.select(".mrate-kp").text().trim();
                    }

                    if (entry.html().contains("class=\"short-desc\""))
                        description_t = entry.select(".short-desc").text().trim();


                    if (entry.html().contains("class=\"short-img img-box with-mask ps-link\""))
                        img = Statics.KINOXA_URL + entry.select(".short-img.img-box.with-mask.ps-link img").attr("src").trim();

                    if (entry.html().contains("class=\"short-meta short-meta-qual"))
                        quality = entry.select(".short-meta.short-meta-qual").text().trim();

                    type = "movie";
                    if (name.trim().endsWith("сезон") || quality.contains("сезон")) {
                        type = "serial";
                        if (quality.contains(" сезон")) {
                            season = quality.split(" сезон")[0].trim();
                            if (quality.contains(" серия"))
                                series = quality.split(" сезон")[1].split(" серия")[0].trim();
                            quality = "error";
                        } else if (name.trim().endsWith("сезон")) {
                            season = name.split("сезон")[0].trim();
                            if (season.contains(" "))
                                season = season.split(" ")[season.split(" ").length - 1].trim();
                            series = "0";
                        }
                        if (name.trim().endsWith("сезон")){
                            name = name.split("сезон")[0].trim()
                                    .replace("(", "/")
                                    .replace(")", "");
                            String n = "";
                            for (int i = 0; i < name.split(" ").length - 1; i++) {
                                n += name.split(" ")[i] + " ";
                            }
                            name = n.trim() + " (" + year + ")";
                        }
                        if (season.contains("-")) {
                            season = season.split("-")[1].trim();
                        }
                    }

                    if (!name.contains(year))
                        name += " (" + year + ")";
                    boolean hide = quality.toLowerCase().contains("ts") && Statics.hideTs;

                    if (!name.contains("error") && !hide) {
                        itemSet();
                        Log.e("test", "parseHtml: "+url_entry );
                    }
                }
            } else if (data.html().contains("class=\"mpage\"")){
                Element entry = data.select(".mpage").first();
                defVal();
                url_entry = this.url;
                if (entry.html().contains("class=\"short-top-left fx-1"))
                    name = entry.select(".short-top-left.fx-1 h1").first().text().trim();
                if (entry.html().contains("class=\"short-original-title"))
                    subname = entry.select(".short-original-title").first().text().trim();
                if (entry.html().contains("class=\"mimg img-wide\""))
                    img = Statics.KINOXA_URL + entry.select(".mimg.img-wide img").attr("src").trim();

                Elements allLines = data.select(".short-info");
                for (Element line : allLines) {
                    if (line.text().contains("Год:"))
                        year = line.text().replace("Год:", "").trim();
                    if (line.text().contains("Страна:"))
                        country = line.text().replace("Страна:", "").trim();
                    if (line.text().contains("Ориг. название:"))
                        subname = line.text().replace("Ориг. название:", "").trim();
                    if (line.text().contains("Актеры:"))
                        actors = line.text().replace("Актеры:", "").trim();
                    if (line.text().contains("Жанр:"))
                        genre = line.text().replace("Жанр:", "").trim();
                    if (line.text().contains("Режиссер:"))
                        director = line.text().replace("Режиссер:", "").trim();
                    if (line.text().contains("Перевод:"))
                        translator = line.text().replace("Перевод:", "").trim();
                    if (line.text().contains("Прод-сть:"))
                        time = line.text().replace("Прод-сть:", "").trim();
                    if (line.text().contains("Время:"))
                        time = line.text().replace("Время:", "").trim();
                }
                if (translator.equals("-"))
                    translator = "error";

                if (data.html().contains("data-kpid=\"")){
                    kpId = data.html().split("data-kpid=\"")[1].split("\"")[0];
                    Statics.KP_ID = kpId;
                }
                if (data.html().contains("/video/")){
                    String id = data.html().split("/video/")[1];
                    if (id.contains("/iframe")) {
                        id = id.split("/iframe")[0];
                        Statics.MOON_ID = id;
                    }
                } else if (data.html().contains("/serial/")) {
                    String id = data.html().split("/serial/")[1];
                    if (id.contains("/iframe")) {
                        id = id.split("/iframe")[0];
                        Statics.MOON_ID = id;
                    }
                }
                if (entry.html().contains("var player = new Playerjs(")){
                    iframe = entry.html().split("var player = new Playerjs\\(")[1].split("\\);")[0];
                } else if (entry.html().contains("<iframe ")){
                    iframe = entry.selectFirst("iframe").attr("src");
                }
                if (entry.html().contains("trailer-place")){
                    trailer = Statics.KINOXA_URL +
                            entry.select("#trailer-place iframe").attr("src");
                }
                if (iframe.trim().isEmpty())
                    iframe = "error";

                if (entry.html().contains("class=\"mtext full-text video-box"))
                    description_t = entry.select(".mtext.full-text.video-box").html().trim();
                description_t = description_t.replace("\"", "")
                        .replace("\t", "").trim();

                if (entry.html().contains("mrate-imdb")) {
                    rating = "IMDB["+entry.select(".mrate-imdb").text().trim()+"] ";
                }
                if (entry.html().contains("mrate-kp")) {
                    if (rating.contains("error"))
                        rating = "KP["+entry.select(".mrate-kp").text().trim()+"] ";
                    else rating += "KP["+entry.select(".mrate-kp").text().trim()+"] ";
                }


                if (entry.html().contains("class=\"short-meta short-meta-qual"))
                    quality = entry.select(".short-meta.short-meta-qual").text().trim();


                type = "movie";
                if (name.trim().endsWith("сезон") || quality.contains("сезон")) {
                    type = "serial";
                    if (quality.contains(" сезон")) {
                        season = quality.split(" сезон")[0].trim();
                        if (quality.contains(" серия"))
                            series = quality.split(" сезон")[1].split(" серия")[0].trim();
                        quality = "error";
                    } else if (name.trim().endsWith("сезон")) {
                        season = name.split("сезон")[0].trim();
                        if (season.contains(" "))
                            season = season.split(" ")[season.split(" ").length - 1].trim();
                        series = "0";
                    }
                    if (name.trim().endsWith("сезон")){
                        name = name.split("сезон")[0].trim()
                                .replace("(", "/")
                                .replace(")", "");
                        String n = "";
                        for (int i = 0; i < name.split(" ").length - 1; i++) {
                            n += name.split(" ")[i] + " ";
                        }
                        name = n.trim() + " (" + year + ")";
                    }
                    if (season.contains("-")) {
                        season = season.split("-")[1].trim();
                    }
                }

                if (entry.html().contains("class=\"f-screens")) {
                    Element allImg = entry.select(".f-screens").first();
                    Elements allImgs = allImg.select("a");

                    for (Element preimg : allImgs) {
                        itempath.setPreImg(Statics.KINOXA_URL + preimg.attr("href"));
                    }
                }

                if (entry.html().contains("class=\"carou-item-wr")) {
                    Log.e("kinoxa", "ParseHtml: 1" );
                    for (Element more : entry.select(".carou-item-wr")) {
                        defMore();
                        moreurl = more.select("a").first().attr("href").trim();
                        moretitle = more.select(".tc-title").text().trim();
                        moreimg = Statics.KINOXA_URL + more.select("img").first().attr("src");

                        if (!moreurl.isEmpty()) {
                            Log.e("kinoxa", "ParseHtml: 2" );
                            itempath.setMoreTitle(moretitle);
                            itempath.setMoreUrl(moreurl);
                            itempath.setMoreImg(moreimg);
                            itempath.setMoreQuality(morequality);
                            itempath.setMoreVoice("error");
                            itempath.setMoreSeason(moreseason);
                            itempath.setMoreSeries(moreseries);
                        } else Log.e("kinoxa", "ParseHtml: 3" );
                    }
                } else Log.e("kinoxa", "ParseHtml: 4" );

                if (!name.contains(year))
                    name += " (" + year + ")";

//                Log.e("tt", "ParseHtml: "+name );

                if (!name.contains("error"))
                    itemSet();
            }
        }

    }
    private Document Postdata(String url) {
        try {
            if (Statics.ProxyUse.contains("kinogid") && Statics.ProxyCur.contains(":") && !Statics.ProxyCur.contains("адрес:порт")){
                System.setProperty("http.proxyHost", Statics.ProxyCur.split(":")[0].trim());
                System.setProperty("http.proxyPort", Statics.ProxyCur.split(":")[1].trim());
            } else {
                System.clearProperty("http.proxyHost");
                System.clearProperty("http.proxyPort");
            }
            return Jsoup.connect(url)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .data("xsort", "1")
                    .data("xs_field", ItemMain.xs_field)
                    .data("xs_value", ItemMain.xs_value)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(30000).ignoreContentType(true).post();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Document Getdata(String url) {
        try {
            if (Statics.ProxyUse.contains("kinogid") && Statics.ProxyCur.contains(":") && !Statics.ProxyCur.contains("адрес:порт")){
                System.setProperty("http.proxyHost", Statics.ProxyCur.split(":")[0].trim());
                System.setProperty("http.proxyPort", Statics.ProxyCur.split(":")[1].trim());
            } else {
                System.clearProperty("http.proxyHost");
                System.clearProperty("http.proxyPort");
            }
            return Jsoup.connect(url)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(30000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
