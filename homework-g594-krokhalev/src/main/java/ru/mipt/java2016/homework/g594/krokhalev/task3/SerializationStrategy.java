package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface SerializationStrategy<T> {
    T deserialize(DataInput dis) throws IOException;

    void serialize(DataOutput dos, Object object) throws IOException;
}
