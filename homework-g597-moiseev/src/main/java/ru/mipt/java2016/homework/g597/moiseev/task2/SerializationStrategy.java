package ru.mipt.java2016.homework.g597.moiseev.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Стратегия сериализации
 *
 * @author Fedor Moiseev
 * @since 26.10.2016
 */

public interface SerializationStrategy<V> {
    void write(RandomAccessFile file, V object) throws IOException;

    V read(RandomAccessFile file) throws IOException;
}
