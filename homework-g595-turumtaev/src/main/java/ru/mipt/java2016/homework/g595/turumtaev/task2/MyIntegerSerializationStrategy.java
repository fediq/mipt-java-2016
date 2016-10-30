package ru.mipt.java2016.homework.g595.turumtaev.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by galim on 31.10.2016.
 */

public class MyIntegerSerializationStrategy implements MySerializationStrategy<Integer> {
    private static final MyIntegerSerializationStrategy INSTANCE = new MyIntegerSerializationStrategy();

    public static MyIntegerSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private MyIntegerSerializationStrategy() {
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
