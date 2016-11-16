package ru.mipt.java2016.homework.g597.nasretdinov.task2;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by isk on 31.10.16.
 */
public interface SerializerInterface<V> {
    void write(DataOutputStream stream, V value) throws IOException;

    V read(DataInputStream stream) throws IOException;
}
