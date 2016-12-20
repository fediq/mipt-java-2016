package ru.mipt.java2016.homework.g595.proskurin.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DoubleSerializer implements MySerializer<Double> {
    public void output(RandomAccessFile out, Double val) throws IOException {
        out.writeDouble(val);
    }

    public Double input(RandomAccessFile in) throws IOException {
        return in.readDouble();
    }
}
