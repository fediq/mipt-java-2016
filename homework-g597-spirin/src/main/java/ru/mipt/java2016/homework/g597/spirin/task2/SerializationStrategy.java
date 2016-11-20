package ru.mipt.java2016.homework.g597.spirin.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by whoami on 10/30/16.
 */
public interface SerializationStrategy<T> {
    T read(RandomAccessFile file) throws IOException;

    void write(RandomAccessFile file, T object) throws IOException;
}
