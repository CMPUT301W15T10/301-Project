/*
 * Copyright 2015 Edmond Chui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cmput301.cs.project;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.cmput301.cs.project.model.Claim;
import com.cmput301.cs.project.model.Expense;
import com.cmput301.cs.project.utils.Utils;

import java.text.DateFormat;

public class ClaimActivity extends ListActivity {
    public static final String KEY_CLAIM = "claim_key";
    public static final String KEY_CLAIM_ID = "claim_key_id";

    public static final String ACTION_DELETE = "action_del";

    private static final int REQ_CODE_EDIT_EXPENSE = 1;

    public static Intent createIntentWithClaim(Context context, Claim claim) {
        final String id = claim.getId();
        App.get(context).putObjectTransfer(id, claim);

        return new Intent(context, ClaimActivity.class).putExtra(KEY_CLAIM, id);
    }

    private Claim mClaim;
    private DateFormat mDateFormat;
    private ExpensesAdapter mAdapter;

    private TextView mTitle;
    private TextView mStatus;
    private TextView mStartTime;
    private TextView mEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_activity);

        mTitle = (TextView) findViewById(R.id.title);
        mStatus = (TextView) findViewById(R.id.status);
        mStartTime = (TextView) findViewById(R.id.start_time);
        mEndTime = (TextView) findViewById(R.id.end_time);

        mClaim = findClaimOrThrow();
        mDateFormat = android.text.format.DateFormat.getMediumDateFormat(this);
        mAdapter = new ExpensesAdapter(this, mClaim.peekExpenses());
        setListAdapter(mAdapter);

        updateUi();
    }

    private Claim findClaimOrThrow() {
        final String claimKey = getIntent().getStringExtra(KEY_CLAIM);
        if (claimKey != null) {
            final Claim claim = App.get(this).getObjectTransfer(claimKey);
            if (claim != null) {
                return claim;
            }
        }
        throw new IllegalStateException("you must pass in KEY_CLAIM");
    }

    private void updateUi() {
        mTitle.setText(mClaim.getTitle());
        mStatus.setText(getString(Utils.stringIdForClaimStatus(mClaim.getStatus())));
        mStartTime.setText(mDateFormat.format(mClaim.getStartTime()));
        mEndTime.setText(mDateFormat.format(mClaim.getEndTime()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit:
                final Intent intent = ClaimBuilderActivity.createIntentWithClaim(this, mClaim);
                intent.addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                startActivity(intent);
                finish();
                return true;
            case R.id.delete:
                setResult(RESULT_OK, new Intent(ACTION_DELETE).putExtra(KEY_CLAIM_ID, mClaim.getId()));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_EDIT_EXPENSE:
                    final String action = data.getAction();
                    if (ExpenseBuilderActivity.ACTION_PUT.equals(action)) {
                        final String expenseKey = data.getStringExtra(ExpenseBuilderActivity.KEY_EXPENSE);
                        final Expense expense = App.get(this).getObjectTransfer(expenseKey);
                        mAdapter.putExpense(expense);
                    }
                    return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final Expense expense = mAdapter.getItem(position);
        final Intent intent = ExpenseActivity.createIntentWithExpense(this, expense);
        startActivityForResult(intent, REQ_CODE_EDIT_EXPENSE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.claim_activity, menu);
        return true;
    }
}
