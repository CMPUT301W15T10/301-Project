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
import com.cmput301.cs.project.controllers.TagsChangedListener;
import com.cmput301.cs.project.controllers.TagsManager;
import com.cmput301.cs.project.model.Claim;
import com.cmput301.cs.project.model.ClaimsList;
import com.cmput301.cs.project.model.Tag;
import com.cmput301.cs.project.utils.Utils;

import java.text.DateFormat;

/**
 * The activity that is called when a item is clicked within {@link com.cmput301.cs.project.activities.ClaimListActivity ClaimListActivity}
 * that shows the specific details of that claim. </br>
 * Menu items allow {@link com.cmput301.cs.project.activities.EditClaimActivity EditClaimActivity} to be called on the claim
 * and for a claim to be deleted.</br>
 * The activity lists the StartDate, EndDate, Currencies, Status, Destinations and calls {@link com.cmput301.cs.project.activities.ExpenseListActivity ExpenseListActivity}
 * when the associated button is clicked.</br>
 * Returns to the {@link com.cmput301.cs.project.activities.ClaimListActivity ClaimListActivity} when Submit button is clicked.
 * <p/>
 * A claim must be passed via an intent for this activity to work
 *
 * @author rozsa
 */

public class ClaimViewActivity extends Activity implements TagsChangedListener {

    private static final int EDIT_CLAIM = 0;
    Claim mClaim;
    Button mExpenseButton;
    Button mSubmitButton;
    TextView mStartDate;
    TextView mEndDate;
    TextView mStatus;
    TextView mTags;
    DateFormat mDateFormat;

    ClaimsList mClaimList;
    private ListView mDestinations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_view_activity);

        mClaimList = ClaimsList.getInstance(this);

        mClaim = getIntent().getExtras().getParcelable(App.KEY_CLAIM);

        mDateFormat = android.text.format.DateFormat.getMediumDateFormat(this);

        findViewsByIds();

        initButtons();

        update();

        TagsManager.get(this).addTagChangedListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TagsManager.get(this).removeTagChangedListener(this);
    }

    private void findViewsByIds() {
        mExpenseButton = (Button) findViewById(R.id.expenseButton);
        mSubmitButton = (Button) findViewById(R.id.submitButton);
        mStartDate = (TextView) findViewById(R.id.startDate);
        mEndDate = (TextView) findViewById(R.id.endDate);
        mStatus = (TextView) findViewById(R.id.statusText);
        mDestinations = (ListView) findViewById(R.id.destinations);
        mTags = (TextView) findViewById(R.id.tags);
        mTags.setHint(R.string.tags_view_hint);
    }

    private void update() {
        mStartDate.setText(mDateFormat.format(mClaim.getStartTime()));
        mEndDate.setText(mDateFormat.format(mClaim.getEndTime()));
        mStatus.setText(Utils.stringIdForClaimStatus(mClaim.getStatus()));
        mDestinations.setAdapter(new DestinationAdapter(this, mClaim.getDestinations()));
        mTags.setText(mClaim.getTagsAsString());
    }

    private void initButtons() {
        mExpenseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClaimViewActivity.this, ExpenseListActivity.class);
                intent.putExtra(App.KEY_CLAIM, mClaim);
                startActivity(intent);

            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mClaim.isCompleted()) {
                    new AlertDialog.Builder(ClaimViewActivity.this)
                            .setMessage("Submit an incomplete Claim?")
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final Claim newClaim = mClaim.edit().submitClaim().build();
                                    mClaimList.editClaim(mClaim, newClaim);
                                    finish();
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .create()
                            .show();
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.claim_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.deleteClaim:

                if (mClaim.getStatus().getAllowEdits()) {
                    mClaimList.deleteClaim(mClaim);
                    finish();
                } else {
                    Toast.makeText(this, "Claim can no longer be deleted!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.editClaim:

                if (mClaim.getStatus().getAllowEdits()) {
                    Intent intent = new Intent(ClaimViewActivity.this, EditClaimActivity.class);
                    intent.putExtra(App.KEY_CLAIM, mClaim);
                    startActivityForResult(intent, EDIT_CLAIM);
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
        if (resultCode == RESULT_OK && requestCode == EDIT_CLAIM) {
            Claim claim = data.getParcelableExtra(App.KEY_CLAIM);


            ClaimsList.getInstance(this).editClaim(mClaim, claim);

            mClaim = claim;

            update();
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
}
