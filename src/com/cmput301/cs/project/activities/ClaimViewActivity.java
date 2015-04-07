package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.adapters.DestinationAdapter;
import com.cmput301.cs.project.controllers.TagsManager;
import com.cmput301.cs.project.listeners.TagsChangedListener;
import com.cmput301.cs.project.models.*;
import com.cmput301.cs.project.utils.Utils;

import java.text.DateFormat;

/**
 * The activity that is called when a item is clicked within {@link com.cmput301.cs.project.activities.ClaimListActivity ClaimListActivity}
 * that shows the specific details of that claim. <p>
 * Menu items allow {@link com.cmput301.cs.project.activities.EditClaimActivity EditClaimActivity} to be called on the claim
 * and for a claim to be deleted.<p>
 * The activity lists the StartDate, EndDate, Currencies, Status, Destinations and calls {@link com.cmput301.cs.project.activities.ExpenseListActivity ExpenseListActivity}
 * when the associated button is clicked.<p>
 * Returns to the {@link com.cmput301.cs.project.activities.ClaimListActivity ClaimListActivity} when Submit button is clicked.
 * <p>
 * A claim must be passed via an intent for this activity to work
 * The claim can be loaded out of the intent by using:
 * <pre>
 * getIntent().getExtras().getString(App.KEY_CLAIM_ID), which gives the claim id, then using ClaimList.getClaim(key)
 *</pre>
 *, where the KEY_CLAIM_ID is the key assigned to the claim when it was put into the intent. <p>
 *
 *The methods onTagDeleted and onTagRemoved ensure that the details of the tags attached to the claim are kept up to date. 
 *
 * @author rozsa
 */

public class ClaimViewActivity extends Activity implements TagsChangedListener {
    private static final int ADD_RETURN_COMMENT = 1;
    private static final int ADD_APPROVE_COMMENT = 2;
    private Claim mClaim;
    private Button mExpenseButton;
    private Button mSubmitButton;
    private Button mReturnButton;
    private Button mApproveButton;
    private TextView mStartDate;
    private TextView mEndDate;
    private TextView mStatus;
    private TextView mTags;
    private TextView mCurrency;
    private DateFormat mDateFormat;

    private ClaimsList mClaimList;
    private ListView mDestinations;
    private MenuItem mEditMenuItem;
    private User mUser;
    private Button mCommentButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_view_activity);

        mClaimList = ClaimsList.getInstance(this);
        mUser = App.get(this).getUser();

        mDateFormat = android.text.format.DateFormat.getMediumDateFormat(this);

        findViewsByIds();

        initButtons();

        TagsManager.get(this).addTagChangedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        reloadClaim();
        update();
    }

    private void reloadClaim() {
        String claimId = getIntent().getStringExtra(App.KEY_CLAIM_ID);
        if (claimId == null) {
            throw new IllegalStateException("Must have claim id passed in using KEY_CLAIM_ID");
        }

        mClaim = mClaimList.getClaimById(claimId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TagsManager.get(this).removeTagChangedListener(this);
    }

    private void findViewsByIds() {
        mExpenseButton = (Button) findViewById(R.id.expenseButton);
        mSubmitButton = (Button) findViewById(R.id.submitButton);
        mApproveButton = (Button) findViewById(R.id.approveButton);
        mReturnButton = (Button) findViewById(R.id.returnButton);
        mCommentButton = (Button) findViewById(R.id.commentsButton);

        mStartDate = (TextView) findViewById(R.id.startDate);
        mEndDate = (TextView) findViewById(R.id.endDate);
        mStatus = (TextView) findViewById(R.id.statusText);
        mDestinations = (ListView) findViewById(R.id.destinations);
        mTags = (TextView) findViewById(R.id.tags);
        mTags.setHint(R.string.tags_view_hint);
        mCurrency = (TextView) findViewById(R.id.currencies);
        mCurrency.setHint(R.string.currencies_view_hint);


    }

    private void update() {
        mStartDate.setText(mDateFormat.format(mClaim.getStartTime()));
        mEndDate.setText(mDateFormat.format(mClaim.getEndTime()));
        mStatus.setText(Utils.stringIdForClaimStatus(mClaim.getStatus()));
        mDestinations.setAdapter(new DestinationAdapter(this, mClaim.getDestinations()));
        mTags.setText(mClaim.getTagsAsString());
        mCurrency.setText(mClaim.getTotalsAsString());

        if (mEditMenuItem != null) {
            updateEditMenuItem();
            invalidateOptionsMenu();  // tell Android to draw
        }

        if((mClaim.getStatus() == Claim.Status.IN_PROGRESS ||  mClaim.getStatus() == Claim.Status.RETURNED) &&
                mClaim.getClaimant().equals(mUser)) {
            mSubmitButton.setVisibility(View.VISIBLE);
            mReturnButton.setVisibility(View.GONE);
            mApproveButton.setVisibility(View.GONE);
        } else if(mClaim.getStatus() == Claim.Status.SUBMITTED &&
                mClaim.canApprove(mUser)) {
            mSubmitButton.setVisibility(View.GONE);
            mReturnButton.setVisibility(View.VISIBLE);
            mApproveButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
            mReturnButton.setVisibility(View.GONE);
            mApproveButton.setVisibility(View.GONE);
        }

        if(mClaim.peekComments().size() == 0) {
            mCommentButton.setVisibility(View.GONE);
        } else {
            mCommentButton.setVisibility(View.VISIBLE);
        }
    }

    private void initButtons() {
        mExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClaimViewActivity.this, ExpenseListActivity.class);
                intent.putExtra(App.KEY_CLAIM_ID, mClaim.getId());
                startActivity(intent);
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mClaim.isCompleted()) {
                    new AlertDialog.Builder(ClaimViewActivity.this)
                            .setMessage("Submit an incomplete Claim?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final Claim newClaim = mClaim.edit().submitClaim().build();
                                    mClaimList.editClaim(newClaim);
                                    finish();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .create()
                            .show();
                } else {
                    final Claim newClaim = mClaim.edit().submitClaim().build();
                    mClaimList.editClaim(newClaim);

                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.claim_view, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mEditMenuItem = menu.findItem(R.id.editClaim);
        updateEditMenuItem();
        return true;
    }

    private void updateEditMenuItem() {
        final boolean editable = mClaim.isEditable();
        mEditMenuItem.setEnabled(editable);
        mEditMenuItem.getIcon().setAlpha(editable ? 255 : 255 / 2);
        // call invalidateOptionsMenu() outside of onPrepareOptionsMenu(Menu)
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteClaim:

                if (mClaim.isEditable()) {
                    mClaimList.deleteClaim(mClaim);
                    setResult(App.RESULT_DELETE);
                    finish();
                } else {
                    Toast.makeText(this, "Claim can no longer be deleted!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.editClaim:

                if (mClaim.isEditable()) {
                    startActivity(EditClaimActivity.intentWithClaimId(this, mClaim.getId()));
                } else {
                    Toast.makeText(this, "Claim can't be edited!", Toast.LENGTH_LONG).show();
                }

                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && (requestCode == ADD_APPROVE_COMMENT || requestCode == ADD_RETURN_COMMENT)) {
            String commentText = data.getStringExtra(CommentActivity.COMMENT_TEXT);

            Comment comment = new Comment(commentText, mUser);

            Claim newClaim;

            if(requestCode == ADD_APPROVE_COMMENT) {
                newClaim = mClaim.edit().approveClaim(mUser, comment).build();
            } else {
                newClaim = mClaim.edit().returnClaim(mUser, comment).build();
            }

            mClaimList.editClaim(newClaim);
            finish();
        }
    }

    @Override
    public void onTagRenamed(Tag tag, Tag oldTag) {
        mClaim = mClaim.edit().removeTag(oldTag).addTag(tag).build();
        update();
    }

    @Override
    public void onTagDeleted(Tag tag) {
        mClaim = mClaim.edit().removeTag(tag).build();
    }

    @Override
    public void onTagCreated(Tag tag) {
        // do nothing
    }

    public void returnClaim(View view) {

        Intent intent = new Intent(this, CommentActivity.class);

        startActivityForResult(intent, ADD_RETURN_COMMENT);
    }

    public void approveClaim(View view) {
        Intent intent = new Intent(this, CommentActivity.class);

        startActivityForResult(intent, ADD_APPROVE_COMMENT);
    }

    public void showComments(View view) {
        Intent intent = new Intent(this, CommentListActivity.class);
        intent.putExtra(App.KEY_CLAIM_ID, mClaim.getId());
        startActivity(intent);
    }
}


