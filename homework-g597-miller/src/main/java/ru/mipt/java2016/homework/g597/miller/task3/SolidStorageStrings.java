package ru.mipt.java2016.homework.g597.miller.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/*
 * Created by Vova Miller on 20.11.2016.
 */
public class SolidStorageStrings extends SolidStorageAbstract<String, String> {

    public SolidStorageStrings(String directoryName) throws IOException {
        super(directoryName);
    }

    @Override
    protected String readKey(DataInput f) throws IOException {
        return readString(f);
    }

    @Override
    protected String readValue(DataInput f) throws IOException {
        return readString(f);
    }

    @Override
    protected void writeKey(DataOutput f, String key) throws IOException {
        writeString(f, key);
    }

    @Override
    protected void writeValue(DataOutput f, String value) throws IOException {
        writeString(f, value);
    }

    private String readString(DataInput f) throws IOException {
        try {
            int n = f.readInt();
            if (n < 0) {
                throw new IOException("Invalid storage file.");
            }
            byte[] b = new byte[n];
            f.readFully(b, 0, n);
            return new String(b, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    private void writeString(DataOutput f, String s) throws IOException {
        if (s == null) {
            throw new NullPointerException();
        }
        try {
            byte[] b = s.getBytes();
            f.writeInt(b.length);
            f.write(b);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}