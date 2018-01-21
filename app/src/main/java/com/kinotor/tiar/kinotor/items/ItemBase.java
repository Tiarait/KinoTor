package com.kinotor.tiar.kinotor.items;

import java.util.ArrayList;

/**
 * Created by Tiar on 13.10.2017.
 */

public class ItemBase {
    ArrayList<String> url = new ArrayList<>();
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

    public void setUrl(String url) {
        this.url.add(url);
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
}
