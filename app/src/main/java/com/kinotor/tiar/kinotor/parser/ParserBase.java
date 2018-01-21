package com.kinotor.tiar.kinotor.parser;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.kinotor.tiar.kinotor.R;
import com.kinotor.tiar.kinotor.items.ItemBase;
import com.kinotor.tiar.kinotor.items.ItemMain;
import com.kinotor.tiar.kinotor.ui.DetailActivity;
import com.kinotor.tiar.kinotor.utils.AdapterVideo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 13.10.2017.
 */

public class ParserBase extends AsyncTask<Void, Void, Void> {
    private String url = "error", season = "error", episode = "error",
            translator = "error";
    private String title, type, stat, id, id_trans, name;
    private String[] quality_arr, url_arr;
    @SuppressLint("StaticFieldLeak")
    private LinearLayout pb;
    private final String tokenHDGO = "2c4lbb21dje7yo7aysht52fj&k";
    private final String tokenMoonwalk = "997e626ac4d9ce453e6c920785db8f45";
    private ItemBase itemBase;
    private ArrayList<String> url_iframe;
    private SharedPreferences preference;
    private Set<String> pref_base;

    //for catalog
    public ParserBase(String name, String type, String stat) {
        itemBase = new ItemBase();
        this.name = name;
        this.type = type;
        this.stat = stat;
    }
    //for season
    public ParserBase(String stat, String type, String id, String id_trans) {
        itemBase = new ItemBase();
        this.stat = stat;
        this.type = type;
        this.id = id;
        this.id_trans = id_trans;
    }
    //for series
    public ParserBase(String stat, String name, String type, String id, String id_trans) {
        itemBase = new ItemBase();
        this.stat = stat;
        this.name = name;
        this.type = type;
        this.id = id;
        this.id_trans = id_trans;
    }
    //for site player
    public ParserBase(ArrayList<String> url_iframe, String stat, String translator, String type) {
        itemBase = new ItemBase();
        this.url_iframe = url_iframe;
        this.translator = translator;
        this.type = type;
        this.stat = stat;
    }
    //for url
    public ParserBase(String stat, String url) {
        this.stat = stat.split("\\.")[0];
        this.type = stat.split("\\.")[1];
        this.url = url;
    }

    @Override
    protected void onPreExecute() {
        HashSet<String> def = new HashSet<>();
        def.add("hdgo");
        def.add("moonwalk");
        preference = PreferenceManager.getDefaultSharedPreferences(DetailActivity.fragm_vid.getContext());
        pref_base = preference.getStringSet("base_video", def);
        pb = (LinearLayout) DetailActivity.fragm_vid.findViewById(R.id.vid_pb);
        pb.setVisibility(View.VISIBLE);
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (!stat.contains("url")) {
            RecyclerView rv = DetailActivity.fragm_vid.findViewById(R.id.catlog_video_list);
            rv.setAdapter(new AdapterVideo(itemBase));
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailActivity.fragm_vid.getContext(), 2);
            builder.setTitle("Выберите качество").setItems(quality_arr, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (type.contains("download")){
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(url_arr[i]));
                        DetailActivity.fragm_vid.getContext().startActivity(intent);
                        Log.d("download", DetailActivity.activity.getTitle() + " " + url_arr[i]);
                    }
                    if (type.contains("play")){
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse(url_arr[i]),
                                "video/mp4");
                        if (type.split("name=")[1].contains("film"))
                            intent.putExtra("title", DetailActivity.activity.getTitle());
                        else intent.putExtra("title", DetailActivity.activity.getTitle() + "/" +
                                type.split("name=")[1] + " серия");
                        DetailActivity.fragm_vid.getContext().startActivity(intent);
                        Log.d("play", DetailActivity.activity.getTitle() + " " + url_arr[i]);
                    }
                }
            });
            builder.create().show();
        }
        pb.setVisibility(View.GONE);
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... voids) {

        if (!stat.contains("url"))
            ItemMain.stat = stat;
        if (stat.equals("catalog")) {
            if (!DetailActivity.iframe.contains("error") && DetailActivity.iframe != null){
                if (DetailActivity.iframe.contains("hdgo"))
                    siteIframeHdgo(DetailActivity.iframe);
            }
            if (pref_base.contains("hdgo"))
                ParseHtmlHDGO(GetdataHDGO(name));
            if (pref_base.contains("moonwalk"))
                ParseHtmlMoonwalk(GetdataMoonwalk(name));
        } else if (stat.contains("season")) {
            if (type.contains("hdgo"))
                ParseSeason(GetSeasonHDGO(id), "hdgo");
            else if (type.contains("moonwalk")) ParseSeason(GetSeasonMoonwalk(id, id_trans), "moonwalk");
        } else if (stat.contains("series")) {
            if (type.contains("hdgo"))
                ParseSeries(GetSeasonHDGO(id), "hdgo", name);
            else if (type.contains("moonwalk")) ParseSeries(GetSeasonMoonwalk(id, id_trans), "moonwalk", name);
            else siteSeries(url_iframe);
        } else if (stat.contains("url")) {
            if (url.contains("hdgo.cc") || url.contains("ref=koshara"))
                ParseHdgoMp4(url);
            else ParseMoonwalkMp4(url);
        }
        return null;
    }

    private void ParseMoonwalkMp4(String url) {
        final ArrayList<String> q = new ArrayList<String>();
        final ArrayList<String> u = new ArrayList<String>();
        Document iframe = GetdataMoonwalkIframe(url);
        if (iframe.body().text().contains("недоступен")) {
            Log.d(TAG, "ParseMoonwalkMp4: видео недоступно");
            q.add("error m3u8");
            u.add("error m3u8");
        }else {
            //token
            String token = iframe.select("title").first().text().split(" ")[1];
            Log.d("mydebug", "token :" + token);
            //mw_pid
            String mw_pid = iframe.html().split("partner_id: ")[1].split(",")[0];
            Log.d("post manifests", "mw_pid :" + mw_pid);
            //p_domain_id
            String p_domain_id = iframe.html().split("domain_id: ")[1].split(",")[0];
            Log.d("post manifests", "p_domain_id :" + p_domain_id);

            Document js_data = GetdataMoonwalkIframe("http://moonwalk.cc" + iframe.html().split("script src=\"")[1].split("\"")[0]);
            //mw_key
            String mw_key = js_data.html().split("mw_key:\"")[1].split("\"")[0];
            Log.d("post manifests", "mw_key :" + mw_key);
            //iframe_version
            String iframe_version = js_data.html().split("iframe_version:\"")[1].split("\"")[0];
            Log.d("post manifests", "iframe_version :" + iframe_version);
            //x params
            String x1 = js_data.html().split("iframe_version:\"")[1].split("\",")[1].split(":")[0];
//            String x2x = js_data.html().split(x1)[1].split("\\}")[0].replaceAll(":", "").trim();
            String x2 = iframe.html().split("window\\[")[1].split("= '")[1].split("';")[0];
            Log.d("post manifests", x1 + ":" + x2);

            //post on http://moonwalk.cc/manifests/video/[token]/all
            Document manifests = PostDataMoonwlk("http://moonwalk.cc/manifests/video/"+token+"/all",
                    mw_key, mw_pid, p_domain_id, x1, x2, iframe_version);
            if (manifests.text().contains("manifest_mp4") && !manifests.text().contains("manifest_mp4\":null")){
                String mp4_url = manifests.text().split("manifest_mp4\":\"")[1].split("\"")[0].replace("\\u0026", "&");
                Document video = GetdataMoonwalkIframe(mp4_url);
                if (video.text().contains("360")) {
                    q.add("360p");
                    u.add(video.text().split("360\":\"")[1].split("\"")[0]);
                } if (video.text().contains("480")) {
                    q.add("480p");
                    u.add(video.text().split("360\":\"")[1].split("\"")[0]);
                } if (video.text().contains("720")) {
                    q.add("720p");
                    u.add(video.text().split("360\":\"")[1].split("\"")[0]);
                } if (video.text().contains("1080")) {
                    q.add("1080p");
                    u.add(video.text().split("1080\":\"")[1].split("\"")[0]);
                }
            } else if (manifests.text().contains("manifest_m3u8")) {
                String m3u8_url = manifests.text().split("manifest_m3u8\":\"")[1].split("\"")[0].replace("\\u0026", "&");
                q.add("m3u8 file");
                u.add(m3u8_url);
            } else {
                q.add("error");
                u.add("error");
            }
            quality_arr = q.toArray(new String[q.size()]);
            url_arr = u.toArray(new String[u.size()]);
        }
    }

    private void ParseHdgoMp4(String url) {
        String ref = url.contains("ref=") ? url.split("ref=")[1] : "hdgo.cc";
        url = url.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", "");
        Document first_iframe = GetdataHdgoIframe(url, ref);
        if (first_iframe.body().html().contains("<iframe ") && !first_iframe.html().contains("embed: '<iframe src")) {
            String s_iframe = first_iframe.select("iframe").first().attr("src");
            ParseHdgoMp42(s_iframe);
        } else {
            ParseHdgoMp42(url);
        }
    }

    private void ParseHdgoMp42(String url) {
        final ArrayList<String> q = new ArrayList<String>();
        final ArrayList<String> u = new ArrayList<String>();
        Document second_iframe = GetdataHdgoIframe(url, "hdgo.cc");
        if (second_iframe == null) {
            Log.d(TAG, "ParseHdgoMp4: некорректная ссылка");
            q.add("error");
            u.add("error");
        } else if (second_iframe.body().html().contains("<video ")) {
            //film
            Elements video = second_iframe.select("video source");
            for (Element iframe : video) {
                if (iframe.attr("src").contains("/1/")) {
                    q.add("360p");
                    u.add(iframe.attr("src"));
                } else if (iframe.attr("src").contains("/2/")) {
                    q.add("480p");
                    u.add(iframe.attr("src"));
                } else if (iframe.attr("src").contains("/3/")) {
                    q.add("720p");
                    u.add(iframe.attr("src"));
                } else if (iframe.attr("src").contains("/4/")) {
                    q.add("1080p");
                    u.add(iframe.attr("src"));
                }
            }
        } else if (second_iframe.body().html().contains("media: [")) {
            String video_arr = second_iframe.body().html().split("media: \\[\\{")[1]
                    .split("\\}]")[0];
            if (video_arr.contains("\\},\\{")) {
                String[] video_url = video_arr.split("\\},\\{");
                for (int i = 0; i < video_url.length; i++) {
                    video_url[i] = video_url[i].replace("url: '", "").replace("', type: 'video/mp4'", "")
                            .replace("'", "");
                    if (video_url[i].contains("/1/")) {
                        q.add("360p");
                        u.add(video_url[i]);
                    } else if (video_url[i].contains("/2/")) {
                        q.add("480p");
                        u.add(video_url[i]);
                    } else if (video_url[i].contains("/3/")) {
                        q.add("720p");
                        u.add(video_url[i]);
                    } else if (video_url[i].contains("/4/")) {
                        q.add("1080p");
                        u.add(video_url[i]);
                    }
                }
            } else {
                String[] video_url = video_arr.split("'");
                for (int i = 0; i < video_url.length; i++) {
                    if (video_url[i].contains("http://")){
                        if (video_url[i].contains("/1/")) {
                            q.add("360p");
                            u.add(video_url[i]);
                        } else if (video_url[i].contains("/2/")) {
                            q.add("480p");
                            u.add(video_url[i]);
                        } else if (video_url[i].contains("/3/")) {
                            q.add("720p");
                            u.add(video_url[i]);
                        } else if (video_url[i].contains("/4/")) {
                            q.add("1080p");
                            u.add(video_url[i]);
                        }
                    }
                }
            }
        } else {
            Log.d(TAG, "ParseHdgoMp4: видео недоступно");
            q.add("error");
            u.add("error");
        }
        quality_arr = q.toArray(new String[q.size()]);
        url_arr = u.toArray(new String[u.size()]);
    }

    private String checkUrl(String url) {
        url = url.replaceAll("\"", "");
        if (!url.contains("http://")) url = url.contains("//")?"http:" + url:"http://" + url;
        return url;
    }

    private void addItem(String base){
        String token = base.equals("hdgo") ? tokenHDGO : tokenMoonwalk;
        itemBase.setTitle(title);
        itemBase.setType(base);
        itemBase.setToken(token);
        itemBase.setId_trans(id_trans);
        itemBase.setId(id);
        itemBase.setUrl(url);
        itemBase.setSeason(season);
        itemBase.setEpisode(episode);
        itemBase.setTranslator(translator);
    }

    private void ParseSeries(Document data, String type, String cur_season) {
        if (data != null) {
            int cur_s = Integer.parseInt(cur_season.trim());

            title = "back";
            if (data.body().text().contains("title_ru\":\""))
                episode = data.body().text().split("title_ru\":\"")[1].split("\",")[0];
            else if (data.body().text().contains("title_ru\": \""))
                episode = data.body().text().split("title_ru\": \"")[1].split("\",")[0];
            if (data.body().text().contains("translator\":\""))
                translator = data.body().text().split("translator\":\"")[1].split("\",")[0];
            else if (data.body().text().contains("translator\": \""))
                translator = data.body().text().split("translator\": \"")[1].split("\",")[0];
            season = cur_season;
            addItem(type);

            String iframe_url = "error";
            if (data.body().text().contains("iframe_url\":\""))
                iframe_url = data.body().text().split("iframe_url\":\"")[1].split("\",")[0];

            String[] array = data.body().text().split("\\{\"season_");
            //если колво сезонов меньше последнего сезона
            for (int i = 1; i < array.length; i ++){
                String numb = array[i].split("number\":")[1].split(",")[0].trim();
                if (numb.equals(cur_season))
                    cur_s = i;
            }
            //разные форматы hdgo и moonwalk
            String series = "";
            if (array[cur_s].contains("episodes\":["))
                series = array[cur_s].split("episodes\":\\[")[1].split("\\]")[0];
            else if (array[cur_s].contains("episodes\": ["))
                series = array[cur_s].split("episodes\": \\[")[1].split("\\]")[0];
            String[] series_arr = series.split(",");
            //построение списка
            for (int i = 0; i < series_arr.length; i ++){
                title = (i + 1) + "";
                if (series_arr[i].contains("\""))
                    url = series_arr[i].replaceAll("\"", "");
                if (!series_arr[i].contains("http://"))
                    series_arr[i] = iframe_url + "?episode=" + series_arr[i] + "&season" + cur_season;
                url = series_arr[i];
                season = cur_season;
                addItem(type);
            }
        }
    }

    private void ParseSeason(Document data, String type) {
        if (data != null) {
            String[] array = data.body().text().split("\\{\"season_");

            title = "back";
            if (data.body().text().contains("title_ru\":\""))
                episode = data.body().text().split("title_ru\":\"")[1].split("\",")[0];
            else if (data.body().text().contains("title_ru\": \""))
                episode = data.body().text().split("title_ru\": \"")[1].split("\",")[0];
            if (data.body().text().contains("translator\":\""))
                translator = data.body().text().split("translator\":\"")[1].split("\",")[0];
            else if (data.body().text().contains("translator\": \""))
                translator = data.body().text().split("translator\": \"")[1].split("\",")[0];
            addItem(type);

            //i = 0 - description season
            for (int i = 1; i < array.length; i ++){
                title = array[i].split("number\":")[1].split(",")[0].trim();
                if (array[i].contains("episodes_count"))
                    season = array[i].split("episodes_count\":")[1].split(",")[0].trim();

                addItem(type);
            }
        }
    }

    private void ParseHtmlMoonwalk(Document data) {
        Log.d(TAG, "ParseHtmlMoonwalk: Start");
        if (data != null) {
            name = name.replaceAll("%20"," ").trim();
            String[] array = data.body().text().split("\\},\\{");
            String types = "error";
            for (int i = 0; i < array.length; i ++){
                String title_m = "error";
                if (array[i].contains("title_ru") && !array[i].contains("title_ru\":null")) {
                    title_m = array[i].split("title_ru\":\"")[1].split("\"")[0].trim();
                }
                if (array[i].contains("kinopoisk_id") && this.name.toLowerCase().replace("ё", "е")
                        .equals(title_m.toLowerCase().replace("ё", "е"))) {
                    title = title_m;
                    id = array[i].split("kinopoisk_id\":")[1].split(",")[0];
                    if (array[i].contains("translator_id"))
                        id_trans = array[i].split("translator_id\":")[1].split(",")[0];
                    if (array[i].contains("seasons_count"))
                        season = array[i].split("seasons_count\":")[1].split(",")[0];
                    if (array[i].contains("episodes_count"))
                        episode = array[i].split("episodes_count\":")[1].split(",")[0];
                    if (array[i].contains("translator\":null"))
                        translator = "Неизвестный";
                    else if (array[i].contains("translator"))
                        translator = array[i].split("translator\":\"")[1].split("\"")[0];
                    if (array[i].contains("type"))
                        types = array[i].split("type\":\"")[1].split("\"")[0];
                    if (array[i].contains("iframe_url"))
                        url = array[i].split("iframe_url\":\"")[1].split("\"")[0];

                    if (this.type.contains(types)) {
                        addItem("moonwalk");
                        Log.d(TAG, "ParseHtmlMoonwalk: " + translator + " add");
                    }
                }
            }
        }
    }

    private void siteSeries(ArrayList<String> url_iframe) {
//        if (url_iframe.get(0).contains("hdgo")){

            title = "superback";
            season = DetailActivity.season;
            addItem(type);

            if (url_iframe.size() > 1 && !DetailActivity.link.contains("coldfilm")){
                for (int i = 0; i < url_iframe.size(); i++){
                    if (url_iframe.get(i).contains("error"))
                        break;
                    else {
                        title = i + 1 + "";
                        url = url_iframe.get(i);
                        addItem(type);
                    }
                }
            } else {
                if (!url_iframe.get(0).contains("error")) {
                    url = url_iframe.get(0);
                    title = DetailActivity.link.contains("coldfilm") ? DetailActivity.serie : "1";
                    addItem(type);
                }
            }
//        }
    }
    //для плеера hdgo с сайта
    private void siteIframeHdgo(String url_iframe) {
        Log.d(TAG, "siteIframeHdgo: "+DetailActivity.title+DetailActivity.voice);
        Document data = GetdataHdgoIframe(url_iframe, DetailActivity.link.split("/")[2]);
        if (data != null)
            if (!data.html().contains("Видео недоступно") && data.html().contains("<iframe")){
                String src = data.select("iframe").first().attr("src");
                Document iframe = GetdataHdgoIframe(src, DetailActivity.link.split("/")[2]);
                if (iframe != null) {
                    if (DetailActivity.type.contains("movie")){
                        title = DetailActivity.title;
                        translator = DetailActivity.voice.contains("error") ? DetailActivity.title : DetailActivity.voice;
                        url = src;
                        type = DetailActivity.type;
                        id = "site";
                        addItem("koshara.co");
                    } else if (iframe.html().contains("season_list[0] = [\"")) {
                        String season_list = iframe.html().split("season_list\\[0] = \\[\"")[1];
                        if (season_list.contains(",];")){
                            season_list = season_list.split(",];")[0]
                                    .replace("\"", "");
                            if (season_list.contains(",")){
                                String[] mp4 = season_list.split(",");
                                title = DetailActivity.title;
                                translator = DetailActivity.voice.contains("error") ? DetailActivity.title : DetailActivity.voice;
                                type = "serial";
                                season = DetailActivity.season;
                                episode = String.valueOf(mp4.length);
                                id = "site";
                                for (int i = 0; i < mp4.length; i++) {
                                    itemBase.setUrl("http://" + src.split("/")[2] + mp4[i]);
                                }
                                addItem("koshara.co");
                                Log.d(TAG, "site koshara.co: add");
                            }
                        }
                    } else if (DetailActivity.link.split("/")[2].contains("coldfilm")){
                        title = DetailActivity.title;
                        translator = "Coldfilm";
                        url = url_iframe;
                        type = "serial";
                        season = DetailActivity.season;
                        episode = DetailActivity.serie;
                        id = "site";
                        addItem("coldfilm.ru");
                        Log.d(TAG, "site coldfilm.ru: add");
                    }
                    if (iframe.html().contains("The video file to be processed.")) {
                        Log.d(TAG, "The video file to be processed.");
                    }
                }
            }
    }

    private void ParseHtmlHDGO(Document data) {
        if (data != null) {
            name = name.replaceAll("%20"," ").trim();

            String[] array = data.body().text().split("\\},");
            String types = "";
            for (int i = 0; i < array.length; i ++){
                if (array[i].contains("title") && !array[i].contains("title\":null"))
                    title = array[i].split("title\":\"")[1].split("\"")[0].trim();
                if (array[i].contains("id_hdgo") && this.name.toLowerCase().replace("ё", "е")
                        .equals(title.toLowerCase().replace("ё", "е"))) {
                    id = array[i].split("id_hdgo\":")[1].split(",")[0];
                    if (array[i].contains("seasons_count"))
                        season = array[i].split("seasons_count\":")[1].split(",")[0];
                    if (array[i].contains("episodes_count"))
                        episode = array[i].split("episodes_count\":")[1].split(",")[0];
                    if (array[i].contains("translator"))
                        translator = array[i].split("translator\":\"")[1].split("\"")[0];
                    if (array[i].contains("type"))
                        types = array[i].split("type\":\"")[1].split("\"")[0];
                    if (array[i].contains("iframe_url"))
                        url = array[i].split("iframe_url\":\"")[1].split("\"")[0];

                    if (this.type.contains(types)) {
                        addItem("hdgo");
                        Log.d(TAG, "ParseHtmlHDGO: " + translator + " add");
                    }
                }
            }
        }
    }

    private Document GetSeasonHDGO(String id) {
        final String url = "http://hdgo.cc/api/serial_episodes.json?token="+ tokenHDGO +
                "&id=" + id;
        try {
            Document htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(50000).ignoreContentType(true).referrer("hdgo.cc").get();
            Log.d(TAG, "GetSeasonHDGO: get connected to " + url);
            return htmlDoc;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "GetSeasonHDGO: error connected to " + url);
            return null;
        }
    }

    private Document GetSeasonMoonwalk(String id, String id_trans) {
        final String url = "http://moonwalk.cc/api/serial_episodes.json?api_token="+ tokenMoonwalk +
                "&kinopoisk_id=" + id + "&translator_id=" + id_trans;
        try {
            Document htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(50000).ignoreContentType(true).referrer("hdgo.cc").get();
            Log.d(TAG, "GetSeasonMoonwalk: get connected to " + url);
            return htmlDoc;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "GetSeasonMoonwalk: error connected to " + url);
            return null;
        }
    }

    private Document GetdataHDGO(String name) {
        name = name.trim().replaceAll(" ", "%20");
        final String url = "http://hdgo.cc/api/video.json?token="+ tokenHDGO +"&title=" + name;
        try {
            Document htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(50000).ignoreContentType(true).referrer("hdgo.cc").get();
            Log.d(TAG, "GetdataHDGO: get connected to " + url);
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "GetdataHDGO: connected false to " + url);
            e.printStackTrace();
            return null;
        }
    }

    private Document GetdataMoonwalk(String name) {
        name = name.trim().replace(" ", "%20");
        name = name.replaceAll("ё", "е");
        final String url = "http://moonwalk.cc/api/videos.json?api_token=" + tokenMoonwalk + "&title=" + name;
        try {
            Document htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(50000).ignoreContentType(true).referrer("moonwalk.cc").get();
            Log.d(TAG, "GetdataMoonwalk: get connected to " + url);
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "GetdataMoonwalk: connected false to " + url);
            return null;
        }
    }

    private Document GetdataHdgoIframe(String url, String referrer){
        try {
            Document htmlDoc = Jsoup.connect(checkUrl(url))
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .referrer("http://" + referrer)
                    .timeout(50000).ignoreContentType(true).get();
            Log.d(TAG, "GetdataHdgoIframe: connected to " + checkUrl(url));
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "GetdataHdgoIframe: connected false to " + checkUrl(url));
            e.printStackTrace();
            return null;
        }
    }

    private Document GetdataMoonwalkIframe(String url){
        try {
            Document htmlDoc = Jsoup.connect(checkUrl(url))
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .referrer("http://moonwalk.cc")
                    .timeout(50000).ignoreContentType(true).get();
            Log.d(TAG, "GetdataMoonwalkIframe: connected to " + checkUrl(url));
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "GetdataMoonwalkIframe: connected false to " + checkUrl(url));
            e.printStackTrace();
            return null;
        }
    }

    private Document PostDataMoonwlk(String url, String mw_key, String mw_pid,
                              String p_domain_id, String x1, String x2, String iframe_version){
        try {
            Document htmlDoc = Jsoup.connect(checkUrl(url))
                    .data("mw_key", mw_key)
                    .data("mw_pid", mw_pid)
                    .data("p_domain_id", p_domain_id)
                    .data("ad_attr", "0")
                    .data("iframe_version", iframe_version)
                    .data(x1, x2)
                    .header("X-Requested-With", "XMLHttpRequest")
                    .header("Accept-Encoding", "gzip, deflate")
                    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36")
                    .referrer("http://moonwalk.cc/").timeout(50000).ignoreContentType(true).post();
            Log.d(TAG, "PostDataMoonwlk: post to " + checkUrl(url));
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "PostDataMoonwlk: post false to " + checkUrl(url));
            e.printStackTrace();
            return null;
        }
    }
}
