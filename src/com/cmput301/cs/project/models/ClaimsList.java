package com.cmput301.cs.project.models;

import android.content.Context;
import com.cmput301.cs.project.utils.LocalClaimSaver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Singleton</br>
 * A class that contains the ClaimList for the app. This is changed whenever a claim is created, edited or removed.
 * 
 * @author rozsa
 *
 */

public class ClaimsList {

    private final List<Claim> defaultClaims = Collections.unmodifiableList(new ArrayList<Claim>() {{
        add(new Claim.Builder(new User("jordan")).putDestinationAndReason("place", "reason").build());
        add(new Claim.Builder(new User("charles")).putDestinationAndReason("Paris", "love").
                startTime(System.currentTimeMillis() * 60).endTime(System.currentTimeMillis() * 60 + 1000000).
                putExpense(new Expense.Builder().description("Hotel").amount(BigDecimal.TEN).build()).build());

    }});

    private final List<Claim> mClaims;

    private static ClaimsList instance;
    private final LocalClaimSaver mClaimSaves;

    public static ClaimsList getInstance(Context context) {
        if(instance == null){
            instance = new ClaimsList(context);
        }

        return instance;
    }

    private ClaimsList(Context context) {
        mClaimSaves = LocalClaimSaver.ofAndroid(context);

        mClaims = mClaimSaves.readAllClaims();
        mClaims.addAll(defaultClaims);

    }

    private void serialize() {
        mClaims.removeAll(defaultClaims);
        mClaimSaves.saveAllClaims(mClaims);
        mClaims.addAll(defaultClaims);
    }

    public void addClaim(Claim claim) {
        mClaims.add(claim);

        serialize();
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

