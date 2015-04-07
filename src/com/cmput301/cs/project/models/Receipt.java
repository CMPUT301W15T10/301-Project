package com.cmput301.cs.project.models;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

/**
 * This class creates an instance that will hold the receipt image file for a particular expense.
 * If the image file exceeds the size limit of 65536 bytes, an exception error occurs.
 */

public class Receipt {
    public static final int MAX_FILE_SIZE = 65536;

    private final String mBase64String;

    public Receipt(String base64String) {
        Log.e("Receipt", "" + base64String.getBytes().length);
        if (base64String.getBytes().length > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File is too large");
        }

        mBase64String = base64String;
    }

    // http://stackoverflow.com/questions/3801760/android-code-to-convert-base64-string-to-bitmap
    // April 6, 2015
    public Bitmap getBitmap() {
        byte[] imageAsBytes = Base64.decode(mBase64String.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
}
