package ru.mipt.java2016.homework.g594.rubanenko.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by king on 30.10.16.
 */

/* ! Special class for serialization of integers */
public class MyIntegerSerializer implements MySerializer<Integer> {
    /* ! Write method */
    @Override
    public void serializeToStream(DataOutputStream output, Integer value) throws IOException {
        output.writeInt(value);
    }

    /* ! Read method */
    @Override
    public Integer deserializeFromStream(DataInputStream input) throws IOException {
        return input.readInt();
    }
}
