package ru.mipt.java2016.homework.g594.petrov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Created by philipp on 30.10.16.
 */

public interface InterfaceSerialization<T> {
    T readValue(DataInputStream inputStream) throws IllegalStateException;

    void writeValue(T obj, DataOutputStream outputStream) throws IllegalStateException;
}
