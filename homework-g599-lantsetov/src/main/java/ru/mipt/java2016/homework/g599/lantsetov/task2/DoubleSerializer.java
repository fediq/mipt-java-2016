package ru.mipt.java2016.homework.g599.lantsetov.task2;

import java.io.IOException;
import java.io.RandomAccessFile;


public class DoubleSerializer implements MySerializer<Double> {
    @Override
    public Double read(RandomAccessFile file) throws IOException {
        return file.readDouble();
    }

    @Override
    public void write(RandomAccessFile file, Double arg) throws IOException {
        file.writeDouble(arg);
    }
}