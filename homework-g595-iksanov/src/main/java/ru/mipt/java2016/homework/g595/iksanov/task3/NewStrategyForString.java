package ru.mipt.java2016.homework.g595.iksanov.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Эмиль
 */
public class NewStrategyForString implements NewSerializationStrategy<String> {
    private static final NewStrategyForString INSTANCE = new NewStrategyForString();

    public static NewStrategyForString getInstance() {
        return INSTANCE;
    }

    private NewStrategyForString() {}

    @Override
    public Long write(String value, RandomAccessFile output) throws IOException {
        Long offset = output.getFilePointer();
        output.writeUTF(value);
        return offset;
    }

    @Override
    public String read(RandomAccessFile input) throws IOException {
        return input.readUTF();
    }
}
