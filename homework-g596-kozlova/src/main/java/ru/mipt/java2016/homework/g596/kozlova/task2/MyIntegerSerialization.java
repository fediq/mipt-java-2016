package ru.mipt.java2016.homework.g596.kozlova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MyIntegerSerialization extends MySerialization<Integer> {

    @Override
    public Integer read(DataInputStream read_from_file) throws IOException {
        return read_from_file.readInt();
    }

    @Override
    public void write(DataOutputStream write_to_file, Integer object) throws IOException {
        write_to_file.writeInt(object);
    }
}