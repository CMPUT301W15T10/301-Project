package com.cmput301.cs.project.activities;

import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.model.Claim;
import com.cmput301.cs.project.model.ClaimsList;
import com.cmput301.cs.project.utils.Utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import java.text.DateFormat;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * The activity that is called when a item is clicked within {@link com.cmput301.cs.project.activites.ClaimListActivity ClaimListActivity}
 * that shows the specific details of that claim. </br>
 * Menu items allow {@link com.cmput301.cs.project.activites.EditClaimActivity EditClaimActivity} to be called on the claim
 * and for a claim to be deleted.</br>
 * The activity lists the StartDate, EndDate, Currencies, Status, Destinations and calls {@link com.cmput301.cs.project.activities.ExpenseListActivity ExpenseListActivity}
 * when the associated button is clicked.</br>
 * Returns to the {@link com.cmput301.cs.project.activites.ClaimListActivity ClaimListActivity} when Submit button is clicked.
 *
 * A claim must be passed via an intent for this activity to work
 *
 * @author rozsa
 */

public class ClaimViewActivity extends Activity {

    Claim mClaim;
    Button mExpenseButton;
    Button mSubmitButton;
    TextView mStartDate;
    TextView mEndDate;
    TextView mStatus;
    DateFormat mDateFormat;

    ClaimsList mClaimList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_view_activity);

        mClaimList = ClaimsList.getInstance(this);

        mClaim = getIntent().getExtras().getParcelable(App.KEY_CLAIM);
        mExpenseButton = (Button) findViewById(R.id.expenseButton);
        mSubmitButton = (Button) findViewById(R.id.submitButton);
        mStartDate = (TextView) findViewById(R.id.startDate);
        mEndDate = (TextView) findViewById(R.id.endDate);
        mStatus = (TextView) findViewById(R.id.statusText);

        mDateFormat = android.text.format.DateFormat.getMediumDateFormat(this);
        mStartDate.setText(mDateFormat.format(mClaim.getStartTime()));
        mEndDate.setText(mDateFormat.format(mClaim.getEndTime()));

        mStatus.setText(Utils.stringIdForClaimStatus(mClaim.getStatus()));


        initButtons();

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
                finish();

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

                mClaimList.deleteClaim(mClaim);
                finish();
                break;
            case R.id.editClaim:
                Intent intent = new Intent(ClaimViewActivity.this, EditClaimActivity.class);
                intent.putExtra(App.KEY_CLAIM, mClaim);
                startActivity(intent);
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }
}
