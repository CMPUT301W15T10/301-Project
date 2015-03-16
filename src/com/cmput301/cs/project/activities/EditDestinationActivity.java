package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.utils.Utils;

/**
 * An activity that allows a destination to be created with an associated reason. </br>
 * Is called when a new destination is created or when a destination is being edited within 
 * {@link com.cmput301.cs.project.activities.EditClaimActivity EditClaimActivity}. </br>
 * The key-value pair is stored within that particular {@link com.cmput301.cs.project.model.Claim Claim}.
 *   
 * @author rozsa
 *
 */

public class EditDestinationActivity extends Activity {

    public static final String DESTINATION = "DESTINATION";
    public static final String REASON = "REASON";
    private TextView mDestination;
    private TextView mReason;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_destination_activity);

        mDestination = (TextView) findViewById(R.id.newDestinationInput);
        mReason = (TextView) findViewById(R.id.newReason);

        mDestination.setText(getIntent().getStringExtra(DESTINATION));
        mReason.setText(getIntent().getStringExtra(REASON));

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		
		
		Utils.setupDiscardDoneBar(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();

                if(mDestination.getText().length() == 0){

                    Toast.makeText(EditDestinationActivity.this, "Destination cannot be empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                intent.putExtra(DESTINATION, mDestination.getText().toString());
                intent.putExtra(REASON, mReason.getText().toString());

                setResult(RESULT_OK, intent);
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
