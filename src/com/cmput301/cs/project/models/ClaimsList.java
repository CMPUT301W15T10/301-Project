package com.cmput301.cs.project.models;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;
import com.cmput301.cs.project.serialization.elasticsearch.SearchResponse;
import com.cmput301.cs.project.serialization.LocalSaver;
import com.cmput301.cs.project.serialization.RemoteSaver;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton<p>
 * A class that contains the ClaimList for the app. This is changed whenever a claim is created, edited or removed.
 * <p/>
 * The most important methods used are:
 * <ul>
 * <li> addClaim(claim), which is used to store a new claim into the list
 * <li> deleteClaim(claim), which is used to delete a specific claim from the list
 * <li> editClaim(oldClaim, newClaim), which is used to replace a claim in the list with a more recently updated version of itself. It is important to note that when editing a claim you should not edit it directly but instead edit a copy of the claim because you need the original claim in order to update the list using this method.
 * </ul>
 * This model is used when populating the {@link com.cmput301.cs.project.activities.ClaimListActivity ClaimListActivity}, {@link com.cmput301.cs.project.activities.ClaimViewActivity ClaimViewActivity}, and {@link com.cmput301.cs.project.activities.ExpenseListActivity ExpenseListActivity}. <p>
 * It is used in the {@link com.cmput301.cs.project.controllers.ClaimListController ClaimListController} to control this activities as well.
 *
 * mergeAllClaims() takes both local and remote claims and decides which claim is the most recent and keeps that one.
 * @author rozsa
 * @author jbenson
 */

public class ClaimsList {


    private static final String LOG_TAG = "ClaimsList";
    private static final String CLAIM_ELASTIC_SEARCH_INDEX = "claims";
    private List<Claim> mClaims = new ArrayList<Claim>();

    private static ClaimsList instance;
    private final LocalSaver mClaimSaves;
    private final RemoteSaver<Claim> mRemoteClaimSaves;
    private final Context mContext;

    public static ClaimsList getInstance(Context context) {
        if (instance == null) {
            instance = new ClaimsList(context);
        }

        return instance;
    }

    private ClaimsList(Context context) {
        mContext = context;

        mClaimSaves = LocalSaver.ofAndroid(context);

        Type type = new TypeToken<SearchResponse<Claim>>() {}.getType();

        mRemoteClaimSaves = new RemoteSaver<Claim>(CLAIM_ELASTIC_SEARCH_INDEX, type);

        mergeAllClaims();
    }

    /**
     * Loads claims from elastic search and local and uses Claim.getModified() to determine the newest claim to keep
     * It is fault tolerant in the sense that if the server is not available due to any reason, it will still save them
     * once connectivity is back.
     */
    private void mergeAllClaims() {
        List<Claim> claims = new ArrayList<Claim>();
        List<Claim> remoteClaims = new ArrayList<Claim>();

        //http://stackoverflow.com/questions/12650921/quick-fix-for-networkonmainthreadexception [blaine1 april 05 2015]

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            remoteClaims = mRemoteClaimSaves.readAll();
        } catch (IOException ex) {
            Toast.makeText(mContext, "Failed to connect to server. In Local mode.", Toast.LENGTH_LONG).show();
        }

        for (Claim next : mClaimSaves.readAllClaims()) {
            Claim rem = null;
            for (Claim remoteClaim : remoteClaims) {
                if (remoteClaim.getId().equals(next.getId())) {
                    rem = remoteClaim;
                }
            }

            if (rem == null) {
                claims.add(next);
            } else {

                remoteClaims.remove(rem);

                if (rem.getModified() > next.getModified()) {
                    claims.add(rem);
                } else {
                    claims.add(next);
                }
            }
        }

        claims.addAll(remoteClaims);

        try {
            mRemoteClaimSaves.saveAll(claims);
        } catch (IOException e) {
            Log.d(LOG_TAG, "Failed to save claims remotely.");
        }

        this.mClaims = claims;
    }

    public void addClaim(Claim claim) {
        mClaims.add(claim);

        serialize();
    }

    public Claim getClaim(String claimId) {
        for (Claim claim : mClaims) {
            if (claim.getId().equals(claimId)) {
                return claim;
            }
        }

        return null;
    }

    public void deleteClaim(Claim claim) {
        editClaim(claim.edit().delete().build());

        serialize();
    }

    /**
     * Update the claim with the ID of the {@code newClaim}.
     * The old claim with the same id will be removed, and the {@code newClaim} will be added at the same location in the list.
     *
     * @param newClaim
     */
    public void editClaim(Claim newClaim) {
        for (int i = 0, mClaimsSize = mClaims.size(); i < mClaimsSize; i++) {
            final Claim claim = mClaims.get(i);
            if (claim.getId().equals(newClaim.getId())) {
                mClaims.remove(i);
                mClaims.add(i, newClaim);
                break;
            }
        }

        serialize();
    }

    private void serialize() {
        mClaimSaves.saveAllClaims(mClaims);

        mergeAllClaims();
    }

    public List<Claim> peekClaims() {
        return new ArrayList<Claim>(mClaims);
    }

    public Claim getClaimById(String id) {
        for (Claim claim : mClaims) {
            if (claim.getId().equals(id)) {
                return claim;
            }
        }

        return null;
    }
}

