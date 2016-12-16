package ru.mipt.java2016.homework.g597.grishutin.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntegerSerializer implements SerializationStrategy<Integer> {
    private static final IntegerSerializer INSTANCE = new IntegerSerializer();

    public static IntegerSerializer getInstance() {
        return INSTANCE;
    }

    @Override
    public void serialize(Integer value, DataOutput raf) throws IOException {
        raf.writeInt(value);
    }

    @Override
    public Integer deserialize(DataInput raf) throws IOException {
        return raf.readInt();
    }

    @Override
    public Long bytesSize(Integer value) {
        return (long) Integer.BYTES;
    }
}
