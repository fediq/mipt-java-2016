package ru.mipt.java2016.homework.g597.smirnova.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Elena Smirnova on 21.11.2016.
 */
public class DataSerializationStrategy implements SerializationStrategy<Date> {
    @Override
    public void writeToStream(DataOutput s, Date value) throws IOException {
        s.writeLong(value.getTime());
    }

    @Override
    public Date readFromStream(DataInput s) throws IOException {
        return new Date(s.readLong());
    }
}
