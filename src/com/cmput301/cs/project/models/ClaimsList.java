package com.cmput301.cs.project.models;

import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;
import com.cmput301.cs.project.utils.LocalClaimSaver;
import com.cmput301.cs.project.utils.RemoteClaimSaver;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Singleton<p>
 * A class that contains the ClaimList for the app. This is changed whenever a claim is created, edited or removed.
 * <p>
 * The most important methods used are:
 * <ul>
 * <li> addClaim(claim), which is used to store a new claim into the list
 * <li> deleteClaim(claim), which is used to delete a specific claim from the list
 * <li> editClaim(oldClaim, newClaim), which is used to replace a claim in the list with a more recently updated version of itself. It is important to note that when editing a claim you should not edit it directly but instead edit a copy of the claim because you need the original claim in order to update the list using this method.
 * </ul>
 * This model is used when populating the {@link com.cmput301.cs.project.activities.ClaimListActivity ClaimListActivity}, {@link com.cmput301.cs.project.activities.ClaimViewActivity ClaimViewActivity}, and {@link com.cmput301.cs.project.activities.ExpenseListActivity ExpenseListActivity}. <p>
 * It is used in the {@link com.cmput301.cs.project.controllers.ClaimListController ClaimListController} to control this activities as well.
 * @author rozsa
 * @author jbenson
 *
 */

public class ClaimsList {


    private static final String LOG_TAG = "ClaimsList";
    private List<Claim> mClaims = new ArrayList<Claim>();

    private static ClaimsList instance;
    private final LocalClaimSaver mClaimSaves;
    private final RemoteClaimSaver mRemoteClaimSaves;
    private final Context mContext;

    public static ClaimsList getInstance(Context context) {
        if(instance == null){
            instance = new ClaimsList(context);
        }

        return instance;
    }

    private ClaimsList(Context context) {
        mContext = context;

        mClaimSaves = LocalClaimSaver.ofAndroid(context);
        mRemoteClaimSaves = RemoteClaimSaver.ofAndroid(context);

        mergeAllClaims();

    }
    /**
     * 
     */
    private void mergeAllClaims() {
        List<Claim> claims = new ArrayList<Claim>();
        List<Claim> remoteClaims = new ArrayList<Claim>();

        //http://stackoverflow.com/questions/12650921/quick-fix-for-networkonmainthreadexception [blaine1 april 05 2015]

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            remoteClaims = mRemoteClaimSaves.readAllClaims();
        } catch (IOException ex) {
            Toast.makeText(mContext, "Failed to connect to server. In Local mode.", Toast.LENGTH_LONG);
        }

        List<Claim> localClaims = mClaimSaves.readAllClaims();

        for (Iterator<Claim> it = localClaims.iterator(); it.hasNext(); ) {
            Claim next =  it.next();

            Claim rem = null;
            for (Claim remoteClaim : remoteClaims) {
                if(remoteClaim.getId().equals(next.getId())){
                    rem = remoteClaim;
                }
            }

            if(rem == null) {
                claims.add(next);
            } else {

                remoteClaims.remove(rem);

                if(rem.getModified() > next.getModified()){
                    claims.add(rem);
                } else {
                    claims.add(next);
                }
            }
        }

        claims.addAll(remoteClaims);

        try {
            mRemoteClaimSaves.saveAllClaims(claims);
        } catch (IOException e) {
            Log.d(LOG_TAG, "Failed to save claims remotely.");
        }

        this.mClaims = claims;
    }

    public void addClaim(Claim claim) {
        mClaims.add(claim);

        serialize();
    }
    
    public void deleteClaim(Claim claim) {
        Iterator<Claim> iterator = mClaims.iterator();
        while (iterator.hasNext()) {
            Claim current = iterator.next();
            if (current.getId().equals(claim.getId())) {
                iterator.remove();
            }
        }

        serialize();
    }
    
    public void editClaim(Claim oldClaim, Claim newClaim) {

        Iterator<Claim> iterator = mClaims.iterator();
        while (iterator.hasNext()) {
            Claim current = iterator.next();
            if (current.getId().equals(oldClaim.getId())) {
                iterator.remove();
            }
        }

        mClaims.add(newClaim);

        serialize();
    }
    
    private void serialize() {
        mClaimSaves.saveAllClaims(mClaims);

        mergeAllClaims();
    }

    public List<Claim> peekClaims() {
        return mClaims;
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

