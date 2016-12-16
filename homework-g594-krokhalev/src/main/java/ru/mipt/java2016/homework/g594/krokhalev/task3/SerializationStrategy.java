package ru.mipt.java2016.homework.g594.krokhalev.task3;

import java.io.IOException;
import java.io.InputStream;

public interface SerializationStrategy<T> {
    T deserialize(InputStream stream) throws IOException;

    byte[] serialize(Object object) throws IOException;
}
