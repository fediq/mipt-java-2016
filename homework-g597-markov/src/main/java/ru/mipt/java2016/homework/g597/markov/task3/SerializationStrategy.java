package ru.mipt.java2016.homework.g597.markov.task3;

/**
 * Created by Alexander on 23.11.2016.
 */

import java.io.IOException;
import java.io.RandomAccessFile;

public interface SerializationStrategy<V> {
    V read(RandomAccessFile fileName) throws IOException;

    void write(RandomAccessFile fileName, V data) throws IOException;
}
