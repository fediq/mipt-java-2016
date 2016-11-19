package ru.mipt.java2016.homework.g594.petrov.task3;

import java.io.DataInput;
import java.io.DataOutput;

/**
 * Created by philipp on 14.11.16.
 */


public interface InterfaceSerialization<T> {
    T readValue(DataInput inputStream) throws IllegalStateException;

    void writeValue(T obj, DataOutput outputStream) throws IllegalStateException;
}
