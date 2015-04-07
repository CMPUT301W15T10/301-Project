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
import com.cmput301.cs.project.models.User;


/**
 * App is a controller of sorts that contains a series of methods for logging in
 * and miscellaneous tasks.
 */
public final class App extends Application {
    public static final String KEY_CLAIM_ID = "key_claim";
    public static final String KEY_EXPENSE_ID = "key_expense";

    public static final int RESULT_DELETE = 16;
    private static final String USER_ID = "user_id";
    private static final String USER_NAME = "user_name";
    private static final String USER_PREFERENCES = "user_preferences";
    private User mUser;

    public static App get(Context context) {
        return (App) context.getApplicationContext();
    }


    public User getUser() {
       return mUser;
    }

    public void setUser(User user) {
        this.mUser = user;
        setStoredUser(user);
    }

    public User getStoredUser() {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        String userName = sharedPreferences.getString(USER_NAME, null);
        String userId = sharedPreferences.getString(USER_ID, null);

        if (userId == null || userName == null || userId.isEmpty() || userName.isEmpty()) {
            return null;
        }

        return new User(userName, userId);
    }

    public void setStoredUser(User user) {
        SharedPreferences sharedPreferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(USER_ID, user.getId());
        editor.putString(USER_NAME, user.getUserName());

        editor.apply();

    }

}

