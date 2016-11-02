package ru.mipt.java2016.homework.g596.stepanova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public interface SerializationStrategy<V> {

    void serializeToFile(V value, DataOutputStream output) throws IOException;

    V deserializeFromFile(DataInputStream input) throws IOException;

}