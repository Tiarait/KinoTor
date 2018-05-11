package com.kinotor.tiar.kinotor.items;

import java.util.ArrayList;

/**
 * Created by Tiar on 24.09.2017.
 */

public class ItemHtml {
    public ArrayList<String> title = new ArrayList<>();
    ArrayList<String> subtitle = new ArrayList<>();
    ArrayList<String> url = new ArrayList<>();
    ArrayList<String> img = new ArrayList<>();
    ArrayList<String> date = new ArrayList<>();
    ArrayList<String> description = new ArrayList<>();
    ArrayList<String> voice = new ArrayList<>();
    ArrayList<String> quality = new ArrayList<>();
    public ArrayList<Integer> season = new ArrayList<>();
    public ArrayList<Integer> series = new ArrayList<>();
    ArrayList<String> country = new ArrayList<>();
    ArrayList<String> genre = new ArrayList<>();
    ArrayList<String> director = new ArrayList<>();
    ArrayList<String> actors = new ArrayList<>();
    ArrayList<String> time = new ArrayList<>();
    ArrayList<String> iframe = new ArrayList<>();
    ArrayList<String> type = new ArrayList<>();

    public ArrayList<String> moretitle = new ArrayList<>();
    ArrayList<String> moreurl = new ArrayList<>();
    ArrayList<String> moreimg = new ArrayList<>();
    ArrayList<String> moreseason = new ArrayList<>();
    ArrayList<String> moreseries = new ArrayList<>();
    ArrayList<String> morequality = new ArrayList<>();
    ArrayList<String> morevoice = new ArrayList<>();


    public ArrayList<String> tortitle = new ArrayList<>();
    ArrayList<String> torurl = new ArrayList<>();
    ArrayList<String> torsize = new ArrayList<>();
    ArrayList<String> tormagnet = new ArrayList<>();
    ArrayList<String> torsid = new ArrayList<>();
    ArrayList<String> torlich = new ArrayList<>();
    ArrayList<String> torcontent = new ArrayList<>();

    public ArrayList<String> preimg = new ArrayList<>();
    String extraDetail = "error";


    //------------------Get---------------------------

    public String getTitle(int i) {
        return title.get(i);
    }
    public String getSubTitle(int i) {
        return subtitle.get(i);
    }
    public String getUrl(int i) {
        return url.get(i);
    }
    public String getImg(int i) {
        return img.get(i);
    }
    public String getDate(int i) {
        return date.get(i);
    }
    public String getDescription(int i) {
        return description.get(i);
    }
    public String getVoice(int i) {
        return voice.get(i);
    }
    public String getQuality(int i) {
        return quality.get(i);
    }
    public int getSeason(int i){
        return season.get(i);
    }
    public int getSeries(int i){
        return series.get(i);
    }
    public String getCountry(int i) {
        return country.get(i);
    }
    public String getGenre(int i) {
        return genre.get(i);
    }
    public String getDirector(int i) {
        return director.get(i);
    }
    public String getActors(int i) {
        return actors.get(i);
    }
    public String getTime(int i) {
        return time.get(i);
    }
    public String getIframe(int i) {
        return iframe.get(i);
    }
    public String getType(int i) {
        return type.get(i);
    }


    public String getMoreTitle(int i) {
        return moretitle.get(i);
    }
    public String getMoreUrl(int i) {
        return moreurl.get(i);
    }
    public String getMoreImg(int i) {
        return moreimg.get(i);
    }
    public String getMoreQu(int i) {
        return morequality.get(i);
    }
    public String getMoreVoice(int i) {
        return morevoice.get(i);
    }
    public String getMoreSeason(int i) {
        return moreseason.get(i);
    }
    public String getMoreSeries(int i) {
        return moreseries.get(i);
    }


    public String getPreImg(int i) {
        return preimg.get(i);
    }
    public String getExtraDetail() {
        return extraDetail;
    }

    //------------------Set---------------------------

    public void setTitle(String title) {
        this.title.add(title);
    }
    public void setSubTitle(String subtitle) {
        this.subtitle.add(subtitle);
    }
    public void setUrl(String url) {
        this.url.add(url);
    }
    public void setImg(String img) {
        this.img.add(img);
    }
    public void setDate(String date) {
        this.date.add(date);
    }
    public void setDescription(String description) {
        this.description.add(description);
    }
    public void setVoice(String voice) {
        this.voice.add(voice);
    }
    public void setQuality(String quality) {
        this.quality.add(quality);
    }
    public void setSeason(int season){
        this.season.add(season);
    }
    public void setSeries(int series){
        this.series.add(series);
    }
    public void setCountry(String i) {
        this.country.add(i);
    }
    public void setGenre(String i) {
        this.genre.add(i);
    }
    public void setDirector(String i) {
        this.director.add(i);
    }
    public void setActors(String i) {
        this.actors.add(i);
    }
    public void setTime(String i) {
        this.time.add(i);
    }
    public void setIframe(String i) {
        this.iframe.add(i);
    }
    public void setType(String i) {
        this.type.add(i);
    }


    public void setMoreTitle(String i) {
        this.moretitle.add(i);
    }
    public void setMoreUrl(String i) {
        this.moreurl.add(i);
    }
    public void setMoreImg(String i) {
        this.moreimg.add(i);
    }
    public void setMoreQuality(String i) {
        this.morequality.add(i);
    }
    public void setMoreVoice(String i) {
        this.morevoice.add(i);
    }
    public void setMoreSeason(String i) {
        this.moreseason.add(i);
    }
    public void setMoreSeries(String i) {
        this.moreseries.add(i);
    }


    public void setTorTitle(String i) {
        this.tortitle.add(i);
    }
    public void setTorUrl(String i) {
        this.torurl.add(i);
    }
    public void setTorSize(String i) {
        this.torsize.add(i);
    }
    public void setTorMagnet(String i) {
        this.tormagnet.add(i);
    }
    public void setTorSid(String i) {
        this.torsid.add(i);
    }
    public void setTorLich(String i) {
        this.torlich.add(i);
    }
    public void setTorContent(String i) {
        this.torcontent.add(i);
    }


    public void setPreImg(String i) {
        this.preimg.add(i);
    }
    public void setExtraDetail(String i) {
        this.extraDetail = i;
    }
}
