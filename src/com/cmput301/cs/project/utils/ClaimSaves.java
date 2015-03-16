/*
 * Copyright 2015 Edmond Chui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cmput301.cs.project.utils;

/**
 * This class is used to save claims via saveAllClaims()
 *
 * It can be used by calling ClaimSaves.ofAndroid() *
 *
 */


import android.content.Context;
import android.util.Log;
import com.cmput301.cs.project.model.Claim;
import com.cmput301.cs.project.model.Expense;
import com.cmput301.cs.project.model.Tag;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class ClaimSaves {
    private static final String LOG_TAG = "ClaimSaves";
    private static final String CLAIMS_FILE_NAME = "claims.json";
    private static final Type CLAIMS_COLLECTION_TYPE = new TypeToken<List<Claim>>() {
    }.getType();

    private static final String TAGS_FILE_NAME = "tags.json";
    private static final Type TAGS_COLLECTION_TYPE = new TypeToken<List<Tag>>() {
    }.getType();

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Expense.class, Expense.getInstanceCreator())
            .registerTypeAdapter(Claim.class, Claim.getInstanceCreator())
            .create();

    private static ClaimSaves sInstance;

    public static ClaimSaves ofAndroid(Context context) {
        if (sInstance == null) {
            sInstance = new AndroidClaimSaves(context);
        }
        return sInstance;
    }

    /**
     * @return the {@code Gson} instance that {@code ClaimSaves} uses
     */
    public static Gson getGson() {
        return GSON;
    }

    /**
     * Obtain the {@code InputStream} for reading the JSON string.
     * <em>Multiple calls should not return the same stream as it might have been closed externally.</em>
     *
     * @param fileName the file to read
     * @return the stream; must not be null
     * @throws IOException fails to obtain the stream; could mean file does not exists ({@link java.io.FileNotFoundException FileNotFoundException}).
     */
    protected abstract InputStream getInputStreamForReading(String fileName) throws IOException;

    /**
     * Obtain the {@code OutputStream} for writing the JSON string.
     * <em>Multiple calls should not return the same stream as it might have been closed externally.</em>
     *
     * @param fileName the file to save
     * @return the stream; must not be null
     * @throws IOException fails to obtain the stream; could mean file is in use
     */
    protected abstract OutputStream getOutputStreamForSaving(String fileName) throws IOException;

    /**
     * Saves all the claims to the file {@link #CLAIMS_FILE_NAME}. Overwrites the previous contents in the file.
     *
     * @param claims non-null instance of an {@link java.lang.Iterable Iterable}
     * @return if the operation is successful
     */
    public boolean saveAllClaims(Iterable<Claim> claims) {
        return saveAll(claims, CLAIMS_FILE_NAME, CLAIMS_COLLECTION_TYPE);
    }

    /**
     * Reads all the {@link Claim Claims} in the file {@link #CLAIMS_FILE_NAME}, in the same order in the file. The returned list is safe to be modified.
     *
     * @return a list of {@code Claims} in the file; otherwise, an empty list if the file does not exist; never null
     */
    public List<Claim> readAllClaims() {
        return readToList(CLAIMS_FILE_NAME, CLAIMS_COLLECTION_TYPE);
    }

    public boolean saveAllTags(Iterable<Tag> tags) {
        return saveAll(tags, TAGS_FILE_NAME, TAGS_COLLECTION_TYPE);
    }

    public List<Tag> readAllTags() {
        return readToList(TAGS_FILE_NAME, TAGS_COLLECTION_TYPE);
    }

    private <T> List<T> readToList(String fileName, Type type) {
        List<T> out = null;
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(getInputStreamForReading(fileName));
            out = GSON.fromJson(reader, type);
        } catch (IOException e) {
            // fresh start
            out = new ArrayList<T>();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "failed to close reader", e);
                if (out == null) {
                    out = new ArrayList<T>();
                }
            }
        }
        return out;
    }

    private <T> boolean saveAll(Iterable<T> iterable, String fileName, Type type) {
        boolean success;
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(getOutputStreamForSaving(fileName));
            GSON.toJson(iterable, type, writer);
            success = true;
        } catch (IOException e) {
            Log.e(LOG_TAG, "file might be in use", e);
            success = false;
        } catch (JsonIOException e) {
            Log.e(LOG_TAG, "failed to write to file", e);
            success = false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();  // also flushes
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "failed to close writer, file might be corrupted", e);
                success = false;
            }
        }
        return success;
    }

    private static final class AndroidClaimSaves extends ClaimSaves {

        private final Context mContext;

        public AndroidClaimSaves(Context context) {
            mContext = context.getApplicationContext();
        }

        @Override
        protected OutputStream getOutputStreamForSaving(String fileName) throws IOException {
            return mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
        }

        @Override
        protected InputStream getInputStreamForReading(String fileName) throws IOException {
            return mContext.openFileInput(fileName);
        }
    }
}
