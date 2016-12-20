package ru.mipt.java2016.homework.g596.kozlova.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MyDoubleSerialization implements MySerialization<Double> {
    @Override
    public void write(Double obj, DataOutput output) throws IOException {
        output.writeDouble(obj);
    }

    @Override
    public Double read(DataInput input) throws IOException {
        return input.readDouble();
    }
}
