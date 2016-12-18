package ru.mipt.java2016.homework.g596.fattakhetdinov.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class IntegerSerializator implements SerializationStrategy<Integer> {
    @Override
    public void serializeToFile(Integer value, DataOutput output) throws IOException {
        output.writeInt(value);
    }

    @Override
    public Integer deserializeFromFile(DataInput input) throws IOException {
        return input.readInt();
    }

    @Override
    public String getType() {
        return "Integer";
    }
}