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

package com.edmondapps.cs301.ass1.utils;

import android.content.Context;
import android.util.Log;
import com.edmondapps.cs301.ass1.model.Claim;
import com.edmondapps.cs301.ass1.model.Expense;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ClaimSaves {
    private static final String LOG_TAG = "ClaimSaves";
    private static final String FILE_NAME = "claims.json";
    private static final Type COLLECTION_TYPE = new TypeToken<List<Claim>>() {
    }.getType();

    private final Context mContext;
    private final Gson mGson;


    public ClaimSaves(Context context) {
        mContext = context;
        mGson = new GsonBuilder()
                .registerTypeAdapter(Expense.class, Expense.getInstanceCreator())
                .registerTypeAdapter(Claim.class, Claim.getInstanceCreator())
                .create();
    }

    public void saveAllClaims(Iterable<Claim> claims) {
        OutputStreamWriter writer = null;
        try {
            writer = new OutputStreamWriter(mContext.openFileOutput(FILE_NAME, Context.MODE_PRIVATE));
            mGson.toJson(claims, COLLECTION_TYPE, writer);
        } catch (FileNotFoundException e) {
            Log.e(LOG_TAG, "failed to write to file", e);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    public List<Claim> readAllClaims() {
        InputStreamReader reader = null;
        try {
            reader = new InputStreamReader(mContext.openFileInput(FILE_NAME));
            return mGson.fromJson(reader, COLLECTION_TYPE);
        } catch (FileNotFoundException e) {
            // fresh start
            return new ArrayList<Claim>();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ignored) {
            }
        }
    }
}
