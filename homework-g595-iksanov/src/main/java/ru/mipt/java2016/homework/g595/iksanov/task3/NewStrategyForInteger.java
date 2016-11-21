package ru.mipt.java2016.homework.g595.iksanov.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Эмиль
 */
public class NewStrategyForInteger implements NewSerializationStrategy<Integer> {
    private static final NewStrategyForInteger INSTANCE = new NewStrategyForInteger();

    public static NewStrategyForInteger getInstance() {
        return INSTANCE;
    }

    private NewStrategyForInteger() {}

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
