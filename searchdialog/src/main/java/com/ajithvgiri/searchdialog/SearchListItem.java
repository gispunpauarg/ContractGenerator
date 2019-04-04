package com.ajithvgiri.searchdialog;

import android.graphics.drawable.Drawable;

/**
 * Created by ajithvgiri on 06/11/17.
 */

public class SearchListItem {
    int id;
    String title;
    Drawable icon;

    public SearchListItem(int id, String title, Drawable icon) {
        this.id = id;
        this.title = title;
        this.icon = icon;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Drawable getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return title;
    }


}
