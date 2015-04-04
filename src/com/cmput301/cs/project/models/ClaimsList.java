package com.cmput301.cs.project.models;

import android.content.Context;
import com.cmput301.cs.project.utils.LocalClaimSaver;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
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

	// this must be deleted after elastic search is working. It is just a couple test claims for the approver.
    private final List<Claim> defaultClaims = Collections.unmodifiableList(new ArrayList<Claim>() {{
        add(new Claim.Builder(new User("jordan")).putDestination(new Destination.Builder("place", "reason").build()).build());
        add(new Claim.Builder(new User("charles")).putDestination(new Destination.Builder("Paris", "love").build()).
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
    
    public void addClaim(Claim claim) {
        mClaims.add(claim);

        serialize();
    }
    
    public void deleteClaim(Claim claim) {
        mClaims.remove(claim);

        serialize();
    }
    
    public void editClaim(Claim old, Claim newClaim) {
        final int location = mClaims.indexOf(old);
        if (location < 0) return;
        mClaims.remove(old);
        mClaims.add(location, newClaim);
        serialize();
    }
    
    private void serialize() {
        mClaims.removeAll(defaultClaims);
        mClaimSaves.saveAllClaims(mClaims);
        mClaims.addAll(defaultClaims);
    }

    public List<Claim> peekClaims() {
        return mClaims;
    }

}

