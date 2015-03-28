package com.cmput301.cs.project.controllers;

import com.cmput301.cs.project.models.Claim;
import com.cmput301.cs.project.models.ClaimsList;
import com.cmput301.cs.project.models.Tag;
import com.cmput301.cs.project.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for {@link com.cmput301.cs.project.models.ClaimsList ClaimsList}.
 *
 * @author rozsa
 */

public class ClaimListController implements TagsChangedListener {

    private final User mUser;
    private final ClaimsList mClaimsList;


    public ClaimListController(User user, ClaimsList claimsList) {
        mClaimsList = claimsList;
        mUser = user;
    }

    public void addClaim(Claim claim) {
        if (!claim.getClaimant().equals(mUser)) {
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

        return approvableClaims;

    }

    public List<Claim> getClaimantClaims() {
        List<Claim> claimantClaims = new ArrayList<Claim>();

        for(Claim claim : mClaimsList.peekClaims()){
            if(claim.getClaimant().equals(mUser)) {
                claimantClaims.add(claim);
            }
        }

        return claimantClaims;
    }

    @Override
    public void onTagRenamed(Tag tag, Tag oldTag) {
        for (Claim claim : mClaimsList.peekClaims()) {
            if (claim.peekTags().contains(oldTag)) {
                final Claim edited = claim.edit().removeTag(oldTag).addTag(tag).build();
                mClaimsList.editClaim(claim, edited);
            }
        }
    }

    @Override
    public void onTagDeleted(Tag tag) {
        for (Claim claim : mClaimsList.peekClaims()) {
            if (claim.peekTags().contains(tag)) {
                final Claim edited = claim.edit().removeTag(tag).build();
                mClaimsList.editClaim(claim, edited);
            }
        }
    }

    @Override
    public void onTagCreated(Tag tag) {
        // do nothing
    }
}



