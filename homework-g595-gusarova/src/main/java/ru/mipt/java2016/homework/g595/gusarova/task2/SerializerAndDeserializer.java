package ru.mipt.java2016.homework.g595.gusarova.task2;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


/**
 * Created by Дарья on 29.10.2016.
 */
public interface SerializerAndDeserializer<T> {
    void serialize(T data, DataOutputStream stream) throws IOException;

    T deserialize(DataInputStream stream) throws IOException;
}




