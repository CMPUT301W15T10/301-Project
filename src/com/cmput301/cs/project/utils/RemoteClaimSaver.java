package com.cmput301.cs.project.utils;

import android.content.Context;
import com.cmput301.cs.project.elasticsearch.SearchResponse;
import com.cmput301.cs.project.models.Claim;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.cmput301.cs.project.*;

public class RemoteClaimSaver {
    private static final String ES_URL = "http://cmput301.softwareprocess.es:8080/cmput301w15t10/claims";

    Context mContext;

    private static RemoteClaimSaver sInstance;

    public static RemoteClaimSaver ofAndroid(Context context) {
        if (sInstance == null) {
            sInstance = new RemoteClaimSaver(context);
        }
        return sInstance;
    }

    private RemoteClaimSaver(Context context) {
        this.mContext = mContext;
    }

    public void saveAllClaims() {

    }
    public List<Claim> readAllClaims() throws IOException {
        List<Claim> claims;

        try {
            URL url = new URL(ES_URL + "/_search");

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());

            Gson gson = new Gson();

            Type type = new TypeToken<SearchResponse<Claim>>() {}.getType();

            SearchResponse<Claim> resp = gson.fromJson(in, type);

            claims = resp.getSources();

            urlConnection.disconnect();
        } catch (IOError err) {
            throw new IOException();

        } catch (MalformedURLException e) {
            throw new IOException();

        } catch (IOException e) {
            throw new IOException();
        }

        return claims;
    }

}
