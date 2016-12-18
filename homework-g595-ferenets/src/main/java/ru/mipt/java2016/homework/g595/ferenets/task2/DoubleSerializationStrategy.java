package ru.mipt.java2016.homework.g595.ferenets.task2;

import java.io.IOException;
import java.io.RandomAccessFile;


public class DoubleSerializationStrategy implements SerializationStrategy<Double> {
    @Override
    public Double read(RandomAccessFile file) throws IOException {
        return file.readDouble();
    }

    @Override
    public void write(RandomAccessFile file, Double value) throws IOException {
        file.writeDouble(value);
    }
}
