package com.cmput301.cs.project.utils;

import android.content.Context;
import android.util.Log;
import com.cmput301.cs.project.elasticsearch.SearchResponse;
import com.cmput301.cs.project.models.Claim;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    public void saveAllClaims(List<Claim> claims) {

        try {

            for (Claim claim : claims) {
                URL url = new URL(ES_URL + "/" + claim.getId());

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);

                Gson gson = new Gson();

                gson.toJson(claim, new OutputStreamWriter(urlConnection.getOutputStream()));

                urlConnection.disconnect();
            }
        } catch (MalformedURLException e) {
            Log.d("malformed url", "yo yo yo");
        } catch (IOException e) {
            Log.d("IO Exception", "lollllllllllllllllllllllllllllllllll");
        }

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

            if(claims == null) {
                claims = new ArrayList<Claim>();
            }

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
