package ru.mipt.java2016.homework.g597.grishutin.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class LongSerializer implements SerializationStrategy<Long> {
    private static final LongSerializer INSTANCE = new LongSerializer();

    public static LongSerializer getInstance() {
        return INSTANCE;
    }

    @Override
    public void serialize(Long value, DataOutput raf) throws IOException {
        raf.writeLong(value);
    }

    @Override
    public Long deserialize(DataInput raf) throws IOException {
        return raf.readLong();
    }

    @Override
    public Long bytesSize(Long value) {
        return (long) Long.BYTES;
    }
}
