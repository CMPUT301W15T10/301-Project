package com.cmput301.cs.project.model;

import android.content.Context;
import com.cmput301.cs.project.utils.ClaimSaves;

import java.util.List;

/**
 * Singleton</br>
 * A class that contains the ClaimList for the app. This is changed whenever a claim is created, edited or removed.
 * 
 * @author rozsa
 *
 */

public class ClaimsList {

    private final Context mContext;
    private final List<Claim> mClaims;

    private static ClaimsList instance;
    private final ClaimSaves mClaimSaves;

    public static ClaimsList getInstance(Context context) {
        if(instance == null){
            instance = new ClaimsList(context);
        }

        return instance;
    }

    private ClaimsList(Context context) {
        mContext = context;
        mClaimSaves = ClaimSaves.ofAndroid(context);

        mClaims = mClaimSaves.readAllClaims();

    }

    public void addClaim(Claim claim) {
        mClaims.add(claim);

        serialize();
    }

    private void serialize() {
        mClaimSaves.saveAllClaims(mClaims);
    }

    public void editClaim(Claim old, Claim newClaim) {
        final int location = mClaims.indexOf(old);
        if (location < 0) return;
        mClaims.remove(old);
        mClaims.add(location, newClaim);
        serialize();
    }

    public void deleteClaim(Claim claim) {
        mClaims.remove(claim);

        serialize();
    }

    public List<Claim> peekClaims() {
        return mClaims;
    }

}

