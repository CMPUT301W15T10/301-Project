package com.cmput301.cs.project.controllers;

import com.cmput301.cs.project.model.Claim;
import com.cmput301.cs.project.model.ClaimsList;
import com.cmput301.cs.project.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for {@link com.cmput301.cs.project.model.ClaimsList ClaimsList}.
 * 
 * @author rozsa
 *
 */

public class ClaimListController {

    private final User mUser;
    private final ClaimsList mClaimsList;


    public ClaimListController(User user, ClaimsList claimsList) {
        mClaimsList = claimsList;
        mUser = user;
    }

    public void addClaim(Claim claim) {
        if(!claim.getClaimant().equals(mUser)){
            throw new IllegalArgumentException("Claim must have a user same as logged in user");
        }

        mClaimsList.addClaim(claim);
    }

    public List<Claim> getApprovableClaims(){
        //TODO: this won't work
        List<Claim> approvableClaims = new ArrayList<Claim>();

        for(Claim claim : mClaimsList.peekClaims()){
            if(claim.canApprove(mUser)){
                approvableClaims.add(claim);
            }
        }

        //return approvableClaims;
        return mClaimsList.peekClaims();

    }

    public List<Claim> getClaimantClaims() {
        return mClaimsList.peekClaims();
    }

}



