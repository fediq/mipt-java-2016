package ru.mipt.java2016.homework.g597.smirnova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Elena Smirnova on 31.10.2016.
 */
public class DoubleSerializationStrategy implements SerializationStrategy<Double> {

    @Override
    public void writeToStream(DataOutputStream s, Double value) throws IOException {
        s.writeDouble(value);
    }

    @Override
    public Double readFromStream(DataInputStream s) throws IOException {
        return s.readDouble();
    }
}
