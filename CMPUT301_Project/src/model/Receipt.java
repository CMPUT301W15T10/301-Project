package com.cmput301_project.model;

import android.graphics.drawable.Drawable;

/**
 * Created by Blaine on 03/03/2015.
 */


public class Receipt {
    private final String mPath;

    public Receipt (String path){
        mPath = path;
    }

    public Drawable getImage() {
        return Drawable.createFromPath(mPath);
    }
}
