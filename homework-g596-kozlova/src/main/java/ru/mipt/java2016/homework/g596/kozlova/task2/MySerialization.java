package ru.mipt.java2016.homework.g596.kozlova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface MySerialization<T> {
    T read(DataInputStream readFromFile) throws IOException;

    void write(DataOutputStream writeToFile, T object) throws IOException;
}
