package com.kinotor.tiar.kinotor.parser.torrents;

import android.os.AsyncTask;
import android.util.Log;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemTorrent;
import com.kinotor.tiar.kinotor.utils.OnTaskTorrentCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import static android.content.ContentValues.TAG;

/**
 * Created by Tiar on 02.2018.
 */

public class Tparser extends AsyncTask<Void, Void, Void> {
    private String title, base;
    private ItemHtml item;
    private ItemTorrent torrent;
    private OnTaskTorrentCallback callback;

    public Tparser(ItemHtml item, String base, OnTaskTorrentCallback callback) {
        this.item = item;
        this.base = base;
        this.callback = callback;
        
        torrent = new ItemTorrent();
        this.title = item.getTitle(0).replaceAll(" ", "%20")
                .replace(".", "");
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(torrent);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (base.contains("rutor.info"))
            parser(Getdata("http://js1.tparser.org/js1/2.tor.php?callback=one&jsonpx=" + title.split("\\(")[0]));
        if (base.contains("rutracker.org"))
            parser(Getdata("http://js3.tparser.org/js3/6.tor.php?callback=one&jsonpx=" + title));
        if (base.contains("underverse.me"))
            parser(Getdata("http://js5.tparser.org/js5/9.tor.php?callback=one&jsonpx=" + title));
        if (base.contains("kinozal.tv"))
            parser(Getdata("http://js5.tparser.org/js5/10.tor.php?callback=one&jsonpx=" + title));

        //thepiratebay.org
//        parser(Getdata("http://js4.tparser.org/js4/8.tor.php?callback=one&jsonpx=" + title));
//        parser(Getdata("http://js2.tparser.org/js2/4.tor.php?callback=one&jsonpx=" + title));

        return null;
    }

    private void parser(Document data) {
        String all;
        String[] list;
        if (data != null) {
            if (data.text().contains("sr':[{") && !data.text().contains("'error'")) {
                all = data.text().split("sr':\\[")[1].split("]\\}")[0];
                list = all.split("\\}, \\{");
                for (String aList : list) {
                    if (aList.split("z':'")[1].split("',")[0].equals("1")) {
                        String z = aList.split("link':'")[1].split("',")[0]
                                .contains("kinozal.tv") ? "2" : "1";
                        String link = aList.split("link':'")[1].split("',")[0];
                        String content = aList.split("link':'")[1].split("'")[0]
                                .split("://")[1].split("/")[0];
                        link = link.contains("fast-tor.net") ? "http://d.rutor.info/download/" +
                                link.split("torrent/")[1].split("/")[0] : link;
                        String name = aList.split("name':'")[1].split("'")[0];
                        String sid = aList.split("s':'")[1].split("'")[0].trim();
                        if (item.getType(0).contains("movie") && name.contains(item.getDate(0).trim()) &&
                                !sid.equals("0") && !sid.equals("1")) {
                            torrent.setTorTitle(name);
                            torrent.setTorUrl(link);
                            torrent.setTorSize(aList.split("size':'")[1].split("'")[0] +
                                    " " + aList.split("t':'")[1].split("',")[0]);
                            torrent.setTorMagnet("http://tparser.org/magnet.php?t=" + z +
                                    aList.split("img':'")[1].split("',")[0] +
                                    aList.split("d':'")[1].split("',")[0]);
                            torrent.setTorSid(sid);
                            torrent.setTorLich(aList.split("l':'")[1].split("'")[0]);
                            torrent.setTorContent(content.replace("fast-tor.net", "rutor.info"));
                        } else if (item.getType(0).contains("serial") && !sid.equals("0") && !sid.equals("1") &&
                                (name.contains("сезон") || name.contains("выпуск") ||
                                        (name.contains(" из ") && name.contains("x")))) {
                            torrent.setTorTitle(name);
                            torrent.setTorUrl(link);
                            torrent.setTorSize(aList.split("size':'")[1].split("'")[0] +
                                    " " + aList.split("t':'")[1].split("',")[0]);
                            torrent.setTorMagnet("http://tparser.org/magnet.php?t=" + z +
                                    aList.split("img':'")[1].split("',")[0] +
                                    aList.split("d':'")[1].split("',")[0]);
                            torrent.setTorSid(aList.split("s':'")[1].split("'")[0]);
                            torrent.setTorLich(aList.split("l':'")[1].split("'")[0]);
                            torrent.setTorContent(content.replace("fast-tor.net", "rutor.info"));
                        }
                    }
                }
            }
        }
    }

    private Document Getdata(String url) {
        try {
            Document htmlDoc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .timeout(10000).ignoreContentType(true).get();
            Log.d(TAG, "Getdata: parser " + url + " | " + title);
            return htmlDoc;
        } catch (Exception e) {
            Log.d(TAG, "Getdata: parser error " + url + " | " + title);
            e.printStackTrace();
            return null;
        }
    }
}
