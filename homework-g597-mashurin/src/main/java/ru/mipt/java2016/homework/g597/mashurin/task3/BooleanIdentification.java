package ru.mipt.java2016.homework.g597.mashurin.task3;

import java.io.RandomAccessFile;
import java.io.IOException;

public class BooleanIdentification implements Identification<Boolean> {

    public static BooleanIdentification get() {
        return new BooleanIdentification();
    }

    @Override
    public void write(RandomAccessFile output, Boolean object) throws IOException {
        output.writeBoolean(object);
    }

    @Override
    public Boolean read(RandomAccessFile input) throws IOException {
        return input.readBoolean();
    }
}
