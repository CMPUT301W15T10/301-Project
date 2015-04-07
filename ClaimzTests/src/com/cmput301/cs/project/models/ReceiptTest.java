package com.cmput301.cs.project.models;

import junit.framework.TestCase;

public class ReceiptTest extends TestCase {

    public void testImageSize() {
        final String smallImage = generateSmallImage();
        new Receipt(smallImage);
        
        try {
            final String bigImage = generateLargeImage();
            new Receipt(bigImage);
            fail("File was of size: " + bigImage.getBytes().length);
        } catch (IllegalArgumentException e) {
            // Success
        }
    }

    public void testNullImage() {
        try {
            new Receipt(null);
            fail();
        } catch (IllegalArgumentException e) {
            // Success
        }
    }


    private String generateSmallImage() {
        byte bytes[] = new byte[Receipt.MAX_FILE_SIZE];
        return new String(bytes);
    }

    private String generateLargeImage() {
        byte bytes[] = new byte[Receipt.MAX_FILE_SIZE + 1];
        return new String(bytes);
    }
}