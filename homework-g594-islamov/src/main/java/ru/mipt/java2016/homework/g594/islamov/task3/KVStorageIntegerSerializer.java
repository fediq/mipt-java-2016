package ru.mipt.java2016.homework.g594.islamov.task3;

import java.io.*;

/**
 * Created by Iskander Islamov on 13.11.2016.
 */

class KVStorageIntegerSerializer implements KVSSerializationInterface<Integer> {
    @Override
    public String serialize(Integer object) {
        return Integer.toString(object);
    }

    @Override
    public Integer deserialize(String object) throws IOException {
        try {
            return Integer.parseInt(object);
        } catch (NumberFormatException e) {
            throw new IOException("Deserialization Error");
        }
    }
}