package ru.mipt.java2016.homework.g594.rubanenko.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by king on 31.10.16.
 */

/* ! Special class for serialization of doubles */
public class MyDoubleSerializer implements MySerializer<Double> {
    /* ! Write method */
    @Override
    public void serializeToStream(DataOutputStream output, Double value) throws IOException {
        output.writeDouble(value);
    }

    /* ! Read method */
    @Override
    public Double deserializeFromStream(DataInputStream input) throws IOException {
        return input.readDouble();
    }
}
