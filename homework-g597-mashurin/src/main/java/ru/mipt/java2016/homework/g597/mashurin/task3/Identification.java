package ru.mipt.java2016.homework.g597.mashurin.task3;

import java.io.RandomAccessFile;
import java.io.IOException;

abstract class Identification<Value> {
    abstract Value read(RandomAccessFile input) throws IOException;

    abstract void write(RandomAccessFile output, Value value) throws IOException;
}
