package ru.mipt.java2016.homework.g597.bogdanov.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class IntegerDoubleSerializationStrategy implements SerializationStrategy<Integer, Double> {
    private static final IntegerDoubleSerializationStrategy INSTANCE = new IntegerDoubleSerializationStrategy();

    public static IntegerDoubleSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private IntegerDoubleSerializationStrategy() {
    }

    @Override
    public void writeKey(DataOutput file, Integer key) throws IOException {
        file.writeInt(key);
    }

    @Override
    public void writeValue(DataOutput file, Double value) throws IOException {
        file.writeDouble(value);
    }

    @Override
    public Integer readKey(DataInput file) throws IOException {
        return file.readInt();
    }

    @Override
    public Double readValue(DataInput file) throws IOException {
        return file.readDouble();
    }
}
