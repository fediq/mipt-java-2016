package ru.mipt.java2016.homework.g594.islamov.task3;

import java.io.IOException;

/**
 * Created by Iskander Islamov on 13.11.2016.
 */

class KVStorageDoubleSerializer implements KVSSerializationInterface<Double> {
    @Override
    public String serialize(Double object) {
        return Double.toString(object);
    }

    @Override
    public Double deserialize(String object) throws IOException {
        try {
            return Double.parseDouble(object);
        } catch (NumberFormatException e) {
            throw new IOException("Deserialization Error");
        }
    }
}