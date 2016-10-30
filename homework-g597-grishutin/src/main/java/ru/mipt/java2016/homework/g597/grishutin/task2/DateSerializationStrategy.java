package ru.mipt.java2016.homework.g597.grishutin.task2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public class DateSerializationStrategy implements SerializationStrategy<Date> {
    public static final DateSerializationStrategy INSTANCE = new DateSerializationStrategy();
    private LongSerializationStrategy longSerializationStrategy = LongSerializationStrategy.INSTANCE;

    @Override
    public void serialize(Date date, RandomAccessFile raf) throws IOException {
        longSerializationStrategy.serialize(date.getTime(), raf);
    }

    @Override
    public Date deserialize(RandomAccessFile raf) throws IOException {
        return new Date(longSerializationStrategy.deserialize(raf));
    }
}
