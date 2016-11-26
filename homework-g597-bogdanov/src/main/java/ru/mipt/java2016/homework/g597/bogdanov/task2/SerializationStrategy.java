package ru.mipt.java2016.homework.g597.bogdanov.task2;

import java.io.IOException;
import java.io.DataOutput;
import java.io.DataInput;

public interface SerializationStrategy<K, V> {

    void writeKey(DataOutput file, K key) throws IOException;

    void writeValue(DataOutput file, V value) throws IOException;

    K readKey(DataInput file) throws IOException;

    V readValue(DataInput file) throws IOException;
}
