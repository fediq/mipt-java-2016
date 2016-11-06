package ru.mipt.java2016.homework.g596.bystrov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by AlexBystrov.
 */
public interface SerializationStrategy<V> {
    void serialize(V value, DataOutputStream out) throws IOException;

    V deserialize(DataInputStream in) throws IOException;
}
