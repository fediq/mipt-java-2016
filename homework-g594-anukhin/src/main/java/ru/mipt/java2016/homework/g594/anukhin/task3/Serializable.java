package ru.mipt.java2016.homework.g594.anukhin.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by clumpytuna on 29.10.16.
 */

public interface Serializable<T> {
    /* Пишет объект типа Т в поток*/
    void serialize(DataOutputStream input, T obj) throws IOException;

    void serialize(RandomAccessFile input, T obj) throws IOException;

    /* Возращает объект типа T из потока*/
    T deserialize(DataInputStream output) throws IOException;

    T deserialize(RandomAccessFile input) throws IOException;

}
