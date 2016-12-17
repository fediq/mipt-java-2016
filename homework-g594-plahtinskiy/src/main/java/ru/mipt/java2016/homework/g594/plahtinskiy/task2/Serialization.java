package ru.mipt.java2016.homework.g594.plahtinskiy.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by VadimPl on 31.10.16.
 */
public abstract class Serialization<T> {

    public abstract void write(RandomAccessFile file, T obj) throws IOException;

    public abstract T read(RandomAccessFile file) throws IOException;

}
