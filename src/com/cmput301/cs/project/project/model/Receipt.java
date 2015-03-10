package com.cmput301.cs.project.project.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * Created by Blaine on 03/03/2015.
 */

public class Receipt implements Parcelable {
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

    protected Receipt(Parcel in) {
        mFile = (File) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(mFile);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Receipt> CREATOR = new Parcelable.Creator<Receipt>() {
        @Override
        public Receipt createFromParcel(Parcel in) {
            return new Receipt(in);
        }

        @Override
        public Receipt[] newArray(int size) {
            return new Receipt[size];
        }
    };
}