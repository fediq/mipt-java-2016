package ru.mipt.java2016.homework.g594.petrov.task3;

import java.io.DataInput;
import java.io.DataOutput;

/**
 * Created by philipp on 14.11.16.
 */

public class SerializeDouble implements InterfaceSerialization<Double> {
    @Override
    public Double readValue(DataInput inputStream) throws IllegalStateException {
        try {
            return inputStream.readDouble();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void writeValue(Double obj, DataOutput outputStream) throws IllegalStateException {
        try {
            outputStream.writeDouble(obj);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }
}
