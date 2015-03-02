/*
 * Copyright 2015 Edmond Chui
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cmput301.cs.project;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.cmput301.cs.project.model.Claim;
import com.cmput301.cs.project.utils.ClaimSaves;

public class ClaimsActivity extends ListActivity {
    private static final int REQ_CODE_ADD_CLAIM = 1;

    private ClaimSaves mClaimSaves;
    private ClaimsAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mClaimSaves = ClaimSaves.ofAndroid(this);
        mAdapter = new ClaimsAdapter(this, mClaimSaves.readAllClaims());
        setListAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.claims_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add:
                startActivityForResult(new Intent(this, ClaimBuilderActivity.class), REQ_CODE_ADD_CLAIM);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        final Claim claim = mAdapter.getItem(position);
        final Intent intent = ClaimActivity.createIntentWithClaim(this, claim);
        startActivityForResult(intent, REQ_CODE_ADD_CLAIM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_ADD_CLAIM:
                    final String action = data.getAction();
                    if (ClaimBuilderActivity.ACTION_PUT.equals(action)) {
                        final String claimKey = data.getStringExtra(ClaimBuilderActivity.KEY_CLAIM);
                        final Claim claim = App.get(this).getObjectTransfer(claimKey);
                        mAdapter.putClaim(claim);
                        mClaimSaves.saveAllClaims(mAdapter.peekAllClaims());
                    } else if (ClaimActivity.ACTION_DELETE.equals(action)) {
                        final String claimId = data.getStringExtra(ClaimActivity.KEY_CLAIM_ID);
                        mAdapter.removeClaimById(claimId);
                        mClaimSaves.saveAllClaims(mAdapter.peekAllClaims());
                    }
                    return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
