package ru.mipt.java2016.homework.g597.dmitrieva.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by macbook on 30.10.16.
 */

public interface SerializationStrategy<T> {

    T read(RandomAccessFile file) throws IOException;

    void write(RandomAccessFile file, T value) throws IOException;
}

