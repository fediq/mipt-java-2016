package ru.mipt.java2016.homework.g595.turumtaev.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by galim on 19.11.2016.
 */
public class MyLongSerializationStrategy implements MySerializationStrategy<Long> {
    private static final MyLongSerializationStrategy INSTANCE = new MyLongSerializationStrategy();

    public static MyLongSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private MyLongSerializationStrategy() {
    }

    @Override
    public Long write(Long value, RandomAccessFile output) throws IOException {
        Long offset = output.getFilePointer();
        output.writeLong(value);
        return offset;
    }

    @Override
    public Long read(RandomAccessFile input) throws IOException {
        return input.readLong();
    }
}
