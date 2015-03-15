package com.cmput301.cs.project.activities;

import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.model.Claim;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ClaimViewActivity extends Activity {
	
	
	Claim claim;
	Button mExpenseButton;
	Button mSubmitButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.claim_view_activity);
		
		claim = getIntent().getExtras().getParcelable(App.KEY_CLAIM);
		mExpenseButton = (Button) findViewById(R.id.expenseButton);
		mSubmitButton = (Button) findViewById(R.id.submitButton);
		
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
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
