package ru.mipt.java2016.homework.g597.smirnova.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Elena Smirnova on 21.11.2016.
 */
public class IntegerSerializationStrategy implements SerializationStrategy<Integer> {
    @Override
    public void writeToStream(DataOutput s, Integer value) throws IOException {
        s.writeInt(value);
    }

    @Override
    public Integer readFromStream(DataInput s) throws IOException {
        return s.readInt();
    }
}
