package com.kinotor.tiar.kinotor.utils;

import com.kinotor.tiar.kinotor.items.ItemSearch;

import java.util.List;

/**
 * Created by Tiar on 04.2018.
 */
public interface OnTaskSearchCallback {
    void OnCompleted(List<ItemSearch> items);
}
