package ru.mipt.java2016.homework.g595.gusarova.task2;


import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


/**
 * Created by Дарья on 29.10.2016.
 */
public interface SerializerAndDeserializer<T> {
    void serialize(T data, DataOutput stream) throws IOException;

    T deserialize(DataInput stream) throws IOException;
}




