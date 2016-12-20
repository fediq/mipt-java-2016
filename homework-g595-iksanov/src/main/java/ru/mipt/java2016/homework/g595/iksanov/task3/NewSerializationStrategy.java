package ru.mipt.java2016.homework.g595.iksanov.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Эмиль
 */
public interface NewSerializationStrategy<V> {

    Long write(V value, RandomAccessFile output) throws IOException;

    V read(RandomAccessFile input) throws IOException;
}
