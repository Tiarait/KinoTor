package com.kinotor.tiar.kinotor.items;

import java.util.ArrayList;
import java.util.List;

public class ItemMain {
    public static int cur_items = 0;
    public static String cur_url = "http://koshara.co/nerufilm/";
    //stat for parse base catalog
    public static String xs_search = "";
    public static String xs_field = "defaultsort";
    public static String xs_value = "";


    public static List<Item> ITEMS = new ArrayList<Item>();
    public void addItem(Item item) {
        ITEMS.add(item);
    }
    public void delItem() {
        ITEMS.clear();
    }


    public static class Item {
        public final int id;
        public final String name;
        final String details;

        public Item(int id, String name, String details) {
            this.id = id;
            this.name = name;
            this.details = details;
        }

        public String getName() {
            return name;
        }
        public String getdetails() {
            return details;
        }
    }
}
