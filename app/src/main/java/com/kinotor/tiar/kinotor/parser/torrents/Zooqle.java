package com.kinotor.tiar.kinotor.parser.torrents;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemTorrent;
import com.kinotor.tiar.kinotor.utils.OnTaskTorrentCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class Zooqle extends AsyncTask<Void, Void, Void> {
    private String title;
    private ItemHtml item;
    private ItemTorrent torrent;
    private OnTaskTorrentCallback callback;

    public Zooqle(ItemHtml item, OnTaskTorrentCallback callback) {
        this.item = item;
        this.callback = callback;

        torrent = new ItemTorrent();
        this.title = item.getTitle(0);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(torrent);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        parseZooqle(Getdata(title, 1));
        parseZooqle(Getdata(title, 2));
        parseZooqle(Getdata(title, 3));
        return null;
    }

    private void parseZooqle(Document data) {
        if (data != null) {
            if (data.html().contains("<item>")) {
                String[] list = data.body().html().split("<item>");
                for (int i = 1; i < list.length; i ++) {
                    boolean check;
                    String sid = findText(list[i], "torrent:seeds");
                    String lich = findText(list[i], "torrent:peers");
                    String magnet = "magnet:?xt=urn:btih:" + findText(list[i], "torrent:infohash")
                            + "&dn=" + findText(list[i], "title");
                    String size = byteInGb(findText(list[i], "torrent:contentlength"));
                    String url = list[i].contains("enclosure url") ?
                            list[i].split("enclosure url=\"")[1].split("\"")[0] :
                            findText(list[i], "url");
                    String name = findText(list[i], "title");
                    if (item.getType(0).contains("serial")) {
                        check = Pattern.compile("\\[.*?\\]").matcher(name).find();
                    } else
                        check = item.getType(0).contains("movie") && !Pattern.compile("\\[.*?\\]")
                                .matcher(name).find();
//                    Log.d(TAG, "Zooqle: "+name +" "+sid+" "+check);

                    if (!sid.trim().equals("1") && !sid.trim().equals("0") &&
                            !name.toLowerCase().contains("mp3") && !name.toLowerCase().contains("fb2")
                            && !name.toLowerCase().contains(" part ") && !name.toLowerCase().contains("cbr")
                            && !name.toLowerCase().contains("pdf")
                            && check) {
//                        Log.d(TAG, "Zooqle add: "+ name + " " + magnet);
                        torrent.setTorTitle(name);
                        torrent.setTorUrl(url.replace("zooqle.com", "zooqle.unblocked.mx"));
                        torrent.setTorSize(size.trim());
                        torrent.setTorMagnet(magnet);
                        torrent.setTorSid(sid.trim());
                        torrent.setTorLich(lich.trim());
                        torrent.setTorContent("zooqle.com");
                    }
                }
            }
        }
    }

    private String findText(String text, String query) {
        //<title?>.*?<
        if (text.contains("<" + query + ">"))
            return text.split("<" + query + ">")[1].split("<")[0].trim();
        else {
            Log.d(TAG, query + " not found in \n" + text);
            return "error";
        }

    }
    private String byteInGb(String bt){
        long bytes = Long.parseLong(bt.trim());
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    private Document Getdata(String title, int pg) {
        try {
            title = URLEncoder.encode(title, "utf-8");
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, "Getdata: Zooqle error encode");
            return null;
        }
        String category = "Movies%2CTV";
        if (item.getType(0).contains("serial"))
            category = "TV";
        if (item.getType(0).contains("movie"))
            category = "Movies";
        if (item.getType(0).contains("anime"))
            category = category + "%2CAnime";

        String url = "https://zooqle.unblocked.mx/search?pg="+pg+"&q=" + title + "+category%3A" + category + "&s=dt&fmt=rss";
        try {
            Document htmlDoc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                    .timeout(10000).ignoreContentType(true).post();
            Log.d(TAG, "Getdata: Zooqle " + url);
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "Getdata: Zooqle error " + url);
            e.printStackTrace();
            return null;
        }
    }
}