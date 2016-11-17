package ru.mipt.java2016.homework.g595.popovkin.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Howl on 17.11.2016.
 */
public class IntegerParserRandomAccess implements ParserInterface<Integer> {

    @Override
    public void serialize(Integer arg, RandomAccessFile out) throws IOException {
        out.writeInt(arg);
    }

    @Override
    public Integer deserialize(RandomAccessFile in) throws IOException {
        return in.readInt();
    }
}
