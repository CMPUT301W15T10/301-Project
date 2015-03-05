package com.cmput301.cs.project.project.model;


import java.io.File;

/**
 * Created by Blaine on 03/03/2015.
 */

public class Receipt {
    public static final int MAX_FILE_SIZE = 65536;

    private final File mFile;

    public Receipt(String path) {
        this(new File(path));
    }

    public Receipt(File file) {
        if (file.length() > MAX_FILE_SIZE) {
            throw new UnsupportedOperationException();

            //TODO: limit file size by compressing etc.
        }
        mFile = file;
    }

    public File getFile() {
        return mFile;
    }
}