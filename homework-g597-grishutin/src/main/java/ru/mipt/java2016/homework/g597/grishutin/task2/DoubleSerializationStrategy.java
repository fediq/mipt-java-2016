package ru.mipt.java2016.homework.g597.grishutin.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DoubleSerializationStrategy implements SerializationStrategy<Double> {
    public static DoubleSerializationStrategy INSTANCE = new DoubleSerializationStrategy();

    @Override
    public void serialize(Double value, RandomAccessFile raf) throws IOException {
        raf.writeDouble(value);
    }

    @Override
    public Double deserialize(RandomAccessFile raf) throws IOException {
        return raf.readDouble();
    }
}
