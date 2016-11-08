package ru.mipt.java2016.homework.g596.stepanova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public interface SerializationStrategy<V> {

    void serializeToFile(V value, DataOutput output) throws IOException;

    V deserializeFromFile(DataInput input) throws IOException;

}