package ru.mipt.java2016.homework.g597.markov.task3;

/**
 * Created by Alexander on 23.11.2016.
 */

import java.io.IOException;
import java.io.RandomAccessFile;

public class DoubleSerializator implements SerializationStrategy<Double> {

    @Override
    public Double read(RandomAccessFile fileName) throws IOException {
        return fileName.readDouble();
    }

    @Override
    public void write(RandomAccessFile fileName, Double data) throws IOException {
        fileName.writeDouble(data);
    }
}