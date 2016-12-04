package ru.mipt.java2016.homework.g597.grishutin.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

public class DateSerializer implements SerializationStrategy<Date> {
    private final LongSerializer longSerializer = LongSerializer.getInstance();

    private static final DateSerializer INSTANCE = new DateSerializer();

    public static DateSerializer getInstance() {
        return INSTANCE;
    }

    @Override
    public void serialize(Date date, DataOutput raf) throws IOException {
        longSerializer.serialize(date.getTime(), raf);
    }

    @Override
    public Date deserialize(DataInput raf) throws IOException {
        return new Date(longSerializer.deserialize(raf));
    }

    @Override
    public Long bytesSize(Date value) {
        return (long) Long.BYTES; // since we consider date as 1 long value
    }
}
