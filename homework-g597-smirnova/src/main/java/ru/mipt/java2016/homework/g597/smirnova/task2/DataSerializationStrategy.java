package ru.mipt.java2016.homework.g597.smirnova.task2;

import java.util.Date;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Elena Smirnova on 31.10.2016.
 */
public class DataSerializationStrategy implements SerializationStrategy<Date> {
    @Override
    public void writeToStream(DataOutputStream s, Date value) throws IOException {
        s.writeLong(value.getTime());
    }

    @Override
    public Date readFromStream(DataInputStream s) throws IOException {
        return new Date(s.readLong());
    }
}
