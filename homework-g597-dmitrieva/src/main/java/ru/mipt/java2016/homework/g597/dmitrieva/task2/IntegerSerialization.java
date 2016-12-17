package ru.mipt.java2016.homework.g597.dmitrieva.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by macbook on 30.10.16.
 */
public class IntegerSerialization implements SerializationStrategy<Integer> {
    @Override
    public Integer read(RandomAccessFile file) throws IOException {
        try {
            return file.readInt();
        } catch (IOException e) {
            throw new IOException("Couldn't read during the Integer deserialization");
        }
    }

    @Override
    public void write(RandomAccessFile file, Integer value) throws IOException {
        try {
            file.writeDouble(value);
        } catch (IOException e) {
            throw new IOException("Couldn't read during the Integer serialization");
        }
    }
}
