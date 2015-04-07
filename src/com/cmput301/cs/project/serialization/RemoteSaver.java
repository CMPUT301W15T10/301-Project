package com.cmput301.cs.project.serialization;



import android.util.Log;
import com.cmput301.cs.project.serialization.elasticsearch.SearchResponse;
import com.cmput301.cs.project.models.Saveable;
import com.google.gson.Gson;

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

/**
 * This class allows any class implementing Saveable to be saved and retrived from an elastic search server.
 *
 * The given Type must be a SearchResponse<Claim> due to Java restrictions.
 *
 * Example Usage:
 *
 * Type type = new TypeToken<SearchResponse<Claim>>() {}.getType();
 * mRemoteClaimSaves = new RemoteSaver<Claim>("claims", type);
 *
 * claims = mRemoteClaimSaves.loadAll();
 * mRemoteClaimSaves.saveAll();
 *
 *
 *
 * saveAll() is fire and forget. It uses a separate thread and if it fails it does so silently.
 *
 * loadAll() will block and wait until the action is complete.
 *
 */

public class RemoteSaver<T extends Saveable> {
    private static final String ES_URL = "http://cmput301.softwareprocess.es:8080/cmput301w15t10/";
    private static final String LOG_TAG = "RemoteSaver";
    private final Type mType;

    private String mIndex;

    /**
     * Constructs a RemoteSaver for saving to an elastic search index
     *
     * @param index The elastic search index corresponding to these items
     * @param type A typetoken of the form new TypeToken<SearchResponse<T>>() {}.getType(); SearchResponse is required because Java limitations
     */
    public RemoteSaver(String index, Type type) {
        mIndex = index;
        mType = type;
    }

    /**
     * Fire and forget when saving items.
     * @param items A list to be saved to the elastic search server.
     * @throws IOException
     */

    public void saveAll(final List<T> items) throws IOException {

        Thread thread = new Thread() {

            protected List<T> mItems = new ArrayList<T>(items);

            @Override
            public void run() {
                //http://developer.android.com/reference/java/net/HttpURLConnection.html [blaine1 april 5 2015]

                for (T item : mItems) {
                    try {

                        URL url = new URL(ES_URL + mIndex + "/" + item.getId());


                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setDoOutput(true);

                        Gson gson = new Gson();
                        OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream());

                        gson.toJson(item, writer);

                        writer.flush();

                        urlConnection.disconnect();

                    } catch (MalformedURLException e) {
                        Log.d(LOG_TAG, "MAL URL" + e.toString());
                    } catch (IOException e) {
                        Log.d(LOG_TAG, "IO EXC: " + e.toString());
                    }
                }

            }
        };

        thread.start();

    }

    /**
     * Loads all T from the elastic search index given.
     *
     * @return The list of T that was read from the server. Empty or null if it failed
     * @throws IOException
     */
    public List<T> readAll() throws IOException {
        List<T> items;

        try {
            //http://developer.android.com/reference/java/net/HttpURLConnection.html [blaine1 april 5 2015]
            URL url = new URL(ES_URL + mIndex + "/_search?size=1000000");

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);

            InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());

            Gson gson = new Gson();

            SearchResponse<T> resp = gson.fromJson(in, mType);

            items = resp.getSources();


            if (items == null) {
                items = new ArrayList<T>();
            }

            urlConnection.disconnect();
        } catch (IOError e) {
            Log.d(LOG_TAG, "IO error " + e.toString());
            throw new IOException();

        } catch (MalformedURLException e) {
            Log.d(LOG_TAG, "MAL URL  " + e.toString());

            throw new IOException();

        } catch (IOException e) {
            Log.d(LOG_TAG, "IO EXC  " + e.toString());

            throw new IOException();
        }

        return items;
    }

}
