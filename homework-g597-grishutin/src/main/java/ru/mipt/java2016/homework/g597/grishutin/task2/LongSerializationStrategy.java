package ru.mipt.java2016.homework.g597.grishutin.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

class LongSerializationStrategy implements SerializationStrategy<Long> {
    private static LongSerializationStrategy instance = new LongSerializationStrategy();

    public static LongSerializationStrategy getInstance() {
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
}
