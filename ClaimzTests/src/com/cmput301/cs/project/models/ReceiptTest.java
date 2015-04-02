package com.cmput301.cs.project.models;

import java.io.File;

import junit.framework.TestCase;

public class ReceiptTest extends TestCase {

    public void fileSize() {
        try {
            final File bigFile = new MockFile("", Receipt.MAX_FILE_SIZE + 1);
            new Receipt(bigFile);
            fail();
        } catch (UnsupportedOperationException e) {
            // Success
        }
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