package ru.mipt.java2016.homework.g595.popovkin.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Howl on 17.11.2016.
 */
public class LongParserRandomAccess implements ParserInterface<Long> {

    @Override
    public void serialize(Long arg, RandomAccessFile out) throws IOException {
        out.writeLong(arg);
    }

    @Override
    public Long deserialize(RandomAccessFile in) throws IOException {
        return in.readLong();
    }
}
