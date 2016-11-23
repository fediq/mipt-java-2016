package ru.mipt.java2016.homework.g596.kupriyanov.task3;

import java.io.RandomAccessFile;
import java.io.IOException;

/**
 * Created by Artem Kupriyanov on 29/10/2016.
 */

public class BooleanSerialization implements SerializationStrategy<Boolean> {

    @Override
    public void write(Boolean value, RandomAccessFile out) throws IOException {
        out.writeBoolean(value);
    }

    @Override
    public Boolean read(RandomAccessFile in) throws IOException {
        return in.readBoolean();
    }
}
