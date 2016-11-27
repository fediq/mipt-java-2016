package ru.mipt.java2016.homework.g597.grishutin.task2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

class DateSerializer implements SerializationStrategy<Date> {
    private LongSerializer longSerializer = LongSerializer.getInstance();

    private static DateSerializer instance = new DateSerializer();

    public static DateSerializer getInstance() {
        return instance;
    }

    @Override
    public void serialize(Date date, RandomAccessFile raf) throws IOException {
        longSerializer.serialize(date.getTime(), raf);
    }

    @Override
    public Date deserialize(RandomAccessFile raf) throws IOException {
        return new Date(longSerializer.deserialize(raf));
    }

    @Override
    public Long bytesSize(Date value) {
        return (long) Long.BYTES; // since we consider date as 1 long value
    }
}
