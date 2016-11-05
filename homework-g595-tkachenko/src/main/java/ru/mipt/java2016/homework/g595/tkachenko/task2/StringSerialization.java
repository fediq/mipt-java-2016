package ru.mipt.java2016.homework.g595.tkachenko.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Dmitry on 30/10/2016.
 */
public class StringSerialization extends Serialization<String> {
    @Override
    public String read(DataInputStream input) throws IOException {
        return readString(input);
    }

    @Override
    public void write(DataOutputStream output, String x) throws IOException {
        writeString(output, x);
    }
}
