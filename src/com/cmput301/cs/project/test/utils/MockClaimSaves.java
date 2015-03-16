package com.cmput301.cs.project.test.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.cmput301.cs.project.activities.ClaimListActivity;

import android.test.ActivityInstrumentationTestCase2;

public class MockClaimSaves extends ActivityInstrumentationTestCase2<ClaimListActivity> {

	public MockClaimSaves() {
		super(ClaimListActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	private String mJsonString;

    protected InputStream getInputStreamForReading(String fileName) throws IOException {
        if (mJsonString == null) {
            throw new FileNotFoundException();
        }
        return new ByteArrayInputStream(mJsonString.getBytes());
    }

    protected OutputStream getOutputStreamForSaving(String fileName) throws IOException {
        return new ByteArrayOutputStream() {
            @Override
            public void flush() throws IOException {
                super.flush();
                mJsonString = toString();
            }

            @Override
            public void close() throws IOException {
                super.close();
                mJsonString = toString();
            }
        };
    }
}
