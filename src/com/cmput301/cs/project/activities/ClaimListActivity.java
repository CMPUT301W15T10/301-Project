package com.cmput301.cs.project.activities;


import android.app.ActionBar;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.cmput301.cs.project.controllers.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.adapters.ClaimsApproverAdapter;
import com.cmput301.cs.project.adapters.ClaimsClaimantAdapter;
import com.cmput301.cs.project.controllers.ClaimListController;
import com.cmput301.cs.project.controllers.TagsManager;
import com.cmput301.cs.project.dialogs.TagSelectorDialogFragment;
import com.cmput301.cs.project.listeners.TagSelectorListener;
import com.cmput301.cs.project.listeners.TagsChangedListener;
import com.cmput301.cs.project.models.Claim;
import com.cmput301.cs.project.models.ClaimsList;
import com.cmput301.cs.project.models.Tag;
import com.cmput301.cs.project.models.User;

import java.util.ArrayList;

/**
 * Is the activity that launches at start of app. <p>
 * An activity that shows a list of {@link com.cmput301.cs.project.models.Claim Claims}. <p>
 * Redirects to {@link com.cmput301.cs.project.activities.LoginActivity LoginActivity} if no user is found.
 * Has menu buttons that allow the creation of new claims via {@link com.cmput301.cs.project.activities.EditClaimActivity EditClaimActivity}
 * and for the {@link com.cmput301.cs.project.activities.TagManagerActivity TagManagerActivity}. <p>
 * If a claim item is clicked {@link com.cmput301.cs.project.activities.ClaimViewActivity ClaimViewActivity} is called for that claim. <p>
 * Finally there are tabs at the top of the activity that allow the user to switch between approver and claimant. Claimant shows only claims
 * for the current user and Approver shows a list of the claims for every user EXCEPT the current user.
 * <p/>
 * If a tag is renamed or deleted the onTagRename and onTagResume methods, respectively, will reload the list of claims.
 *
 * @author rozsa
 * @author jbenson
 */

public class ClaimListActivity extends ListActivity implements TagsChangedListener, TagSelectorListener {

    private static final int POSITION_CLAIMANT = 0;
    private static final int POSITION_APPROVER = 1;
    private static final int VIEW_CLAIM = 0;

    private ArrayList<Tag> mWantedTags;

    private ClaimListController mClaimListController;
    private ClaimsApproverAdapter mApproverAdapter;
    private ClaimsClaimantAdapter mClaimantAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_list_activity);

        User user = App.get(this).getUser();

        if (user == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        mWantedTags = new ArrayList<Tag>();
        mClaimListController = new ClaimListController(user, ClaimsList.getInstance(this));

        setupListView();
        setupActionBar();

        TagsManager.get(this).addTagChangedListener(mClaimListController);
    }

    @Override
    public void onResume() {
        super.onResume();

        setupListView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TagsManager.get(this).removeTagChangedListener(mClaimListController);
    }

    private void setupListView() {
        mApproverAdapter = new ClaimsApproverAdapter(this, mClaimListController.getApprovableClaims());
        mClaimantAdapter = new ClaimsClaimantAdapter(this, mClaimListController.getClaimantClaims());
        mClaimantAdapter.updateFilter(mWantedTags);

        mClaimantAdapter.sort(Claim.START_ASCENDING);
        mApproverAdapter.sort(Claim.START_DESCENDING);

        if (showClaimantList()) {
            setListAdapter(mClaimantAdapter);
        } else {
            setListAdapter(mApproverAdapter);
        }
    }

    private boolean showClaimantList() {
        final ActionBar actionBar = getActionBar();
        return actionBar == null || actionBar.getSelectedTab() == null || actionBar.getSelectedTab().getPosition() == POSITION_CLAIMANT;
    }

    @Override
    public void onListItemClick(ListView lv, View v, int position, long id) {
        @SuppressWarnings("unchecked") // Both Adapters extend ArrayAdapter<Claim>
        ArrayAdapter<Claim> adapter = (ArrayAdapter<Claim>) getListAdapter();
        Intent i = new Intent(this, ClaimViewActivity.class);

        i.putExtra(App.KEY_CLAIM_ID, adapter.getItem(position).getId());

        startActivityForResult(i, VIEW_CLAIM);
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
                            setListAdapter(mClaimantAdapter);
                            break;
                        case POSITION_APPROVER:
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
                startActivity(new Intent(this, EditClaimActivity.class));
                return true;
            case R.id.filter:
                startTagSelector();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startTagSelector() {
        ArrayList<Tag> allTags = new ArrayList<Tag>(TagsManager.get(this).peekTags());
        if (allTags.size() > 0) {
            DialogFragment fragment = TagSelectorDialogFragment.newInstance(allTags, mWantedTags);
            fragment.show(getFragmentManager(), "dialog");
        } else {
            Toast.makeText(this, "There are no tags to filter", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == VIEW_CLAIM) {
            mClaimantAdapter = new ClaimsClaimantAdapter(this, mClaimListController.getClaimantClaims());
            setListAdapter(mClaimantAdapter);
        }
    }

    @Override
    public void onTagRenamed(Tag tag, Tag oldName) {
        setupListView();

        if (mWantedTags.contains(oldName)) {
            mWantedTags.remove(tag);
            mWantedTags.add(tag);

            mClaimantAdapter.updateFilter(mWantedTags);
        }
    }

    @Override
    public void onTagDeleted(Tag tag) {
        setupListView();

        if (mWantedTags.contains(tag)) {
            mWantedTags.remove(tag);

            mClaimantAdapter.updateFilter(mWantedTags);
        }
    }

    @Override
    public void onTagCreated(Tag tag) {
        // do nothing
    }

    @Override
    public void wantedTagsChanged(ArrayList<Tag> newWantedTags) {
        mWantedTags = newWantedTags;
        mClaimantAdapter.updateFilter(mWantedTags);
    }
}