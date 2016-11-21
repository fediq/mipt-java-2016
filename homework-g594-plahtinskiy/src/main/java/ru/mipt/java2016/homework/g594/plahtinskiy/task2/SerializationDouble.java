package ru.mipt.java2016.homework.g594.plahtinskiy.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by VadimPl on 31.10.16.
 */
public class SerializationDouble extends Serialization<Double> {

    @Override
    public void write(RandomAccessFile file, Double obj) throws IOException {
        file.writeDouble(obj);
    }

    @Override
    public Double read(RandomAccessFile file) throws IOException {
        return file.readDouble();
    }
}
