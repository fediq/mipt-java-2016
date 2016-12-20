package ru.mipt.java2016.homework.g594.petrov.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by philipp on 19.11.16.
 */
public class SerializeLong implements InterfaceSerialization<Long> {
    @Override
    public Long readValue(DataInput inputStream) throws IllegalStateException {
        try {
            return inputStream.readLong();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void writeValue(Long obj, DataOutput outputStream) throws IllegalStateException {
        try {
            outputStream.writeLong(obj);
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }
}
