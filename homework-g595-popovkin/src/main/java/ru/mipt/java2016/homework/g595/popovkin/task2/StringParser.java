package ru.mipt.java2016.homework.g595.popovkin.task2;

import java.io.*;

/**
 * Created by Howl on 30.10.2016.
 */

public class StringParser implements ItemParser<String> {

    @Override
    public void serialize(String arg, OutputStream out) throws IOException {
        IntegerParser tmp = new IntegerParser();
        tmp.serialize(arg.length(), out);
        byte[] buffer = arg.getBytes();
        out.write(buffer);
    }

    @Override
    public String deserialize(InputStream in) throws IOException {
        IntegerParser tmp = new IntegerParser();
        int len = tmp.deserialize(in);
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < len; ++i) {
            buffer.append((char) in.read());
        }
        return buffer.toString();
    }
}