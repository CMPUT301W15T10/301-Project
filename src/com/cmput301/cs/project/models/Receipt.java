package com.cmput301.cs.project.models;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

/**
 * This class creates an instance that will hold the receipt image for a particular expense.
 * If the image exceeds the size limit of 65536 bytes, an exception error occurs.
 * Stores the receipt in String {@code Base64} format compatible with ElasticSearch
 */
public class Receipt {
    public static final int MAX_FILE_SIZE = 65535;

    private final String mBase64String;

    public Receipt(String base64String) {
        Log.e("Receipt", "" + base64String.getBytes().length);
        if (base64String.getBytes().length > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File is too large");
        }

        mBase64String = base64String;
    }

    /*
     * @return the receipt as a {@code Bitmap}
     */
    public Bitmap getBitmap() {
        // http://stackoverflow.com/questions/3801760/android-code-to-convert-base64-string-to-bitmap
        // April 6, 2015
        byte[] imageAsBytes = Base64.decode(mBase64String.getBytes(), Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
    }
}
