package ru.mipt.java2016.homework.g595.novikov.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by igor on 10/25/16.
 */
public class DoubleSerialization extends MySerialization<Double> {
    @Override
    public void serialize(DataOutput file, Double object) throws IOException {
        serializeDouble(file, object);
    }

    @Override
    public Double deserialize(DataInput file) throws IOException {
        return deserializeDouble(file);
    }

    @Override
    public long getSizeSerialized(Double object) {
        return getSizeSerializedDouble(object);
    }
}
