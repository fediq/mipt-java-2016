package ru.mipt.java2016.homework.g597.smirnova.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Elena Smirnova on 21.11.2016.
 */
public class LongSerializationStrategy implements SerializationStrategy<Long> {
    @Override
    public void writeToStream(DataOutput s, Long value) throws IOException {
        s.writeLong(value);
    }

    @Override
    public Long readFromStream(DataInput s) throws IOException {
        return s.readLong();
    }
}
