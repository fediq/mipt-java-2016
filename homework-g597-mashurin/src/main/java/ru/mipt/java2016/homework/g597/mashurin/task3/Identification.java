package ru.mipt.java2016.homework.g597.mashurin.task3;

import java.io.RandomAccessFile;
import java.io.IOException;

public interface Identification<Value> {
    Value read(RandomAccessFile input) throws IOException;

    void write(RandomAccessFile output, Value value) throws IOException;
}
