package com.cmput301.cs.project;

import com.cmput301.cs.project.model.Receipt;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class ReceiptTest {

    @Test(expected = UnsupportedOperationException.class)
    public void fileSize() {
        final File bigFile = new MockFile("", Receipt.MAX_FILE_SIZE + 1);
        new Receipt(bigFile);
    }

    @Test
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