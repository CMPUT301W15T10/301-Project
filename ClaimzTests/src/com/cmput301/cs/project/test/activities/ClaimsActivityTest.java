package com.cmput301.cs.project.test.activities;

import com.cmput301.cs.project.activities.ClaimListActivity;

import android.test.ActivityInstrumentationTestCase2;

public class ClaimsActivityTest extends ActivityInstrumentationTestCase2<ClaimListActivity> {

	public ClaimsActivityTest() {
		super(ClaimListActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testLoginLaunchesIfNoUserId() {
        fail();
    }

    public void testLoginLaunchesIfNoUserName() {
        fail();
    }

}
