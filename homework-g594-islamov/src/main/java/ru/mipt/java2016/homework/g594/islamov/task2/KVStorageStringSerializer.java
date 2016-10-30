package ru.mipt.java2016.homework.g594.islamov.task2;

/**
 * Created by Iskander Islamov on 30.10.2016.
 */

public class KVStorageStringSerializer implements KVSSerializationInterface<String> {

    @Override
    public String serialize(String object) {
        return object;
    }

    @Override
    public String deserialize(String object) {
        return object;
    }
}
