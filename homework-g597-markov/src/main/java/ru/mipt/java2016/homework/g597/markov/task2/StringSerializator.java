package ru.mipt.java2016.homework.g597.markov.task2;

/**
 * Created by Alexander on 30.10.2016.
 */

import java.io.IOException;
import java.io.RandomAccessFile;


public class StringSerializator implements SerializationStrategy<String> {


    @Override
    public String read(RandomAccessFile fileName) throws IOException {
        int length = fileName.readInt();
        byte[] bytes = new byte[length];
        fileName.readFully(bytes);
        return new String(bytes, "UTF-8");
    }

    @Override
    public void write(RandomAccessFile fileName, String data) throws IOException {
        byte[] bytes = data.getBytes();
        fileName.writeInt(bytes.length);
        fileName.write(bytes);
    }
}
