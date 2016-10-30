package ru.mipt.java2016.homework.g594.islamov.task2;

/**
 * Created by Iskander Islamov on 30.10.2016.
 */

public class KVStorageDoubleSerializer implements KVSSerializationInterface<Double> {

    @Override
    public String serialize(Double object) {
        return Double.toString(object);
    }

    @Override
    public Double deserialize(String object) throws BadStorageException {
        try {
            return Double.parseDouble(object);
        } catch (NumberFormatException e) {
            throw new BadStorageException("Deserialization Error");
        }
    }
}
