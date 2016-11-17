package ru.mipt.java2016.homework.g595.popovkin.task2;

import java.io.*;

/**
 * Created by Howl on 30.10.2016.
 */
public interface ItemParser<T> {

    void serialize(T arg, OutputStream out) throws IOException;

    T deserialize(InputStream in) throws IOException;
}
