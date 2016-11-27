package ru.mipt.java2016.homework.g597.markov.task3;

/**
 * Created by Alexander on 23.11.2016.
 */

import java.io.IOException;
import java.io.RandomAccessFile;

public class LongSerializator implements SerializationStrategy<Long> {

    @Override
    public Long read(RandomAccessFile fileName) throws IOException {
        return fileName.readLong();
    }

    @Override
    public void write(RandomAccessFile fileName, Long data) throws IOException {
        fileName.writeLong(data);
    }
}
