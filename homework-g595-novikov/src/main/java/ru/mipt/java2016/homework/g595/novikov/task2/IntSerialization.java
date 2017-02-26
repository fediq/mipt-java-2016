package ru.mipt.java2016.homework.g595.novikov.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by igor on 10/24/16.
 */
public class IntSerialization extends MySerialization<Integer> {
    @Override
    public void serialize(DataOutput file, Integer object) throws IOException {
        serializeInteger(file, object);
    }

    @Override
    public Integer deserialize(DataInput file) throws IOException {
        return deserializeInteger(file);
    }

    @Override
    public long getSizeSerialized(Integer object) {
        return getSizeSerializedInteger(object);
    }
}
