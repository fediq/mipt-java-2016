package ru.mipt.java2016.homework.g595.tkachenko.task3;

import java.io.*;

/**
 * Created by Dmitry on 20/11/2016.
 */
public class BooleanSerialization extends Serialization<Boolean> {
    @Override
    public Boolean read(DataInput input) throws IOException {
        return input.readBoolean();
    }

    @Override
    public void write(DataOutput output, Boolean x) throws IOException {
        output.writeBoolean(x);
    }
}
