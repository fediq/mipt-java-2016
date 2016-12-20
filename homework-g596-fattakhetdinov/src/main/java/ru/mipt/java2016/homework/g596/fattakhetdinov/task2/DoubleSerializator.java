package ru.mipt.java2016.homework.g596.fattakhetdinov.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class DoubleSerializator implements SerializationStrategy<Double> {
    @Override
    public void serializeToFile(Double value, DataOutput output) throws IOException {
        output.writeDouble(value);
    }

    @Override
    public Double deserializeFromFile(DataInput input) throws IOException {
        return input.readDouble();
    }

    @Override
    public String getType() {
        return "Double";
    }
}