package com.cmput301.cs.project.activities;

import android.test.ActivityInstrumentationTestCase2;

import org.junit.Test;

import com.cmput301.cs.project.project.activities.ClaimListActivity;

public class ClaimsActivityTest extends ActivityInstrumentationTestCase2<ClaimListActivity> {

    public ClaimsActivityTest() {
        super(ClaimListActivity.class);
    }

    @Test
    public void testLoginLaunchesIfNoUserId() {
        fail();
    }

    @Test
    public void testLoginLaunchesIfNoUserName() {
        fail();
    }
}
