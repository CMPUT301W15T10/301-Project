package com.cmput301.cs.project.project.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.cmput301.cs.project.project.App;
import com.cmput301.cs.project.R;


public class ClaimListActivity extends Activity {

    public final int LOGIN_REQUEST = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.claim_list_activity);

        App app = App.get(this);

        if(app.getUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
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
            	startActivity(new Intent(this, NewClaimActivity.class));
            	return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}