package ru.mipt.java2016.homework.g597.mashurin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IntegerIdentification extends Identification<Integer> {

    public static IntegerIdentification get() {
        return new IntegerIdentification();
    }

    @Override
    public void write(DataOutputStream output, Integer object) throws IOException {
        output.writeInt(object);
    }

    @Override
    public Integer read(DataInputStream input) throws IOException {
        return input.readInt();
    }
}
