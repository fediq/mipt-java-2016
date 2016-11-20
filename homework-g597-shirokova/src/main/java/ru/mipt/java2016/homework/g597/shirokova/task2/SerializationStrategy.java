package ru.mipt.java2016.homework.g597.shirokova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface SerializationStrategy<T> {
    void serialize(DataOutputStream output, T value) throws IOException;

    T deserialize(DataInputStream input) throws IOException;
}
