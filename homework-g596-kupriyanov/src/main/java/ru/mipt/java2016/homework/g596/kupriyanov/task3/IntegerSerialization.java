package ru.mipt.java2016.homework.g596.kupriyanov.task3;

import java.io.RandomAccessFile;
import java.io.IOException;

/**
 * Created by Artem Kupriyanov on 29/10/2016.
 */

public class IntegerSerialization implements SerializationStrategy<Integer> {
    @Override
    public void write(Integer value, RandomAccessFile out) throws IOException {
        out.writeInt(value);
    }

    @Override
    public Integer read(RandomAccessFile in) throws IOException {
        return in.readInt();
    }
}