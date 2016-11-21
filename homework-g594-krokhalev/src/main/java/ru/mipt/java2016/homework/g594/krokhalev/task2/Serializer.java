package ru.mipt.java2016.homework.g594.krokhalev.task2;

import java.io.IOException;

/**
 * Created by wheeltune on 14.11.16.
 */
public interface Serializer<K, V> {
    byte[] serialize(Object object) throws IOException;

    Object deserialize(Class<?> oClass, byte[] buffer);

    K deserializeKey(byte[] buffer);

    V deserializeValue(byte[] buffer);
}
