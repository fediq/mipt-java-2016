package ru.mipt.java2016.homework.g595.proskurin.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

public class StringSerializer implements MySerializer<String> {
    public void output(RandomAccessFile out, String val) throws IOException {
        out.writeUTF(val);
    }

    public String input(RandomAccessFile in) throws IOException {
        return in.readUTF();
    }
}
