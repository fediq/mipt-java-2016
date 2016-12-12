package ru.mipt.java2016.homework.g597.grishutin.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface SerializationStrategy<T> {
    /*
        serializes value to current location of caret in file
     */
    void serialize(T value, DataOutput raf) throws IOException;

    /*
        deserializes value from current location of caret in file
     */
    T deserialize(DataInput raf) throws IOException;

    /*
        returns number of bytes value will take after serialization
     */
    Long bytesSize(T value);
}

