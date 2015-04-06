package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.controllers.SettingsController;
import com.cmput301.cs.project.models.Destination;

public class SettingsActivity extends Activity {

    private static final int REQ_CODE_LOCATION = 1;

    private Button mHome;
    private Destination mDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        mDestination = SettingsController.get(this).loadHomeAsDestination();

        mHome = (Button) findViewById(R.id.home_btn);
        mHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(SettingsActivity.this, MapActivity.class)
                        .putExtra(MapActivity.KEY_DESTINATION, mDestination), REQ_CODE_LOCATION);
            }
        });

        updateUi();
    }

    private void updateUi() {
        final String name = mDestination.getName();
        if (name != null) {
            mHome.setText(name);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQ_CODE_LOCATION:
                if (resultCode == RESULT_OK) {
                    final Destination destination = data.getParcelableExtra(MapActivity.KEY_DESTINATION);
                    SettingsController.get(this).saveHomeAsDestination(destination);
                    mDestination = destination;
                    updateUi();
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
