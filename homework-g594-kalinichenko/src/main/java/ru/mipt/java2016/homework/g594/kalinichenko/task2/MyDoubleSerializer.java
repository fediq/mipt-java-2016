package ru.mipt.java2016.homework.g594.kalinichenko.task2;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by masya on 30.10.16.
 */
public class MyDoubleSerializer extends MySerializer<Double> {
    public Double get(FileInputStream in) {
        return getDouble(in);
    }

    @Override
    public void put(FileOutputStream out, Double num) {
        putDouble(out, num);
    }
}
