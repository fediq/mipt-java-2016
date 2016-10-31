package ru.mipt.java2016.homework.g597.komarov.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Михаил on 30.10.2016.
 */
public interface Serializer<T> {

    public T read(RandomAccessFile file) throws IOException;

    public void write(RandomAccessFile file, T arg) throws IOException;
}
