package com.kinotor.tiar.kinotor.items;


/**
 * Created by Tiar on 02.2018.
 */

public class ItemNewTor {
  String tortitle;
  String torurl;
  String url;
  String torsize;
  String tormagnet;
  String torsid;
  String torlich;
  String torcontent;

  public String getTortitle() {
    return tortitle;
  }

  public String getTorurl() {
    if (torurl != null)
      return torurl;
    else return "error";
  }

  public String getUrl() {
    return url;
  }

  public String getTorsize() {
    return torsize;
  }

  public String getTormagnet() {
    if (tormagnet != null)
      return tormagnet;
    else return "error";
  }

  public String getTorsid() {
    return torsid;
  }

  public String getTorlich() {
    return torlich;
  }

  public String getTorcontent() {
    return torcontent;
  }

  public ItemNewTor(String title, String url, String size, String torrent, String magnet,
                    String sid, String lich, String content) {
    this.tortitle = title;
    this.url = url;
    this.torsize = size;
    this.torurl = torrent;
    this.tormagnet = magnet;
    this.torsid = sid;
    this.torlich = lich;
    this.torcontent = content;
  }
}
