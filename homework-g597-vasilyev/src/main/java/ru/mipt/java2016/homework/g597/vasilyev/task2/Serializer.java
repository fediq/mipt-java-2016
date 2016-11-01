package ru.mipt.java2016.homework.g597.vasilyev.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Saves or constructs object of type T
 */
public interface Serializer<E> {
    void write(E value, DataOutput destination) throws IOException;

    E read(DataInput source) throws IOException;
}
