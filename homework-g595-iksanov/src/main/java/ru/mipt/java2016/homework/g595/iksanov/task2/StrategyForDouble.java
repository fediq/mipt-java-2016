package ru.mipt.java2016.homework.g595.iksanov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Эмиль
 */
public class StrategyForDouble implements SerializationStrategy<Double> {
    private static final StrategyForDouble INSTANCE = new StrategyForDouble();

    public static StrategyForDouble getInstance() {
        return INSTANCE;
    }

    private StrategyForDouble() {
    }

    @Override
    public void write(Double value, DataOutputStream output) throws IOException {
        output.writeDouble(value);
    }

    @Override
    public Double read(DataInputStream input) throws IOException {
        return input.readDouble();
    }
}