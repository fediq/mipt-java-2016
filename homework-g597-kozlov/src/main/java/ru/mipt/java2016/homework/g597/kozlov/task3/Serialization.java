package ru.mipt.java2016.homework.g597.kozlov.task3;

/**
 * Created by Alexander on 21.11.2016.
 * Интерфейс сериализации.
 */

import java.io.IOException;
import java.io.RandomAccessFile;

public interface Serialization<V> {
    V read(RandomAccessFile file, long shift) throws IOException;

    void write(RandomAccessFile file, V object, long shift) throws IOException;
}