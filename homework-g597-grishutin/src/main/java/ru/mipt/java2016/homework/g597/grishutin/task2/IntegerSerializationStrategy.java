package ru.mipt.java2016.homework.g597.grishutin.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

public class IntegerSerializationStrategy implements SerializationStrategy<Integer> {
    private static IntegerSerializationStrategy instance = new IntegerSerializationStrategy();

    public static IntegerSerializationStrategy getInstance() {
        return instance;
    }

    @Override
    public void serialize(Integer value, RandomAccessFile raf) throws IOException {
        raf.writeInt(value);
    }

    @Override
    public Integer deserialize(RandomAccessFile raf) throws IOException {
        return raf.readInt();
    }
}
