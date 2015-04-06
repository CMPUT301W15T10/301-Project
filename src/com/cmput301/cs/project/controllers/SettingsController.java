package com.cmput301.cs.project.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import com.cmput301.cs.project.models.Destination;
import com.google.android.gms.maps.model.LatLng;

public class SettingsController {
    public static final String PREF_NAME = "HOME_LOCATION";
    public static final String KEY_NAME = "HOME_NAME";
    public static final String KEY_LATLONG = "HOME_LATLONG";


    public static SettingsController get(Context context) {
        if (sInstance == null) {
            return new SettingsController(context);
        }
        return sInstance;
    }

    private static SettingsController sInstance;

    private final Context mContext;

    private SettingsController(Context context) {
        mContext = context.getApplicationContext();
    }

    public boolean isLocationHome(LatLng latLng) {
        final LatLng home = parseLatLng(getPreferences().getString(KEY_LATLONG, null));
        return latLng.equals(home);
    }

    public Destination loadHomeAsDestination() {
        final Destination.Builder builder = new Destination.Builder();
        final SharedPreferences pref = getPreferences();
        if (pref.contains(KEY_NAME)) {
            builder.name(pref.getString(KEY_NAME, null));
        }
        if (pref.contains(KEY_LATLONG)) {
            builder.location(parseLatLng(pref.getString(KEY_LATLONG, null)));
        }
        return builder.build();
    }

    public void saveHomeAsDestination(Destination destination) {
        final SharedPreferences.Editor pref = getPreferences().edit();
        final String name = destination.getName();
        if (name != null) {
            pref.putString(KEY_NAME, name);
        }
        final LatLng location = destination.getLocation();
        pref.putString(KEY_LATLONG, serializeLatLng(location));  // location is required
        pref.apply();
    }

    private SharedPreferences getPreferences() {
        return mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    private static String serializeLatLng(LatLng latLng) {
        return latLng.latitude + "," + latLng.longitude;
    }

    private static LatLng parseLatLng(String string) {
        final String[] split = string.split(",");
        return new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
    }
}
