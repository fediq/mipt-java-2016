package ru.mipt.java2016.homework.g596.ivanova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by julia on 30.10.16.
 * @param <T> - type of object we serialize
 */
public interface Serialisation<T> {
    /**
     * @param file - binary file where data is stored.
     * @throws IOException - if an I/O error occurs.
     */
    T read(DataInput file) throws IOException;

    /**
     * @param file - binary file where data is stored.
     * @param object - object we serialize.
     * @throws IOException - if an I/O error occurs.
     */
    long write(DataOutput file, T object) throws IOException;
}
