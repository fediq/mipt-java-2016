package ru.mipt.java2016.homework.g594.petrov.task3;

import java.io.DataInput;
import java.io.DataOutput;

/**
 * Created by philipp on 14.11.16.
 */

public class SerializeString implements InterfaceSerialization<String> {
    @Override
    public String readValue(DataInput inputStream) throws IllegalStateException {
        try {
            return inputStream.readUTF();
            /*int length = inputStream.readInt();
            byte buffer[] = new byte[length];
            inputStream.read(buffer);
            return new String(buffer);*/
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }

    @Override
    public void writeValue(String obj, DataOutput outputStream) throws IllegalStateException {
        try {
            outputStream.writeUTF(obj);
            /*outputStream.writeInt(obj.getBytes().length);
            outputStream.write(obj.getBytes());*/
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e.getCause());
        }
    }
}
