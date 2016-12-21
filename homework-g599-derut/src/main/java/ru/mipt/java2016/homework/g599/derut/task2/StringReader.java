package ru.mipt.java2016.homework.g599.derut.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

public class StringReader implements Serializer<String> {

    @Override
    public void write(RandomAccessFile f, String val) throws IOException {
        int length = val.length();
        char[] c = val.toCharArray();
        f.writeInt(length);
        for (int i = 0; i < length; i++) {
            f.writeChar(c[i]);
        }

    }

    @Override
    public String read(RandomAccessFile f) throws IOException {
        int length = f.readInt();
        char[] c = new char[length];
        for (int i = 0; i < length; i++) {
            c[i] = f.readChar();
        }
        return new String(c);
    }

}
