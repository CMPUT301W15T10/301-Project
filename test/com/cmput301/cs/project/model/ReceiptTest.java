package com.cmput301.cs.project.model;

import org.junit.Test;

import java.io.File;

public class ReceiptTest {
    @Test(expected = UnsupportedOperationException.class)
    public void fileSize() {
        final File bigFile = new MockFile("", Receipt.MAX_FILE_SIZE + 1);
        new Receipt(bigFile);
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