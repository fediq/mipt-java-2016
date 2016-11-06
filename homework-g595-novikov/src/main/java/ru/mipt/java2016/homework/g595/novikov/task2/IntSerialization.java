package ru.mipt.java2016.homework.g595.novikov.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by igor on 10/24/16.
 */
public class IntSerialization extends MySerialization<Integer> {
    @Override
    public void serialize(RandomAccessFile file, Integer object) throws IOException {
        serializeInteger(file, object);
    }

    @Override
    public Integer deserialize(RandomAccessFile file) throws IOException {
        return deserializeInteger(file);
    }
}
