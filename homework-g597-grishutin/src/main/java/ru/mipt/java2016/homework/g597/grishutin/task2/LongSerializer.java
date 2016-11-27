package ru.mipt.java2016.homework.g597.grishutin.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

public class LongSerializer implements SerializationStrategy<Long> {
    private static LongSerializer instance = new LongSerializer();

    public static LongSerializer getInstance() {
        return instance;
    }

    @Override
    public void serialize(Long value, RandomAccessFile raf) throws IOException {
        raf.writeLong(value);
    }

    @Override
    public Long deserialize(RandomAccessFile raf) throws IOException {
        return raf.readLong();
    }

    @Override
    public Long bytesSize(Long value) {
        return (long) Long.BYTES;
    }
}
