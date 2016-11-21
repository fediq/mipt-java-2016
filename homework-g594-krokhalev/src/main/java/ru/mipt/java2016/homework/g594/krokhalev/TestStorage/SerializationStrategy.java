package ru.mipt.java2016.homework.g594.krokhalev.TestStorage;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

public interface SerializationStrategy<T> {
    T deserialize(InputStream stream) throws IOException;

    byte[] serialize(Object object) throws IOException;
}
