package ru.mipt.java2016.homework.g595.ferenets.task2;


import java.io.IOException;
import java.io.RandomAccessFile;

public interface SerializationStrategy<T> {
    T read(RandomAccessFile file) throws IOException;

    void write(RandomAccessFile file, T value) throws IOException;
}
