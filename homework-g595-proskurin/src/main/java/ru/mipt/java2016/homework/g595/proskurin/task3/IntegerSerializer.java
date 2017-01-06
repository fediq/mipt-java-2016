package ru.mipt.java2016.homework.g595.proskurin.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

public class IntegerSerializer implements MySerializer<Integer> {
    public void output(RandomAccessFile out, Integer val) throws IOException {
        out.writeInt(val);
    }

    public Integer input(RandomAccessFile in) throws  IOException {
        return in.readInt();
    }
}
