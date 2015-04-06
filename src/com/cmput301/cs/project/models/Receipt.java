package com.cmput301.cs.project.models;


import android.net.Uri;

import java.io.File;

/**
 * This class creates an instance that will hold the receipt image file for a particular expense.
 * If the image file exceeds the size limit of 65536 bytes, an exception error occurs.
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

    public Uri getUri() {
        return Uri.fromFile(mFile);
    }

    public File getFile() {
        return mFile;
    }
}
