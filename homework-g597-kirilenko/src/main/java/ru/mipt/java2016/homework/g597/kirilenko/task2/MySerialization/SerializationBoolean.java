package ru.mipt.java2016.homework.g597.kirilenko.task2.MySerialization;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Natak on 29.10.2016.
 */
public class SerializationBoolean implements MySerialization<Boolean> {
    static SerializationBoolean serialize = new SerializationBoolean();
    private SerializationBoolean() { };
    public static SerializationBoolean getSerialization() {
        return serialize;
    }
    @Override
    public void write(RandomAccessFile file, Boolean value) throws IOException {
        try {
            file.writeBoolean(value);
        } catch (IOException e) {
            throw new IOException("File write error");
        }
    }

    @Override
    public Boolean read(RandomAccessFile file) throws IOException {
        Boolean value = null;
        try {
            value = file.readBoolean();
        } catch (IOException e) {
            throw new IOException("File read error");
        }
        return value;
    }
}
