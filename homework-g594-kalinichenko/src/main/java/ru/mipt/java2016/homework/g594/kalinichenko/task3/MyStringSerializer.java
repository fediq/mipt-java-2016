package ru.mipt.java2016.homework.g594.kalinichenko.task3;

import java.io.RandomAccessFile;
import java.io.FileOutputStream;

/**
 * Created by masya on 30.10.16.
 */

public class MyStringSerializer extends MySerializer<String> {
    @Override
    public String get(RandomAccessFile in) {
        return getStr(in);
    }

    @Override
    public void put(FileOutputStream out, String str) {
        putStr(out, str);
    }
}

