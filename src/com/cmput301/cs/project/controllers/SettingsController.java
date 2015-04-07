package com.cmput301.cs.project.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import com.cmput301.cs.project.models.Destination;
import com.google.android.gms.maps.model.LatLng;

/**
 * Bridges Java objects and {@link SharedPreferences}, saves and loads items.
 * <p/>
 * Use {@link #get(Context)} to obtain the singleton.
 */
public class SettingsController {
    private static final String PREF_NAME = "HOME_LOCATION";
    private static final String KEY_NAME = "HOME_NAME";
    private static final String KEY_LATLONG = "HOME_LATLONG";

    public static final float DISTANCE_CITY = 200f * 1000;  // 200km: Edmonton <-> Calgary
    public static final float DISTANCE_GLOBE = 800f * 1000;  // 800km: Edmonton <-> Vancouver

    /**
     * Obtains the singleton for {@code SettingsController}.
     *
     * @param context non-null instance of {@link Context}
     * @return the singleton
     */
    public static SettingsController get(Context context) {
        if (sInstance == null) {
            sInstance = new SettingsController(context);
        }
        return sInstance;
    }

    private static SettingsController sInstance;

    private final Context mContext;

    private SettingsController(Context context) {
        mContext = context.getApplicationContext();
    }

    /**
     * Returns if the {@link LatLng} equals to the home location. If home isn't set or {@code latLng} is null, returns false
     *
     * @param latLng an instance of {@link LatLng}
     * @return if the {@code LatLng} equals to the home location; false if home isn't set, or {@code latLng} is null
     * @see #saveHomeAsDestination(Destination)
     */
    public boolean isLocationHome(LatLng latLng) {
        final String latLongStr = getPreferences().getString(KEY_LATLONG, null);
        if (latLongStr == null || latLng == null) return false;
        final LatLng home = parseLatLng(latLongStr);
        return latLng.equals(home);
    }

    /**
     * Loads home from {@code SharedPreferences}. Never returns null,
     * but subsequent calls to {@link Destination#getName()} or {@link Destination#getLocation()} can be null.
     *
     * @return non-null instance of {@link Destination}
     * @see #saveHomeAsDestination(Destination)
     */
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

    /**
     * Saves a {@link Destination} as home.
     *
     * @param destination non-null instance of {@code Destination}
     * @see #loadHomeAsDestination()
     */
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

    /**
     * Obtains the {@link Color} code based on the {@link #distanceFromHome(LatLng) distance} from home.
     *
     * @param latLng the target {@code LatLng}; nullable
     * @return the {@link Color} code
     */
    public int colourForLatLng(LatLng latLng) {
        final LatLng home = loadHomeAsDestination().getLocation();
        if (home == null || latLng == null) return Color.TRANSPARENT;

        final float distance = distanceFromHome(latLng);
        if (distance > DISTANCE_GLOBE) {
            return Color.RED;
        } else if (distance > DISTANCE_CITY) {
            return Color.YELLOW;
        } else {
            return Color.GREEN;
        }
    }

    /**
     * Calculates distance from home in meters. Distance for home must be saved previously or this method will fail.
     *
     * @param latLng target {@link LatLng}
     * @return distance from home in meters
     * @see #saveHomeAsDestination(Destination)
     */
    public float distanceFromHome(LatLng latLng) {
        final LatLng home = loadHomeAsDestination().getLocation();
        final double homeLat = home.latitude;
        final double homeLong = home.longitude;
        final double targetLat = latLng.latitude;
        final double targetLong = latLng.longitude;
        final float[] result = new float[1];
        Location.distanceBetween(homeLat, homeLong, targetLat, targetLong, result);
        return result[0];
    }

    private static String serializeLatLng(LatLng latLng) {
        return latLng.latitude + "," + latLng.longitude;
    }

    private static LatLng parseLatLng(String string) {
        final String[] split = string.split(",");
        return new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
    }

    private SharedPreferences getPreferences() {
        return mContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
