package ru.mipt.java2016.homework.g595.efimochkin.task2.Serializers;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Made by Sergey on 27/11/2016.
 */
public interface BaseSerialization<T> {

    Long write(RandomAccessFile file, T object) throws IOException;

    T read(RandomAccessFile file) throws IOException;
}
