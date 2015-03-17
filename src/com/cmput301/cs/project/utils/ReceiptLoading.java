package com.cmput301.cs.project.utils;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/*
 * This class is meant to provide a standard way to get the Uri for a receipt.
 */
public class ReceiptLoading {
    private static String LOG_TAG = "ReceiptLoading";

    /*
     * @return Will return the {link @Uri} for the given expense id. Each expenseId will have a unique receipt file.
     */
    public static Uri getReceiptUri(String expenseId) {
        File file = new File(getStorageFolder(), expenseId + ".jpg");
        return Uri.fromFile(file);
    }

    // This is from http://developer.android.com/training/basics/data-storage/files.html
    // March 15, 2015
    private static File getStorageFolder() {
        File file = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Was unable to make the directory " + file.toString());
        }

        return file;
    }
}
