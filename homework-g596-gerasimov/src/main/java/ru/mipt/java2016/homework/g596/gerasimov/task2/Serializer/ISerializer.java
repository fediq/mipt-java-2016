package ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer;

import java.nio.ByteBuffer;

/**
 * Created by geras-artem on 30.10.16.
 */

public interface ISerializer<T> {
    int sizeOfSerialization(T object);

    ByteBuffer serialize(T object);

    T deserialize(ByteBuffer code);
}
