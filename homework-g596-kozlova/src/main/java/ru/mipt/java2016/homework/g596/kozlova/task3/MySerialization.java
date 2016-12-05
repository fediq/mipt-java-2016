package ru.mipt.java2016.homework.g596.kozlova.task3;

import java.io.DataInput;
import java.io.IOException;

public interface MySerialization<T> {
    T read(DataInput input) throws IOException;

    String write(T obj) throws IOException;
}