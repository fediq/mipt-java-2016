package ru.mipt.java2016.homework.g595.popovkin.task2;

import java.io.*;

/**
 * Created by Howl on 30.10.2016.
 */
public class BooleanParser implements ItemParser<Boolean> {

    @Override
    public void serialize(Boolean arg, OutputStream out) throws IOException {
        out.write((arg ? 1 : 0));
    }

    @Override
    public Boolean deserialize(InputStream in) throws IOException {
        return in.read() == 1;
    }
}