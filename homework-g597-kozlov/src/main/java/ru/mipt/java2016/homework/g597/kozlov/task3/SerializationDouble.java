package ru.mipt.java2016.homework.g597.kozlov.task3;

/**
 * Created by Alexander on 21.11.2016.
 */

import java.io.IOException;
import java.io.RandomAccessFile;

public class SerializationDouble implements Serialization<Double> {

    @Override
    public Double read(RandomAccessFile file, long shift) throws IOException {
        file.seek(shift);
        return file.readDouble();
    }

    @Override
    public void write(RandomAccessFile file, Double object, long shift) throws IOException {
        file.seek(shift);
        file.writeDouble(object);
    }
}