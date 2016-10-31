package ru.mipt.java2016.homework.g594.stepanov.task2;


import javafx.util.Pair;

import java.io.IOException;

public class StringStringSerializator extends ObjectSerializator<String, String> {

    public StringStringSerializator(String directory) throws IOException {
        super(directory);
    }

    @Override
    void write(String key, String value) throws IOException {
        Pair p = new Pair(key, value);
        currentHash += p.hashCode();
        outputStream.print(start);
        outputStream.print(key);
        outputStream.print(separator);
        outputStream.print(value);
        outputStream.print(finish);
        outputStream.print("\n");
    }

    @Override
    Pair<String, String> read() throws IOException {
        String s = inputStream.readLine();
        if (s == null) {
            throw new IOException("File end");
        }
        int pos = s.indexOf(separator);
        if (!s.startsWith(start) || !s.endsWith(finish) || pos == -1) {
            throw new IOException("Invalid string in input file");
        }
        String key = s.substring(1, pos);
        String value = s.substring(pos + 1, s.length() - 1);
        return new Pair(key, value);
    }
}
