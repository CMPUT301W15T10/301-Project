package com.cmput301.cs.project.activities;

import com.cmput301.cs.project.R;
import com.cmput301.cs.project.utils.Utils;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class EditDestinationActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_destination_activity);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		
		Utils.setupDiscardDoneBar(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
		
		findViewById(R.id.deleteTag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
		
		return true;
		
		
	}

}
