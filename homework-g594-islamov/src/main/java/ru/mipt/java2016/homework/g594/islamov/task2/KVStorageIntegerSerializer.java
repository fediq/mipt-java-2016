package ru.mipt.java2016.homework.g594.islamov.task2;

/**
 * Created by Iskander Islamov on 29.10.2016.
 */

public class KVStorageIntegerSerializer implements KVSSerializationInterface<Integer> {

    @Override
    public String serialize(Integer object) {
        return Integer.toString(object);
    }

    @Override
    public Integer deserialize(String object) throws BadStorageException {
        try {
            return Integer.parseInt(object);
        } catch (NumberFormatException e) {
            throw new BadStorageException("Deserialization Error");
        }
    }
}