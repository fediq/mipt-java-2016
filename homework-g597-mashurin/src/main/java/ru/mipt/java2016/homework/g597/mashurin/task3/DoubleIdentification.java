package ru.mipt.java2016.homework.g597.mashurin.task3;

import java.io.RandomAccessFile;
import java.io.IOException;

public class DoubleIdentification implements Identification<Double> {

    public static DoubleIdentification get() {
        return new DoubleIdentification();
    }

    @Override
    public void write(RandomAccessFile output, Double object) throws IOException {
        output.writeDouble(object);
    }

    @Override
    public Double read(RandomAccessFile input) throws IOException {
        return input.readDouble();
    }
}
