package ru.mipt.java2016.homework.g595.gusarova.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Дарья on 19.11.2016.
 */
public interface SerializerAndDeserializer<T> {
    void serialize(T data, DataOutput file) throws IOException;

    T deserialize(DataInput file) throws IOException;
}
