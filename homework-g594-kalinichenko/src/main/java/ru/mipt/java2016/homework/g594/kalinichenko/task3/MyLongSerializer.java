package ru.mipt.java2016.homework.g594.kalinichenko.task3;

import java.io.RandomAccessFile;
import java.io.FileOutputStream;

/**
 * Created by masya on 30.10.16.
 */

public class MyLongSerializer extends MySerializer<Long> {
    @Override
    public Long get(RandomAccessFile in) {
        return getLong(in);
    }

    @Override
    public void put(FileOutputStream out, Long val) {
        putLong(out, val);
    }
}
