package ru.mipt.java2016.homework.g596.kozlova.task3;

import java.io.DataInput;
import java.io.IOException;

public class MyIntegerSerialization implements MySerialization<Integer> {
    @Override
    public String write(Integer obj) throws IOException {
        return obj.toString();
    }

    @Override
    public Integer read(DataInput input) throws IOException {
        return input.readInt();
    }
}
