package ru.mipt.java2016.homework.g594.anukhin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by clumpytuna on 29.10.16.
 */
public interface Serializable<T> {
    void serialize(DataOutputStream input, T obj) throws IOException;

    T deserialize(DataInputStream output) throws IOException;
}
