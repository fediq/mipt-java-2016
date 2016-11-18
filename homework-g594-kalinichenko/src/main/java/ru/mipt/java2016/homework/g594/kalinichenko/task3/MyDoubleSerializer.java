package ru.mipt.java2016.homework.g594.kalinichenko.task3;

import java.io.RandomAccessFile;
import java.io.FileOutputStream;

/**
 * Created by masya on 30.10.16.
 */
public class MyDoubleSerializer extends MySerializer<Double> {
    public Double get(RandomAccessFile in) {
        return getDouble(in);
    }

    @Override
    public void put(FileOutputStream out, Double num) {
        putDouble(out, num);
    }
}
