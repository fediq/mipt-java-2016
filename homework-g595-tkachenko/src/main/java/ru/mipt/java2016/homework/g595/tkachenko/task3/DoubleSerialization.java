package ru.mipt.java2016.homework.g595.tkachenko.task3;

import java.io.*;

/**
 * Created by Dmitry on 20/11/2016.
 */

public class DoubleSerialization extends Serialization<Double> {
    @Override
    public Double read(DataInput input) throws IOException {
        return input.readDouble();
    }

    @Override
    public void write(DataOutput output, Double x) throws IOException {
        output.writeDouble(x);
    }
}
