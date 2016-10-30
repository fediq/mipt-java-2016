package ru.mipt.java2016.homework.g597.moiseev.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Стратегия сериализации для Integer
 *
 * @author Fedor Moiseev
 * @since 26.10.2016
 */

public class IntegerSerializationStrategy implements SerializationStrategy<Integer> {
    private static final IntegerSerializationStrategy INSTANCE = new IntegerSerializationStrategy();

    public static IntegerSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private IntegerSerializationStrategy() {
    }

    @Override
    public void write(RandomAccessFile file, Integer object) throws IOException {
        file.writeInt(object);
    }

    @Override
    public Integer read(RandomAccessFile file) throws IOException {
        return file.readInt();
    }
}
