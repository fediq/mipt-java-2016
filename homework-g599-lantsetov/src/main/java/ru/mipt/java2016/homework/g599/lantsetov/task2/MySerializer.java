package ru.mipt.java2016.homework.g599.lantsetov.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Михаил on 30.10.2016.
 */
public interface MySerializer<T> {

    T read(RandomAccessFile file) throws IOException;

    void write(RandomAccessFile file, T arg) throws IOException;
}