package ru.mipt.java2016.homework.g596.kozlova.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MyStringSerialization implements MySerialization<String> {
    @Override
    public String read(DataInput input) throws IOException {
        return input.readUTF();
    }

   @Override
    public void write(String obj, DataOutput output) throws IOException {
        output.writeUTF(obj);
    }
}