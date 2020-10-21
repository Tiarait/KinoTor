package com.kinotor.tiar.kinotor.items;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class ItemVideo {
    public ArrayList<String> url = new ArrayList<>();
    public ArrayList<String> urlSite = new ArrayList<>();
    public ArrayList<String> urlTrailer = new ArrayList<>();
    public ArrayList<String> title = new ArrayList<>();
    public ArrayList<String> id = new ArrayList<>();
    public ArrayList<String> seasons_count = new ArrayList<>();
    public ArrayList<String> episodes_count = new ArrayList<>();
    public ArrayList<String> type = new ArrayList<>();
    public ArrayList<String> token = new ArrayList<>();
    public ArrayList<String> id_trans = new ArrayList<>();
    public ArrayList<String> translator = new ArrayList<>();
    public ArrayList<String> description = new ArrayList<>();

    public String getUrl(int i) {
        try {
            return url.get(i);
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    public String getUrlSite(int i) {
        try {
            return urlSite.get(i);
        } catch (Exception e) {
            
            return "error";
        }
    }
    public String getUrlTrailer(int i) {
        try {
            return urlTrailer.get(i);
        } catch (Exception e) {
            
            return "error";
        }
    }
    public String getTitle(int i) {
        try {
            return title.get(i);
        } catch (Exception e) {
            return "error";
        }
    }
    public String getType(int i) {
        try {
            return type.get(i);
        } catch (Exception e){
            return "error";
        }
    }
    public String getId(int i) {
        try {
            return id.get(i);
        } catch (Exception e) {
            return "error";
        }
    }
    public String getSeason(int i) {
        try {
            return seasons_count.get(i);
        } catch (Exception e) {
            return "error";
        }
    }
    public String getEpisode(int i) {
        try {
            return episodes_count.get(i);
        } catch (Exception e) {
            return "error";
        }
    }
    public String getTranslator(int i) {
        try {
            return translator.get(i);
        } catch (Exception e) {
            return "error";
        }
    }
    public String getToken(int i) {
        try {
            return token.get(i);
        } catch (Exception e) {
            return "error";
        }
    }
    public String getId_trans(int i) {
        try {
            return id_trans.get(i);
        } catch (Exception e) {
            return "error";
        }
    }
    public String getDescription(int i) {
        try {
            return description.get(i);
        } catch (Exception e) {
            return "error";
        }
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
    public void setUrlTrailer(String urlTrailer) {
        this.urlTrailer.add(urlTrailer);
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
    public void setDescription(String description) {
        this.description.add(description);
    }

    public void addItems (ItemVideo items) {
        this.url.addAll(items.url);
        this.urlSite.addAll(items.urlSite);
        this.urlTrailer.addAll(items.urlTrailer);
        this.title.addAll(items.title);
        this.id.addAll(items.id);
        this.seasons_count.addAll(items.seasons_count);
        this.episodes_count.addAll(items.episodes_count);
        this.type.addAll(items.type);
        this.token.addAll(items.token);
        this.id_trans.addAll(items.id_trans);
        this.translator.addAll(items.translator);
        this.description.addAll(items.description);
    }
}
