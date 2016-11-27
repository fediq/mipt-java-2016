package ru.mipt.java2016.homework.g597.grishutin.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DoubleSerializer implements SerializationStrategy<Double> {
    private static DoubleSerializer instance = new DoubleSerializer();

    public static DoubleSerializer getInstance() {
        return instance;
    }

    @Override
    public void serialize(Double value, RandomAccessFile raf) throws IOException {
        raf.writeDouble(value);
    }

    @Override
    public Double deserialize(RandomAccessFile raf) throws IOException {
        return raf.readDouble();
    }

    @Override
    public Long bytesSize(Double value) {
        return (long) Double.BYTES;
    }
}
