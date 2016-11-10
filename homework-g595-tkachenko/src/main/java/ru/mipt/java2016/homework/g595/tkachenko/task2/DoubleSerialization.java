package ru.mipt.java2016.homework.g595.tkachenko.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Dmitry on 30/10/2016.
 */
public class DoubleSerialization extends Serialization<Double> {
    @Override
    public Double read(DataInputStream input) throws IOException {
        return input.readDouble();
    }

    @Override
    public void write(DataOutputStream output, Double x) throws IOException {
        output.writeDouble(x);
    }
}
