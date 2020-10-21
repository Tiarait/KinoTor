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
public class ParserAnidub extends AsyncTask<Void, Void, Void> {
    private String url;
    private ArrayList<ItemHtml> items;
    private ItemHtml itempath;
    private OnTaskCallback callback;

    private String url_entry = "error ", img = "error ", kpId = "error",
            quality = "error ", rating = "error ";
    private String name = "error", subname = "error", year = "error ", country = "error ",
            genre = "error ", time = "error ", translator = "error ",
            director = "error ", actors = "error ", description_t = "error ",
            iframe = "error", type = "error";
    private String moretitle = "error", moreurl = "error", moreimg = "error", moreseason = "0",
            moreseries = "0", morequality = "error";
    private String season = "0", series = "0";


    public ParserAnidub(String url, ArrayList<ItemHtml> items, ItemHtml itempath, OnTaskCallback callback) {
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

//        Log.e("test", "Getdata 2: "+url );
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
        if (url_entry.trim().isEmpty())
            url_entry = this.url;

        if (subname.trim().isEmpty())
            subname = "error";
        if (!name.contains("error")) {
            Log.e("test", "itemSet: "+url_entry);

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
            String dd = data.html();
            if (dd.contains("news_full")) {
                defVal();

                if (dd.contains("class=\"titlfull\"")) {
                    name = data.select(".titlfull").text()
                            .replace("Смотреть аниме ","").trim();
                }
                url_entry = this.url;
                iframe = this.url;
                quality = "HD 720";
                time = "N/A";

                if (dd.contains("class=\"maincont")){
                    description_t = data.selectFirst(".maincont").text();
                    if (description_t.contains("Описание:"))
                        description_t = description_t.split("Описание:")[1].trim();
                    if (description_t.contains("Подпишись на наш канал"))
                        description_t = description_t.split("Подпишись на наш канал")[0].trim();
                    if (description_t.contains("Смотреть "+ name))
                        description_t = description_t.split("Смотреть "+ name)[0].trim();
                }
//                else if (url.contains("/")){
//                    String u = url.split("/")[url.split("/").length-1].trim();
//                    if (u.contains("-"))
//                        iframe = u.split("-")[0].trim();
//                }
//                Log.e("test", "Getdata id1: "+iframe+"|"+url );

                if (dd.contains("class=\"reset\"")) {
                    for (Element item : data.select(".reset li")) {
                        String string = item.text();
                        if (string.contains("Год:")) {
                            year = item.text().replace("Год:", "").trim();
                        }
                        if (string.contains("Жанр:")) {
                            genre = item.text().replace("Жанр:", "").trim();
                        }
                        if (string.contains("Страна:")) {
                            country = item.text().replace("Страна:", "").trim();
                        }
                        if (string.contains("Режиссер:")) {
                            director = item.text().replace("Режиссер:", "").trim();
                        }
                        if (string.contains("Озвучивание:")) {
                            translator = item.text().replace("Озвучивание:", "").trim();
                        }
                        if (string.contains("Описание")) {
                            description_t = item.text().replace("Описание", "")
                                    .replace(":","").trim();
                        }
                    }
                }


                if (dd.contains("class=\"poster_img\"")){
                    img = data.select(".poster_img img").attr("src").trim();
                }

                if (dd.contains("class=\"bestRating\"")) rating = data.select(".bestRating").first().text().trim();
                if(rating.contains("%")) rating = rating.split("%")[0].trim();
                if(rating.contains("%")) rating = rating.split("%")[0].trim();
                if(rating.contains("Голосов")) {
                    String r = rating.split("Голосов")[1].trim();
                    if (r.contains(" ")){
                        rating = r.split(" ")[r.split(" ").length-1].trim();
                    } else
                        rating = rating.split("Голосов")[0].replace("/5","").trim();
                }

                if (name.contains("/")) {
                    subname = name.split("/")[name.split("/").length-1].trim();
//                    if (name.split("/").length > 1)
//                        name = name.split("/")[1].trim();
//                    else
                        name = name.split("/")[0].trim();
                }
                if (name.contains("ТВ-")) name = name.split("ТВ-")[0].trim();
                if (subname.contains("TV-")) {
                    season = subname.split("TV-")[1].trim();
                    if (season.contains("из")) series = season.split("из")[0].trim();
                    if (series.contains("[")) series = series.split("\\[")[1].trim();
                    if (season.contains("[")) season = season.split("\\[")[0].trim();
                    subname = subname.split("TV-")[0].trim();
                }
                if (subname.contains("[")) {
                    if (subname.contains("из")) {
                        season = "1";
                        series = subname.split("из")[0].trim();
                        if (series.contains("[")) series = series.split("\\[")[1].trim();
                    }
                    subname = subname.split("\\[")[0].trim();
                }
                if (name.contains("[")) name = name.split("\\[")[0].trim();
                if (name.contains("OVA")) name = name.split("OVA")[0].trim();

                if (!series.contains("error") || !season.contains("error")) type = "serial";
                else type = "movie";

                if (url.contains("/anime/"))
                    type += " anime";
                if (!year.contains("error"))
                    name += " (" + year + ")";

                boolean hide = quality.toLowerCase().contains("ts") && Statics.hideTs;
//                Log.e("test", "Getdata 12: "+name+hide );
                if (!name.contains("error") && !hide)
                    itemSet();
            } else if (dd.contains("class=\"news_short\"")) {
                Elements allEntries = data.select(".news_short");
                for (Element entry : allEntries) {
                    String ee = entry.html();
                    defVal();

                    if (ee.contains("class=\"posters\"")) {
                        name = entry.select(".posters").attr("alt")
                                .replace("Смотреть аниме ","").trim();
                        img = entry.select(".posters").attr("data-original").trim();
                    } else if (ee.contains("class=\"poster_img\"")) {
                        name = entry.select(".poster_img img").attr("alt")
                                .replace("Смотреть аниме ","").trim();
                        img = entry.select(".poster_img img").attr("src").trim();
                    }
                    if (ee.contains("class=\"newsmore\"")) {
                        url_entry = entry.select(".newsmore a").attr("href").trim();
                    }
                    Log.e("test", "ParseHtml: "+name+url_entry);
                    if (!url_entry.contains("/anidub_news/") && !url_entry.contains("/videoblog/")) {
                        if (ee.contains("itemprop=\"year\""))
                            year = entry.select("span[itemprop^='year']").first().text().trim();
                        if (ee.contains("itemprop=\"year\""))
                            genre = entry.select("span[itemprop^='genre']").first().text().trim();

                        if (ee.contains("class=\"reset\"")) {
                            for (Element item : entry.select(".reset li")) {
                                String string = item.text();
                                if (string.contains("Страна:")) {
                                    country = item.text().replace("Страна:", "").trim();
                                }
                                if (string.contains("Режиссер:")) {
                                    director = item.text().replace("Режиссер:", "").trim();
                                }
                                if (string.contains("Озвучивание:")) {
                                    translator = item.text().replace("Озвучивание:", "").trim();
                                }
//                            if (string.contains("Количество серий:")) {
//                                series = item.text().replace("Количество серий:", "").trim();
//                            }
                            }
                        }

                        if (ee.contains("class=\"rate_view\"")) {
                            rating = entry.select(".rate_view").first().text()
                                    .replace("Рейтинг:", "").trim();
                        }
                        if(rating.contains("%")) rating = rating.split("%")[0].trim();
                        if(rating.contains("Голосов")) {
                            String r = rating.split("Голосов")[1].trim();
                            if (r.contains(" ")){
                                rating = r.split(" ")[r.split(" ").length-1].trim();
                            } else
                                rating = rating.split("Голосов")[0].replace("/5","").trim();
                        }

                        if (name.contains("/")) {
                            subname = name.split("/")[name.split("/").length - 1].trim();
//                            if (name.split("/").length > 1)
//                                name = name.split("/")[1].trim();
//                            else
                                name = name.split("/")[0].trim();
                        }
                        if (name.contains("ТВ-")) name = name.split("ТВ-")[0].trim();
                        if (subname.contains("TV-")) {
                            season = subname.split("TV-")[1].trim();
                            if (season.contains("из")) series = season.split("из")[0].trim();
                            if (series.contains("[")) series = series.split("\\[")[1].trim();
                            if (season.contains("[")) season = season.split("\\[")[0].trim();
                            subname = subname.split("TV-")[0].trim();
                        }
                        if (subname.contains("[")) {
                            if (subname.contains("из")) {
                                season = "1";
                                series = subname.split("из")[0].trim();
                                if (series.contains("[")) series = series.split("\\[")[1].trim();
                            }
                            subname = subname.split("\\[")[0].trim();
                        }
                        if (name.contains("[")) name = name.split("\\[")[0].trim();
                        if (name.contains("OVA")) name = name.split("OVA")[0].trim();

                        if (!series.contains("error") || !season.contains("error")) type = "serial";
                        else type = "movie";

                        if (url.contains("/anime/"))
                            type += " anime";
                        if (!year.contains("error"))
                            name += " (" + year + ")";

                        boolean hide = quality.toLowerCase().contains("ts") && Statics.hideTs;
                        if (!name.contains("error") && !hide)
                            itemSet();
                    }
                }
            }
//
//
//            if (dd.contains("class=\"frames-list\"")) {
//                Elements allImg = data.select(".frames-list li");
//                for (Element preimg : allImg) {
//                    String img = preimg.select("a").first().attr("href");
//                    if (Statics.FILMIX_URL.startsWith("http://cameleo.xyz/r?url="))
//                        img = img.replace("/","%2F");
//                    itempath.setPreImg(Statics.FILMIX_URL + img);
//                }
//            }
////            Log.e("test", "Getdata 13: "+url );
//
//            if (dd.contains("class=\"slider-item\"")){
//                Elements allMore = data.select(".slider-item");
//                for (Element entry : allMore) {
//                    defMore();
//                    moreurl = entry.select("a").first().attr("href").trim();
//                    moretitle = entry.select(".film-name").text().trim();
//                    moreimg = entry.select("img").first().attr("src");
//
//                    if (!moreurl.isEmpty()) {
//                        itempath.setMoreTitle(moretitle);
//                        itempath.setMoreUrl(moreurl);
//                        itempath.setMoreImg(moreimg);
//                        itempath.setMoreQuality(morequality);
//                        itempath.setMoreVoice("error");
//                        itempath.setMoreSeason(moreseason);
//                        itempath.setMoreSeries(moreseries);
//                    }
//                }
//            }

        }

    }

    private Document Getdata(String url) {
        try {
            Document htmlDoc;
            if (url.contains("'page'")) {

                Log.d("anidub", "Getdata: get connected to " + url +ItemMain.xs_search);
                htmlDoc = Jsoup.connect(url.split( "'page'")[0])
                        .data("do", "search")
                        .data("subaction", "search")
                        .data("story", ItemMain.xs_search.replace("-", " "))
                        .data("search_start", url.split( "'page'")[1])
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .timeout(5000).ignoreContentType(true).post();
            } else {
                htmlDoc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .timeout(5000).ignoreContentType(true).get();
            }
            Log.d("anidub", "Getdata: get connected to " + url);
            return htmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
