package ru.mipt.java2016.homework.g597.bogdanov.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class StringStringSerializationStrategy implements SerializationStrategy<String, String> {
    private static final StringStringSerializationStrategy INSTANCE = new StringStringSerializationStrategy();

    public static StringStringSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private StringStringSerializationStrategy() {
    }

    @Override
    public void writeKey(DataOutput file, String key) throws IOException {
        byte[] bytes = key.getBytes();
        file.writeInt(bytes.length);
        file.write(bytes);
    }

    @Override
    public void writeValue(DataOutput file, String value) throws IOException {
        byte[] bytes = value.getBytes();
        file.writeInt(bytes.length);
        file.write(bytes);
    }


    @Override
    public String readKey(DataInput file) throws IOException {
        int length = file.readInt();
        byte[] bytes = new byte[length];
        file.readFully(bytes);
        return new String(bytes);
    }

    @Override
    public String readValue(DataInput file) throws IOException {
        int length = file.readInt();
        byte[] bytes = new byte[length];
        file.readFully(bytes);
        return new String(bytes);
    }
}
