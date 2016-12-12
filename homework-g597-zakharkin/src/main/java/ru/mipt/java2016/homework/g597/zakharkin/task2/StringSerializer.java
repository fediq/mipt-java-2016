package ru.mipt.java2016.homework.g597.zakharkin.task2;

import java.io.*;

/**
 * Serialization strategy for String type
 *
 * @autor Ilya Zakharkin
 * @since 31.10.16.
 */
public class StringSerializer implements Serializer<String> {
    private StringSerializer() {
    }

    private static class InstanceHolder {
        public static final StringSerializer INSTANCE = new StringSerializer();
    }

    public static StringSerializer getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void write(DataOutput file, String data) throws IOException {
        byte[] bytesOfString = data.getBytes();
        IntegerSerializer lengthWriter = IntegerSerializer.getInstance();
        lengthWriter.write(file, bytesOfString.length);
        file.write(bytesOfString);
    }

    @Override
    public String read(DataInput file) throws IOException {
        IntegerSerializer lengthReader = IntegerSerializer.getInstance();
        int length = lengthReader.read(file);
        byte[] bytesOfString = new byte[length];
        file.readFully(bytesOfString);
        String string = new String(bytesOfString);
        return string;
    }
}
