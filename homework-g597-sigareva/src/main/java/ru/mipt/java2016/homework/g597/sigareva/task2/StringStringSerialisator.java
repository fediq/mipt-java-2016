package ru.mipt.java2016.homework.g597.sigareva.task2;

import javafx.util.Pair;

class StringStringSerializer extends ObjectSerializer<String, String> {

    StringStringSerializer(String newPath) {
        super(newPath);
    }

    @Override
    protected Pair<String, String> convert() {
        int border = lastRead.indexOf(":");
        String key = lastRead.substring(0, border);
        String value = lastRead.substring(border + 1, lastRead.length());
        return new Pair(key, value);
    }

    @Override
    void write(String key, String value) {
        outputStream.print(key);
        outputStream.print(":");
        outputStream.print(value);
        outputStream.print("\n");
    }
}

