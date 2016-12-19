package ru.mipt.java2016.homework.g595.popovkin.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Howl on 17.11.2016.
 */
public class DoubleParserRandomAccess implements ParserInterface<Double> {

    @Override
    public void serialize(Double arg, RandomAccessFile out) throws IOException {
        out.writeDouble(arg);
    }

    @Override
    public Double deserialize(RandomAccessFile in) throws IOException {
        return in.readDouble();
    }
}