package ru.mipt.java2016.homework.g597.kozlov.task2;

/**
 * Created by Alexander on 31.10.2016.
 * Интерфейс сериализации.
 */

import java.io.RandomAccessFile;
import java.io.IOException;

public interface Serialization<V> {
    V read(RandomAccessFile file) throws IOException;
    
    void write(RandomAccessFile file, V object) throws IOException;
}