package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.adapters.CommentAdapter;
import com.cmput301.cs.project.models.Claim;
import com.cmput301.cs.project.models.ClaimsList;
import com.cmput301.cs.project.models.Comment;

import java.util.List;

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

        CommentAdapter adapter = new CommentAdapter(this, R.layout.comment_list_item, mClaim.peekComments());

        mComments.setAdapter(adapter);
    }
}