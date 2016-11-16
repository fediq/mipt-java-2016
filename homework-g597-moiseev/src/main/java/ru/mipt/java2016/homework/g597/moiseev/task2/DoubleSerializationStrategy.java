package ru.mipt.java2016.homework.g597.moiseev.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Стратегия сериализации для Double
 *
 * @author Fedor Moiseev
 * @since 26.10.2016
 */

public class DoubleSerializationStrategy implements SerializationStrategy<Double> {
    private static final DoubleSerializationStrategy INSTANCE = new DoubleSerializationStrategy();

    public static DoubleSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private DoubleSerializationStrategy() {
    }

    @Override
    public void write(RandomAccessFile file, Double object) throws IOException {
        file.writeDouble(object);
    }

    @Override
    public Double read(RandomAccessFile file) throws IOException {
        return file.readDouble();
    }
}
