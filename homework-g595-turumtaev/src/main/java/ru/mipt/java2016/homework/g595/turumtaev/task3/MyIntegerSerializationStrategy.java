package ru.mipt.java2016.homework.g595.turumtaev.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by galim on 19.11.2016.
 */
public class MyIntegerSerializationStrategy implements MySerializationStrategy<Integer> {
    private static final MyIntegerSerializationStrategy INSTANCE = new MyIntegerSerializationStrategy();

    public static MyIntegerSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private MyIntegerSerializationStrategy() {
    }

    @Override
    public Long write(Integer value, RandomAccessFile output) throws IOException {
        Long offset = output.getFilePointer();
        output.writeInt(value);
        return offset;
    }

    @Override
    public Integer read(RandomAccessFile input) throws IOException {
        return input.readInt();
    }
}
