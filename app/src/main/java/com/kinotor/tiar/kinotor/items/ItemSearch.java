package com.kinotor.tiar.kinotor.items;

/**
 * Created by Tiar on 24.09.2017.
 */

public class ItemSearch {
    public String title;
    private String subtitle;
    private String img;
    private String url;

    public ItemSearch(String title, String subtitle, String img, String url) {
        setTitle(title);
        setSubtitle(subtitle);
        setImg(img);
        setUrl(url);
    }
    //------------------Get---------------------------

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getImg() {
        return img;
    }

    public String getUrl() {
        return url;
    }


    //------------------Set---------------------------

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setImg(String img) {
        this.img = img;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
