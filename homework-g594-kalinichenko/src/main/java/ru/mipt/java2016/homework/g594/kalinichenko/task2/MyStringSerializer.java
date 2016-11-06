package ru.mipt.java2016.homework.g594.kalinichenko.task2;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by masya on 30.10.16.
 */

public class MyStringSerializer extends MySerializer<String> {
    @Override
    public String get(FileInputStream in) {
        return getStr(in);
    }

    @Override
    public void put(FileOutputStream out, String str) {
        putStr(out, str);
    }
}

