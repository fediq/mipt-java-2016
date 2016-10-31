package ru.mipt.java2016.homework.g595.novikov.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by igor on 10/25/16.
 */
public class DoubleSerialization extends MySerialization<Double> {
    @Override
    public void serialize(RandomAccessFile file, Double object) throws IOException {
        serializeDouble(file, object);
    }

    @Override
    public Double deserialize(RandomAccessFile file) throws IOException {
        return deserializeDouble(file);
    }
}
