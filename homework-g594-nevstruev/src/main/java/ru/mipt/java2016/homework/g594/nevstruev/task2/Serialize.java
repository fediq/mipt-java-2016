package ru.mipt.java2016.homework.g594.nevstruev.task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Владислав on 30.10.2016.
 */
public interface Serialize<T> {

    T read(BufferedReader input) throws IOException;

    void write(PrintWriter output, T object);
}
