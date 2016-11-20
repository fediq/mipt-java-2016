package ru.mipt.java2016.homework.g595.turumtaev.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by galim on 19.11.2016.
 */
public class MyDoubleSerializationStrategy implements MySerializationStrategy<Double> {
    private static final MyDoubleSerializationStrategy INSTANCE = new MyDoubleSerializationStrategy();

    public static MyDoubleSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private MyDoubleSerializationStrategy() {
    }

    @Override
    public Long write(Double value, RandomAccessFile output) throws IOException {
        Long offset = output.getFilePointer();
        output.writeDouble(value);
        return offset;
    }

    @Override
    public Double read(RandomAccessFile input) throws IOException {
        return input.readDouble();
    }
}
