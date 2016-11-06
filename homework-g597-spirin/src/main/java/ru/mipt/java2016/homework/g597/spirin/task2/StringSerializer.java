package ru.mipt.java2016.homework.g597.spirin.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by whoami on 10/30/16.
 */
public class StringSerializer implements SerializationStrategy<String> {

    private static class SingletonHolder {
        static final StringSerializer HOLDER_INSTANCE = new StringSerializer();
    }

    static StringSerializer getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    private final IntegerSerializer integerSerializer = IntegerSerializer.getInstance();

    @Override
    public String read(RandomAccessFile file) throws IOException {
        int len = integerSerializer.read(file);
        byte[] characters = new byte[len];
        file.readFully(characters);
        return new String(characters);
    }

    @Override
    public void write(RandomAccessFile file, String object) throws IOException {
        byte[] characters = object.getBytes();
        integerSerializer.write(file, characters.length);
        file.write(characters);
    }
}
