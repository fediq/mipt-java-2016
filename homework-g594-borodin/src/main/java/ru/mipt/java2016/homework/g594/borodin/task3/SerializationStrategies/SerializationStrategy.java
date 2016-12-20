package ru.mipt.java2016.homework.g594.borodin.task3.SerializationStrategies;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Maxim on 10/31/2016.
 */
public interface SerializationStrategy<T> {
    void serialize(T value, DataOutput dataOutputStream) throws IOException;

    T deserialize(DataInput dataInputStream) throws IOException;
}