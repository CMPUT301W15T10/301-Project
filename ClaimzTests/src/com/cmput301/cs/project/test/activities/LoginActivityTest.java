package com.cmput301.cs.project.test.activities;

import com.cmput301.cs.project.activities.ClaimListActivity;

import android.test.ActivityInstrumentationTestCase2;

public class LoginActivityTest extends ActivityInstrumentationTestCase2<ClaimListActivity> {

	public LoginActivityTest() {
		super(ClaimListActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testValidNameLaunchesClaims() {
        fail();
    }

    public void testInvalidName() {
        fail();
    }

}
