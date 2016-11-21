package ru.mipt.java2016.homework.g594.gorelick.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

public class DoubleFileWorker implements FileWorker<Double> {
    @Override
    public Double read(RandomAccessFile file, long position) throws IOException {
        file.seek(position);
        return file.readDouble();
    }

    @Override
    public void write(RandomAccessFile file, Double object, long position) throws IOException {
        file.seek(position);
        file.writeDouble(object);
    }
}
