package ru.mipt.java2016.homework.g595.iksanov.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Эмиль
 */
public class NewStrategyForLong implements NewSerializationStrategy<Long> {
    private static final NewStrategyForLong INSTANCE = new NewStrategyForLong();

    public static NewStrategyForLong getInstance() {
        return INSTANCE;
    }

    private NewStrategyForLong() {

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
