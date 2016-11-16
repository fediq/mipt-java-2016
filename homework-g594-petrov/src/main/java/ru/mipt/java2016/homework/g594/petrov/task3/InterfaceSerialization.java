package ru.mipt.java2016.homework.g594.petrov.task3;

import java.io.RandomAccessFile;

/**
 * Created by philipp on 14.11.16.
 */


public interface InterfaceSerialization<T> {
    T readValue(RandomAccessFile inputStream) throws IllegalStateException;

    void writeValue(T obj, RandomAccessFile outputStream) throws IllegalStateException;
}
