package com.cmput301_project.model;

import android.graphics.drawable.Drawable;

import java.io.File;
import java.nio.channels.UnsupportedAddressTypeException;

/**
 * Created by Blaine on 03/03/2015.
 */


public class Receipt {
    private static final int MAX_FILE_SIZE = 65536;
    private final String mPath;

    public Receipt (String path){
        File file = new File(path);
        if(file.length() > MAX_FILE_SIZE){
            throw new UnsupportedOperationException();

            //TODO: limit file size by compressing etc.
        }
        mPath = path;
    }

    public Drawable getImage() {
        return Drawable.createFromPath(mPath);
    }
}
