package ru.mipt.java2016.homework.g597.kirilenko.task2.MySerialization;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Natak on 29.10.2016.
 */
public interface MySerialization <T> {

    public void write(RandomAccessFile file, T value) throws IOException;

    public T read(RandomAccessFile file) throws IOException;
}
