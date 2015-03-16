package com.cmput301.cs.project.activities;


import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.adapters.ClaimsAdapter;
import com.cmput301.cs.project.controllers.ClaimListController;
import com.cmput301.cs.project.model.Claim;
import com.cmput301.cs.project.model.ClaimsList;
import com.cmput301.cs.project.model.User;

/**
 * Is the activity that launches at start of app. </br>
 * An activity that shows a list of {@link com.cmput301.cs.project.model.Claim Claims}. </br>
 * Redirects to {@link com.cmput301.cs.project.activites.LoginActivity LoginActivity} if no user is found.
 * Has menu buttons that allow the creation of new claims via {@link com.cmput301.cs.project.activites.EditClaimActivity EditClaimActivity} 
 * and for the {@link com.cmput301.cs.project.activites.TagManagerActivity TagManagerActivity}. </br>
 * If a claim item is clicked {@link com.cmput301.cs.project.activites.ClaimViewActivity ClaimViewActivity} is called for that claim. </br>
 * Finally there are tabs at the top of the activity that allow the user to switch between approver and claimant (not yet implemented).
 * 
 * @author rozsa
 *
 */


public class ClaimListActivity extends ListActivity {

    private static final int POSITION_CLAIMANT = 0;
    private static final int POSITION_APPROVER = 1;
    private static final int NEW_CLAIM = 0;

    User mUser;
    ClaimListController mClaimListController;
    ClaimsAdapter mApproverAdapter;
    ClaimsAdapter mClaimantAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_list_activity);

        App app = App.get(this);

        Log.d("my tag", "HELLO WORLD!!!");


        mUser = app.getUser();

        mClaimListController = new ClaimListController(mUser, ClaimsList.getInstance(this));

        setupListView();
        setupActionBar();
    }

    @Override
    public void onResume() {
        super.onResume();

        mClaimantAdapter.notifyDataSetChanged();
        mApproverAdapter.notifyDataSetChanged();


        Log.d("my tag", "" + mClaimantAdapter.getCount());

        ((ClaimsAdapter)getListAdapter()).notifyDataSetChanged();

    }

	private void setupListView()
	{
		mApproverAdapter = new ClaimsAdapter(this, mClaimListController.getApprovableClaims());
        mClaimantAdapter = new ClaimsAdapter(this, mClaimListController.getClaimantClaims());
		setListAdapter(mClaimantAdapter);
	
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id){


		ClaimsAdapter adapter = (ClaimsAdapter) getListAdapter();
		Intent i = new Intent(this, ClaimViewActivity.class);
		
		i.putExtra(App.KEY_CLAIM, adapter.getItem(position));
		
		startActivity(i);
		
	}
	
	private void setupActionBar()
	{

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
    public void onActivityResult(int requestCode, int resultCode, Intent data){
    	if (requestCode == NEW_CLAIM) {
    		if (resultCode == RESULT_OK) {
    			Claim claim = data.getExtras().getParcelable(App.KEY_CLAIM);
                Log.d("my tag", "" + mClaimantAdapter.getCount());
                mClaimListController.addClaim(claim);
    		}
    	}
    }
}