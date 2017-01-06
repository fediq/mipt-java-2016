package ru.mipt.java2016.homework.g597.kochukov.task3;

import java.io.IOException;
import java.io.RandomAccessFile;
/**
 * Created by tna0y on 27/11/16.
 */


public interface MegaSerializer<V> {
    void serialize(V value, RandomAccessFile f) throws IOException;

    V deserialize(RandomAccessFile f) throws IOException;
}
