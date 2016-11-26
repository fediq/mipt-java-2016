package ru.mipt.java2016.homework.g594.borodin.task2.SerializationStrategies;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Maxim on 10/31/2016.
 */
public interface SerializationStrategy<T> {
    void serialize(T value, DataOutputStream dataOutputStream) throws IOException;

    T deserialize(DataInputStream dataInputStream) throws IOException;
}