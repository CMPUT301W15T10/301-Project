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

package com.cmput301.cs.project;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.cmput301.cs.project.model.ClaimUtils;
import com.cmput301.cs.project.model.User;

import java.util.HashMap;
import java.util.Map;


public final class App extends Application {
    private static final String USER_PREFERENCES = "USER_PREFERENCES";
    private static final String USER_ID = "USER_ID";
    private static final String USER_NAME = "USER_NAME";
    private final Map<String, Object> mObjectTransfer = new HashMap<String, Object>();

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }

    public void putObjectTransfer(String key, Object value) {
        ClaimUtils.nonNullnonEmptyOrThrow(key, "key");
        ClaimUtils.nonNullOrThrow(value, "value");
        mObjectTransfer.put(key, value);
    }

    public <T> T getObjectTransfer(String key) {
        if (!mObjectTransfer.containsKey(key)) {
            throw new IllegalStateException("key already used or never existed");
        }
        @SuppressWarnings("unchecked")
        final T object = (T) mObjectTransfer.get(key);
        mObjectTransfer.remove(key);
        return object;
    }

    public User getUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        String userName = sharedPreferences.getString(USER_NAME, null);
        String userId = sharedPreferences.getString(USER_ID, null);

        if(userId == null || userName == null) {
            return null;
        }

        User user = new User(userName, userId);

        return user;
    }

    public void createUser(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(USER_ID, user.getUserId().toString());
        editor.putString(USER_NAME, user.getUserName());

        editor.apply();

    }
}