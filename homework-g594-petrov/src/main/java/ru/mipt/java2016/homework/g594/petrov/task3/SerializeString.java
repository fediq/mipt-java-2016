package ru.mipt.java2016.homework.g594.petrov.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by philipp on 14.11.16.
 */

public class SerializeString implements InterfaceSerialization<String> {
    @Override
    public String readValue(DataInputStream inputStream) throws IllegalStateException {
        try {
            return inputStream.readUTF();
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void writeValue(String obj, DataOutputStream outputStream) throws IllegalStateException {
        try {
            outputStream.writeUTF(obj);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }
}
