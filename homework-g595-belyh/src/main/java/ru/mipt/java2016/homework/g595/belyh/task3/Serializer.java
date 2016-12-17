package ru.mipt.java2016.homework.g595.belyh.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by white2302 on 29.10.2016.
 */
public interface Serializer<V> {
    void serialize(V value, RandomAccessFile f) throws IOException;

    V deserialize(RandomAccessFile f) throws IOException;
}