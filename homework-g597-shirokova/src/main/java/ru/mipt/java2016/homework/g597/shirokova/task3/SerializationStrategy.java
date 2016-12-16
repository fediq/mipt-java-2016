package ru.mipt.java2016.homework.g597.shirokova.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface SerializationStrategy<T> {
    void serialize(DataOutput output, T value) throws IOException;

    T deserialize(DataInput input) throws IOException;
}
