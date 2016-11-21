package ru.mipt.java2016.homework.g597.bogdanov.task2;

import java.io.IOException;
import java.io.RandomAccessFile;


public class IntegerDoubleSerializationStrategy implements SerializationStrategy<Integer, Double> {
    private static final IntegerDoubleSerializationStrategy INSTANCE = new IntegerDoubleSerializationStrategy();

    public static IntegerDoubleSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private IntegerDoubleSerializationStrategy() {
    }

    @Override
    public void writeKey(RandomAccessFile file, Integer key) throws IOException {
        file.writeInt(key);
    }

    @Override
    public void writeValue(RandomAccessFile file, Double value) throws IOException {
        file.writeDouble(value);
    }

    @Override
    public Integer readKey(RandomAccessFile file) throws IOException {
        return file.readInt();
    }

    @Override
    public Double readValue(RandomAccessFile file) throws IOException {
        return file.readDouble();
    }
}
