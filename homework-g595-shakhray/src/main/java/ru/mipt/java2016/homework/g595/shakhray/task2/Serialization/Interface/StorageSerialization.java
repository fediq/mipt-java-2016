package ru.mipt.java2016.homework.g595.shakhray.task2.Serialization.Interface;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Vlad on 29/10/2016.
 */
public interface StorageSerialization<T> {

    /**
     * Writes serialized data to file
     */
    void write(RandomAccessFile file, T object) throws IOException;

    /**
     * Reads and deserialized object from file
     */
    T read(RandomAccessFile file) throws IOException;
}
