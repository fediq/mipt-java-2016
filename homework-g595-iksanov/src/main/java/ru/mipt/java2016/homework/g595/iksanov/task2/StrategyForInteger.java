package ru.mipt.java2016.homework.g595.iksanov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Эмиль
 */
public class StrategyForInteger implements SerializationStrategy<Integer> {
    private static final StrategyForInteger INSTANCE = new StrategyForInteger();

    public static StrategyForInteger getInstance() {
        return INSTANCE;
    }

    private StrategyForInteger() {
    }

    @Override
    public void write(Integer value, DataOutputStream output) throws IOException {
        output.writeInt(value);
    }

    @Override
    public Integer read(DataInputStream input) throws IOException {
        return input.readInt();
    }
}
