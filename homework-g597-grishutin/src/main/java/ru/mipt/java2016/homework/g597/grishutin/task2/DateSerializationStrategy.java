package ru.mipt.java2016.homework.g597.grishutin.task2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

class DateSerializationStrategy implements SerializationStrategy<Date> {
    private LongSerializationStrategy longSerializationStrategy = LongSerializationStrategy.getInstance();

    private static DateSerializationStrategy instance = new DateSerializationStrategy();

    public static DateSerializationStrategy getInstance() {
        return instance;
    }

    @Override
    public void serialize(Date date, RandomAccessFile raf) throws IOException {
        longSerializationStrategy.serialize(date.getTime(), raf);
    }

    @Override
    public Date deserialize(RandomAccessFile raf) throws IOException {
        return new Date(longSerializationStrategy.deserialize(raf));
    }
}
