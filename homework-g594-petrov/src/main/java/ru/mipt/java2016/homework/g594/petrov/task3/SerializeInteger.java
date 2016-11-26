package ru.mipt.java2016.homework.g594.petrov.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by philipp on 14.11.16.
 */

public class SerializeInteger implements InterfaceSerialization<Integer> {
    @Override
    public Integer readValue(DataInputStream inputStream) throws IllegalStateException {
        try {
            return inputStream.readInt();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void writeValue(Integer obj, DataOutputStream outputStream) throws IllegalStateException {
        try {
            outputStream.writeInt(obj);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }
}
