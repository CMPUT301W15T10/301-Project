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

package com.edmondapps.cs301.ass1;

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.*;
import com.edmondapps.cs301.ass1.model.Claim;
import com.edmondapps.cs301.ass1.model.Expense;
import com.edmondapps.cs301.ass1.utils.Utils;

import java.text.DateFormat;

public class ClaimBuilderActivity extends ListActivity implements AdapterView.OnItemSelectedListener {
    public static final String KEY_CLAIM = "claim_key";

    public static final String ACTION_PUT = "action_put";

    public static Intent createIntentWithClaim(Context context, Claim claim) {
        final String id = claim.getId();
        App.get(context).putObjectTransfer(id, claim);

        return new Intent(context, ClaimBuilderActivity.class).putExtra(KEY_CLAIM, id);
    }

    private static final int REQ_CODE_START_TIME = 1;
    private static final int REQ_CODE_END_TIME = 2;
    private static final int REQ_CODE_EXPENSE = 3;

    private Claim.Builder mBuilder;
    private DateFormat mDateFormat;
    private ExpensesAdapter mAdapter;

    private EditText mTitle;
    private Button mStartTime;
    private Button mEndTime;
    private Spinner mStatusSpinner;
    private TextView mEditDisabledPrompt;

    private View[] mDisableViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        Utils.setupDiscardDoneBar(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putClaimToResult();
                finish();
            }
        });

        setContentView(R.layout.claim_builder_activity);
        initViews();

        final Claim claim = tryFindingClaim();
        if (claim == null) {
            mBuilder = new Claim.Builder();
        } else {
            mBuilder = Claim.Builder.copyFrom(claim);
        }

        mDateFormat = android.text.format.DateFormat.getMediumDateFormat(this);

        mAdapter = new ExpensesAdapter(this, mBuilder.peekExpenses());
        setListAdapter(mAdapter);

        updateUI();
    }

    private void initViews() {
        mTitle = (EditText) findViewById(R.id.title);

        mTitle.addTextChangedListener(new TextWaterAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                mBuilder.title(s.toString());
            }
        });

        mStartTime = (Button) findViewById(R.id.start_time);
        mStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(launchCalendarForStartTime(), REQ_CODE_START_TIME);
            }
        });

        mEndTime = (Button) findViewById(R.id.end_time);
        mEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(launchCalendarForEndTime(), REQ_CODE_END_TIME);
            }
        });

        mStatusSpinner = (Spinner) findViewById(R.id.status_spinner);
        mStatusSpinner.setAdapter(new ClaimsStatusAdapter(this));
        mStatusSpinner.setOnItemSelectedListener(this);

        mEditDisabledPrompt = (TextView) findViewById(R.id.edit_disabled_prompt);

        final View newExpenseFrame = findViewById(R.id.new_expense);
        newExpenseFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(ClaimBuilderActivity.this, ExpenseBuilderActivity.class)
                        .putExtra(ExpenseBuilderActivity.KEY_START_TIME, mBuilder.getStartTime()), REQ_CODE_EXPENSE);
            }
        });

        findViewById(R.id.email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Claim claim = mBuilder.build();
                final String title = getString(R.string.claim_format_title, claim.getTitle());
                final Intent intent = Utils.intentWithEmailString(title, makeClaimString(claim));
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(ClaimBuilderActivity.this, R.string.no_email_app, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mDisableViews = new View[]{mTitle, mStartTime, mEndTime, getListView(), newExpenseFrame};
    }

    private String makeClaimString(Claim claim) {
        final String startTime = mDateFormat.format(claim.getStartTime());
        final String endTime = mDateFormat.format(claim.getEndTime());
        final String status = getString(Utils.stringIdForClaimStatus(claim.getStatus()));
        final String title = claim.getTitle();
        final String claimStr = getString(R.string.claim_format_template, title, startTime, endTime, status);

        final StringBuilder builder = new StringBuilder(claimStr);
        for (Expense e : claim.peekExpenses()) {
            final String time = mDateFormat.format(e.getTime());
            builder.append(getString(R.string.expense_format_template, e.getTitle(), e.getAmount(), time, e.getCategory()));
        }
        return builder.toString();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        mBuilder.status(Claim.Status.values()[position]);
        updateUI();
    }

    private Intent launchCalendarForStartTime() {
        final CalendarActivity.Builder builder = new CalendarActivity.Builder(this);
        if (mBuilder.isStartTimeSet()) {
            builder.selectedDate(mBuilder.getStartTime());
        }
        if (mBuilder.isEndTimeSet()) {
            builder.maxDate(mBuilder.getEndTime());
        }
        return builder.build();
    }

    private Intent launchCalendarForEndTime() {
        final CalendarActivity.Builder builder = new CalendarActivity.Builder(this);
        if (mBuilder.isEndTimeSet()) {
            builder.selectedDate(mBuilder.getEndTime());
        }
        if (mBuilder.isStartTimeSet()) {
            builder.minDate(mBuilder.getStartTime());
        }
        return builder.build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_START_TIME: {
                    final long date = data.getLongExtra(CalendarActivity.KEY_DATE, -1);
                    if (date != -1) {
                        mBuilder.startTime(date);
                        updateUI();
                    }
                    break;
                }
                case REQ_CODE_END_TIME:
                    final long date = data.getLongExtra(CalendarActivity.KEY_DATE, -1);
                    if (date != -1) {
                        mBuilder.endTime(date);
                        updateUI();
                    }
                    break;
                case REQ_CODE_EXPENSE:
                    final String id = data.getStringExtra(ExpenseBuilderActivity.KEY_EXPENSE);
                    final Expense expense = App.get(this).getObjectTransfer(id);
                    final String action = data.getAction();
                    if (ExpenseBuilderActivity.ACTION_PUT.equals(action)) {
                        mBuilder.putExpense(expense);
                        mAdapter.putExpense(expense);
                    }
//                    else if (ExpenseBuilderActivity.ACTION_DELETE.equals(action)) {
//                        mBuilder.removeExpense(expense);
//                        mAdapter.removeExpense(expense);
//                    }
                default:
                    super.onActivityResult(requestCode, resultCode, data);
                    break;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final Intent intent = ExpenseBuilderActivity.createIntentWithExpense(this, mAdapter.getItem(position));
        startActivityForResult(intent, REQ_CODE_EXPENSE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        final String claimKey = getIntent().getStringExtra(KEY_CLAIM);
        if (claimKey != null) {
            App.get(this).putObjectTransfer(claimKey, mBuilder.build());
        }
    }

    private Intent intentWithClaim() {
        final Claim claim = mBuilder.build();
        final String claimId = claim.getId();
        App.get(this).putObjectTransfer(claimId, claim);
        return new Intent().putExtra(KEY_CLAIM, claimId);
    }

    private void putClaimToResult() {
        setResult(RESULT_OK, intentWithClaim().setAction(ACTION_PUT));
    }

    private Claim tryFindingClaim() {
        final String claimKey = getIntent().getStringExtra(KEY_CLAIM);
        if (claimKey != null) {
            return App.get(this).getObjectTransfer(claimKey);
        }
        return null;
    }

    private void updateUI() {
        if (mBuilder.isTitleSet()) {
            mTitle.setText(mBuilder.getTitle());
        }
        if (mBuilder.isStartTimeSet()) {
            mStartTime.setText(mDateFormat.format(mBuilder.getStartTime()));
        }
        if (mBuilder.isEndTimeSet()) {
            mEndTime.setText(mDateFormat.format(mBuilder.getEndTime()));
        }
        mAdapter.putAllExpenses(mBuilder.peekExpenses());

        final Claim.Status status = mBuilder.getStatus();
        mStatusSpinner.setSelection(status.ordinal(), true);

        final boolean allowEdits = status.getAllowEdits();
        animateEditPromptVisibility(allowEdits);
        for (View view : mDisableViews) {
            view.setEnabled(allowEdits);
        }
    }

    private void animateEditPromptVisibility(boolean gone) {
        if (gone) {
            mEditDisabledPrompt.animate().alpha(0).withEndAction(new Runnable() {
                @Override
                public void run() {
                    mEditDisabledPrompt.setVisibility(View.GONE);
                }
            });
        } else {
            mEditDisabledPrompt.setText(stringForDisabledPrompt(mBuilder.getStatus()));
            mEditDisabledPrompt.setVisibility(View.VISIBLE);
            mEditDisabledPrompt.animate().alpha(1);
        }
    }

    private String stringForDisabledPrompt(Claim.Status status) {
        switch (status) {
            case SUBMITTED:
                return getString(R.string.disabled_prompt_submitted);
            case APPROVED:
                return getString(R.string.disabled_prompt_approved);
            default:
                throw new UnsupportedOperationException("undefined disable prompt for status: " + status);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }
}
