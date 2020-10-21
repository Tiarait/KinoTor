package com.kinotor.tiar.kinotor.parser.torrents;

import android.os.AsyncTask;

import com.kinotor.tiar.kinotor.items.ItemHtml;
import com.kinotor.tiar.kinotor.items.ItemTorrent;
import com.kinotor.tiar.kinotor.items.Statics;
import com.kinotor.tiar.kinotor.utils.OnTaskTorrentCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by Tiar on 02.2018.
 */

public class Tparser extends AsyncTask<Void, Void, Void> {
    private String title, base;
    private ItemHtml item;
    private ItemTorrent torrent;
    private OnTaskTorrentCallback callback;
    String title_ru;
    String title_en;
    String date;

    public Tparser(ItemHtml item, OnTaskTorrentCallback callback) {
        this.item = item;
//        this.base = base;
        this.callback = callback;
        
        torrent = new ItemTorrent();
        title = item.getTitle(0).trim();
        title = title
                .replace(" ", "%20")
                .replace("\u00a0", "%20").replace(".", "").trim();

        this.title_ru = item.getTitle(0).trim();
        this.title_en = item.getSubTitle(0).trim();
        this.date = item.getDate(0).trim();
        if (title_ru.contains("("))
            title_ru = title_ru.split("\\(")[0].trim();
        if (title_ru.contains("["))
            title_ru = title_ru.split("\\[")[0].trim();

        if (title_en.contains("("))
            title_en = title_en.split("\\(")[0].trim();
        if (title_en.contains("["))
            title_en = title_en.split("\\[")[0].trim();

        if (date.contains("."))
            date = date.split("\\.")[date.split("\\.").length - 1];

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callback.OnCompleted(torrent);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        String n;
        String torS = Statics.torS;
        if (torS.contains("title") || torS.contains("orig") || torS.contains("year")) {
            n = "";
            if (torS.contains("title") && !this.title_ru.contains("error"))
                n += this.title_ru;
            if (torS.contains("orig") && !this.title_en.contains("error"))
                n += " " + this.title_en;
            if (torS.contains("year") && !this.date.contains("error"))
                n += " " + this.date;
        } else n = title;
        n = n.replace("error","").trim();


        parserTparser(Getdata(Statics.TPARSER_URL+"/a?q=" + n.replace("&","%26") +"&l=50&gs="));
//        if (base.contains("rutor(tparser)"))
//            parser(Getdata("http://js1.tparser.org/js1/2.tor.php?callback=one&jsonpx=" + title.split("\\(")[0]));
//        if (base.contains("rutracker(tparser)"))
//            parser(Getdata("http://js3.tparser.org/js3/6.tor.php?callback=one&jsonpx=" + title));
//        if (base.contains("underverse(tparser)"))
//            parser(Getdata("http://js5.tparser.org/js5/9.tor.php?callback=one&jsonpx=" + title));
//        if (base.contains("kinozal(tparser)"))
//            parser(Getdata("http://js5.tparser.org/js5/10.tor.php?callback=one&jsonpx=" + title));
//        if (base.contains("nnmclub(tparser)"))
//            parser(Getdata("http://js2.tparser.org/js2/4.tor.php?callback=one&jsonpx=" + title));
        //thepiratebay.org
//        parser(Getdata("http://js4.tparser.org/js4/8.tor.php?callback=one&jsonpx=" + title));

        return null;
    }

    private void parserTparser(Document data) {
        if (data != null) {
            if (data.text().contains("items\": [{\"id\":")) {
                for (String aList : data.text().split("\"id\":")) {
                    String title = "error";
                    String sid = "error";
                    String lich = "error";
                    String source = "error";
                    String size = "error";
                    String link = "error";
                    String linkMagnet = "error";
                    source = "tparser";
                    if (aList.contains("\"title\":")) {
                        title = aList.split("\"title\":")[1].split(",")[0]
                                .replace("\"","").trim();
                    }
                    if (aList.contains("\"tracker__slug\":")) {
                        source = "tparser ("+aList.split("\"tracker__slug\":")[1].split(",")[0]
                                .replace("\"","").trim()+")";
                    }
                    if (aList.contains("\"title\":")) {
                        title = aList.split("\"title\":")[1].split(",")[0]
                                .replace("\"","").trim();
                    }
                    if (aList.contains("\"t_leech\":")) {
                        lich = aList.split("\"t_leech\":")[1].split(",")[0]
                                .replace("\"","").trim();
                    }
                    if (aList.contains("\"t_seed\":")) {
                        sid = aList.split("\"t_seed\":")[1].split(",")[0]
                                .replace("\"","").trim();
                    }
                    if (aList.contains("\"url\":")) {
                        link = aList.split("\"url\":")[1].split(",")[0]
                                .replace("\"","").trim();
                    }
                    if (aList.contains("\"t_magnet_url\":")) {
                        linkMagnet = aList.split("\"t_magnet_url\":")[1].split(",")[0]
                                .replace("\"","").trim();
                    }
                    if (aList.contains("\"t_size\":")) {
                        size = aList.split("\"t_size\":")[1].split(",")[0]
                                .replace("\"","").trim();
                    }
                    try {
                        float s = Float.parseFloat(size)/1010000000;
                        size = String.format("%.2f", s) + " GB";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    boolean t = (" "+title+" ").toLowerCase().contains((" "+this.title_ru+" ").toLowerCase()) ||
                            (" "+title+" ").toLowerCase().contains((" "+this.title_en+" ").toLowerCase()) ||
                            (" "+this.title_en+" ").toLowerCase().contains((" "+title+" ").toLowerCase()) ||
                            (" "+this.title_ru+" ").toLowerCase().contains((" "+title+" ").toLowerCase());
                    if (t) {
                        if (!title.equals("error") && !title.isEmpty()) {
                            torrent.setTorTitle(title);
                            torrent.setUrl(link);
                            torrent.setTorUrl("error");
                            torrent.setTorSize(size);
                            torrent.setTorMagnet(linkMagnet);
                            torrent.setTorSid(sid);
                            torrent.setTorLich(lich);
                            torrent.setTorContent(source);
                        }
                    }
                }
            }
        }
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
                        String magnet = Statics.TPARSER_URL + "/magnet.php?t=" + z +
                                aList.split("img':'")[1].split("',")[0] +
                                aList.split("d':'")[1].split("',")[0];
                        String content = base;
                        String name = aList.split("name':'")[1].split("',")[0];
                        String sid = aList.split("s':'")[1].split("'")[0].trim();
                        String size = aList.split("size':'")[1].split("'")[0] +
                                " " + aList.split("t':'")[1].split("',")[0];

                        if (link.contains("kinozal.tv")) {
                            link = "https://ndvm3-dot-kzal-tv.appspot.com/more?id=" + link.split("id=")[1].trim();
                            magnet = link;
                        } else
                            link = link.contains("fast-tor.net") ? "http://d.rutor.info/download/" +
                                link.split("torrent/")[1].split("/")[0] : link;

                        if (size.contains("MB")){
                            if (size.contains("."))
                                size = size.split("\\.")[0].trim();
                            else size = size.split("MB")[0].trim();

                            float s = Float.parseFloat(size)/1000;
                            size = String.format("%.2f", s) + " GB";
                        }
                        size = size.replace(",", ".");

                        if (!sid.equals("0")) {
                            torrent.setTorTitle(name);
                            torrent.setUrl(link);
                            torrent.setTorUrl("error");
                            torrent.setTorSize(size);
                            torrent.setTorMagnet(magnet);
                            torrent.setTorSid(sid);
                            torrent.setTorLich(aList.split("l':'")[1].split("'")[0]);
                            torrent.setTorContent(content);
                        }
//                        else if (item.getType(0).contains("serial") && !sid.equals("0") &&
//                                (name.contains("сезон") || name.contains("выпуск") ||
//                                        (name.contains(" из ") && name.contains("x")))) {
//                            torrent.setTorTitle(name);
//                            torrent.setTorUrl("error");
//                            torrent.setUrl(link);
//                            torrent.setTorSize(size);
//                            torrent.setTorMagnet(Statics.TPARSER_URL+"/magnet.php?t=" + z +
//                                    aList.split("img':'")[1].split("',")[0] +
//                                    aList.split("d':'")[1].split("',")[0]);
//                            torrent.setTorSid(aList.split("s':'")[1].split("'")[0]);
//                            torrent.setTorLich(aList.split("l':'")[1].split("'")[0]);
//                            torrent.setTorContent(content.replace("fast-tor.net", "rutor"));
//                        }
                    }
                }
            }
        }
    }

    private Document Getdata(String url) {
        try {
            //            Log.d(TAG, "Getdata: tparser " + url);
            return Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Windows; U; Windows NT 5.1; de; rv:1.9) Gecko/2008052906 Firefox/3.0")
                        .timeout(5000).ignoreContentType(true).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
