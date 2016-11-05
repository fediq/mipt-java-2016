package ru.mipt.java2016.homework.g597.mashurin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class BooleanIdentification extends Identification<Boolean> {

    public static BooleanIdentification get() {
        return new BooleanIdentification();
    }

    @Override
    public void write(DataOutputStream output, Boolean object) throws IOException {
        output.writeBoolean(object);
    }

    @Override
    public Boolean read(DataInputStream input) throws IOException {
        return input.readBoolean();
    }
}
