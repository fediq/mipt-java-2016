package ru.mipt.java2016.homework.g595.popovkin.task3;

import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * Created by Howl on 17.11.16.
 */

interface ParserInterface<Type> {
    void serialize(Type value, RandomAccessFile out)  throws IOException;

    Type deserialize(RandomAccessFile in) throws IOException;
}
