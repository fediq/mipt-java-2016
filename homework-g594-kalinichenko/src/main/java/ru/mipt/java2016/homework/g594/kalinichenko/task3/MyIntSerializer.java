package ru.mipt.java2016.homework.g594.kalinichenko.task3;

import java.io.RandomAccessFile;
import java.io.FileOutputStream;

/**
 * Created by masya on 30.10.16.
 */

public class MyIntSerializer extends MySerializer<Integer> {
    @Override
    public Integer get(RandomAccessFile in) {
        return getInt(in);
    }

    @Override
    public void put(FileOutputStream out, Integer val) {
        putInt(out, val);
    }
}
