package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.model.Claim;
import com.cmput301.cs.project.utils.Utils;

import java.text.DateFormat;

public class EditClaimActivity extends Activity {
    public static final String KEY_CLAIM = "key_claim";

    public static Intent intentWithClaim(Context context, Claim claim) {
        return new Intent(context, EditClaimActivity.class).putExtra(KEY_CLAIM, claim);
    }

    private static final int REQ_CODE_PICK_START_DATE = 1;
    private static final int REQ_CODE_PICK_END_DATE = 2;

    private Button mStartDate;
    private Button mEndDate;
    private Button mNewDestination;

    private Claim.Builder mBuilder;
    private DateFormat mDateFormat;

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
                setResult(RESULT_OK, new Intent().putExtra(KEY_CLAIM, mBuilder.build()));
                finish();
            }
        });

        setContentView(R.layout.edit_claim_activity);

        mDateFormat = android.text.format.DateFormat.getMediumDateFormat(this);

        mStartDate = (Button) findViewById(R.id.startDate);
        mEndDate = (Button) findViewById(R.id.endDate);
        mNewDestination = (Button) findViewById(R.id.newDestination);

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
                startActivity(intent);
            }
        });

        initBuilder();
    }

    private void initBuilder() {
        final Claim claim = getIntent().getParcelableExtra(KEY_CLAIM);
        if (claim == null) {
            mBuilder = new Claim.Builder();
        } else {
            mBuilder = Claim.Builder.copyFrom(claim);
            updateUI();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_PICK_START_DATE:
                if (resultCode == RESULT_OK) {
                    mBuilder.startTime(data.getLongExtra(CalendarActivity.KEY_DATE, -1));
                    updateUI();
                }
                break;
            case REQ_CODE_PICK_END_DATE:
                if (resultCode == RESULT_OK) {
                    mBuilder.endTime(data.getLongExtra(CalendarActivity.KEY_DATE, -1));
                    updateUI();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateUI() {
        if (mBuilder.isStartTimeSet()) {
            mStartDate.setText(mDateFormat.format(mBuilder.getStartTime()));
        }
        if (mBuilder.isEndTimeSet()) {
            mEndDate.setText(mDateFormat.format(mBuilder.getEndTime()));
        }

    }
}
