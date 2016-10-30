package ru.mipt.java2016.homework.g597.kirilenko.task2.MySerialization;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Natak on 29.10.2016.
 */
public class SerializationDouble implements MySerialization <Double> {
    static SerializationDouble serialize = new SerializationDouble();
    private SerializationDouble() { };
    public static SerializationDouble getSerialization() {
        return serialize;
    }
    @Override
    public void write(RandomAccessFile file, Double value) throws IOException {
        try {
            file.writeDouble(value);
        } catch (IOException e) {
            throw new IOException("File write error");
        }
    }

    @Override
    public Double read(RandomAccessFile file) throws IOException {
        Double value = null;
        try {
            value = file.readDouble();
        } catch (IOException e) {
            throw new IOException("File read error");
        }
        return value;
    }
}
