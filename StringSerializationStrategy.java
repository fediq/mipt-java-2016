package ru.mipt.java2016.homework.g595.ferenets.task2;

import java.io.IOException;
import java.io.RandomAccessFile;


public class StringSerializationStrategy implements SerializationStrategy<String> {
    @Override
    public String read(RandomAccessFile file) throws IOException {
        return file.readUTF();
    }

    @Override
    public void write(RandomAccessFile file, String value) throws IOException {
        file.writeUTF(value);
    }
}
