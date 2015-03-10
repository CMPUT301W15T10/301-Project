package com.cmput301.cs.project.project.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.project.model.Claim;

public class EditClaimActivity extends Activity {
    public static final String KEY_CLAIM = "key_claim";

    public static Intent intentWithClaim(Context context, Claim claim) {
        return new Intent(context, EditClaimActivity.class).putExtra(KEY_CLAIM, claim);
    }

    private static final int REQ_CODE_PICK_START_DATE = 1;
    private static final int REQ_CODE_PICK_END_DATE = 2;

    private Button mStartDateButt;
    private Button mEndDateButt;

    private Claim.Builder mBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_claim_activity);

        initBuilder();

        mStartDateButt = (Button) findViewById(R.id.startDateButt);
        mEndDateButt = (Button) findViewById(R.id.endDateButt);

        mStartDateButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CalendarActivity.Builder builder = new CalendarActivity.Builder(EditClaimActivity.this);
                if (mBuilder.isStartTimeSet()) {
                    builder.selectedDate(mBuilder.getStartTime());
                }
                if (mBuilder.isEndTimeSet()) {
                    builder.maxDate(mBuilder.getEndTime());
                }
                startActivityForResult(builder.build(), REQ_CODE_PICK_START_DATE);
            }
        });

        mEndDateButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CalendarActivity.Builder builder = new CalendarActivity.Builder(EditClaimActivity.this);
                if (mBuilder.isEndTimeSet()) {
                    builder.selectedDate(mBuilder.getEndTime());
                }
                if (mBuilder.isStartTimeSet()) {
                    builder.minDate(mBuilder.getStartTime());
                }
                startActivityForResult(builder.build(), REQ_CODE_PICK_END_DATE);
            }
        });
    }

    private void initBuilder() {
        final Claim claim = getIntent().getParcelableExtra(KEY_CLAIM);
        mBuilder = claim == null ? new Claim.Builder() : Claim.Builder.copyFrom(claim);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final long date = data.getLongExtra(CalendarActivity.KEY_DATE, -1);
        switch (requestCode) {
            case REQ_CODE_PICK_START_DATE:
                if (resultCode == RESULT_OK && date != -1) {
                    mBuilder.startTime(date);
                }
                break;
            case REQ_CODE_PICK_END_DATE:
                if (resultCode == RESULT_OK && date != -1) {
                    mBuilder.endTime(date);
                }
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_claim, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
