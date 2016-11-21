package ru.mipt.java2016.homework.g594.gorelick.task3;

import java.io.RandomAccessFile;
import java.io.IOException;

public interface FileWorker<V> {
    V read(RandomAccessFile file, long position) throws IOException;

    void write(RandomAccessFile file, V object, long position) throws IOException;
}







