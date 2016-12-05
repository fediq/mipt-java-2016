package ru.mipt.java2016.homework.g596.kozlova.task3;

import java.io.DataInput;
import java.io.IOException;

public class MyStringSerialization implements MySerialization<String> {
    @Override
    public String read(DataInput input) throws IOException {
        return input.readUTF();
    }

    @Override
    public String write(String s) throws IOException {
        return s;
    }
}