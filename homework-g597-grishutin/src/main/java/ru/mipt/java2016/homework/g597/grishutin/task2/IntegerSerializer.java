package ru.mipt.java2016.homework.g597.grishutin.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

public class IntegerSerializer implements SerializationStrategy<Integer> {
    private static IntegerSerializer instance = new IntegerSerializer();

    public static IntegerSerializer getInstance() {
        return instance;
    }

    @Override
    public void serialize(Integer value, RandomAccessFile raf) throws IOException {
        raf.writeInt(value);
    }

    @Override
    public Integer deserialize(RandomAccessFile raf) throws IOException {
        return raf.readInt();
    }

    @Override
    public Long bytesSize(Integer value) {
        return (long) Integer.BYTES;
    }
}
