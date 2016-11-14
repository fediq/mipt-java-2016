package ru.mipt.java2016.homework.g595.popovkin.task2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Howl on 30.10.2016.
 */
public class BooleanParser implements ItemParser<Boolean> {

    @Override
    public void serialize(Boolean arg, FileOutputStream out) throws IOException {
        out.write((arg ? 1 : 0));
    }

    @Override
    public Boolean deserialize(FileInputStream in) throws IOException {
        return in.read() == 1;
    }
}