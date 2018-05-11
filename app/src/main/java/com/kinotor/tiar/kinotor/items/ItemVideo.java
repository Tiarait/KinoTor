package com.kinotor.tiar.kinotor.items;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class ItemVideo {
    public ArrayList<String> url = new ArrayList<>();
    ArrayList<String> urlSite = new ArrayList<>();
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> id = new ArrayList<>();
    ArrayList<String> seasons_count = new ArrayList<>();
    ArrayList<String> episodes_count = new ArrayList<>();
    ArrayList<String> type = new ArrayList<>();
    ArrayList<String> token = new ArrayList<>();
    ArrayList<String> id_trans = new ArrayList<>();
    public ArrayList<String> translator = new ArrayList<>();

    public String getUrl(int i) {
        return url.get(i);
    }

    public String getUrlSite(int i) {
        return urlSite.get(i);
    }
    public String getTitle(int i) {
        return title.get(i);
    }
    public String getType(int i) {
        return type.get(i);
    }
    public String getId(int i) {
        return id.get(i);
    }
    public String getSeason(int i) {
        return seasons_count.get(i);
    }
    public String getEpisode(int i) {
        return episodes_count.get(i);
    }
    public String getTranslator(int i) {
        return translator.get(i);
    }
    public String getToken(int i) {
        return token.get(i);
    }
    public String getId_trans(int i) {
        return id_trans.get(i);
    }
    public ArrayList<String> getAll_url() {
        return url;
    }
    public ArrayList<String> getAll_urlSite() {
        return urlSite;
    }

    public void setUrl(String url) {
        this.url.add(url);
    }
    public void setUrlSite(String urlSite) {
        this.urlSite.add(urlSite);
    }
    public void setTitle(String title) {
        this.title.add(title);
    }
    public void setType(String type) {
        this.type.add(type);
    }
    public void setId(String id) {
        this.id.add(id);
    }
    public void setSeason(String season) {
        this.seasons_count.add(season);
    }
    public void setEpisode(String episode) {
        this.episodes_count.add(episode);
    }
    public void setTranslator(String translator) {
        this.translator.add(translator);
    }
    public void setToken(String token) {
        this.token.add(token);
    }
    public void setId_trans(String id_trans) {
        this.id_trans.add(id_trans);
    }

    public void addItems (ItemVideo items) {
        this.url.addAll(items.url);
        this.urlSite.addAll(items.urlSite);
        this.title.addAll(items.title);
        this.id.addAll(items.id);
        this.seasons_count.addAll(items.seasons_count);
        this.episodes_count.addAll(items.episodes_count);
        this.type.addAll(items.type);
        this.token.addAll(items.token);
        this.id_trans.addAll(items.id_trans);
        this.translator.addAll(items.translator);
    }
}
