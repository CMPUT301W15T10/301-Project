package com.cmput301.cs.project.activities;


import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import com.cmput301.cs.project.App;
import com.cmput301.cs.project.R;
import com.cmput301.cs.project.adapters.ClaimsAdapter;
import com.cmput301.cs.project.utils.ClaimSaves;


public class ClaimListActivity extends Activity {

    private static final int POSITION_CLAIMANT = 0;
    private static final int POSITION_APPROVER = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_list_activity);

        App app = App.get(this);

        if (app.getUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        
        
        
        setupListView();
        setupActionBar();
    }

	private void setupListView()
	{
		ListView claimsList = (ListView) findViewById(R.id.claims_list);
		ClaimSaves claimSaves = ClaimSaves.ofAndroid(this);
		claimSaves.readAllClaims();
		ClaimsAdapter adapter = new ClaimsAdapter(this, claimSaves.readAllClaims());
		
		claimsList.setAdapter(adapter);
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
                            break;
                        case POSITION_APPROVER:
                            // TODO impl approver filter
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}