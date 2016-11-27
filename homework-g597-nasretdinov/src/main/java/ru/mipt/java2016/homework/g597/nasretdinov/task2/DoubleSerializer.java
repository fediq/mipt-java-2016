package ru.mipt.java2016.homework.g597.nasretdinov.task2;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by isk on 31.10.16.
 */
public class DoubleSerializer implements SerializerInterface<Double> {
    @Override
    public void write(DataOutputStream stream, Double doubleData) throws IOException {
        stream.writeDouble(doubleData);
    }

    @Override
    public Double read(DataInputStream stream) throws IOException {
        return stream.readDouble();
    }
}