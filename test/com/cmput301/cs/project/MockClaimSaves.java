package com.cmput301.cs.project;

import com.cmput301.cs.project.utils.ClaimSaves;

import java.io.*;

public final class MockClaimSaves extends ClaimSaves {
    private String mJsonString;

    @Override
    protected InputStream getInputStreamForReading(String fileName) throws IOException {
        if (mJsonString == null) {
            throw new FileNotFoundException();
        }
        return new ByteArrayInputStream(mJsonString.getBytes());
    }

    @Override
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