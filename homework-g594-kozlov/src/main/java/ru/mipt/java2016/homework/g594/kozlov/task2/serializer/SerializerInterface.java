package ru.mipt.java2016.homework.g594.kozlov.task2.serializer;

import ru.mipt.java2016.homework.g594.kozlov.task2.StorageException;

/**
 * Created by Anatoly on 25.10.2016.
 */
public interface SerializerInterface<T> {

    String serialize(T objToSerialize);

    T deserialize(String inputString) throws StorageException;

    String getClassString();
}
