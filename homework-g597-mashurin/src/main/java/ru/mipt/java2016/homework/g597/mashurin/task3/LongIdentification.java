package ru.mipt.java2016.homework.g597.mashurin.task3;

import java.io.RandomAccessFile;
import java.io.IOException;

public class LongIdentification implements Identification<Long> {

    public static LongIdentification get() {
        return new LongIdentification();
    }

    @Override
    public void write(RandomAccessFile output, Long object) throws IOException {
        output.writeLong(object);
    }

    @Override
    public Long read(RandomAccessFile input) throws IOException {
        return input.readLong();
    }
}

