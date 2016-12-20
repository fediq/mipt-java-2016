package ru.mipt.java2016.homework.g599.lantsetov.task2;

import java.io.IOException;
import java.io.RandomAccessFile;


public class IntegerSerializer implements MySerializer<Integer> {
    @Override
    public Integer read(RandomAccessFile file) throws IOException {
        return file.readInt();
    }

    @Override
    public void write(RandomAccessFile file, Integer arg) throws IOException {
        file.writeInt(arg);
    }
}