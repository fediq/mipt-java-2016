package ru.mipt.java2016.homework.g596.kozlova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MyIntegerSerialization extends MySerialization<Integer> {

    @Override
    public Integer read(DataInputStream readFromFile) throws IOException {
        return readFromFile.readInt();
    }

    @Override
    public void write(DataOutputStream writeToFile, Integer object) throws IOException {
        writeToFile.writeInt(object);
    }
}