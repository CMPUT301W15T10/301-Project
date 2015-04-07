package com.cmput301.cs.project.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.adapters.DestinationAdapter;
import com.cmput301.cs.project.controllers.TagsManager;
import com.cmput301.cs.project.models.Claim;
import com.cmput301.cs.project.models.ClaimsList;
import com.cmput301.cs.project.models.Destination;
import com.cmput301.cs.project.models.Tag;
import com.cmput301.cs.project.utils.Utils;

import java.text.DateFormat;
import java.util.SortedSet;

/**
 * The activity that is called when a New Claim is created or when an existing claim is going to be edited. <p>
 * Able to add {@link com.cmput301.cs.project.models.Expense Expenses} and {@link com.cmput301.cs.project.models.Claim Destinations}
 * from this screen as well as {@literal StartDate} and {@literal EndDate}.
 * <p/>
 * A claim must be passed via an intent as App.KEY_CLAIM_ID.
 * <p/>
 * If there is no claim passed it is assumed that the activity is creating a new claim
 *
 * @author rozsa
 */

public class EditClaimActivity extends Activity {

    private static final int REQ_CODE_CREATE_DESTINATION = 3;
    private static final int REQ_CODE_EDIT_DESTINATION = 6;

    public static Intent intentWithClaimId(Context context, String claimId) {
        return new Intent(context, EditClaimActivity.class).putExtra(App.KEY_CLAIM_ID, claimId);
    }

    private static final int REQ_CODE_PICK_START_DATE = 1;
    private static final int REQ_CODE_PICK_END_DATE = 2;

    private Button mStartDate;
    private Button mEndDate;
    private Button mNewDestination;
    private TextView mTags;
    private ListView mDestinations;

    private Claim.Builder mBuilder;
    private DateFormat mDateFormat;

    private Destination mEdittingDestination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_claim_activity);

        mDateFormat = android.text.format.DateFormat.getMediumDateFormat(this);

        mStartDate = (Button) findViewById(R.id.startDate);
        mEndDate = (Button) findViewById(R.id.endDate);
        mNewDestination = (Button) findViewById(R.id.newDestination);
        mDestinations = (ListView) findViewById(R.id.destinationList);
        mTags = (TextView) findViewById(R.id.tags);

        initBuilder();
        initButtons();
        initListeners();


        update();
    }

    private void initListeners() {
        mDestinations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(EditClaimActivity.this, EditDestinationActivity.class);
                DestinationAdapter adapter = ((DestinationAdapter) mDestinations.getAdapter());
                Destination item = adapter.getItem(position);
                intent.putExtra(EditDestinationActivity.KEY_DESTINATION, item);
                mEdittingDestination = item;

                startActivityForResult(intent, REQ_CODE_EDIT_DESTINATION);
            }
        });

        mDestinations.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(EditClaimActivity.this)
                        .setMessage("Delete this Destination?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final DestinationAdapter adapter = (DestinationAdapter) parent.getAdapter();
                                mBuilder.removeDestination(adapter.getItem(position));
                                update();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .create()
                        .show();
                return true;
            }
        });


        mTags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] tags = getTagNamesFromManager();
                final boolean[] states = getTagsState();
                new AlertDialog.Builder(EditClaimActivity.this)
                        .setMultiChoiceItems(tags, states, new DialogInterface.OnMultiChoiceClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                                states[which] = isChecked;
                            }
                        })
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final TagsManager manager = TagsManager.get(EditClaimActivity.this);
                                for (int i = 0, tagsLength = tags.length; i < tagsLength; i++) {
                                    final Tag tag = manager.findTagByName(tags[i]);
                                    final boolean isChecked = states[i];
                                    if (isChecked) {
                                        mBuilder.addTag(tag);
                                    } else {
                                        mBuilder.removeTag(tag);
                                    }
                                }
                                update();
                            }
                        })
                        .create()
                        .show();
            }
        });
    }

    private String[] getTagNamesFromManager() {
        final SortedSet<Tag> tags = TagsManager.get(EditClaimActivity.this).peekTags();
        final String[] out = new String[tags.size()];
        int i = 0;
        for (Tag tag : tags) {  // would crash if someone else modifies the tags during this loop
            out[i] = tag.getName();
            i += 1;
        }
        return out;
    }

    /**
     * Method that sets up all the click listeners in this activity. Includes the discard bar as well.
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


        Utils.setupDiscardDoneBar(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClaimsList claimList = ClaimsList.getInstance(EditClaimActivity.this);

                // Checking if it was just created
                if (getClaimId() == null) {
                    claimList.addClaim(mBuilder.build());
                } else {
                    claimList.editClaim(mBuilder.build());
                }

                finish();
            }
        });
    }


    private void initBuilder() {
        ClaimsList claimList = ClaimsList.getInstance(this);

        String claimId = getClaimId();

        if (claimList.getClaimById(claimId) == null) {
            mBuilder = new Claim.Builder(App.get(this).getUser());
        } else {
            mBuilder = claimList.getClaimById(claimId).edit();
        }
    }

    private String getClaimId() {
        return getIntent().getStringExtra(App.KEY_CLAIM_ID);
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
                    Destination destination = data.getParcelableExtra(EditDestinationActivity.KEY_DESTINATION);
                    mBuilder.putDestination(destination);
                    update();
                }
                break;
            case REQ_CODE_EDIT_DESTINATION:
                if (resultCode == RESULT_OK) {
                    //TODO: bugged when editting a reason
                    Destination destination = data.getParcelableExtra(EditDestinationActivity.KEY_DESTINATION);
                    mBuilder.removeDestination(mEdittingDestination);
                    mBuilder.putDestination(destination);
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
        mTags.setText(getTagsAsCharSequence());
    }

    private CharSequence getTagsAsCharSequence() {
        final StringBuilder builder = new StringBuilder();

        String separator = "";
        for (Tag tag : mBuilder.peekTags()) {
            builder.append(separator).append(tag.getName());
            separator = ", ";
        }

        return builder.toString();
    }

    private boolean[] getTagsState() {
        final SortedSet<Tag> allTags = TagsManager.get(this).peekTags();
        final SortedSet<Tag> addedTags = mBuilder.peekTags();
        final boolean[] out = new boolean[allTags.size()];
        int i = 0;
        for (Tag tag : allTags) {
            out[i] = addedTags.contains(tag);
            i += 1;
        }
        return out;
    }
}
