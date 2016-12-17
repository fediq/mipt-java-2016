package ru.mipt.java2016.homework.g594.islamov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Iskander Islamov on 30.10.2016.
 */

public interface KVSSerializationInterface<K> {
    void serialize(DataOutputStream out, K object) throws IOException;

    K deserialize(DataInputStream in) throws IOException;
}
