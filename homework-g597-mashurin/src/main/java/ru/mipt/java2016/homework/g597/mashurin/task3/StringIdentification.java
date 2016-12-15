package ru.mipt.java2016.homework.g597.mashurin.task3;

import java.io.RandomAccessFile;
import java.io.IOException;

public class StringIdentification implements Identification<String> {

    public static StringIdentification get() {
        return new StringIdentification();
    }

    @Override
    public void write(RandomAccessFile output, String object) throws IOException {
        output.writeUTF(object);
    }

    @Override
    public String read(RandomAccessFile input) throws IOException {
        return input.readUTF();
    }
}
