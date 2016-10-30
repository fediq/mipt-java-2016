package ru.mipt.java2016.homework.g597.moiseev.task2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Стратегия сериализации для Date
 *
 * @author Fedor Moiseev
 * @since 26.10.2016
 */
public class DateSerializationStrategy implements SerializationStrategy<Date> {
    private static final DateSerializationStrategy INSTANCE = new DateSerializationStrategy();

    public static DateSerializationStrategy getInstance() {
        return INSTANCE;
    }

    private DateSerializationStrategy() {
    }

    @Override
    public void write(RandomAccessFile file, Date object) throws IOException {
        file.writeLong(object.getTime());
    }

    @Override
    public Date read(RandomAccessFile file) throws IOException {
        return new Date(file.readLong());
    }
}
