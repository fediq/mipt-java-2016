package ru.mipt.java2016.homework.g595.turumtaev.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by galim on 19.11.2016.
 */
public interface MySerializationStrategy<V> {

    Long write(V value, RandomAccessFile output) throws IOException;

    V read(RandomAccessFile input) throws IOException;

}
