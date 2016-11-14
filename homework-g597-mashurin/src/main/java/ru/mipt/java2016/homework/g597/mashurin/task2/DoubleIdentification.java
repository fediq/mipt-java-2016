package ru.mipt.java2016.homework.g597.mashurin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DoubleIdentification extends Identification<Double> {

    public static DoubleIdentification get() {
        return new DoubleIdentification();
    }

    @Override
    public void write(DataOutputStream output, Double object) throws IOException {
        output.writeDouble(object);
    }

    @Override
    public Double read(DataInputStream input) throws IOException {
        return input.readDouble();
    }
}
