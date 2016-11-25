package ru.mipt.java2016.homework.g597.mashurin.task3;

import java.io.RandomAccessFile;
import java.io.IOException;

public class IntegerIdentification implements Identification<Integer> {

    public static IntegerIdentification get() {
        return new IntegerIdentification();
    }

    @Override
    public void write(RandomAccessFile output, Integer object) throws IOException {
        output.writeInt(object);
    }

    @Override
    public Integer read(RandomAccessFile input) throws IOException {
        return input.readInt();
    }
}
