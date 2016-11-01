package ru.mipt.java2016.homework.g595.tkachenko.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Dmitry on 30/10/2016.
 */

public class IntSerialization extends Serialization<Integer> {
    @Override
    public Integer read(DataInputStream input) throws IOException {
        return input.readInt();
    }

    @Override
    public void write(DataOutputStream output, Integer x) throws IOException {
        output.writeInt(x);
    }
}
