package ru.mipt.java2016.homework.g596.kozlova.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface MySerialization<T> {
    void write(T value, DataOutput output) throws IOException;

    T read(DataInput input) throws IOException;
}