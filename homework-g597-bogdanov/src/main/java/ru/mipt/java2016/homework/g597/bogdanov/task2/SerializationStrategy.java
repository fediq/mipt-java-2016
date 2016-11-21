package ru.mipt.java2016.homework.g597.bogdanov.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

public interface SerializationStrategy<K, V> {

    void write(RandomAccessFile file, K key, V value) throws IOException;

    K readKey(RandomAccessFile file) throws IOException;

    V readValue(RandomAccessFile file) throws IOException;
}
