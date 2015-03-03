package com.cmput301_project;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.cmput301_project.LoginTest \
 * com.cmput301_project.tests/android.test.InstrumentationTestRunner
 */

import com.cmput301_project.login.Login;

public class LoginTest extends ActivityInstrumentationTestCase2<Login> {

    public LoginTest() {
        super("com.cmput301_project", Login.class);
    }

}
