package ru.mipt.java2016.homework.g597.miller.task2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * Created by Vova Miller on 31.10.2016.
 */
public class MillerStorageStrings extends MillerStorageAbstract<String, String> {

    public MillerStorageStrings(String directoryName) throws IOException {
        super(directoryName);
    }

    @Override
    protected String readKey(RandomAccessFile file) throws IOException {
        return readString(file);
    }

    @Override
    protected String readValue(RandomAccessFile file) throws IOException {
        return readString(file);
    }

    @Override
    protected void writeKey(RandomAccessFile file, String key) throws IOException {
        writeString(file, key);
    }

    @Override
    protected void writeValue(RandomAccessFile file, String value) throws IOException {
        writeString(file, value);
    }

    private String readString(RandomAccessFile file) throws IOException {
        try {
            int n = file.readInt();
            if (n < 0) {
                throw new IOException("Invalid storage file.");
            }
            byte[] b = new byte[n];
            file.read(b, 0, n);
            return new String(b, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    private void writeString(RandomAccessFile file, String s) throws IOException {
        if (s == null) {
            throw new NullPointerException();
        }
        try {
            byte[] b = s.getBytes();
            file.writeInt(b.length);
            file.write(b);
        } catch (IOException e) {
            throw new IOException(e);
        }
    }
}