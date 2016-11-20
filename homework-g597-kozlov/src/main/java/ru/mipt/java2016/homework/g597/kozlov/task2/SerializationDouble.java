package ru.mipt.java2016.homework.g597.kozlov.task2;

/**
 * Created by Alexander on 31.10.2016.
 */

import java.io.IOException;
import java.io.RandomAccessFile;

public class SerializationDouble implements Serialization<Double> {

    @Override
    public Double read(RandomAccessFile file) throws IOException {
        return file.readDouble();
    }

    @Override
    public void write(RandomAccessFile file, Double object) throws IOException {
        file.writeDouble(object);
    }
}