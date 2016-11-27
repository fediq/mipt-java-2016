package ru.mipt.java2016.homework.g597.grishutin.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

public class BooleanSerializer implements SerializationStrategy<Boolean> {
    private static BooleanSerializer instance = new BooleanSerializer();

    public static BooleanSerializer getInstance() {
        return instance;
    }

    @Override
    public void serialize(Boolean value, RandomAccessFile raf) throws IOException {
        raf.writeBoolean(value);
    }

    @Override
    public Boolean deserialize(RandomAccessFile raf) throws IOException {
        return raf.readBoolean();
    }

    @Override
    public Long bytesSize(Boolean value) {
        return (long) 1;
    }
}
