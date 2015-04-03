package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.utils.Utils;

public class MapActivity extends Activity {
    public static final String KEY_NAME = "key_name";
    public static final String KEY_LOCATION = "key_location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        setContentView(R.layout.map_activity);
        setResult(RESULT_CANCELED);

    }

    private void setupActionBar() {
        Utils.setupDiscardDoneBar(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
