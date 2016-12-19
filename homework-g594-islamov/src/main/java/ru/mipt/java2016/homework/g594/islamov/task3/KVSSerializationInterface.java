package ru.mipt.java2016.homework.g594.islamov.task3;

import java.io.IOException;

/**
 * Created by Iskander Islamov on 13.11.2016.
 */

public interface KVSSerializationInterface<K> {
    String serialize(K object);

    K deserialize(String object) throws IOException;
}
