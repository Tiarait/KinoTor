package com.kinotor.tiar.kinotor.items;

import java.util.ArrayList;

/**
 * Created by Tiar on 28.09.2017.
 */

public class ItemDetail {
    String name;
    String year;
    String country;
    String genre;
    String time;
    String quality;
    String translator;
    String director;
    String actors;
    String description;
    String file;
    String iframe;
    ArrayList<String> img = new ArrayList<>();
    public ArrayList<String> torrents = new ArrayList<>();
    ArrayList<String> tor_name = new ArrayList<>();
    ArrayList<String> tor_magnet = new ArrayList<>();
    ArrayList<String> tor_size = new ArrayList<>();
    ArrayList<String> tor_content = new ArrayList<>();
    ArrayList<String> tor_lich = new ArrayList<>();
    ArrayList<String> tor_sid = new ArrayList<>();
    ArrayList<String> more_title = new ArrayList<>();
    ArrayList<String> more_img = new ArrayList<>();
    ArrayList<String> more_url = new ArrayList<>();
    int cur;
    int season;
    int series;
    ArrayList<String> vid_name = new ArrayList<>();
    public ArrayList<String> vid_url = new ArrayList<>();
    ArrayList<String> vid_content = new ArrayList<>();
    public ArrayList<String> vid_quality = new ArrayList<>();

    //------------------Set---------------------------

    public void setName(String name) {
        this.name = name;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public void setTranslator(String translator) {
        this.translator = translator;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImg(String img) {
        this.img.add(img);
    }

    public void setTorrents(String torrents) {
        this.torrents.add(torrents);
    }

    public void setTor_name(String tor_name) {
        this.tor_name.add(tor_name);
    }

    public void setTor_magnet(String tor_magnet) {
        this.tor_magnet.add(tor_magnet);
    }

    public void setTor_size(String tor_size) {
        this.tor_size.add(tor_size);
    }

    public void setTor_content(String tor_content) {
        this.tor_content.add(tor_content);
    }

    public void setTor_lich(String tor_lich) {
        this.tor_lich.add(tor_lich);
    }

    public void setTor_sid(String tor_sid) {
        this.tor_sid.add(tor_sid);
    }

    public void setIframe(String iframe) {
        this.iframe = iframe;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setSeason(int season){
        this.season = season;
    }

    public void setSeries(int series){
        this.series = series;
    }

    public void setCur(int cur){
        this.cur = cur;
    }

    public void setVid_name(String vid_name) {
        this.vid_name.add(vid_name);
    }

    public void setVid_url(String vid_url) {
        this.vid_url.add(vid_url);
    }

    public void setVid_content(String vid_content) {
        this.vid_content.add(vid_content);
    }

    public void setMore_title(String more_title) {
        this.more_title.add(more_title);
    }

    public void setMore_img(String more_img) {
        this.more_img.add(more_img);
    }

    public void setMore_url(String more_url) {
        this.more_url.add(more_url);
    }

    //------------------Get---------------------------

    public String getName() {
        return name;
    }

    public String getYear() {
        return year;
    }

    public String getCountry() {
        return country;
    }

    public String getGenre() {
        return genre;
    }

    public String getTime() {
        return time;
    }

    public String getQuality() {
        return quality;
    }

    public String getTranslator() {
        return translator;
    }

    public String getDirector() {
        return director;
    }

    public String getActors() {
        return actors;
    }

    public String getDescription() {
        return description;
    }

    public String getImg(int i) {
        return img.get(i);
    }

    public String getTorrents(int i) {
        return torrents.get(i);
    }

    public String getTor_name(int i) {
        return tor_name.get(i);
    }

    public String getTor_magnet(int i) {
        return tor_magnet.get(i);
    }

    public String getTor_size(int i) {
        return tor_size.get(i);
    }

    public String getTor_content(int i) {
        return tor_content.get(i);
    }

    public String getTor_lich(int i) {
        return tor_lich.get(i);
    }

    public String getTor_sid(int i) {
        return tor_sid.get(i);
    }

    public String getFile() {
        return file;
    }

    public String getIframe() {
        return iframe;
    }

    public int getCur(){
        return cur;
    }

    public int getSeason(){
        return season;
    }

    public int getSeries(){
        return series;
    }

    public String getVid_name(int i) {
        return vid_name.get(i);
    }

    public String getVid_url(int i) {
        return vid_url.get(i);
    }

    public String getVid_content(int i) {
        return vid_content.get(i);
    }

    public String getVid_quality(int i) {
        return vid_quality.get(i);
    }
}
