package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.controllers.SettingsController;
import com.cmput301.cs.project.models.Claim;
import com.cmput301.cs.project.models.ClaimsList;
import com.cmput301.cs.project.models.Destination;
import com.cmput301.cs.project.models.Expense;
import com.google.android.gms.maps.model.LatLng;
import org.joda.money.Money;

import java.text.DateFormat;


// This activities formatting and general flow was influenced by
// https://github.com/chuihinwai/echui-notes/blob/master/src/com/edmondapps/cs301/ass1/ExpenseActivity.java
// on March 12, 2015

/**
 * An activity that shows the details of an {@link com.cmput301.cs.project.models.Expense Expense}. <p>
 * Has a menu button that calls {@link com.cmput301.cs.project.activities.EditExpenseActivity EditExpenseActivity} for editing
 * on that expense.
 * <p/>
 * Expects to be given a claim using App.KEY_CLAIM_ID and an expense using App.KEY_EXPENSE_ID in its intent.
 *
 * @author rozsa
 */

public class ExpenseViewActivity extends Activity {
    private Claim mClaim;
    private Expense mExpense;

    private DateFormat mDateFormat;

    private TextView mDescription;
    private TextView mMoney;
    private TextView mDate;
    private TextView mCategory;
    private TextView mCompleted;
    private TextView mLocation;

    private ImageView mReceipt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expense_view_activity);

        loadExpense();

        mDateFormat = android.text.format.DateFormat.getMediumDateFormat(this);

        mDescription = (TextView) findViewById(R.id.description);
        mMoney = (TextView) findViewById(R.id.money);
        mDate = (TextView) findViewById(R.id.date);
        mCategory = (TextView) findViewById(R.id.category);
        mCompleted = (TextView) findViewById(R.id.completed);
        mReceipt = (ImageView) findViewById(R.id.receiptImage);

        mLocation = (TextView) findViewById(R.id.location);

        final Destination destination = mExpense.getDestination();
        if (destination != null) {
            final LatLng latLng = destination.getLocation();
            if (latLng != null) {
                mLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final Uri uri = getUriForDestination(destination);
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }
                });
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        loadExpense();
        updateUi();
    }

    // Apr 6, 2015 https://developers.google.com/maps/documentation/android/intents
    private static Uri getUriForDestination(Destination destination) {
        final StringBuilder builder = new StringBuilder("geo:0,0?q=");
        final LatLng latLng = destination.getLocation();
        final String name = destination.getName();
        if (latLng != null) {
            builder.append(latLng.latitude)
                    .append(",")
                    .append(latLng.longitude);
        }
        if (name != null) {
            builder.append("(")
                    .append(name)
                    .append(")");
        }
        // format: geo:0,0?q=latitude,longitude(label)
        return Uri.parse(builder.toString());
    }

    private void loadExpense() {
        ClaimsList claimList = ClaimsList.getInstance(this);

        mClaim = claimList.getClaim(getClaimId());

        mExpense = mClaim.getExpense(getExpenseId());
    }

    private String getClaimId() {
        String claimId = getIntent().getStringExtra(App.KEY_CLAIM_ID);
        if (claimId == null) {
            throw new IllegalStateException("Expected an Expense");
        }
        return claimId;
    }

    private String getExpenseId() {
        String expenseId = getIntent().getStringExtra(App.KEY_EXPENSE_ID);
        if (expenseId == null) {
            throw new IllegalStateException("Expected an Expense ID");
        }
        return expenseId;
    }

    private void updateUi() {
        final Money amount = mExpense.getAmount();

        mDescription.setText(mExpense.getDescription());
        mMoney.setText(amount.toString());
        mDate.setText(mDateFormat.format(mExpense.getTime()));
        mCategory.setText(mExpense.getCategory());

        mCompleted.setText(mExpense.isCompleted() ? "Completed" : "In Progress");

        if (mExpense.hasReceipt()) {
            mReceipt.setImageBitmap(mExpense.getReceipt().getBitmap());
        } else {
            mReceipt.setImageDrawable(null);
        }

        final Destination destination = mExpense.getDestination();
        if (destination != null) {
            if (SettingsController.get(this).isLocationHome(destination.getLocation())) {
                mLocation.setText(getString(R.string.formated_home, destination.getName()));
            } else {
                mLocation.setText(destination.getName());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.expense_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete) {
            setResult(App.RESULT_DELETE, new Intent().putExtra(App.KEY_EXPENSE_ID, mExpense.getId()));
            finish();

            return true;
        } else if (id == R.id.edit) {
            Intent intent = new Intent(this, EditExpenseActivity.class);
            intent.putExtra(App.KEY_CLAIM_ID, getClaimId());
            intent.putExtra(App.KEY_EXPENSE_ID, mExpense.getId());
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
