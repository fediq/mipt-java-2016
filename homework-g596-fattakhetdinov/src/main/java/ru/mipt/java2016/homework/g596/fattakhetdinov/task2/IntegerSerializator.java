package ru.mipt.java2016.homework.g596.fattakhetdinov.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IntegerSerializator implements SerializationStrategy<Integer> {
    @Override
    public void serializeToFile(Integer value, DataOutputStream output) throws IOException {
        output.writeInt(value);
    }

    @Override
    public Integer deserializeFromFile(DataInputStream input) throws IOException {
        return input.readInt();
    }

    @Override
    public String getType() {
        return "Integer";
    }
}