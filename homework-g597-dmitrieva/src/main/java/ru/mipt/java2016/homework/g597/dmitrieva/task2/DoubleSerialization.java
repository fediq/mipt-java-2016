package ru.mipt.java2016.homework.g597.dmitrieva.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by macbook on 30.10.16.
 */
public class DoubleSerialization implements SerializationStrategy<Double> {

    @Override
    public Double read(RandomAccessFile file) throws IOException {
        try {
            return file.readDouble();
        } catch (IOException e) {
            throw new IOException("Couldn't read during the Double deserialization");
        }
    }

    @Override
    public void write(RandomAccessFile file, Double value) throws IOException {
        try {
            file.writeDouble(value);
        } catch (IOException e) {
            throw new IOException("Couldn't write during the Double serialization");
        }
    }
}
