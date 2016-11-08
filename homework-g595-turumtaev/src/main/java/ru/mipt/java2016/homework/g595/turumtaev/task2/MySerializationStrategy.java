package ru.mipt.java2016.homework.g595.turumtaev.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by galim on 29.10.2016.
 */
public interface MySerializationStrategy<V> {

    void write(V value, DataOutputStream output) throws IOException;

    V read(DataInputStream input) throws IOException;

}
