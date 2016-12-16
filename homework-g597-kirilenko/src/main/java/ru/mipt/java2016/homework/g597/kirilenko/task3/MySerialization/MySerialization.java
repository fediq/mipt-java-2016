package ru.mipt.java2016.homework.g597.kirilenko.task3.MySerialization;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Natak on 29.10.2016.
 */
public interface MySerialization<T> {

    void write(RandomAccessFile file, T value) throws IOException;

    T read(RandomAccessFile file) throws IOException;
}
