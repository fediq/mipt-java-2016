package ru.mipt.java2016.homework.g597.bogdanov.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

public class StringStringSerializationStrategy implements SerializationStrategy<String, String> {
    private static final StringStringSerializationStrategy INSTANCE = new StringStringSerializationStrategy();

    public static StringStringSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private StringStringSerializationStrategy() {
    }

    @Override
    public void write(RandomAccessFile file, String key, String value) throws IOException {
        byte[] bytes = key.getBytes();
        file.writeInt(bytes.length);
        file.write(bytes);
        bytes = value.getBytes();
        file.writeInt(bytes.length);
        file.write(bytes);
    }

    @Override
    public String readKey(RandomAccessFile file) throws IOException {
        int length = file.readInt();
        byte[] bytes = new byte[length];
        file.readFully(bytes);
        return new String(bytes);
    }

    @Override
    public String readValue(RandomAccessFile file) throws IOException {
        int length = file.readInt();
        byte[] bytes = new byte[length];
        file.readFully(bytes);
        return new String(bytes);
    }
}
