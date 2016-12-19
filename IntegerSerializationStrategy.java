package ru.mipt.java2016.homework.g595.ferenets.task2;

import java.io.IOException;
import java.io.RandomAccessFile;


public class IntegerSerializationStrategy implements SerializationStrategy<Integer> {

    @Override
    public Integer read(RandomAccessFile file) throws IOException {
        return file.readInt();
    }

    @Override
    public void write(RandomAccessFile file, Integer value) throws IOException {
        file.writeInt(value);
    }
}
