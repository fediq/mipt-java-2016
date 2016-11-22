package ru.mipt.java2016.homework.g597.spirin.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by whoami on 11/21/16.
 */
public interface SerializationStrategy<T> {
    T read(RandomAccessFile file) throws IOException;

    void write(RandomAccessFile file, T object) throws IOException;
}
