package ru.mipt.java2016.homework.g597.grishutin.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

public interface SerializationStrategy<T> {
    /*
        serializes value to current location of caret in file
     */
    void serialize(T value, RandomAccessFile raf) throws IOException;

    /*
        deserializes value from current location of caret in file
     */
    T deserialize(RandomAccessFile raf) throws IOException;


}

