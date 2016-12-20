package ru.mipt.java2016.homework.g599.lantsetov.task2;

import java.io.IOException;
import java.io.RandomAccessFile;


public class StringSerializer implements MySerializer<String> {
    @Override
    public String read(RandomAccessFile file) throws IOException {
        int length = file.readInt();
        byte[] bytes = new byte[length];
        file.readFully(bytes);
        return new String(bytes);
    }

    @Override
    public void write(RandomAccessFile file, String arg) throws IOException {
        byte[] bytes = arg.getBytes();
        file.writeInt(bytes.length);
        file.write(bytes);
    }
}