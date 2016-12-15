package ru.mipt.java2016.homework.g597.moiseev.task3;

import ru.mipt.java2016.homework.g597.moiseev.task2.SerializationStrategy;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Стратегия сериализации для Long
 *
 * @author Fedor Moiseev
 * @since 19.11.2016
 */

public class LongSerializationStrategy implements SerializationStrategy<Long> {
    private static final LongSerializationStrategy INSTANCE = new LongSerializationStrategy();

    public static LongSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private LongSerializationStrategy() {
    }

    @Override
    public void write(RandomAccessFile file, Long object) throws IOException {
        file.writeLong(object);
    }

    @Override
    public Long read(RandomAccessFile file) throws IOException {
        return file.readLong();
    }
}
