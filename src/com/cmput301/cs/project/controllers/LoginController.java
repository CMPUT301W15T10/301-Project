package com.cmput301.cs.project.controllers;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;
import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.elasticsearch.SearchResponse;
import com.cmput301.cs.project.models.User;
import com.cmput301.cs.project.utils.RemoteSaver;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class LoginController {

    private static final String USER_INDEX = "users";
    private static final String LOG_TAG = "LoginController";
    private final Context mContext;

    public LoginController(Context context) {
        mContext = context;
    }

    public void attemptLogin(String username) {
        Type type = new TypeToken<SearchResponse<User>>() {
        }.getType();

        RemoteSaver<User> userLoader = new RemoteSaver<User>(USER_INDEX, type);
        List<User> users = null;

        //http://stackoverflow.com/questions/12650921/quick-fix-for-networkonmainthreadexception [blaine1 april 05 2015]

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            users = userLoader.readAll();
        } catch (IOException e) {
            useStoredUsers();
            return;
        }

        if (users == null) {
            useStoredUsers();
            return;
        }

        for (User user : users) {
            if (user.getUserName().equals(username)) {
                App.get(mContext).setUser(user);
                return;
            }
        }

        User newUser = new User(username);

        users.add(newUser);

        App.get(mContext).setUser(newUser);

        try {
            userLoader.saveAll(users);
        } catch (IOException e) {
            Log.d(LOG_TAG, "uh ohhhh");
        }
    }

    private void useStoredUsers() {
        User user = App.get(mContext).getStoredUser();

        if (user == null) {
            Toast.makeText(mContext, "You have no internet and haven't logged in,", Toast.LENGTH_LONG).show();
        } else {
            App.get(mContext).setUser(user);
        }
    }
}
