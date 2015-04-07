package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.adapters.CommentAdapter;
import com.cmput301.cs.project.models.Claim;
import com.cmput301.cs.project.models.ClaimsList;

/**
 * Lists all comments and their authors. Takes a claim id via App.KEY_CLAIM_ID
 *
 * @author Blaine
 */

public class CommentListActivity extends Activity {
    ListView mComments;
    Claim mClaim;
    private ClaimsList mClaimList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comment_list);

        mComments = (ListView) findViewById(R.id.comments);
        mClaimList = ClaimsList.getInstance(this);

        String claimId = getIntent().getStringExtra(App.KEY_CLAIM_ID);
        if (claimId == null) {
            throw new IllegalStateException("Must have claim id passed in using KEY_CLAIM_ID");
        }

        mClaim = mClaimList.getClaimById(claimId);

        CommentAdapter adapter = new CommentAdapter(this, mClaim.peekComments());

        mComments.setAdapter(adapter);
    }
}