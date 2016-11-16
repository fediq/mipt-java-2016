package ru.mipt.java2016.homework.g595.iksanov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Эмиль
 */
public interface SerializationStrategy<V> {

    void write(V value, DataOutputStream output) throws IOException;

    V read(DataInputStream input) throws IOException;

}
