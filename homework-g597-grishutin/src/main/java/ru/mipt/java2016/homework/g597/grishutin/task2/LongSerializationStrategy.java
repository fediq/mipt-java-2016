package ru.mipt.java2016.homework.g597.grishutin.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

public class LongSerializationStrategy implements SerializationStrategy<Long> {
    public static LongSerializationStrategy INSTANCE = new LongSerializationStrategy();

    @Override
    public void serialize(Long value, RandomAccessFile raf) throws IOException {
        raf.writeLong(value);
    }

    @Override
    public Long deserialize(RandomAccessFile raf) throws IOException {
        return raf.readLong();
    }
}
