package ru.mipt.java2016.homework.g597.mashurin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StringIdentification extends Identification<String> {

    public static StringIdentification get() {
        return new StringIdentification();
    }

    @Override
    public void write(DataOutputStream output, String object) throws IOException {
        output.writeUTF(object);
    }

    @Override
    public String read(DataInputStream input) throws IOException {
        return input.readUTF();
    }
}
