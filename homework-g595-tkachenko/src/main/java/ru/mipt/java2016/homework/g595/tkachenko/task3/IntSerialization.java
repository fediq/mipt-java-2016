package ru.mipt.java2016.homework.g595.tkachenko.task3;

import java.io.*;

/**
 * Created by Dmitry on 20/11/2016.
 */

public class IntSerialization extends Serialization<Integer> {
    @Override
    public Integer read(DataInput input) throws IOException {
        return input.readInt();
    }

    @Override
    public void write(DataOutput output, Integer x) throws IOException {
        output.writeInt(x);
    }
}
