package com.kinotor.tiar.kinotor.items;

import java.util.ArrayList;

/**
 * Created by Tiar on 02.2018.
 */

public class ItemTorrent {
    public ArrayList<String> tortitle = new ArrayList<>();
    ArrayList<String> torurl = new ArrayList<>();
    ArrayList<String> url = new ArrayList<>();
    ArrayList<String> torsize = new ArrayList<>();
    ArrayList<String> tormagnet = new ArrayList<>();
    ArrayList<String> torsid = new ArrayList<>();
    ArrayList<String> torlich = new ArrayList<>();
    ArrayList<String> torcontent = new ArrayList<>();

    public String getTorTitle(int i) {
        return tortitle.get(i);
    }
    public String getTorUrl(int i) {
        return torurl.get(i);
    }
    public String getUrl(int i) {
        return url.get(i);
    }
    public String getTorSize(int i) {
        return torsize.get(i);
    }
    public String getTorMagnet(int i) {
        return tormagnet.get(i);
    }
    public String getTorSid(int i) {
        return torsid.get(i);
    }
    public String getTorLich(int i) {
        return torlich.get(i);
    }
    public String getTorContent(int i) {
        return torcontent.get(i);
    }

    public void setTorTitle(String i) {
        this.tortitle.add(i);
    }
    public void setTorUrl(String i) {
        this.torurl.add(i);
    }
    public void setUrl(String i) {
        this.url.add(i);
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

    public void addHtmlItems (ItemHtml items) {
        this.tortitle.addAll(items.tortitle);
        this.torurl.addAll(items.torurl);
        this.url.addAll(items.toru);
        this.torsize.addAll(items.torsize);
        this.tormagnet.addAll(items.tormagnet);
        this.torsid.addAll(items.torsid);
        this.torlich.addAll(items.torlich);
        this.torcontent.addAll(items.torcontent);
    }

    public void addItems (ItemTorrent items) {
        this.tortitle.addAll(items.tortitle);
        this.torurl.addAll(items.torurl);
        this.url.addAll(items.url);
        this.torsize.addAll(items.torsize);
        this.tormagnet.addAll(items.tormagnet);
        this.torsid.addAll(items.torsid);
        this.torlich.addAll(items.torlich);
        this.torcontent.addAll(items.torcontent);
    }
}
