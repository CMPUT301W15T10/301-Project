package com.cmput301.cs.project.activities;

import android.test.ActivityInstrumentationTestCase2;
import org.junit.Test;

import static org.junit.Assert.*;

public class ClaimsActivityTest extends ActivityInstrumentationTestCase2<ClaimsActivity> {

    public ClaimsActivityTest(Class<ClaimsActivity> activityClass) {
        super(activityClass);
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
