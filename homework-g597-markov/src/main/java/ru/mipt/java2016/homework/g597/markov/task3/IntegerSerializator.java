package ru.mipt.java2016.homework.g597.markov.task3;

/**
 * Created by Alexander on 23.11.2016.
 */

import java.io.IOException;
import java.io.RandomAccessFile;

public class IntegerSerializator implements SerializationStrategy<Integer> {

    @Override
    public Integer read(RandomAccessFile fileName) throws IOException {
        return fileName.readInt();
    }

    @Override
    public void write(RandomAccessFile fileName, Integer data) throws IOException {
        fileName.writeInt(data);
    }
}
