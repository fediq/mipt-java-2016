package ru.mipt.java2016.homework.g595.iksanov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Эмиль
 */
public class StrategyForString implements SerializationStrategy<String> {
    private static final StrategyForString INSTANCE = new StrategyForString();

    public static StrategyForString getInstance() {
        return INSTANCE;
    }

    private StrategyForString() {
    }

    @Override
    public void write(String value, DataOutputStream output) throws IOException {
        output.writeUTF(value);
    }

    @Override
    public String read(DataInputStream input) throws IOException {
        return input.readUTF();
    }

}
