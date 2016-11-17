package ru.mipt.java2016.homework.g597.vasilyev.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by mizabrik on 30.10.16.
 */
public class IntegerSerializer implements Serializer<Integer> {
    @Override
    public void write(Integer value, DataOutput destination) throws IOException {
        destination.writeInt(value);
    }

    @Override
    public Integer read(DataInput source) throws IOException {
        return source.readInt();
    }
}
