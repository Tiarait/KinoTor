package com.kinotor.tiar.kinotor.parser.catalog;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskCallback;
import com.kinotor.tiar.kinotor.utils.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

/**
 * Created by Tiar on 08.2018.
 */
public class ParserKinopoisk extends AsyncTask<Void, Void, Void> {
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


    public ParserKinopoisk(String url, ArrayList<ItemHtml> items, ItemHtml itempath, OnTaskCallback callback) {
        Log.e("test", "ParserFilmix: "+url );
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
        if (url.contains("film/")) {
            url = url.split("film/")[1];
            if (url.contains("/")) {
                url = url.split("/")[0];
                if (url.contains("-")) {
                    url = url.split("-")[url.split("-").length - 1];
                }
            }
        }

        ParseHtmlJson(GetdataTiar(url));
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

    private void itemSet(){
//        Log.e("test", "loadDone: "+name );
        if (url_entry.trim().isEmpty())
            url_entry = this.url;

        if (subname.trim().isEmpty())
            subname = "error";
        if (!name.contains("error")) {
//            Log.e("test", "itemSet: "+url_entry);

            itempath.setUrl(url_entry);
            itempath.setTitle(Utils.unicodeToString(name));
            itempath.setImg(img);
            itempath.setSubTitle(subname);
            itempath.setQuality(quality);
            itempath.setVoice(translator);
            itempath.setRating(rating);
            itempath.setDescription(Utils.unicodeToString(description_t));
            itempath.setDate(year);
            itempath.setKpId(kpId);
            itempath.setCountry(Utils.unicodeToString(country));
            itempath.setGenre(Utils.unicodeToString(Utils.renGenre(genre)));
            itempath.setDirector(Utils.unicodeToString(director));
            Log.e("qwe", actors);
            Log.e("qwe", Utils.unicodeToString(actors));
            itempath.setActors(Utils.unicodeToString(actors));
            itempath.setTime(Utils.unicodeToString(time).replace("%/",""));
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

    private void ParseHtmlJson(Document data) {
        if (data != null) {
            String ee = data.text();
            defVal();
            if (ee.contains("\"id\":\"")) {
                url_entry = "https://www.kinopoisk.ru/film/"+url+"/";
                if (ee.contains("\"name_ru\":\""))
                    name = ee.split("\"name_ru\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"name_en\":\""))
                    subname = ee.split("\"name_en\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"year\":\""))
                    year = ee.split("\"year\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"country\":\""))
                    country = ee.split("\"country\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"description\":\""))
                    description_t = ee.split("\"description\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"poster_film_big\":\""))
                    img = ee.split("\"poster_film_big\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"genre\":\""))
                    genre = ee.split("\"genre\":\"")[1].split("\"")[0].trim();
                if (ee.contains("\"time_film\":\""))
                    time = ee.split("\"time_film\":\"")[1].split("\"")[0].trim();
                type = "movie";
                quality = "N/A";
                translator = "N/A";
                //----------------------------------------------------------------------------------
                if (ee.contains("\"actor\":[")) {
                    actors = "";
                    String actor = ee.split("\"actor\":\\[")[1].split("\\]")[0].trim();
                    if (actor.contains("},{")) {
                        for (String act : actor.split("\\},\\{")) {
                            if (act.contains("name_person_ru\":\""))
                                actors += act.split("name_person_ru\":\"")[1].split("\"")[0].trim() + " ";
                        }
                    }
                    actors = actors.trim().replace(" ", ", ");
                }
                if (ee.contains("\"director\":[")) {
                    director = "";
                    String actor = ee.split("\"director\":\\[")[1].split("\\]")[0].trim();
                    if (actor.contains("},{")) {
                        for (String act : actor.split("\\},\\{")) {
                            if (act.contains("name_person_ru\":\""))
                                director += act.split("name_person_ru\":\"")[1].split("\"")[0].trim() + " ";
                        }
                    }
                    director = director.trim().replace(" ", ", ");
                }
                //----------------------------------------------------------------------------------
                if (ee.contains("\"imdb\":\"")) {
                    if (rating.contains("error"))
                        rating = "IMDB[" + ee.split("\"imdb\":")[1].split(",")[0].trim() + "] ";
                    else
                        rating += "IMDB[" + ee.split("\"imdb\":")[1].split(",")[0].trim() + "] ";
                }
                if (ee.contains("\"kp_rating\":\"")) {
                    if (rating.contains("error"))
                        rating = "KP[" + ee.split("\"kp_rating\":")[1].split(",")[0].trim() + "] ";
                    else rating += "KP[" + ee.split("\"kp_rating\":")[1].split(",")[0].trim() + "] ";
                }
                rating = rating.replace("\"","").trim();
                //----------------------------------------------------------------------------------
                if (ee.contains("\"screen_film\":[")) {
                    String imgs = ee.split("\"screen_film\":\\[")[1].split("\\]")[0].trim();
                    if (imgs.contains("},{")) {
                        for (String preimg : imgs.split("\\},\\{")) {
                            if (preimg.contains("preview\":\""))
                                itempath.setPreImg(preimg.split("preview\":\"")[1].split("\"")[0].trim());
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


    private Document GetdataTiar(String url) {
        try {
            Log.e("kpip", "GetdataTiar: "+url);
            return Jsoup.connect("http://apivideo.ru/api/kinopoisk.json?token=037313259a17be837be3bd04a51bf678&id="+
                    url)
                    .timeout(30000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
