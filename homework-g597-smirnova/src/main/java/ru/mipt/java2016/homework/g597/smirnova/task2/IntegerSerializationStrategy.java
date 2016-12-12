package ru.mipt.java2016.homework.g597.smirnova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Elena Smirnova on 31.10.2016.
 */
public class IntegerSerializationStrategy implements SerializationStrategy<Integer> {
    @Override
    public void writeToStream(DataOutputStream s, Integer value) throws IOException {
        s.writeInt(value);
    }

    @Override
    public Integer readFromStream(DataInputStream s) throws IOException {
        return s.readInt();
    }
}
