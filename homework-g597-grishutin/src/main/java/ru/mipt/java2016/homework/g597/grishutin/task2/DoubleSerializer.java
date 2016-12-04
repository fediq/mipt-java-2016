package ru.mipt.java2016.homework.g597.grishutin.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DoubleSerializer implements SerializationStrategy<Double> {
    private static final DoubleSerializer INSTANCE = new DoubleSerializer();

    public static DoubleSerializer getInstance() {
        return INSTANCE;
    }

    @Override
    public void serialize(Double value, DataOutput raf) throws IOException {
        raf.writeDouble(value);
    }

    @Override
    public Double deserialize(DataInput raf) throws IOException {
        return raf.readDouble();
    }

    @Override
    public Long bytesSize(Double value) {
        return (long) Double.BYTES;
    }
}
