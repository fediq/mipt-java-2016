package ru.mipt.java2016.homework.g595.novikov.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by igor on 10/24/16.
 */
public class StringSerialization extends MySerialization<String> {
    @Override
    public void serialize(RandomAccessFile file, String object) throws IOException {
        serializeString(file, object);
    }

    @Override
    public String deserialize(RandomAccessFile file) throws IOException {
        return deserializeString(file);
    }
}
