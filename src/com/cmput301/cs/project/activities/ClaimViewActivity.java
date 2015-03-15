package com.cmput301.cs.project.activities;

import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.model.Claim;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.text.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ClaimViewActivity extends Activity {
	
	
	Claim claim;
	Button mExpenseButton;
	Button mSubmitButton;
	TextView mStartDate;
	TextView mEndDate;
	DateFormat mDateFormat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.claim_view_activity);
		
		claim = getIntent().getExtras().getParcelable(App.KEY_CLAIM);
		mExpenseButton = (Button) findViewById(R.id.expenseButton);
		mSubmitButton = (Button) findViewById(R.id.submitButton);
		mStartDate = (TextView) findViewById(R.id.startDate);
		mEndDate = (TextView) findViewById(R.id.endDate);
		mDateFormat = android.text.format.DateFormat.getMediumDateFormat(this);
		mStartDate.setText(mDateFormat.format(claim.getStartTime()));
		mEndDate.setText(mDateFormat.format(claim.getEndTime()));
		
	
		
		initButtons();
				
	}
	
	private void initButtons() {
		mExpenseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ClaimViewActivity.this, ExpenseListActivity.class);
				intent.putExtra(App.KEY_CLAIM, claim);
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
				
			case R.id.editClaim:
				Intent intent = new Intent(ClaimViewActivity.this, EditClaimActivity.class);
				intent.putExtra(App.KEY_CLAIM, claim);
				startActivity(intent);
				
			default:
				return super.onOptionsItemSelected(item);
		}
		
		
	}
}
