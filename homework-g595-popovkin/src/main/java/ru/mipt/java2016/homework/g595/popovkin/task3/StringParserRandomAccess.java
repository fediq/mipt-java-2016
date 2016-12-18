package ru.mipt.java2016.homework.g595.popovkin.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Howl on 17.11.2016.
 */
public class StringParserRandomAccess implements ParserInterface<String> {

    @Override
    public void serialize(String arg, RandomAccessFile out) throws IOException {
        out.writeUTF(arg);
    }

    @Override
    public String deserialize(RandomAccessFile in) throws IOException {
        return in.readUTF();
    }
}
