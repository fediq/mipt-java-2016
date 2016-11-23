package ru.mipt.java2016.homework.g596.kupriyanov.task3;

import java.io.RandomAccessFile;
import java.io.IOException;

/**
 * Created by Artem Kupriyanov on 29/10/2016.
 */

public interface SerializationStrategy<V> {
    void write(V value, RandomAccessFile out) throws IOException;

    V read(RandomAccessFile in) throws IOException;
}