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

package com.cmput301_project.utils;

import android.content.Context;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import com.cmput301_project.model.Claim;
import com.cmput301_project.model.Expense;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public abstract class ClaimSaves {
    private static final String LOG_TAG = "ClaimSaves";
    private static final String FILE_NAME = "claims.json";
    private static final Type COLLECTION_TYPE = new TypeToken<List<Claim>>() {
    }.getType();
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Expense.class, Expense.getInstanceCreator())
            .registerTypeAdapter(Claim.class, Claim.getInstanceCreator())
            .create();

    public static ClaimSaves ofAndroid(Context context) {
        return new AndroidClaimSaves(context);
    }

    public static ClaimSaves ofTest() {
        return new MockClaimSaves();
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
     * @return the stream; must not be null
     * @throws IOException fails to obtain the stream; could mean file does not exists ({@link java.io.FileNotFoundException FileNotFoundException}).
     */
    protected abstract InputStream getInputStreamForReading() throws IOException;

    /**
     * Obtain the {@code OutputStream} for writing the JSON string.
     * <em>Multiple calls should not return the same stream as it might have been closed externally.</em>
     *
     * @return the stream; must not be null
     * @throws IOException fails to obtain the stream; could mean file is in use
     */
    protected abstract OutputStream getOutputStreamForSaving() throws IOException;

    /**
     * Saves all the claims to the file {@link #FILE_NAME}. Overwrites the previous contents in the file.
     *
     * @param claims non-null instance of an {@link java.lang.Iterable Iterable}
     * @return if the operation is successful
     */
    public boolean saveAllClaims(Iterable<Claim> claims) {
        boolean success;
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(getOutputStreamForSaving());
            GSON.toJson(claims, COLLECTION_TYPE, writer);
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

    /**
     * Reads all the {@link Claim Claims} in the file {@link #FILE_NAME}, in the same order in the file. The returned list is safe to be modified.
     *
     * @return a list of {@code Claims} in the file; otherwise, an empty list if the file does not exist; never null
     */
    public List<Claim> readAllClaims() {
        List<Claim> out = null;
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(getInputStreamForReading());
            out = GSON.fromJson(reader, COLLECTION_TYPE);
        } catch (IOException e) {
            // fresh start
            out = new ArrayList<Claim>();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "failed to close reader", e);
                if (out == null) {
                    out = new ArrayList<Claim>();
                }
            }
        }
        return out;
    }

    private static final class AndroidClaimSaves extends ClaimSaves {

        private final Context mContext;

        public AndroidClaimSaves(Context context) {
            mContext = context;
        }

        @Override
        protected OutputStream getOutputStreamForSaving() throws IOException {
            return mContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
        }

        @Override
        protected InputStream getInputStreamForReading() throws IOException {
            return mContext.openFileInput(FILE_NAME);
        }
    }

    private static final class MockClaimSaves extends ClaimSaves {
        private String mJsonString;

        @Override
        protected InputStream getInputStreamForReading() throws IOException {
            final String string = mJsonString == null ? "" : mJsonString;
            return new ByteArrayInputStream(string.getBytes());
        }

        @Override
        protected OutputStream getOutputStreamForSaving() throws IOException {
            return new ByteArrayOutputStream() {
                @Override
                public void flush() throws IOException {
                    super.flush();
                    mJsonString = toString();
                }

                @Override
                public void close() throws IOException {
                    super.close();
                    mJsonString = toString();
                }
            };
        }
    }
}
