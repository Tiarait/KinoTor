package com.kinotor.tiar.kinotor.parser.catalog;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
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
public class ParserFanserials extends AsyncTask<Void, Void, Void> {
    private String url;
    private ArrayList<ItemHtml> items;
    private ItemHtml itempath;
    private OnTaskCallback callback;
    private boolean t = true;

    private String url_entry = "error ", img = "error ", kpId = "error",
            quality = "error ", rating = "error ";
    private String name = "error", subname = "error", year = "error ", country = "error ",
            genre = "error ", time = "error ", translator = "error ",
            director = "error ", actors = "error ", description_t = "error ",
            iframe = "error", type = "error";
    private String moretitle = "error", moreurl = "error", moreimg = "error", moreseason = "0",
            moreseries = "0", morequality = "error";
    private String season = "0", series = "0";


    public ParserFanserials(String url, ArrayList<ItemHtml> items, ItemHtml itempath, OnTaskCallback callback) {
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
        ParseHtml(Getdata(url));
        return null;
    }

    private void defVal(){
        url_entry = "error ";
        img = "error ";
        quality = "error ";
        rating = "error ";
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
        boolean hide = quality.toLowerCase().contains("ts") && Statics.hideTs;

        if (!hide) {
            itempath.setUrl(url_entry);
            itempath.setTitle(name);
            itempath.setImg(img);
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
            } catch (Exception e) {
                itempath.setSeason(0);
                itempath.setSeries(0);
            }
            items.add(itempath);
        }
    }

    private void ParseHtml(Document data) {
        if (data != null) {
            String dataHtml = data.html();
            if (dataHtml.contains("item-serial")) {
                if (dataHtml.contains("class=\"serial-page-desc single")){
                    Element item = data.select(".item-serial").first();
                    if (item.html().contains("class=\"field-title\"")) {
                        ParseHtml(Getdata(item.select(".field-title a").attr("href").trim()));
                    }
                } else {
                    Elements allEntries = data.select(".item-serial");
                    for (Element entry : allEntries) {
                        String entryHtml = entry.html();
                        defVal();
                        if (entryHtml.contains("class=\"field-title\"")) {
                            name = entry.select(".field-title a").text().trim();
                            url_entry = Statics.FANSERIALS_URL +
                                    entry.select(".field-title a").attr("href").trim();
                        }
                        if (name.contains("/"))
                            name = name.split("/")[0].trim();
                        if (entryHtml.contains("class=\"field-img\""))
                            img = entry.select(".field-img")
                                    .attr("style").trim();
                        if (img.contains("url("))
                            img = img.split("url\\(")[1].split("\\)")[0].trim().replace("'","");

                        if (entryHtml.contains("class=\"serial-translate\""))
                            translator = entry.select(".serial-translate").text().trim();

                        type = "serial";
                        if (entryHtml.contains("class=\"field-description")) {
                            String text = entry.select(".field-description").text().trim();
                            if (text.contains(" сезон"))
                                season = text.split(" сезон")[0].trim();

                            if (season.contains("-"))
                                season = season.split("-")[1].trim();

                            if (text.contains(" серия"))
                                series = text.split(" серия")[0].trim();
                            if (series.contains("сезон "))
                                series = series.split("сезон ")[1].trim();

                            if (series.contains("-"))
                                series = series.split("-")[1].trim();
                        }
                        if (season.contains("error") || season.trim().isEmpty() || season.trim().equals("0"))
                            season = "1";

                        if (!year.contains("error"))
                            name += " (" + year + ")";

//                        Log.e("tt", "ParseHtml: "+name );
                        if (!name.contains("error") && !name.trim().contains("Результаты поиска"))
                            itemSet();
                    }
                }
            } else if (!this.url.contains("query=")){
//                Log.e("tt", "ParseHtml: else");
                defVal();
                if (dataHtml.contains("class=\"page-title\"")) {
                    name = data.select(".page-title").text().trim();
                    url_entry = this.url;
                }
                if (dataHtml.contains("itemprop=\"thumbnailUrl\"")) {
                    img = Statics.FANSERIALS_URL + data.select("link[itemprop^='thumbnailUrl']")
                            .attr("href").trim();
                }
                if (dataHtml.contains("itemprop=\"description\""))
                    description_t = data.select("div[itemprop^='description']").text().trim();

                if (dataHtml.contains("playerData = '"))
                    iframe = dataHtml.split("playerData = '")[1].split("';")[0];
                if (iframe.isEmpty())
                    iframe = "error";
                iframe = Utils.unicodeToString(iframe);
                if (iframe.contains("\"name\":\"")){
                    String[] trans = iframe.split("\"name\":\"");
                    translator = "";
                    for (String n : trans){
                        if (n.contains("http:") && n.contains("umovies"))
                            translator += n.split("\"")[0] + "| ";
                    }
                }

                if (iframe.contains("/serial/") && iframe.contains("/iframe")){
                    Statics.MOON_ID = iframe.split("/serial/")[1].split("/iframe")[0];
                } else if (iframe.contains("/serial\\/") && iframe.contains("\\/iframe")) {
                    Statics.MOON_ID = iframe.split("/serial/")[1].split("/iframe")[0];
                }

                translator = translator.trim().replace("| ", ", ").replace("|", "");
                if (translator.isEmpty())
                    translator = "error";
                iframe = iframe.replace("%", "");

                Elements allTorrents = data.select(".torrent tbody tr");

                for (Element torrent : allTorrents) {
                    String tor_name = "error";

                    if (torrent.html().contains("class=\"studio-voice"))
                        tor_name = torrent.select(".studio-voice").text();

                    Elements allTorr = torrent.select("a[href$=\".torrent\"]");
                    for (Element torr : allTorr) {
                        String t_name, cont = "";
                        if (!tor_name.contains("error")) {
                            t_name = tor_name;
                            if (torr.html().contains("class=\"help")) {
                                t_name += " " + torr.select(".help").text();
                            }
                            cont = torr.attr("href").split("/")
                                    [torr.attr("href").split("/").length-1]
                                    .replace(".torrent", "")
                                    .replace("[FanSerials.club]", "")
                                    .replace("_", " ").trim() + "\n";
                        } else t_name = torr.attr("href").split("/")
                                [torr.attr("href").split("/").length-1];
                        if (!itempath.tortitle.contains(t_name)) {
                            itempath.setTorUrl(torr.attr("href"));
                            itempath.setTorU(this.url);
                            itempath.setTorTitle(t_name);
                            itempath.setTorSize("error");
                            itempath.setTorMagnet("error");
                            itempath.setTorContent(cont + "fanserials");
                            itempath.setTorLich("x");
                            itempath.setTorSid("x");
                        }
                    }
                }

                type = "serial";
                if (dataHtml.contains("property=\"ya:ovs:season\" content=\"")) {
                    season = dataHtml.split("property=\"ya:ovs:season\" content=\"")[1].split("\"")[0].trim();
                }
                if (dataHtml.contains("property=\"ya:ovs:episode\" content=\"")) {
                    series = dataHtml.split("property=\"ya:ovs:episode\" content=\"")[1].split("\"")[0].trim();
                }
                if (name.contains(" серия")) {
                    series = name.split(" серия")[0].split(" ")
                            [name.split(" серия")[0].split(" ").length-1].trim();
                }
                if (name.contains(" сезон")) {
                    season = name.split(" сезон")[0].split(" ")
                            [name.split(" сезон")[0].split(" ").length-1].trim();
                }
                if (name.contains(season + " сезон"))
                    name = name.split(season + " сезон")[0].trim();
                if (name.contains(series + " серия"))
                    name = name.split(series + " серия")[0].trim();

                if (dataHtml.contains("class=\"breadcrumbs")){
                    Element links = data.selectFirst(".breadcrumbs");
                    if (links.html().contains("itemprop=\"url\"")){
                        String serialLink = Statics.FANSERIALS_URL+links.select("a[itemprop^='url']").last().attr("href");
                        if (serialLink.contains(Statics.FANSERIALS_URL + "/") || serialLink.contains("fanserials")){
                            Log.e("Fanserials", "ParseHtml: "+serialLink);
                            if (serialLink.contains(Statics.FANSERIALS_URL + "/"))
                                serialLink = Statics.FANSERIALS_URL + "/" +
                                        serialLink.split(Statics.FANSERIALS_URL + "/")[1].split("/")[0];
                            else
                                serialLink = Statics.FANSERIALS_URL + "/" +
                                        serialLink.split("fanserials")[1].split("/")[1];
                            Document newdata = Getdata(serialLink);
                            if (newdata != null) {
                                String newdataHtml = newdata.html();
                                if (newdataHtml.contains("class=\"info-list\"")){
                                    Elements allLines = newdata.select(".info-list li");
                                    for (Element line : allLines) {
                                        String lineText = line.text();
                                        if (lineText.contains("Год:")) {
                                            year = lineText.replace("Год:", "").trim();
                                            if (year.contains("-"))
                                                year = year.split("-")[0].trim();
                                        }
                                        if (lineText.contains("Рейтинг:")) {
                                            if (line.html().contains("imdh"))
                                                rating = line.select(".imdh").text().trim();
                                            else if (line.html().contains("kinigo"))
                                                rating = line.select(".kinigo").text().trim();
                                        }
                                        if (lineText.contains("Жанр:"))
                                            genre = lineText.replace("Жанр:", "").trim();
                                        if (lineText.contains("Длительность:"))
                                            time = lineText.replace("Длительность:", "").trim();
                                        if (lineText.contains("Страна:"))
                                            country = lineText.replace("Страна:", "").trim();
                                        if (lineText.contains("Режиссер:"))
                                            director = lineText.replace("Режиссер:", "").trim();
                                        if (lineText.contains("Оригинальное:"))
                                            subname = lineText.replace("Оригинальное:", "").trim();
                                        else if (lineText.contains("Альтернативное:"))
                                            subname = lineText.replace("Альтернативное:", "").trim();
                                        if (lineText.contains("Актёры:"))
                                            actors = lineText.replace("Актёры:", "").trim();
                                    }
                                    if (subname.contains("/"))
                                        subname = subname.split("/")[1].trim();
                                }
                                if (newdataHtml.contains("class=\"field-poster\"")){
                                    img = newdata.select(".field-poster img").last().attr("src");
                                }
                                if (newdataHtml.contains("class=\"cat-desc-serial\"")){
                                    if (newdata.selectFirst(".cat-desc-serial").html().contains(".body") &&
                                            (description_t.isEmpty() || description_t.contains("error")))
                                        description_t = newdata.select(".cat-desc-serial.body").html();
                                    description_t = description_t.replace("</p>", "")
                                            .replace("<p>", "<br>");
                                }
                                if (newdataHtml.contains("class=\"item-serial\"")){
                                    Elements allLines = newdata.select(".item-serial");
                                    for (Element more : allLines) {
//                                        Log.e("tt", "ParseHtml: "+more.html() );
                                        defMore();
                                        moreurl = Statics.FANSERIALS_URL + more.select(".field-title a").attr("href").trim();
                                        moretitle = more.select(".field-title").text().trim();
                                        moreimg = more.select(".field-img").first().attr("style");
                                        if (moreimg.contains("url('"))
                                            moreimg = moreimg.split("url\\(")[1].split("\\)")[0].trim().replace("'","");

                                        String desc = more.select(".field-description").text().trim();
                                        if (desc.contains(" сезон"))
                                            moreseason = desc.split(" сезон")[0];
                                        if (desc.contains("серия"))
                                            moreseries = desc.split("серия")[0].trim();
                                        if (moreseries.contains("сезон "))
                                            moreseries = moreseries.split("сезон ")[1];

                                        if (!moreurl.isEmpty()) {
                                            if (moreseason.equals(season) && moreseries.equals(series)){
//                                                Log.e("tt", "ParseHtml: eq");
                                            } else {
//                                                Log.e("tt", "ParseHtml: moreadd");
                                                itempath.setMoreTitle(moretitle);
                                                itempath.setMoreUrl(moreurl);
                                                itempath.setMoreImg(moreimg);
                                                itempath.setMoreQuality(morequality);
                                                itempath.setMoreVoice("error");
                                                itempath.setMoreSeason(moreseason);
                                                itempath.setMoreSeries(moreseries);
                                            }
                                            itempath.setPreImg(moreimg);
                                        }
                                    }
                                }
                                if (newdataHtml.contains("class=\"new-serials-item")){
                                    Elements allLines = newdata.select(".new-serials-item");
                                    for (Element more : allLines) {
                                        defMore();
                                        moreurl = more.select(".field-title a").attr("href").trim();
                                        moretitle = more.select(".field-title").text().trim();
                                        moreimg = more.select(".field-poster img").first().attr("src");

                                        Log.e("rty", "ParseHtml: "+moreimg );
                                        if (!moreurl.isEmpty()) {
//                                            Log.e("tt", "ParseHtml: moreadd");
                                            itempath.setMoreTitle(moretitle);
                                            itempath.setMoreUrl(moreurl);
                                            itempath.setMoreImg(moreimg);
                                            itempath.setMoreQuality(morequality);
                                            itempath.setMoreVoice("error");
                                            itempath.setMoreSeason(moreseason);
                                            itempath.setMoreSeries(moreseries);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (season.contains("error") || season.trim().isEmpty() || season.trim().equals("0"))
                    season = "1";

                if (!name.contains("error") && !name.trim().contains("Результаты поиска"))
                    itemSet();
            }
        }

    }

    private Document Getdata(String url) {
        try {
            Document htmlDoc;
            url = url.replace("page/1/","");
            if (!t && !Statics.FANSERIALS_COOKIE.contains("null"))
                htmlDoc = Jsoup.connect(url)
                        .header("Cookie", "cf_clearance="+Statics.FANSERIALS_COOKIE)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36 OPR/62.0.3331.99")
                        .ignoreContentType(true).get();
            else
                htmlDoc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.100 Safari/537.36 OPR/62.0.3331.99")
                        .ignoreContentType(true).get();

//            Log.e("fanserial", "Getdatahtml: "+htmlDoc.body().html() );
            return htmlDoc;
        } catch (Exception e) {
            if (t && !Statics.FANSERIALS_COOKIE.contains("null")) {
                t = false;
                ParseHtml(Getdata(url));
            }
            e.printStackTrace();
            return null;
        }
    }
}
