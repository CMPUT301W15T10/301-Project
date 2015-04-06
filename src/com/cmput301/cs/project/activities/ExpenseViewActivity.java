package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
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
 * Expects to be given a claim using App.KEY_CLAIM and an expense using App.KEY_EXPENSE in its intent.
 *
 * @author rozsa
 */

public class ExpenseViewActivity extends Activity {
    private static final int EDIT_EXPENSE = 0;

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

        initExpenseController();

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

    private void initExpenseController() {
        mClaim = getIntent().getParcelableExtra(App.KEY_CLAIM);
        mExpense = getIntent().getParcelableExtra(App.KEY_EXPENSE);
        if (mExpense == null) {
            throw new IllegalStateException("Expected an Expense");
        } else if (mClaim == null) {
            throw new IllegalStateException("Expected a Claim");
        }
    }

    private void updateUi() {
        final Money amount = mExpense.getAmount();

        mDescription.setText(mExpense.getDescription());
        mMoney.setText(amount.toString());
        mDate.setText(mDateFormat.format(mExpense.getTime()));
        mCategory.setText(mExpense.getCategory());

        mCompleted.setText(mExpense.isCompleted() ? "Completed" : "In Progress");

        if (mExpense.hasReceipt()) {
            final Uri receiptFileUri = mExpense.getReceipt().getUri();
            final BitmapDrawable drawable = new BitmapDrawable(getResources(), receiptFileUri.getPath());
            mReceipt.setImageDrawable(drawable);
        } else {
            mReceipt.setImageDrawable(null);
        }

        final Destination destination = mExpense.getDestination();
        if (destination != null) {
            mLocation.setText(destination.getName());
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
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.delete) {
            setResult(App.RESULT_DELETE, new Intent().putExtra(App.KEY_EXPENSE, mExpense));
            finish();

            return true;
        } else if (id == R.id.edit) {
            Intent intent = new Intent(this, EditExpenseActivity.class);
            intent.putExtra(App.KEY_EXPENSE, mExpense);
            startActivityForResult(intent, EDIT_EXPENSE);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_EXPENSE) {
            mExpense = data.getParcelableExtra(App.KEY_EXPENSE);

            updateClaim();

            updateUi();
        }
    }

    private void updateClaim() {
        final ClaimsList claimsList = ClaimsList.getInstance(this);
        final Claim newClaim = mClaim.edit().putExpense(mExpense).build();

        claimsList.editClaim(newClaim);

        mClaim = newClaim;
    }
}
