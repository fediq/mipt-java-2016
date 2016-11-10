package ru.mipt.java2016.homework.g597.miller.task2;

import java.io.IOException;

/**
 * Created by Vova Miller on 31.10.2016.
 */
public class MillerStorageStrings extends MillerStorageAbstract<String, String> {

    public MillerStorageStrings(String directoryName) {
        super(directoryName);
    }

    @Override
    protected String readKey() {
        return readString();
    }

    @Override
    protected String readValue() {
        return readString();
    }

    @Override
    protected void writeKey(String key) {
        writeString(key);
    }

    @Override
    protected void writeValue(String value) {
        writeString(value);
    }

    private String readString() {
        try {
            char c;
            StringBuilder sb = new StringBuilder();
            int n = file.readInt();
            if (n < 0) {
                throw new RuntimeException("Invalid storage file.");
            }
            for (int i = 0; i < n; ++i) {
                c = file.readChar();
                sb.append(c);
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeString(String s) {
        if (s == null) {
            throw new NullPointerException();
        }
        try {
            file.writeInt(s.length());
            file.writeChars(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}