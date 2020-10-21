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
public class ParserKinolive extends AsyncTask<Void, Void, Void> {
    private String url;
    private ArrayList<ItemHtml> items;
    private ItemHtml itempath;
    private OnTaskCallback callback;

    private String url_entry = "error", img = "error", kpId = "error",
            quality = "error", rating = "error";
    private String name = "error", subname = "error", year = "error", country = "error",
            genre = "error", time = "error", translator = "error",
            director = "error", actors = "error", description_t = "error",
            iframe = "error", type = "error";
    private String moretitle = "error", moreurl = "error", moreimg = "error", moreseason = "0",
            moreseries = "0", morequality = "error";
    private String season = "0", series = "0";


    public ParserKinolive(String url, ArrayList<ItemHtml> items, ItemHtml itempath, OnTaskCallback callback) {
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
        if (url.contains("index.php?do=search"))
            ParseHtml(GetdataSearch(url));
        else ParseHtml(Getdata(url));
        return null;
    }

    private void defVal(){
        url_entry = "error";
        img = "error";
        quality = "error";
        rating = "error";
        name = "error";
        subname = "error";
        year = "error";
        kpId = "error";
        country = "error";
        genre = "error";
        time = "error";
        translator = "error";
        director = "error";
        actors = "error";
        description_t = "error";
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
                e.printStackTrace();
                itempath.setSeason(0);
                itempath.setSeries(0);
            }
            items.add(itempath);
        }
    }

    private void ParseHtml(Document data) {
        if (data != null) {
            String dataHtml = data.body().html();
//            Log.d("qwe", "ParseHtml" + data.html());

            if (url.endsWith(".html") && data.html().contains("id=\"dle-content\"")) {
                defVal();
                Element entry = data.selectFirst("#dle-content");
                dataHtml = entry.html();
                if (dataHtml.contains("<h1")) {
                    name = data.select("h1").text().replace("HD","").trim();
                    url_entry = this.url;
                }
                if (dataHtml.contains("<a")) {
                    year = data.selectFirst("a").text().trim();
                }
                if (dataHtml.contains("class=\"ah4\"")) {
                    genre = data.selectFirst(".ah4").text().trim();
                    if (genre.contains(":"))
                        genre = genre.split(":")[1].trim();
                    if (genre.contains("|"))
                        genre = genre.split("\\|")[0].trim();
                }
                if (dataHtml.contains("class=\"tezt\"")) {
                    Element all = data.selectFirst(".tezt");
                    String allHtml = all.html();
                    if (allHtml.contains("<img")) {
                        img = all.selectFirst("img").attr("src").trim();
                        if (img.startsWith("/"))
                            img = Statics.KINOLIVE_URL + img;
                    }
                    if (allHtml.contains("<!--TEnd-->")) {
                        description_t = allHtml.split("<!--TEnd-->")[1].trim();
                    } else if (allHtml.contains("a>")) {
                        description_t = allHtml.split("a>")[1].trim();
                    } else if (allHtml.contains("br>")) {
                        description_t = allHtml.split("br>")[1].trim();
                    }
                    if (description_t.contains("colorend-->")) {
                        description_t = description_t.split("<br>")[1].trim();
                    }
                    if (description_t.contains("<br")) {
                        description_t = description_t.split("<br")[0].trim();
                    }
                    description_t = description_t.replace("<","")
                            .replace(">","").trim();
                    parseQual(allHtml);
                    if (allHtml.contains("Оригинальное название: ")) {
                        subname = allHtml.split("Оригинальное название: ")[1].trim();
                        if (subname.contains("<br")) {
                            subname = subname.split("<br")[0].trim();
                        }
                    }
                    if (allHtml.contains("В ролях: ")) {
                        actors = allHtml.split("В ролях: ")[1].trim();
                        if (actors.contains("<br")) {
                            actors = actors.split("<br")[0].trim();
                        }
                    }
                    if (allHtml.contains("Год выпуска: ")) {
                        year = allHtml.split("Год выпуска: ")[1].trim();
                        if (year.contains("<br")) {
                            year = year.split("<br")[0].trim();
                        }
                    }
                    if (allHtml.contains("Год: ")) {
                        year = allHtml.split("Год: ")[1].trim();
                        if (year.contains("<br")) {
                            year = year.split("<br")[0].trim();
                        }
                    }
                    if (allHtml.contains("Жанр: ")) {
                        if (genre.contains("error"))
                            genre = allHtml.split("Жанр: ")[1].trim();
//                        else if (!genre.contains(allHtml.split("Жанр: ")[1].trim()))
//                            genre += ", " + allHtml.split("Жанр: ")[1].trim();
                        if (genre.contains("<br")) {
                            genre = genre.split("<br")[0].trim();
                        }
                    }
                    if (allHtml.contains("Режиссер: ")) {
                        director = allHtml.split("Режиссер: ")[1].trim();
                        if (director.contains("<br")) {
                            director = director.split("<br")[0].trim();
                        }
                    }
                    if (allHtml.contains("Перевод: ")) {
                        translator = allHtml.split("Перевод: ")[1].trim();
                        if (translator.contains("<br")) {
                            translator = translator.split("<br")[0].trim();
                        }
                    }
                    if (allHtml.contains("Страна: ")) {
                        country = allHtml.split("Страна: ")[1].trim();
                        if (country.contains("<br")) {
                            country = country.split("<br")[0].trim();
                        }
                    }
                    if (allHtml.contains("Продолжительность: ")) {
                        time = allHtml.split("Продолжительность: ")[1].trim();
                        if (time.contains("/")) {
                            time = time.split("/")[0].trim();
                        }
                        if (time.contains("<br")) {
                            time = time.split("<br")[0].trim();
                        }
                    }
                    if (allHtml.contains("imdb: ")) {
                        if (rating.contains("error"))
                            rating = "IMDB[" + allHtml.split("imdb: ")[1].split(" ")[0].trim() + "] ";
                        else rating += " IMDB[" + allHtml.split("imdb: ")[1].split(" ")[0].trim() + "] ";
                    }
                    if (allHtml.contains("Кинопоиск: ")) {
                        if (rating.contains("error"))
                            rating = "KP[" + allHtml.split("Кинопоиск: ")[1].split(" ")[0].trim();
                        else
                            rating += " KP[" + allHtml.split("Кинопоиск: ")[1].split(" ")[0].trim();
                    }
                }
                type = "movie";
                parseSeason(dataHtml);

                if (year.contains("-"))
                    year = year.split("-")[0];
                if (name.contains("("))
                    name = name.split("\\(")[0].trim();

                if (!year.contains("error"))
                    name += " (" + year + ")";

                if (!name.contains("error"))
                    itemSet();
            } else if (url.contains("index.php?do=search") && dataHtml.contains("<div class=\"line\">")) {
                String[] allEntries = data.select("#dle-content").html().split("<div class=\"line\">");
                for (String entry : allEntries) {
                    parseSearch(entry);
                }
            } else if (dataHtml.contains("dle-content") && dataHtml.contains("<div class=\"line\">")) {
//                Log.e("qwe", "ParseHtml: dle-content");
                String[] allEntries = data.select("#dle-content").html().split("<div class=\"line\">");
                for (String entry : allEntries) {
                    parseMain(entry);
                }
            } else if (dataHtml.contains("div class=\"spopup\"")) {
//                Log.e("qwe", "ParseHtml: spopup");
                Elements allEntries = data.select("td");
                for (Element entry : allEntries) {
                    parseFilm(entry);
                }
            }
//            else
//                Log.e("qwe", "ParseHtml: " + dataHtml);
        }

    }
    private void parseFilm(Element entrys) {
        defVal();
        type = "movie";
//        String entry = entrys.html();
        if (entrys.html().contains("<a")) {
            url_entry = entrys.selectFirst("a").attr("href").trim();
            name = entrys.selectFirst("a").text()
                    .replace(" в новом окне", "")
                    .replace("Смотреть ","").trim();
        }
        if (entrys.html().contains("<img"))
            img = entrys.selectFirst("img").attr("src").trim();
        if (img.startsWith("/"))
            img = Statics.KINOLIVE_URL + img;
        if (entrys.html().contains("<div class=\"spopup\">")) {
            Element e = entrys.selectFirst("div.spopup");
            String en = e.html();
            if (en.contains("<h1"))
                year = e.selectFirst("h1 a").text().trim();
            if (en.contains("<h2"))
                genre = e.selectFirst("h2").text().trim();
            if (en.contains("<p")) {
                String entry = e.selectFirst("p").text().trim();

                parseQual(entry);
                if (entry.contains("Перевод: "))
                    translator = entry.split("Перевод: ")[1].split("<br")[0]
                            .replace("\"","").trim();
                if (entry.contains("Кинопоиск: "))
                    rating = entry.split("Кинопоиск: ")[1].split(" ")[0].trim();
                else if (entry.contains("imdb: "))
                    rating = entry.split("imdb: ")[1].split(" ")[0].trim();
            }
        }

        genre = genre.replace("hd 1080","")
                .replace(", ","  ").replace(",","  ").trim()
                .replace("  ",", ").trim();

        if (name.contains("("))
            name = name.split("\\(")[0].trim();

        if (!year.contains("error"))
            name += " (" + year + ")";

        boolean hide = quality.toLowerCase().contains("ts") && Statics.hideTs;
        if (!name.contains("error") && !hide)
            itemSet();
    }
    private void parseMain(String entry) {
        defVal();
        if (entry.contains("<h1><a href=\"")) {
            url_entry = entry.split("<h1><a href=\"")[1].split("\"")[0].trim();
        }
        if (entry.contains(url_entry + "\">")) {
            name = entry.split(url_entry + "\">")[1].split("</a>")[0].trim();
        }
        if (name.contains("/"))
            name = name.split("/")[0].trim();
        if (entry.contains("<img src=\""))
            img = entry.split("<img src=\"")[1].split("\"")[0].trim();
        if (img.startsWith("/"))
            img = Statics.KINOLIVE_URL + img;

        if (entry.contains("Год выпуска: "))
            year = entry.split("Год выпуска: ")[1].split("<br")[0].trim();
        else if (entry.contains("Год: "))
            year = entry.split("Год: ")[1].split("<br")[0].trim();
        else if (entry.contains("Год выхода: <a href="))
            year = entry.split("Год выхода: <a href=")[1].split(">")[1].split("<")[0].trim();
        if (year.contains("-"))
            year = year.split("-")[0].trim();

        if (entry.contains("class=\"ah3\">Категория: "))
            genre = entry.split("class=\"ah3\">Категория: ")[1].split("<")[0].trim();
        else if (entry.contains("<h2>")) {
            String h2 = entry.split("<h2>")[1].split("</h2>")[0].trim();
            if (entry.contains(",")) {
                genre = "";
                for (String hh : h2.split(",")) {
                    if (hh.contains(">"))
                        genre +=  "  " + hh.split(">")[1].split("<")[0].trim();
                }
            }
        } else if (entry.contains("<div class=\"ah3\">")) {
            String h2 = entry.split("<div class=\"ah3\">")[1].split("</div>")[0].trim();
            if (entry.contains(",")) {
                genre = "";
                for (String hh : h2.split(",")) {
                    if (hh.contains(">"))
                        genre +=  "  " + hh.split(">")[1].split("<")[0].trim();
                }
            }
        }

        parseQual(entry);

        genre = genre.replace("hd 1080","").trim();
        genre = genre.trim().replace("  ",", ").trim();
        if (entry.contains("Перевод: "))
            translator = entry.split("Перевод: ")[1].split("<br")[0].trim();
        if (entry.contains("Кинопоиск: "))
            rating = entry.split("Кинопоиск: ")[1].split(" ")[0].trim();
        else if (entry.contains("imdb: "))
            rating = entry.split("imdb: ")[1].split(" ")[0].trim();
        type = "movie";

        parseSeason(entry);

        if (name.contains("("))
            name = name.split("\\(")[0].trim();

        if (!year.contains("error"))
            name += " (" + year + ")";

        boolean hide = quality.toLowerCase().contains("ts") && Statics.hideTs;
        if (!name.contains("error") && !hide)
            itemSet();
    }
    private void parseSearch(String entry) {
        defVal();
        if (entry.contains("class=\"more\">")) {
            url_entry = entry.split("class=\"more\">")[1].split("</div")[0].trim();
            if (url_entry.contains("href=\""))
                url_entry = url_entry.split("href=\"")[1].split("\"")[0].trim();
        }
        if (entry.contains("<h1>")) {
            name = entry.split("<h1>")[1].split("<")[0].trim();
        }
        if (name.contains(": "))
            name = name.split(": ")[1].trim();
        if (entry.contains("<img src=\""))
            img = entry.split("<img src=\"")[1].split("\"")[0].trim();
        if (img.startsWith("/"))
            img = Statics.KINOLIVE_URL + img;

        if (entry.contains("Год выпуска: "))
            year = entry.split("Год выпуска: ")[1].split("<br")[0].trim();
        else if (entry.contains("Год: "))
            year = entry.split("Год: ")[1].split("<br")[0].trim();
        else if (entry.contains("Год выхода: <a href="))
            year = entry.split("Год выхода: <a href=")[1].split(">")[1].split("<")[0].trim();
        if (year.contains("-"))
            year = year.split("-")[0].trim();

        if (entry.contains("<h3>")) {
            String h2 = entry.split("<h3>")[1].split("</h3>")[0].trim();
            if (entry.contains(",")) {
                genre = "";
                for (String hh : h2.split(",")) {
                    if (hh.contains(">"))
                        genre +=  "  " + hh.split(">")[1].split("<")[0].trim();
                }
            }
        }
        parseQual(entry);
        genre = genre.replace("hd 1080","").trim();
        genre = genre.trim().replace("  ",", ").trim();
        if (entry.contains("Перевод: "))
            translator = entry.split("Перевод: ")[1].split("<br")[0].trim();
        if (entry.contains("Кинопоиск: "))
            rating = entry.split("Кинопоиск: ")[1].split(" ")[0].trim();
        else if (entry.contains("imdb: "))
            rating = entry.split("imdb: ")[1].split(" ")[0].trim();
        type = "movie";

        parseSeason(entry);

        if (name.contains("("))
            name = name.split("\\(")[0].trim();

        if (!year.contains("error"))
            name += " (" + year + ")";


        Log.e("kl2", "parseMain: "+name+"|"+url_entry);
        if (!name.contains("error"))
            itemSet();
    }
    private void parseSeason(String entry) {
        if (name.contains(" сезон")) {
            type = "serial";
            season = name.split(" сезон")[0].trim().split(" ")[
                    name.split(" сезон")[0].trim().split(" ").length-1].trim();
            season = season.replace("(","").trim();
            series = "0";
            if (name.contains(" серия") || name.contains(" серии") || name.contains(" серий")) {
                series = name.split(" сери")[0].trim().split(" ")[
                        name.split(" сери")[0].trim().split(" ").length-1].trim();
            } else if (entry.contains("class=\"tezt\"")) {
                String tezt = entry.split("class=\"tezt\"")[1]
                        .replace("<!--/colorstart-->"," ")
                        .replace("<!--dle_image_end-->"," ").trim();
                if (tezt.contains("Жанр: "))
                    genre = genre.replace("error","") + ", " + entry.split("Жанр: ")[1].split("<br")[0].trim();
                if (tezt.contains(" серия") || tezt.contains(" серии") || tezt.contains(" серий")) {
                    Log.e("kl2", "parseMain: 8");
                    series = tezt.split(" сери")[0].trim().split(" ")[
                            tezt.split(" сери")[0].trim().split(" ").length - 1].trim();
                }
            }
            name = name.replace("1-"+season,"")
                    .replace(season,"")
                    .replace("сезон","")
                    .replace("( )","")
                    .replace("()","").trim();
            if (season.contains("-"))
                season = season.split("-")[1];
        } else if (name.contains(" серия") || name.contains(" серии") || name.contains(" серий")) {
            season = "1";
            type = "serial";
            series = name.split(" сери")[0].trim().split(" ")[
                    name.split(" сери")[0].trim().split(" ").length-1].trim();
        } else if (entry.contains("class=\"tezt\"")) {
            String tezt = entry.split("class=\"tezt\"")[1]
                    .replace("<!--/colorstart-->"," ")
                    .replace("<!--dle_image_end-->"," ").trim();
            if (tezt.contains(" серия") || tezt.contains(" серии") || tezt.contains(" серий")) {
                season = "1";
                type = "serial";
                series = tezt.split(" сери")[0].trim().split(" ")[
                        tezt.split(" сери")[0].trim().split(" ").length - 1].trim();
            }
        }
        if (genre.toLowerCase().contains("сериал")) {
            if (season.contains("error") || season.equals("0"))
                season = "1";
            if (series.contains("error"))
                series = "0";
            type = "serial";
        }

        series = series.replace("(","").replace("<!--/colorstart-->","");
        if (series.contains("-"))
            series = series.split("-")[1].trim();
    }
    private void parseQual(String entry) {
        if (entry.contains("Качество: "))
            quality = entry.split("Качество: ")[1].split("<br")[0].trim();
        if (quality.toLowerCase().contains("web"))
            quality = "WEB";
        else if (quality.toLowerCase().contains("hd"))
            quality = "HD";
        else if (quality.toLowerCase().contains("bd"))
            quality = "BD";
        else if (genre.toLowerCase().contains("1080"))
            quality = "FHD";
        else if (quality.toLowerCase().contains(">ts"))
            quality = "TS";
        else if (quality.toLowerCase().contains("ts"))
            quality = "TS";
        else if (quality.toLowerCase().contains("sat"))
            quality = "SAT";
        else if (quality.toLowerCase().contains("dvd"))
            quality = "DVD";
    }

    private Document GetdataSearch(String url) {
        try {
            Document htmlDoc;
            String p = "1";
            if (url.contains("&search_start=")) {
                p = url.split("&search_start=")[1].trim();
                url = url.split("&search_start=")[0].trim();
            }
            htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .data("do","search")
                    .data("subaction","search")
                    .data("search_start", p)
                    .data("story", ItemMain.xs_search.replace("+"," "))
                    .ignoreContentType(true).post();
            return htmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private Document Getdata(String url) {
        try {
            Document htmlDoc;
            if (url.contains("ajax/pages.php")) {
                String p = "1";
//                Log.e("qwe", "Getdata: " + url );
                if (url.contains("&search_start=")) {
                    p = url.split("&search_start=")[1].trim();
                    url = url.split("&search_start=")[0].trim();
                }
                htmlDoc = Jsoup.connect(url)
                        .data("title", "block36")
                        .data("page", p)
                        .data("category","0")
                        .header("X-Requested-With","XMLHttpRequest")
                        .referrer(Statics.KINOLIVE_URL)
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .ignoreContentType(true).post();
            } else {
                htmlDoc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .ignoreContentType(true).get();
            }
            return htmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
