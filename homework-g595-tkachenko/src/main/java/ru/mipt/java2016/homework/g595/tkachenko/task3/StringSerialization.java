package ru.mipt.java2016.homework.g595.tkachenko.task3;

import java.io.*;

/**
 * Created by Dmitry on 20/11/2016.
 */

public class StringSerialization extends Serialization<String> {

    @Override
    public String read(DataInput input) throws IOException {
        return input.readUTF();
    }

    @Override
    public void write(DataOutput output, String x) throws IOException {
        output.writeUTF(x);
    }
}
