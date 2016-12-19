package ru.mipt.java2016.homework.g597.moiseev.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Стратегия сериализации для String
 *
 * @author Fedor Moiseev
 * @since 26.10.2016
 */

public class StringSerializationStrategy implements SerializationStrategy<String> {
    private static final StringSerializationStrategy INSTANCE = new StringSerializationStrategy();

    private final IntegerSerializationStrategy integerSerializationStrategy =
            IntegerSerializationStrategy.getInstance();

    public static StringSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private StringSerializationStrategy() {
    }

    @Override
    public void write(RandomAccessFile file, String object) throws IOException {
        byte[] bytes = object.getBytes();
        integerSerializationStrategy.write(file, bytes.length);
        file.write(bytes);
    }

    @Override
    public String read(RandomAccessFile file) throws IOException {
        int length = integerSerializationStrategy.read(file);
        byte[] bytes = new byte[length];
        file.readFully(bytes);
        return new String(bytes);
    }
}
