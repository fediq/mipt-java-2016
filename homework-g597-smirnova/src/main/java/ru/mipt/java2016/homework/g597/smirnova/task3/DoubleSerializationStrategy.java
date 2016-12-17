package ru.mipt.java2016.homework.g597.smirnova.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Elena Smirnova on 21.11.2016.
 */
public class DoubleSerializationStrategy implements SerializationStrategy<Double> {
    @Override
    public void writeToStream(DataOutput s, Double value) throws IOException {
        s.writeDouble(value);
    }

    @Override
    public Double readFromStream(DataInput s) throws IOException {
        return s.readDouble();
    }
}
