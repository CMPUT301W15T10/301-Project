package com.cmput301.cs.project.activities;

import android.test.ActivityInstrumentationTestCase2;

import org.junit.Test;

import com.cmput301.cs.project.project.activities.ClaimsActivity;

public class ClaimsActivityTest extends ActivityInstrumentationTestCase2<ClaimsActivity> {

    public ClaimsActivityTest() {
        super(ClaimsActivity.class);
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
