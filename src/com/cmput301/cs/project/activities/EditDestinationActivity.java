package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.TextView;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.adapters.TextWatcherAdapter;
import com.cmput301.cs.project.models.Destination;
import com.cmput301.cs.project.utils.Utils;

/**
 * An activity that allows a destination to be created with an associated reason. <p>
 * Is called when a new destination is created or when a destination is being edited within
 * {@link com.cmput301.cs.project.activities.EditClaimActivity EditClaimActivity}. <p>
 * The key-value pair is stored within that particular {@link com.cmput301.cs.project.models.Claim Claim}.
 *
 * @author rozsa
 */

public class EditDestinationActivity extends Activity {
    public static final String KEY_DESTINATION = "key_destination";

    private static final int REQ_CODE_MAP = 1;

    private Destination.Builder mBuilder;
    private TextView mDestination;
    private TextView mReason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpActionBar();
        setResult(RESULT_CANCELED);

        setContentView(R.layout.edit_destination_activity);

        initBuilder();

        mDestination = (TextView) findViewById(R.id.newDestinationInput);
        mReason = (TextView) findViewById(R.id.newReason);

        updateUI();
        installListeners();
    }

    private void initBuilder() {
        final Destination destination = getIntent().getParcelableExtra(KEY_DESTINATION);
        if (destination == null) {
            mBuilder = new Destination.Builder();
        } else {
            mBuilder = destination.edit();
        }
    }

    private void installListeners() {
        mDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(EditDestinationActivity.this, MapActivity.class)
                        .putExtra(MapActivity.KEY_DESTINATION, mBuilder.build()), REQ_CODE_MAP);
            }
        });
        mReason.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    mReason.setError(getString(R.string.empty_error));
                } else {
                    mBuilder.reason(s.toString());
                }
            }
        });

        findViewById(R.id.deleteTag).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO actually delete
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE_MAP) {
            if (resultCode == RESULT_OK) {
                final Destination destination = data.getParcelableExtra(MapActivity.KEY_DESTINATION);
                mBuilder.name(destination.getName())
                        .location(destination.getLocation());
                updateUI();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void updateUI() {
        mDestination.setText(mBuilder.getName());
        mReason.setText(mBuilder.getReason());
    }

    private void setUpActionBar() {
        Utils.setupDiscardDoneBar(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, new Intent().putExtra(KEY_DESTINATION, mBuilder.build()));
                finish();
            }
        });
    }
}
