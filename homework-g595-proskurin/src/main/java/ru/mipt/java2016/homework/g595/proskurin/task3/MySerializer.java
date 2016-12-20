package ru.mipt.java2016.homework.g595.proskurin.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

public interface MySerializer<V> {
    void output(RandomAccessFile out, V val) throws IOException;

    V input(RandomAccessFile in) throws IOException;
}
