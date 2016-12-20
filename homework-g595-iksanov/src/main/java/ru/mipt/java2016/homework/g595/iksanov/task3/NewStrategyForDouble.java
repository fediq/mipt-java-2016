package ru.mipt.java2016.homework.g595.iksanov.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Эмиль
 */
public class NewStrategyForDouble implements NewSerializationStrategy<Double> {
    private static final NewStrategyForDouble INSTANCE = new NewStrategyForDouble();

    public static NewStrategyForDouble getInstance() {
        return INSTANCE;
    }

    private NewStrategyForDouble() {

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
