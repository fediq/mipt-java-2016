package ru.mipt.java2016.homework.g597.spirin.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by whoami on 11/21/16.
 */
public interface SerializationStrategy<T> {
    T read(DataInput file) throws IOException;

    void write(DataOutput file, T object) throws IOException;
}
