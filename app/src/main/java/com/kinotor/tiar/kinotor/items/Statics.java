package com.kinotor.tiar.kinotor.items;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tiar on 04.2018.
 */
public class Statics {
    public static int CATALOG_W = 175;
    public static int CATALOG_H = 295;
    public static boolean firsStart = true;
    public static boolean refreshMain = false;
    public static boolean backClick = false;
    public static boolean hideTs = false;
    public static boolean rateImdb = true;
    public static String torS = "";

    public static String FILMIX_HIST = "null";

    public static String CATALOG = "filmix";

    public static String AMCET_URL = "https://amcet.net";
    public static String KOSHARA_URL = "http://koshara777.net";
    public static String KINOFS_URL = "http://kino-fs.ucoz.net";
    public static String KINOXA_URL = "http://kinoxa.me";
    public static String RUFILMTV_URL = "http://rufilmtv.club";
    public static String TOPKINO_URL = "https://infilms.ru";
    public static String MYHIT_URL = "https://my-hit.org";

    public static String ANIMEVOST_URL = "http://animevost.org";
    public static String COLDFILM_URL = "http://coldfilm.cc";
    public static String FANSERIALS_URL = "http://fanserials.email";
    public static String FANSERIALS_COOKIE = "null";

    public static String KINOSHA_URL = "http://kinosha.se";
    public static String MOONWALK_URL = "http://smartportaltv.ru/20/4.php?url=";
    public static String MOVIESHD_URL = "http://movies-hd.pp.ua";
    public static String KINOHD_URL = "https://kino-v-hd.com";
    public static String KINOLIVE_URL = "http://kino-live2.life";
    public static String KINODOM_URL = "http://kino-dom.tv";
    public static String FILMIX_URL = "https://filmix.today";
    public static String KINOPUB_URL = "https://kinotor.kinopub.club";
    public static String ZOMBIEFILM_URL = "https://zombie-film.com";
    public static String ZOMBIEFILM_URL_True = "https://zombie-film.com";
    public static String ANIDUB_URL = "https://anidub.tv";
    public static String ANIDUB_TR_URL = "https://tr.anidub.com";
    public static String ANIMEDIA_URL = "http://online.animedia.tv";


    public static String KP_URL = "https://www.kinopoisk.ru";

    public static String KP_ID = "error";
    public static String MOON_ID = "error";
    public static String FILMIX_COOCKIE = "dle_user_id=deleted, dle_password=deleted, dle_hash=deleted, remember_me=deleted";
    public static String FILMIX_ACC = "";
    public static Boolean FILMIX_PRO = false;


    public static String KINOZAL_COOCKIE = "uid=deleted, pass=deleted, domain=.kinozal.tv";
    public static String KINOZAL_ACC = "";

    public static String HURTOM_COOCKIE = "toloka_data=deleted, toloka_sid=deleted, toloka_ssl=deleted";
    public static String HURTOM_ACC = "";
    public static String HURTOM_PASS = "";

    public static String ANIDUB_TR_COOCKIE = "dle_user_id=deleted, dle_password=deleted, dle_hash=deleted";
    public static String ANIDUB_TR_ACC = "";
    public static String ANIDUB_TR_PASS = "";

    public static String KINODOM_COOCKIE = "dle_user_id=deleted, dle_password=deleted, dle_hash=deleted, remember_me=deleted";
    public static String KINODOM_ACC = "";

    public static String KINOPUB_COOCKIE = "";

    public static String FREERUTOR_URL = "https://kinopad.club";
    public static String BITRU_URL = "http://bit-ru.org";
    public static String MEGAPEER_URL = "http://shad.megapeer.ru";
    public static String NNM_URL = "http://nnm-club-me.appspot.com";
    public static String RUTOR_URL = "http://the-rutor.org";
    public static String TPARSER_URL = "http://tparser.me";
    public static String BA3A_URL = "http://ba3a.net";
    public static String PIRATBIT_URL = "https://pb.wtf";
    public static String RUTRACKER_URL = "https://rutracker.org";
    public static String RUTRACKER_COOCKIE = "bb_session=deleted";
    public static String RUTRACKER_ACC = "";
    public static String GREENTEA_TR_URL = "http://tracker.green-teatv.com";
    public static String GREENTEA_TR_COOCKIE = "bb_data=deleted";
    public static String GREENTEA_TR_ACC = "";
    public static String KINOZAL_URL = "http://kinozal.tv";
    public static String HURTOM_URL = "https://toloka.to";
    public static String TORLOOK_URL = "https://torlook.info";


    public static String ProxyUse = "...";
    public static String ProxyCur = "адрес:порт";
    public static String ProxyUA = "178.54.214.101:44735";
    public static String ProxyRU = "176.99.209.9:46507";

    public static ItemVideo itemsVidVoice = null;
    public static ItemVideo itemsVidSeason = null;
    public static ItemVideo itemsVideo = null;
    public static ItemHtml itemLast = null;

    public static String[] videoList = null;
    public static String[] videoListName = null;

    public static String videoBase = "";
    public static String torrentBase = "";
    public static List<String> list = new ArrayList<>();


    public static String newVerLog = "";

    public static Intent video = null;
    public static boolean adbWached = false;
    public static String curReclam = "error";
    public static String curAct = "error";

    public static int CATALOG_H () {
        if (CATALOG.toLowerCase().equals("fanserials") ||
                CATALOG.toLowerCase().equals("rufilmtv"))
            return 185;
        else return 295;
    }

    public static void defDomen() {
        AMCET_URL = "https://amcet.net";
        KOSHARA_URL = "http://octopushome.org";
        KINOFS_URL = "http://kino-fs.ucoz.net";
        KINOXA_URL = "http://kinoxa.me";
        RUFILMTV_URL = "http://rufilmtv.club";
        TOPKINO_URL = "https://infilms.ru";
        MYHIT_URL = "https://my-hit.org";

        ANIMEVOST_URL = "http://animevost.org";
        COLDFILM_URL = "http://coldfilm.cc";
        FANSERIALS_URL = "http://fanserials.email";

        KINOSHA_URL = "http://kinosha.se";
        MOONWALK_URL = "http://smartportaltv.ru/20/4.php?url=";
        MOVIESHD_URL = "http://movies-hd.pp.ua";
        KINOHD_URL = "https://kino-v-hd.com";
        KINOLIVE_URL = "http://kino-live2.life";
        KINODOM_URL = "http://kino-dom.tv";
        FILMIX_URL = "https://filmix.today";
        KINOPUB_URL = "https://kinotor.kinopub.club";
        ZOMBIEFILM_URL = "https://zombie-film.com";
        ZOMBIEFILM_URL_True = "https://zombie-film.com";
        ANIDUB_URL = "https://anidub.tv";
        ANIDUB_TR_URL = "https://tr.anidub.com";
        ANIMEDIA_URL = "http://online.animedia.tv";

        FREERUTOR_URL = "https://kinopad.club";
        BITRU_URL = "http://bit-ru.org";
        MEGAPEER_URL = "http://shad.megapeer.ru";
        NNM_URL = "http://nnm-club-me.appspot.com";
        RUTOR_URL = "http://the-rutor.org";
        TPARSER_URL = "http://tparser.me";
        BA3A_URL = "http://ba3a.net";
        PIRATBIT_URL = "https://pb.wtf";
        RUTRACKER_URL = "https://rutracker.org";
        GREENTEA_TR_URL = "http://tracker.green-teatv.com";
        KINOZAL_URL = "http://kinozal.tv";
        HURTOM_URL = "https://toloka.to";
        TORLOOK_URL = "https://torlook.info";
    }
}
