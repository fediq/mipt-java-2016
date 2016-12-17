package ru.mipt.java2016.homework.g596.kozlova.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MyIntegerSerialization implements MySerialization<Integer> {
    @Override
    public void write(Integer obj, DataOutput output) throws IOException {
        output.writeInt(obj);
    }

    @Override
    public Integer read(DataInput input) throws IOException {
        return input.readInt();
    }
}