package com.kinotor.tiar.kinotor.items;

import java.util.ArrayList;

/**
 * Created by Tiar on 24.09.2017.
 */

public class ItemHtml {
    public ArrayList<String> title = new ArrayList<>();
    ArrayList<String> url = new ArrayList<>();
    ArrayList<String> img = new ArrayList<>();
    ArrayList<String> date = new ArrayList<>();
    ArrayList<String> description = new ArrayList<>();
    ArrayList<String> voice = new ArrayList<>();
    ArrayList<String> quality = new ArrayList<>();
    ArrayList<Integer> season = new ArrayList<>();
    ArrayList<Integer> series = new ArrayList<>();

    //------------------Get---------------------------

    public String getTitle(int i) {
        return title.get(i);
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

    //------------------Set---------------------------

    public void setTitle(String title) {
        this.title.add(title);
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
}
