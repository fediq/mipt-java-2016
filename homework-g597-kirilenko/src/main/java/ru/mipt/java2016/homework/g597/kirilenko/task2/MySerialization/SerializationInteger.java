package ru.mipt.java2016.homework.g597.kirilenko.task2.MySerialization;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Natak on 29.10.2016.
 */
public class SerializationInteger implements MySerialization <Integer>{

    static SerializationInteger serialize = new SerializationInteger();
    private SerializationInteger() { };
    public static SerializationInteger getSerialization() {
        return serialize;
    }
    @Override
    public void write(RandomAccessFile file, Integer value) throws IOException {
        try {
            file.writeInt(value);
        } catch (IOException e) {
            throw new IOException("File write error");
        }
    }

    @Override
    public Integer read(RandomAccessFile file) throws IOException {
        Integer value = null;
        try {
            value = file.readInt();
        } catch (IOException e) {
            throw new IOException("File read error");
        }
        return value;
    }
}
