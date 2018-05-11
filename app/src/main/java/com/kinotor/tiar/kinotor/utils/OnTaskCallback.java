package com.kinotor.tiar.kinotor.utils;

import com.kinotor.tiar.kinotor.items.ItemHtml;

import java.util.ArrayList;

/**
 * Created by Tiar on 04.2018.
 */
public interface OnTaskCallback {
    void OnCompleted(ArrayList<ItemHtml> items, ItemHtml itempath);
}
