package ru.mipt.java2016.homework.g595.tkachenko.task3;

import java.io.*;

/**
 * Created by Dmitry on 20/11/2016.
 */
public class LongSerialization extends Serialization<Long> {
    @Override
    public Long read(DataInput input) throws IOException {
        return input.readLong();
    }

    @Override
    public void write(DataOutput output, Long x) throws IOException {
        output.writeLong(x);
    }
}
