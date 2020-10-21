package com.kinotor.tiar.kinotor.parser.video.filmix;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemVideo;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.ui.DetailActivity;
import com.kinotor.tiar.kinotor.utils.OnTaskVideoCallback;
import com.kinotor.tiar.kinotor.utils.Utils;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Tiar on 19.02.2018.
 */

public class ParserFilmix extends AsyncTask<Void, Void, Void> {
    private String search_title, idCur;
    private ItemHtml itempath;
    private OnTaskVideoCallback callback;
    private ItemVideo items;

    private String title_m = "error";
    private String year_m = "error";
    private String season = "error";
    private String episode = "error";
    private String id = "error";
    private String trailer = "error";


    public ParserFilmix(ItemHtml item, OnTaskVideoCallback callback) {
        this.itempath = item;
        this.callback = callback;
        this.items = new ItemVideo();

        if (itempath.getTitle(0).contains("("))
            search_title = itempath.getTitle(0).split("\\(")[0].trim();
        else search_title = itempath.getTitle(0).trim();
        search_title = search_title.trim().replace("\u00a0", " ");
        idCur = item.getIframe(0).contains("error") ? null : item.getIframe(0);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(items);
    }

    @Override
    protected Void doInBackground(Void... voids) {
//        Log.e("test", "idCur: "+idCur);
        if (idCur != null && (DetailActivity.url.contains(Statics.FILMIX_URL) || DetailActivity.url.contains("filmix"))){
            String id = idCur.replace("http://", "");
            if (idCur.contains("[trailer]")) {
                trailer = id.split("\\[trailer\\]")[1];
                id = id.split("\\[trailer\\]")[0];
            }
            getUppod(GetDataUpod(id));
        } else {
            SearchListSuggest(GetDataSuggest(search_title));
//        SearchList(GetData(search_title));
        }
        return null;
    }

    private void SearchListSuggest(String doc) {
        if (doc != null) {
//            Log.e("test", "SearchListSuggest: "+doc );
            if (doc.contains("{\"id\":")){
                String[] list = doc.split("\\{\"id\":");
                for (String cur : list){
                    title_m = "error";
                    String title_eng_m;
                    year_m = "error";
                    season = "error";
                    episode = "error";
                    id = "error";

                    id = cur.split(",")[0].replace("\"","");
                    if (cur.contains("\"title\":\"")) {
                        title_m = cur.split("\"title\":\"")[1].split("\"")[0];
                        title_eng_m = cur.split("\"original_name\":\"")[1].split("\"")[0];
                        year_m = cur.split("\"year\":")[1].split(",")[0].replace("\"","");;

                        title_m = Utils.unicodeToString(title_m).replace("\u00a0", " ");
                        title_eng_m = Utils.unicodeToString(title_eng_m).replace("\u00a0", " ");

                        boolean y = year_m.contains("error") ||
                                itempath.getDate(0).trim().contains("error") ||
                                year_m.trim().contains(itempath.getDate(0).trim());

//                        Log.e("test", "SearchList:" + title_m.toLowerCase() + "|" + search_title.toLowerCase().trim());
//                        Log.e("test", "SearchList:" + title_eng_m.toLowerCase() + "|" + itempath.getSubTitle(0)
//                                .toLowerCase().replace("\u00a0", " ").trim());
//                        Log.e("test", "SearchListSuggest: " + id + "|" + year_m + "|" + itempath.getDate(0).trim());
                        if (title_m.toLowerCase().equals(search_title.toLowerCase().trim()) ||
                                title_eng_m.toLowerCase().equals(itempath.getSubTitle(0)
                                        .toLowerCase().replace("\u00a0", " ").trim())) {
//                            Log.e(TAG, "SearchListSuggest: true");
                            if (itempath.getSeason(0) != 0 && !id.contains("error")) {
                                if (!cur.contains("last_serie\":\"\""))
//                                        && !cur.contains("anime") &&
//                                        !cur.contains("\\u0410\\u043d\\u0438\\u043c\\u0435"))
                                    getUppod(GetDataUpod(id));
                            } else if (y && !id.contains("error")) {
                                getUppod(GetDataUpod(id));
                            }
                        }
                    }
                }
            }
        }
    }

//    private void SearchList(Document doc) {
//        if (doc != null) {
//            if (items == null) items = new ItemVideo();
////            Log.e(TAG, "SearchList: 0" + doc.html());
//            if (doc.html().contains("article")){
//                Elements allEntries = doc.select("article");
////                Log.e(TAG, "SearchList: 1");
//                for (Element entry : allEntries) {
//                    title_m = "error";
//                    String title_eng_m = "error";
//                    year_m = "error";
//                    season = "error";
//                    episode = "error";
//                    id = "error";
//
//                    if (entry.html().contains("class=\"name")) {
//                        title_m = entry.select(".name").attr("content").trim();
//                    }
//                    if (entry.html().contains("class=\"origin-name"))
//                        title_eng_m = entry.select(".origin-name").attr("content").trim();
//
////                    Log.e(TAG, "SearchList:" +title_m.toLowerCase() +"|"+search_title.toLowerCase().trim());
////                    Log.e(TAG, "SearchList:" +title_eng_m.toLowerCase() +"|"+itempath.getSubTitle(0).toLowerCase().trim());
//
//                    if (title_m.toLowerCase().equals(search_title.toLowerCase().trim())
//                            || title_eng_m.toLowerCase().equals(itempath.getSubTitle(0).toLowerCase().trim())) {
//                        if (entry.html().contains("class=\"like"))
//                            id = entry.select(".like").attr("data-id").trim();
//                        if (entry.html().contains("class=\"item year"))
//                            year_m = entry.select(".item.year").text().trim()
//                                    .replace("Год:", "");
//                        if (entry.html().contains("class=\"added-info")) {
//                            String added = entry.select(".added-info").text().trim();
//                            if (added.contains(" серия"))
//                                episode = added.split(" серия")[0];
//                            if (episode.contains("-"))
//                                episode = episode.split("-")[1];
//                            if (added.contains(" сезон)") & added.contains("("))
//                                season = added.split(" сезон\\)")[0].split("\\(")[1];
//                            if (season.contains("-"))
//                                season = season.split("-")[1];
//                        }
////                        Log.e(TAG, "SearchList:" +year_m +"|"+itempath.getDate(0).trim()+"|"+id);
//                        if (DetailActivity.url.contains(Statics.COLDFILM_URL) ||
//                                DetailActivity.url.contains(Statics.KOSHARA_URL) &&
//                                        itempath.getSeason(0) != 0 && !id.contains("error")){
//                            getUppod(GetDataUpod(id));
//                        } else if (!year_m.contains("error") &&
//                                year_m.trim().contains(itempath.getDate(0).trim()) &&
//                                !id.contains("error")) {
//                            getUppod(GetDataUpod(id));
//                        }
//                    }
//                }
//            }
//        }
//    }

    private void getUppod(Document doc){
        if (doc != null) {
//            Log.e("test", "getUppod0: " + doc.text());
            if (doc.text().contains("trailers\":{")) {
                String uppods = doc.text().split("trailers\":\\{")[1]
                        .split("\\},")[0];
                if (uppods.contains(":\"")) {
                    String url = uppods.split(":\"")[1].split("\"")[0].replace("\"", "").trim();
                    trailer = Utils.decodeUppod(url);
                }
            }
            if (doc.text().contains("translations\":{\"flash\":{")) {
                String uppods = doc.text().split("translations\":\\{\"flash\":\\{")[1]
                        .split("\\},")[0];
                Log.e("ParserFilmix", "getUppod: "+uppods );
                if (uppods.contains("\",\"")) {
                    String[] uppodArr = uppods.split("\",\"");
                    for (String uppod : uppodArr) {
                        if (uppod.contains("\":\"")) {
                            String trans = uppod.split("\":\"")[0].replace("\"", "").trim();
                            String url = uppod.split("\":\"")[1].replace("\"", "").trim();
                            if (url.contains("}"))
                                url = url.split("\\}")[0].trim();
                            url = Utils.decodeUppod(url);
//                            Log.e("test", "getUppod2: "+url );
                            if (url.contains(".txt")) {
                                getUppodSerial(url, trans);
                            } else
                                addItem(Utils.unicodeToString(trans).trim(), url);
                        }
                    }
                } else if (uppods.contains("\":\"")) {
                    String trans = uppods.split("\":\"")[0].replace("\"", "").trim();
                    String url = uppods.split("\":\"")[1].replace("\"", "").trim();
                    url = Utils.decodeUppod(url);
//                        Log.e("test", "getUppod3: "+url );

                    if (url.contains(".txt")) {
                        getUppodSerial(url, trans);
                    } else
                        addItem(Utils.unicodeToString(trans).trim(), url);
                }
            } else if (doc.text().contains("translations\":{\"video\":{")) {
                String uppods = doc.text().split("translations\":\\{\"video\":\\{")[1]
                        .split("\\},")[0];
//                Log.e("test", "getUppod01: " + uppods);
                if (uppods.contains("\",\"")) {
                    String[] uppodArr = uppods.split("\",\"");
                    for (String uppod : uppodArr) {
                        if (uppod.contains("\":\"")) {
                            String trans = uppod.split("\":\"")[0].replace("\"", "").trim();
                            String url = uppod.split("\":\"")[1].replace("\"", "").trim();
                            if (url.contains("}"))
                                url = url.split("\\}")[0].trim();
//                            Log.e("test", "getUppod2: " + url);

                            url = Utils.decodeUppod(url);

//                            Log.e("test", "getUppod2: " + url);
                            if (url.contains(".txt")) getUppodSerial(url, trans);
                            else addItem(Utils.unicodeToString(trans).trim(), url);
                        }
                    }
                } else if (uppods.contains("\":\"")) {
                    String trans = uppods.split("\":\"")[0].replace("\"", "").trim();
                    String url = uppods.split("\":\"")[1].replace("\"", "").trim();
//                    Log.e("test", "getUppod3: " + url);

                    url = Utils.decodeUppod(url);

//                    Log.e("test", "getUppod3: " + url);

                    if (url.contains(".txt")) getUppodSerial(url, trans);
                    else addItem(Utils.unicodeToString(trans).trim(), url);
                }
            }
        }
    }

    private void getUppodSerial(String url, String trans){
        Document dTxt = GetDataTxt(url);
//        Log.e("test", "getSerial0: "+url );
        if (dTxt != null){
            String text = Utils.decodeUppod(dTxt.text());
            String t2 = text.replace(" ", "");
//            Log.e("test", "getSerial: "+t2 );
            if (t2.contains("title\":\"Сезон")) {
                String lastS = t2.split("title\":\"Сезон")[t2.split("title\":\"Сезон").length-1];
                season = lastS.split("\"")[0];
                if (lastS.contains("title\":\"Серия"))
                    episode = lastS.split("title\":\"Серия")[lastS.split("title\":\"Серия").length-1]
                            .split("\\(")[0];
            } else {
                season = "1";
                if (t2.contains("title\":\"Серия"))
                    episode = t2.split("title\":\"Серия")[t2.split("title\":\"Серия").length-1]
                            .split("\"")[0];
            }
            addItem(Utils.unicodeToString(trans).trim(), text);
        }
    }

    private void addItem (String trans, String url) {
//        Log.e(TAG, "addItem: +" );
        String qual = "";

        if (trans.contains("[")){
            for (String q : trans.split("\\[")){
                qual += q.trim() + " ";
            }
            qual = qual.replace("]", "").trim();
            trans = trans.split("\\[")[0].trim();
        }

        if (title_m.contains("error") || title_m.trim().isEmpty()) title_m = "";
        else title_m = title_m + " ";
        if (year_m.contains("error") || year_m.trim().isEmpty()) year_m = "";
        else year_m = year_m + " ";

        String type = title_m + year_m + qual;
        String tr = trailer.contains("error") ? "" : " [+trailer]";
//        if (!trailer.startsWith("http:"))
//            trailer = "http://" + trailer;
//        else if (trailer.startsWith("http:") && !trailer.startsWith("http://"))
//            trailer = "http://" + trailer.replace("http:", "");

        if (season.equals("error")) items.setTitle("catalog video");
        else items.setTitle("catalog serial");
        if (!type.trim().isEmpty())
            items.setType( type.trim() + "\nfilmix" + tr);
        else items.setType( "filmix" + tr);
        items.setToken("null");
        items.setId_trans("null");
        items.setId(id);
        items.setUrl(url);
        items.setUrlTrailer(trailer);
//        items.setUrlSite("error");
        items.setSeason(season.trim());
        items.setEpisode(episode.trim());
        items.setTranslator(trans);
//        Log.d(TAG, "Filmix: " + trans + " add " + year_m);
    }

    private String GetDataSuggest(String s){
//        Log.d(TAG, "GetDataFilmix: "+ s.trim() +"/"+ itempath.getSubTitle(0));
        String url = Statics.FILMIX_URL + "/api/v2/suggestions?search_word="+s.trim();
        if (url.startsWith("http://cameleo.xyz/r?url="))
            url = "http://cameleo.xyz/r?url=" +
                    url.split("r\\?url=")[1].replace("/","%2F");
        try {
            Document htmlDoc = Jsoup.connect(url)
                    .data("search_word", s.trim())
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false)
                    .timeout(30000).ignoreContentType(true).get();
            return htmlDoc.text();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Document GetDataUpod(String s){
//        Log.e("test", "GetDataUpod: "+ s.trim());
        String url = Statics.FILMIX_URL + "/api/movies/player_data";
        if (url.startsWith("http://cameleo.xyz/r?url="))
            url = "http://cameleo.xyz/r?url=" +
                    url.split("r\\?url=")[1].replace("/","%2F");
        try {
            Connection.Response res = Jsoup
                    .connect(Statics.FILMIX_URL + "/search/")
                    .execute();
            String loginCookies = res.cookies().toString().replace("{","")
                    .replace("}","")
                    .replace("dle_user_id=deleted","")
                    .replace("dle_password=deleted","")
                    .replace("dle_hash=deleted","")
                    .replace("remember_me=deleted","").trim()+", "+Statics.FILMIX_COOCKIE+";";
            loginCookies = loginCookies.replace(" , ","").replace(",,","")
                    .replace(",",";");
//            Log.e("test coockie", "GetDataSearch: "+loginCookies);

            return Jsoup.connect(url)
                    .data("post_id", s.trim())
                    .data("showfull", "true")
                    .header("Cookie", loginCookies)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false)
                    .timeout(5000).ignoreContentType(true).post();
        } catch (Exception e) {
            e.printStackTrace();
//            Log.e(TAG, "GetDataUpod: "+ s.trim());
            return null;
        }
    }

    private Document GetDataTxt(String s){
        try {
//            Log.d(TAG, "GetdataFilmix: get connected to " + s);

            return Jsoup.connect(s)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .validateTLSCertificates(false)
                    .timeout(5000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
