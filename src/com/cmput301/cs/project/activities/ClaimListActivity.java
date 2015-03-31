package com.cmput301.cs.project.activities;


import android.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.adapters.ClaimsAdapter;
import com.cmput301.cs.project.controllers.ClaimListController;
import com.cmput301.cs.project.controllers.TagsChangedListener;
import com.cmput301.cs.project.controllers.TagsManager;
import com.cmput301.cs.project.models.Claim;
import com.cmput301.cs.project.models.ClaimsList;
import com.cmput301.cs.project.models.Tag;
import com.cmput301.cs.project.models.User;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Is the activity that launches at start of app. </br>
 * An activity that shows a list of {@link com.cmput301.cs.project.models.Claim Claims}. </br>
 * Redirects to {@link com.cmput301.cs.project.activities.LoginActivity LoginActivity} if no user is found.
 * Has menu buttons that allow the creation of new claims via {@link com.cmput301.cs.project.activities.EditClaimActivity EditClaimActivity}
 * and for the {@link com.cmput301.cs.project.activities.TagManagerActivity TagManagerActivity}. </br>
 * If a claim item is clicked {@link com.cmput301.cs.project.activities.ClaimViewActivity ClaimViewActivity} is called for that claim. </br>
 * Finally there are tabs at the top of the activity that allow the user to switch between approver and claimant (not yet implemented).
 *
 * @author rozsa
 */

// http://developer.android.com/google/auth/api-client.html#Starting Mar 31, 2015
public class ClaimListActivity extends ListActivity implements TagsChangedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int POSITION_CLAIMANT = 0;
    private static final int POSITION_APPROVER = 1;
    private static final int NEW_CLAIM = 0;

    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    private static final String STATE_RESOLVING_ERROR = "resolving_error";

    private User mUser;
    private ClaimListController mClaimListController;
    private ClaimsAdapter mApproverAdapter;
    private ClaimsAdapter mClaimantAdapter;

    private GoogleApiClient mGoogleApiClient;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_list_activity);

        mResolvingError = savedInstanceState != null
                && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        mUser = App.get(this).getUser();

        if (mUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        mClaimListController = new ClaimListController(mUser, ClaimsList.getInstance(this));

        setupListView();
        setupActionBar();

        TagsManager.get(this).addTagChangedListener(mClaimListController);
    }

    @Override
    public void onResume() {
        super.onResume();

        mClaimantAdapter.notifyDataSetChanged();
        mApproverAdapter.notifyDataSetChanged();

        mClaimantAdapter.sort(Claim.START_ASCENDING);
        mApproverAdapter.sort(Claim.START_DESCENDING);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TagsManager.get(this).removeTagChangedListener(mClaimListController);
    }

    private void setupListView() {
        mApproverAdapter = new ClaimsAdapter(this, mClaimListController.getApprovableClaims());
        mClaimantAdapter = new ClaimsAdapter(this, mClaimListController.getClaimantClaims());

        setListAdapter(mClaimantAdapter);

    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {


        ClaimsAdapter adapter = (ClaimsAdapter) getListAdapter();
        Intent i = new Intent(this, ClaimViewActivity.class);

        i.putExtra(App.KEY_CLAIM, adapter.getItem(position));

        startActivity(i);

    }

    private void setupActionBar() {

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            final ActionBar.TabListener listener = new ActionBar.TabListener() {
                @Override
                public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                    final int position = tab.getPosition();
                    switch (position) {
                        case POSITION_CLAIMANT:
                            // TODO impl claimant filter
                            setListAdapter(mClaimantAdapter);
                            break;
                        case POSITION_APPROVER:
                            // TODO impl approver filter
                            setListAdapter(mApproverAdapter);
                            break;
                        default:
                            throw new AssertionError("unexpected position: " + position);
                    }
                }

                @Override
                public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                    // do nothing
                }

                @Override
                public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                    // do nothing
                }
            };

            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            actionBar.addTab(actionBar.newTab()
                    .setText(R.string.claimant)
                    .setTabListener(listener));
            actionBar.addTab(actionBar.newTab()
                    .setText(R.string.approver)
                    .setTabListener(listener));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.claim_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tagManager:
                startActivity(new Intent(this, TagManagerActivity.class));
                return true;
            case R.id.add:
                startActivityForResult(new Intent(this, EditClaimActivity.class), NEW_CLAIM);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_CLAIM) {
            if (resultCode == RESULT_OK) {
                Claim claim = data.getExtras().getParcelable(App.KEY_CLAIM);
                mClaimListController.addClaim(claim);

                mClaimantAdapter = new ClaimsAdapter(this, mClaimListController.getClaimantClaims());
                setListAdapter(mClaimantAdapter);
            }
        } else if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onTagRenamed(Tag tag, Tag oldName) {
        setupListView();
    }

    @Override
    public void onTagDeleted(Tag tag) {
        setupListView();
    }

    @Override
    public void onTagCreated(Tag tag) {
        // do nothing
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        // The good stuff goes here.
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    // The rest of this code is all about building the error dialog

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode, this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            ((ClaimListActivity) getActivity()).onDialogDismissed();
        }
    }
}