package ru.mipt.java2016.homework.g596.pockonechny.task3;

import java.io.*;

/**
 * Created by celidos on 30.10.16.
 */

public interface SerializationStrategy<T> {
    T read(DataInput readingDevice) throws IOException;

    void write(DataOutput writingDevice, T obj) throws IOException;

    String getType();
}
