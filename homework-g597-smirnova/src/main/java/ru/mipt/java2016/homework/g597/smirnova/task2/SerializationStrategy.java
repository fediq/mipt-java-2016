package ru.mipt.java2016.homework.g597.smirnova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Elena Smirnova on 30.10.2016.
 */
public interface SerializationStrategy<T> {
    void writeToStream(DataOutputStream s, T value) throws IOException;

    T readFromStream(DataInputStream s) throws IOException;
}
