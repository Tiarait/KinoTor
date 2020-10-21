package com.kinotor.tiar.kinotor.items;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class ItemNewVideo {
    String url;
    String urlSite;
    ArrayList<String> allUrlSite;
    String urlTrailer;
    String title;
    String id;
    String seasons_count;
    String episodes_count;
    String type;
    String token;
    String id_trans;
    String translator;
    String description;

    public String getUrl() {
        return url;
    }

    public String getUrlSite() {
        return urlSite;
    }

    public ArrayList<String> getAllUrlSite() {
        return allUrlSite;
    }

    public String getUrlTrailer() {
        return urlTrailer;
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getSeasons_count() {
        return seasons_count;
    }

    public String getEpisodes_count() {
        return episodes_count;
    }

    public String getType() {
        return type;
    }

    public String getToken() {
        return token;
    }

    public String getId_trans() {
        return id_trans;
    }

    public String getTranslator() {
        return translator;
    }
    public String getDescription() {
        return description;
    }

    public ItemNewVideo(
            String url,
            String urlSite,
            ArrayList<String> allUrlSite,
            String urlTrailer,
            String title,
            String id,
            String seasons_count,
            String episodes_count,
            String type,
            String token,
            String id_trans,
            String translator,
            String description) {
        this.url = url;
        this.urlSite = urlSite;
        this.allUrlSite = allUrlSite;
        this.urlTrailer = urlTrailer;
        this.title = title;
        this.id = id;
        this.seasons_count = seasons_count;
        this.episodes_count = episodes_count;
        this.type = type;
        this.token = token;
        this.id_trans = id_trans;
        this.translator = translator;
        this.description = description;
    }
}
