package ru.mipt.java2016.homework.g597.vasilyev.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by mizabrik on 30.10.16.
 */
public class DoubleSerializer implements Serializer<Double> {
    @Override
    public void write(Double value, DataOutput destination) throws IOException {
        destination.writeDouble(value);
    }

    @Override
    public Double read(DataInput source) throws IOException {
        return source.readDouble();
    }
}
