package com.kinotor.tiar.kinotor.parser.catalog.filmix;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskCallback;
import com.kinotor.tiar.kinotor.utils.Utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Created by Tiar on 08.2018.
 */
public class ParserFilmix extends AsyncTask<Void, Void, Void> {
    private String url;
    private ArrayList<ItemHtml> items;
    private ItemHtml itempath;
    private OnTaskCallback callback;

    private String url_entry = "error ", img = "error ", kpId = "error",
            quality = "error ", rating = "error ";
    private String name = "error", subname = "error", year = "error ", country = "error ",
            genre = "error ", time = "error ", translator = "error ",
            director = "error ", actors = "error ", description_t = "error ",
            iframe = "error", type = "error", trailer = "error";
    private String moretitle = "error", moreurl = "error", moreimg = "error", moreseason = "0",
            moreseries = "0", morequality = "error";
    private String season = "0", series = "0";


    public ParserFilmix(String url, ArrayList<ItemHtml> items, ItemHtml itempath, OnTaskCallback callback) {
//        Log.e("test", "ParserFilmix: "+url );
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
        if (url != null) {
            if (url.contains("sphinx_search.php"))
                ParseHtml(GetDataSearch());
            else if (url.endsWith(".html") && (url.contains("filmix.co") || url.contains("filmix.life")))
                ParseHtmlJson(GetdataTiar(url));
            else ParseHtml(Getdata(url));
        }
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
        trailer = "error";
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
//        Log.e("test", "loadDone: "+name );
        if (url_entry.trim().isEmpty())
            url_entry = this.url;

        if (subname.trim().isEmpty())
            subname = "error";
        if (!name.contains("error")) {
//            Log.e("test", "itemSet: "+url_entry);

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
            itempath.setTrailer(trailer);
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

    private void ParseHtmlJson(Document data) {
        if (data != null) {
            String ee = data.text().replace("\\", "")
                    .replace("https://filmix.co", Statics.FILMIX_URL)
                    .replace("https://filmix.life", Statics.FILMIX_URL);
            defVal();
            if (ee.contains("\"url\":\"")) {
                if (ee.contains("\"Title\":\""))
                    name = ee.split("\"Title\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"url\":\""))
                    url_entry = ee.split("\"url\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"Title_eng\":\""))
                    subname = ee.split("\"Title_eng\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"Year\":\""))
                    year = ee.split("\"Year\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"Country\":\""))
                    country = ee.split("\"Country\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"Description\":\""))
                    description_t = ee.split("\"Description\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"Poster\":\""))
                    img = ee.split("\"Poster\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"Quality\":\""))
                    quality = ee.split("\"Quality\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"Genre\":\""))
                    genre = ee.split("\"Genre\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"Voice\":\""))
                    translator = ee.split("\"Voice\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"Actors\":\""))
                    actors = ee.split("\"Actors\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"Director\":\""))
                    director = ee.split("\"Director\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"Time\":\""))
                    time = ee.split("\"Time\":\"")[1].split("\"")[0].trim();
                type = "movie";
                //----------------------------------------------------------------------------------
                if (ee.contains("\"Status\":\"")) {
                    String status = ee.split("\"Status\":\"")[1].split("\"")[0].trim();
                    if (status.contains(" сезон)")) {
                        type = "serial";
                        season = status.split(" сезон\\)")[0].split("\\(")[1].trim();
                    }
                    if (season.contains("-")) {
                        season = season.split("-")[1].trim();
                    }
                    if (status.contains(" серия")) {
                        type = "serial";
                        series = status.split(" серия")[0].trim();
                    }
                    if (series.contains("-"))
                        series = series.split("-")[1].trim();
                }
                //----------------------------------------------------------------------------------
                if (description_t.contains("Хотим порекомендовать к просмотру"))
                    description_t = description_t.split("Хотим порекомендовать к просмотру")[0];
                if (description_t.contains("Поклонникам")) {
                    if (description_t.split("Поклонникам")[1].contains("мы рекомендуем"))
                        description_t = description_t.split("Поклонникам")[0];
                }
                if (url.contains("/")) {
                    String u = url.split("/")[url.split("/").length - 1].trim();
                    if (u.contains("-"))
                        iframe = u.split("-")[0].trim();
                }

                //----------------------------------------------------------------------------------
                if (ee.contains("\"rate_site\":")) {
                    rating = "SITE[" + ee.split("\"rate_site\":")[1].split(",")[0]
                            .replace("\"", "").trim() + "]";
                }
                if (ee.contains("\"rate_imdb\":\"")) {
                    if (rating.contains("error"))
                        rating = "IMDB[" + ee.split("\"rate_imdb\":")[1].split(",")[0].trim() + "] ";
                    else
                        rating += "IMDB[" + ee.split("\"rate_imdb\":")[1].split(",")[0].trim() + "] ";
                }
                if (ee.contains("\"rate_kp\":\"")) {
                    if (rating.contains("error"))
                        rating = "KP[" + ee.split("\"rate_kp\":")[1].split(",")[0].trim() + "] ";
                    else rating += "KP[" + ee.split("\"rate_kp\":")[1].split(",")[0].trim() + "] ";
                }
                rating = rating.replace("\"","").trim();
                //----------------------------------------------------------------------------------
                if (ee.contains("\"Images\":\"")) {
                    String imgs = ee.split("\"Images\":\"")[1].split("\"")[0].trim();
                    if (imgs.contains("||")) {
                        for (String preimg : imgs.split("\\|\\|")) {
                            itempath.setPreImg(preimg.trim());
                        }
                    } else itempath.setPreImg(imgs.trim());
                }
                //----------------------------------------------------------------------------------
                if (ee.contains("\"Recommendations\":{")) {
                    String recommendations = ee.split("\"Recommendations\":\\{")[1].split("\\}")[0].trim();
                    if (recommendations.contains("\"title\":\"")) {
                        String moretitles = recommendations.split("\"title\":\"")[1].split("\"")[0].trim();
                        if (moretitles.contains("||")) {
                            for (String title : moretitles.split("\\|\\|")) {
                                itempath.setMoreTitle(title);
                                itempath.setMoreQuality("error");
                                itempath.setMoreVoice("error");
                                itempath.setMoreSeason("error");
                                itempath.setMoreSeries("error");
                            }
                        } else {
                            itempath.setMoreTitle(moretitles);
                            itempath.setMoreQuality("error");
                            itempath.setMoreVoice("error");
                            itempath.setMoreSeason("error");
                            itempath.setMoreSeries("error");
                        }
                    }
                    if (recommendations.contains("\"poster\":\"")) {
                        String posters = recommendations.split("\"poster\":\"")[1].split("\"")[0].trim();
                        if (posters.contains("||")) {
                            for (String poster : posters.split("\\|\\|")) {
                                itempath.setMoreImg(poster);
                            }
                        } else itempath.setMoreImg(posters);
                    }
                    if (recommendations.contains("\"url\":\"")) {
                        String urls = recommendations.split("\"url\":\"")[1].split("\"")[0].trim();
                        if (urls.contains("||")) {
                            for (String url : urls.split("\\|\\|")) {
                                itempath.setMoreUrl(url);
                            }
                        } else {
                            itempath.setMoreUrl(urls);
                        }
                    }
                }
                //----------------------------------------------------------------------------------
                if (!year.contains("error"))
                    name += " (" + year + ")";

                boolean hide = quality.toLowerCase().contains("ts") && Statics.hideTs;
                if (!name.contains("error") && !hide)
                    itemSet();
            }
        }
    }

    private void ParseHtml(Document data) {
        if (data != null) {
            String dd = data.html();

//            Log.e("test", "loadDone: 0");
            Elements allEntries = data.select("article");
            for (Element entry : allEntries) {
                String ee = entry.html();
                defVal();

                if (ee.contains("class=\"name\"")) {
                    name = entry.select(".name").text().trim();
                    url_entry = entry.select(".name a").attr("href").trim();
                }
                if (ee.contains("data-id")){
                    iframe = entry.attr("data-id");
                } else if (url.contains("/")){
                    String u = url.split("/")[url.split("/").length-1].trim();
                    if (u.contains("-"))
                        iframe = u.split("-")[0].trim();
                }
                if (dd.contains("trailer_id = ")){
                    String tr = Statics.FILMIX_URL + "/play/" + dd.split("trailer_id = ")[1].split(";")[0];
                    trailer = tr;
                    if (!tr.trim().isEmpty())
                        iframe = iframe + "[trailer]" + tr;
                }
//                Log.e("test", "Getdata id1: "+iframe+"|"+url );

                if (ee.contains("class=\"origin-name\""))
                    subname = entry.select(".origin-name").text().trim();
                if (ee.contains("class=\"item year\""))
                    year = entry.select(".year > .item-content a").first().text().trim();
                if (ee.contains("class=\"item contry\""))
                    country = entry.select(".contry > .item-content").text().trim();
                if (ee.contains("class=\"full-story\""))
                    description_t = entry.select(".full-story").html().trim();
                else if (year.contains("error")) {
                    description_t = "";
                    Elements ell = entry.select(".item");
                    for (Element item : ell) {
                        if (item.text().contains("Место рождения:")) {
                            country = item.text().replace("Место рождения:", "").trim();
                        } else if (!item.text().contains("Жанры:") && !item.text().contains("Дата рождения:")
                                && !item.text().contains("Дата смерти:")) {
                            description_t += "\n<br>" + item.text().replace(",", ", ")
                                    .replace("  ", " ")
                                    .replace(":", ":\t").trim() + "\n\n\t";
                        }
                    }
                }

                description_t = description_t.replace("\"", "").trim();
                if (description_t.contains("Хотим порекомендовать к просмотру"))
                    description_t = description_t.split("Хотим порекомендовать к просмотру")[0];
                if (description_t.contains("Поклонникам")){
                    if (description_t.split("Поклонникам")[1].contains("мы рекомендуем"))
                        description_t = description_t.split("Поклонникам")[0];
                }

                if (ee.contains("birthDate") && dd.contains("class=\"slider-item\"") &&
                        !this.url.contains("persons")) {
                    year = entry.select("time[itemprop='birthDate']").text().trim();
                    if (ee.contains("deathDate"))
                        year += "/" + entry.select("time[itemprop='deathDate']").text().trim();
                    description_t += "\n";
                }

                if (ee.contains("class=\"poster poster-tooltip\""))
                    img = entry.select(".poster.poster-tooltip").attr("src").trim();
                else if (ee.contains("class=\"poster\""))
                    img = entry.select(".poster").attr("src").trim();
                if (ee.contains("class=\"quality\""))
                    quality = entry.select(".quality").text().trim();

                if (ee.contains("class=\"like\"")) {
                    rating = "SITE["+ entry.select(".like span").first().text().trim() + "] ";
                } else if (ee.contains("class=\"rateinf ratePos") && ee.contains("class=\"rateinf rateNeg")) {
                    try {
                        int plus = Integer.parseInt( entry.select(".rateinf.ratePos").first().text().trim());
                        int minus = Integer.parseInt( entry.select(".rateinf.rateNeg").first().text().trim());
                        rating = "SITE["+ (plus - minus) +"]";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (ee.contains("class=\"imdb")) {
                    if (rating.contains("error"))
                        rating =  "IMDB["+ entry.select(".imdb p").first().text().trim()+"] ";
                    else rating += "IMDB["+ entry.select(".imdb p").first().text().trim()+"] ";
                }
                if (ee.contains("class=\"kinopoisk")) {
                    if (rating.contains("error"))
                        rating = "KP["+ entry.select(".kinopoisk p").first().text().trim()+"]";
                    else rating += "KP["+ entry.select(".kinopoisk p").first().text().trim()+"]";
                }
                if (ee.contains("class=\"item category\""))
                    genre = entry.select(".category > .item-content").first().text().trim();
                if (ee.contains("itemprop=\"genre\"")) {
                    Elements genres = entry.select("a[itemprop='genre']");
                    genre = "";
                    for (Element g : genres){
                        genre += g.text().trim() + " ";
                    }
                } else if (ee.contains("Жанры:")){
                    String gnr = ee.split("Жанры:")[1];
                    if (gnr.contains("text-item\">")) {
                        gnr = gnr.split("text-item\">")[1];
                        if (gnr.contains("<span")) genre = gnr.split("<span")[0]
                                .replace("\"", "").trim();
                    }

                }

                if (ee.contains("class=\"item translate\""))
                    translator = entry.select(".translate > .item-content").first().text().trim();
                else if (ee.contains("item count-movies"))
                    translator = entry.select(".item.count-movies").text().trim();
                translator = translator.replace(",", " ")
                        .replace("Всего фильмов:", "").trim();
                if (ee.contains("class=\"item actors\""))
                    actors = entry.select(".actors > .item-content").text().trim();
                if (ee.contains("class=\"item directors\""))
                    director = entry.select(".directors > .item-content").text().trim();
                if (ee.contains("class=\"item durarion\""))
                    time = entry.select(".durarion > .item-content").text().trim();
                type = "movie";
                if (ee.contains("class=\"added-info\"")) {
                    String inf = entry.select(".added-info").text().trim();
                    if (inf.contains(" сезон)")) {
                        type = "serial";
                        season = inf.split(" сезон\\)")[0].split("\\(")[1].trim();
                    }
                    if (season.contains("-")) {
                        season = season.split("-")[1].trim();
                    }
                    if (inf.contains(" серия")) {
                        type = "serial";
                        series = inf.split(" серия")[0].trim();
                    }
                    if (series.contains("-"))
                        series = series.split("-")[1].trim();
                }

                if (dd.contains("class=\"slider-item\"") && ee.contains("class=\"fancybox\"")){
                    img = entry.select(".fancybox").attr("href").trim();
                }

                if (url.contains("/anime/"))
                    type += " anime";
                if (!year.contains("error"))
                    name += " (" + year + ")";

                boolean hide = quality.toLowerCase().contains("ts") && Statics.hideTs;
//                Log.e("test", "loadDone: "+name+hide );
                if (!name.contains("error") && !hide)
                    itemSet();
                if (entry.className().contains("fullstory news"))
                    break;
            }

            if (dd.contains("class=\"frames-list\"")) {
                Elements allImg = data.select(".frames-list li");
                for (Element preimg : allImg) {
                    String img = preimg.select("a").first().attr("href");
                    if (Statics.FILMIX_URL.startsWith("http://cameleo.xyz/r?url="))
                        img = img.replace("/","%2F");
                    itempath.setPreImg(Statics.FILMIX_URL + img);
                }
            }
//            Log.e("test", "Getdata 13: "+url );

            if (dd.contains("class=\"slider-item\"")){
                Elements allMore = data.select(".slider-item");
                for (Element entry : allMore) {
                    defMore();
                    moreurl = entry.select("a").first().attr("href").trim();
                    moretitle = entry.select(".film-name").text().trim();
                    moreimg = entry.select("img").first().attr("src");

                    if (!moreurl.isEmpty()) {
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

            if (Statics.rateImdb) {
                String ids = "";
                for (String cur : itempath.url) {
                    if (cur.contains("/"))
                        cur = cur.split("/")[cur.split("/").length - 1];
                    if (cur.contains("-"))
                        cur = cur.split("-")[0];
                    ids += "  " + cur;
                }
                ids = ids.trim().replace("  ", "-");
                setRating(GetdataKovalen("http://kovalen.ru/filmix/?list=" + ids));

//                Log.e("qwe", "setRating: " + ids );
            }

        }

    }

    private void setRating(String data) {
        if (!data.isEmpty()) {
            for (int j = 0; j < itempath.url.size() - 1; j++) {
                String i = itempath.getUrl(j);
                if (i.contains("/"))
                    i = i.split("/")[i.split("/").length - 1];
                if (i.contains("-"))
                    i = i.split("-")[0];

                if (data.contains("\"" + i + "\":{")) {
                    String r = data.split("\"" + i + "\":\\{")[1].split("\\}")[0];
                    if (!r.contains("imdb\":0") && r.contains("imdb\":")) {
                        r = r.split("imdb\":")[1].replace("}", "").trim();
                    } else if (!r.contains("kp\":0") && r.contains("kp\":"))
                        r = r.split("kp\":")[1].replace("}", "").trim();
                    if (r.contains(","))
                        r = r.split(",")[0];
                    r = r.replace("\"","")
                            .replace(":","")
                            .replace("kp","")
                            .replace("imdb","").trim();
                    if (r.equals("0") || r.equals(""))
                        r = itempath.getRating(j);
                    itempath.rating.set(j, r);
                }
            }
        }
    }

    private Document GetDataSearch(){
        String url = Statics.FILMIX_URL + "/engine/ajax/sphinx_search.php";
        String search;
        if (url.startsWith("http://cameleo.xyz/r?url=")) {
            url = "http://cameleo.xyz/r?url=" +
                    url.split("r\\?url=")[1].replace("/", "%2F");
            search = "%2Fsearch%2F";
        } else search = "/search/";
        try {
            if (Statics.ProxyUse.contains("filmix") && Statics.ProxyCur.contains(":") && !Statics.ProxyCur.contains("адрес:порт")){
                System.setProperty("http.proxyHost", Statics.ProxyCur.split(":")[0].trim());
                System.setProperty("http.proxyPort", Statics.ProxyCur.split(":")[1].trim());
            } else {
                System.clearProperty("http.proxyHost");
                System.clearProperty("http.proxyPort");
            }
            Connection.Response res = Jsoup
                    .connect(Statics.FILMIX_URL + search)
                    .execute();
            String loginCookies = res.cookies().toString().replace("{","")
                    .replace("}","")
                    .replace("dle_user_id=deleted","")
                    .replace("dle_password=deleted","")
                    .replace("dle_hash=deleted","")
                    .replace("remember_me=deleted","")
                    .trim()+", "+Statics.FILMIX_COOCKIE+";";
            loginCookies = loginCookies.replace(" , ","").replace(",,","")
                    .replace(",",";");
            Log.e("test coockie", "GetDataSearch: "+loginCookies);
            Document htmlDoc = Jsoup.connect(url)
                    .data("scf", "fx")
                    .data("story", ItemMain.xs_search)
                    .data("search_start", ItemMain.xs_value)
                    .header("Cookie", loginCookies)
//                    .header("Cookie", loginCookies.replace("per_page_news=15", "per_page_news=60"))
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false)
                    .timeout(30000).ignoreContentType(true).post();
            return htmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Document Getdata(String url) {
        try {
            String loginCookies = Statics.FILMIX_COOCKIE.replace(" , ", "")
                    .replace(",,", "")
                    .replace(",", ";") + ";";
//            Log.e("test", "Getdata 0: "+url );
            if (ItemMain.xs_field.isEmpty() || ItemMain.xs_field.contains("error")) {
                return Jsoup.connect(url)
                        .header("X-Requested-With", "XMLHttpRequest")
                        .header("Cookie", loginCookies)
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .validateTLSCertificates(false)
                        .timeout(30000).ignoreContentType(true).get();
            } else {
                Log.e("test", "Getdata test: "+url +ItemMain.xs_field);
                //.data("do","search")
                //.data("subaction","search")
                //.data("story", ItemMain.xs_search)
                Connection.Response res = Jsoup
                        .connect(Statics.FILMIX_URL + "/films")
                        .method(Connection.Method.POST)
                        .data("dlenewssortby", ItemMain.xs_field)
                        .data("set_new_sort","dle_sort_cat")
                        .data("set_direction_sort","dle_direction_cat")
                        .data("dledirection","desc")
                        .header("X-Requested-With", "XMLHttpRequest")
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .execute();
//                String cookies = res.cookies().toString().replace("{","")
//                        .replace("}","")
//                        .replace("dle_user_id=deleted","")
//                        .replace("dle_password=deleted","")
//                        .replace("dle_hash=deleted","")
//                        .replace("remember_me=deleted","")
//                        .trim()+", "+Statics.FILMIX_COOCKIE+";";
//                cookies = cookies.replace(" , ","").replace(",,","")
//                        .replace(",",";");
                return Jsoup.connect(url)
                        .header("X-Requested-With", "XMLHttpRequest")
//                        .header("Cookie", res.cookies())
                        .cookies(res.cookies())
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .validateTLSCertificates(false)
                        .timeout(30000).ignoreContentType(true).get();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String GetdataKovalen(String url) {
        try {
            return Jsoup.connect(url)
                    .timeout(30000).ignoreContentType(true).get().text();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    private Document GetdataTiar(String url) {
        try {
            return Jsoup.connect("http://tiardev.ru/parse/filmix_data.php?url="+
                    url.replace(Statics.FILMIX_URL, "https://filmix.co"))
                    .timeout(30000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
