package ru.mipt.java2016.homework.g597.komarov.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Михаил on 31.10.2016.
 */
public class DoubleSerializer implements Serializer<Double> {
    @Override
    public Double read(RandomAccessFile file) throws IOException {
        return file.readDouble();
    }

    @Override
    public void write(RandomAccessFile file, Double arg) throws IOException {
        file.writeDouble(arg);
    }
}
