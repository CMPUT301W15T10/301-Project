package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.adapters.DestinationAdapter;
import com.cmput301.cs.project.adapters.ExpensesAdapter;
import com.cmput301.cs.project.model.Claim;
import com.cmput301.cs.project.model.Expense;
import com.cmput301.cs.project.utils.Utils;

import java.text.DateFormat;

/**
 * The activity that is called when a New Claim is created or when an existing claim is going to be edited. </br>
 * Able to add {@link com.cmput301.cs.project.model.Expense Expenses} and {@link com.cmput301.cs.project.model.Claim Destinations}
 *  from this screen as well as {@literal StartDate} and {@literal EndDate}.
 *
 * A claim must be passed via an intent as App.KEY_CLAIM.
 *
 * If there is no claim passed it is assumed that the activity is creating a new claim
 *
 * @author rozsa
 *
 */

public class EditClaimActivity extends Activity {

    private static final int REQ_CODE_CREATE_DESTINATION = 3;
    private static final int REQ_NEW_EXPENSE = 4;
    private static final int REQ_EDIT_EXPENSE = 5;
    private static final int REQ_CODE_EDIT_DESTINATION = 6;

    private ListView mDestinations;
    private ListView mExpenses;

    public static Intent intentWithClaim(Context context, Claim claim) {
        return new Intent(context, EditClaimActivity.class).putExtra(App.KEY_CLAIM, claim);
    }

    private static final int REQ_CODE_PICK_START_DATE = 1;
    private static final int REQ_CODE_PICK_END_DATE = 2;

    private Button mStartDate;
    private Button mEndDate;
    private Button mNewDestination;
    private Button mNewExpense;

    private Claim.Builder mBuilder;
    private DateFormat mDateFormat;

    private Expense mEdittingExpense;
    private Pair<String, String> mEdittingDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_claim_activity);

        mDateFormat = android.text.format.DateFormat.getMediumDateFormat(this);

        mStartDate = (Button) findViewById(R.id.startDate);
        mEndDate = (Button) findViewById(R.id.endDate);
        mNewDestination = (Button) findViewById(R.id.newDestination);
        mNewExpense = (Button) findViewById(R.id.newExpense);
        mDestinations = (ListView) findViewById(R.id.destinationList);
        mExpenses = (ListView) findViewById(R.id.expenseList);

        initBuilder();
        initButtons();
        initListeners();

        mExpenses.setAdapter(new ExpensesAdapter(this, mBuilder.getExpenses()));

        update();

    }

    private void initListeners() {
        mDestinations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(EditClaimActivity.this, EditDestinationActivity.class);
                DestinationAdapter adapter = ((DestinationAdapter) mDestinations.getAdapter());
                Pair<String, String> item = adapter.getItem(position);
                intent.putExtra(EditDestinationActivity.DESTINATION, item.first);
                intent.putExtra(EditDestinationActivity.REASON, item.second);

                mEdittingDestination = item;

                startActivityForResult(intent, REQ_CODE_EDIT_DESTINATION);
            }
        });

        mExpenses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(EditClaimActivity.this, EditExpenseActivity.class);
                ExpensesAdapter adapter = ((ExpensesAdapter) mExpenses.getAdapter());
                Expense expense = adapter.getItem(position);
                intent.putExtra(App.KEY_EXPENSE, expense);

                mEdittingExpense = expense;

                startActivityForResult(intent, REQ_EDIT_EXPENSE);
            }
        });
    }


    /**
     * 
     * Method that sets up all the click listeners in this activity. Includes the discard bar as well.
     * @author rozsa
     */
    private void initButtons() {
    	
    	mStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new CalendarActivity.Builder(EditClaimActivity.this)
                        .selectedDate(mBuilder.getStartTime()).maxDate(mBuilder.getEndTime())
                        .build(), REQ_CODE_PICK_START_DATE);
            }
        });

        mEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new CalendarActivity.Builder(EditClaimActivity.this)
                        .selectedDate(mBuilder.getEndTime()).minDate(mBuilder.getStartTime())
                        .build(), REQ_CODE_PICK_END_DATE);
            }
        });

        mNewDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditClaimActivity.this, EditDestinationActivity.class);
                startActivityForResult(intent, REQ_CODE_CREATE_DESTINATION);
            }
        });
        
        mNewExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditClaimActivity.this, EditExpenseActivity.class);
                startActivityForResult(intent, REQ_NEW_EXPENSE);
            }
        });
        Utils.setupDiscardDoneBar(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra(App.KEY_CLAIM, mBuilder.build()));
                finish();
            }
        });
    }
    
    
    private void initBuilder() {
        final Claim claim = getIntent().getParcelableExtra(App.KEY_CLAIM);
        if (claim == null) {
            mBuilder = new Claim.Builder(App.get(this).getUser());
        } else {
            mBuilder = Claim.Builder.copyFrom(claim);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_PICK_START_DATE:
                if (resultCode == RESULT_OK) {
                    mBuilder.startTime(data.getLongExtra(CalendarActivity.KEY_DATE, -1));
                    update();
                }
                break;
            case REQ_CODE_PICK_END_DATE:
                if (resultCode == RESULT_OK) {
                    mBuilder.endTime(data.getLongExtra(CalendarActivity.KEY_DATE, -1));
                    update();
                }
                break;
            case REQ_CODE_CREATE_DESTINATION:
                if (resultCode == RESULT_OK) {
                    //TODO: bugged when editting a reason
                    String reason = data.getStringExtra(EditDestinationActivity.REASON);
                    String destination = data.getStringExtra(EditDestinationActivity.DESTINATION);
                    mBuilder.putDestinationAndReason(destination, reason);
                    update();
                }
                break;
            case REQ_CODE_EDIT_DESTINATION:
                if (resultCode == RESULT_OK) {
                    //TODO: bugged when editting a reason
                    String reason = data.getStringExtra(EditDestinationActivity.REASON);
                    String destination = data.getStringExtra(EditDestinationActivity.DESTINATION);
                    mBuilder.removeDestination(mEdittingDestination.first);
                    mBuilder.putDestinationAndReason(destination, reason);
                    update();
                }
                break;
            case REQ_NEW_EXPENSE:
                if (resultCode == RESULT_OK) {
                    //TODO: bugged when editting
                    Expense expense = data.getParcelableExtra(App.KEY_EXPENSE);
                    mBuilder.putExpense(expense);
                    update();
                }
                break;

            case REQ_EDIT_EXPENSE:
                if (resultCode == RESULT_OK) {
                    //TODO: bugged when editting
                    Expense expense = data.getParcelableExtra(App.KEY_EXPENSE);
                    mBuilder.removeExpenseById(mEdittingExpense);
                    mBuilder.putExpense(expense);
                    update();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void update() {
        if (mBuilder.isStartTimeSet()) {
            mStartDate.setText(mDateFormat.format(mBuilder.getStartTime()));
        }
        if (mBuilder.isEndTimeSet()) {
            mEndDate.setText(mDateFormat.format(mBuilder.getEndTime()));
        }

        mDestinations.setAdapter(new DestinationAdapter(this, mBuilder.getDestinations()));
        mExpenses.setAdapter(new ExpensesAdapter(this, mBuilder.getExpenses()));

    }
}
