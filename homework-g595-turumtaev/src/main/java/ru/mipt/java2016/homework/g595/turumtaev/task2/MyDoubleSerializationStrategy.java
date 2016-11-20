package ru.mipt.java2016.homework.g595.turumtaev.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by galim on 31.10.2016.
 */
public class MyDoubleSerializationStrategy implements MySerializationStrategy<Double> {
    private static final MyDoubleSerializationStrategy INSTANCE = new MyDoubleSerializationStrategy();

    public static MyDoubleSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private MyDoubleSerializationStrategy() {
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
