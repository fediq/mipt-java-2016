package ru.mipt.java2016.homework.g594.petrov.task3;

import java.io.RandomAccessFile;

/**
 * Created by philipp on 14.11.16.
 */

public class SerializeDouble implements InterfaceSerialization<Double> {
    @Override
    public Double readValue(RandomAccessFile inputStream) throws IllegalStateException {
        try {
            return inputStream.readDouble();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void writeValue(Double obj, RandomAccessFile outputStream) throws IllegalStateException {
        try {
            outputStream.writeDouble(obj);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }
}
