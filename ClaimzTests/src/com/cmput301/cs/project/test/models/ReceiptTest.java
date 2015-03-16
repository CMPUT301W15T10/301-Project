package com.cmput301.cs.project.test.models;


import java.io.File;


import com.cmput301.cs.project.activities.ClaimListActivity;
import com.cmput301.cs.project.model.Receipt;

import android.test.ActivityInstrumentationTestCase2;

public class ReceiptTest extends ActivityInstrumentationTestCase2<ClaimListActivity> {

	public ReceiptTest() {
		super(ClaimListActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}
	
    public void fileSize() {
        final File bigFile = new MockFile("", Receipt.MAX_FILE_SIZE + 1);
        new Receipt(bigFile);
    }

    public void testGetImage() throws Exception {
        String path = "path";
        final Receipt receipt = new Receipt(path);
        assertEquals(new File(path), receipt.getFile());
    }

    private static class MockFile extends File {

        private final int mLength;

        public MockFile(String path, int length) {
            super(path);
            mLength = length;
        }

        @Override
        public long length() {
            return mLength;
        }
    }

}
