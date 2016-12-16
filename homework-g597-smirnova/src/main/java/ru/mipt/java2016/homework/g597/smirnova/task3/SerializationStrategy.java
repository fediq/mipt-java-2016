package ru.mipt.java2016.homework.g597.smirnova.task3;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Elena Smirnova on 21.11.2016.
 */
public interface SerializationStrategy<T> {
    void writeToStream(DataOutput s, T value) throws IOException;

    T readFromStream(DataInput s) throws IOException;
}
