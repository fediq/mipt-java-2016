package ru.mipt.java2016.homework.g594.nevstruev.task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Владислав on 31.10.2016.
 */
public class StringSerialize implements Serialize<String> {
    @Override
    public String read(BufferedReader input) throws IOException {
        return input.readLine();
    }

    @Override
    public void write(PrintWriter output, String object) {
        output.println(object);
    }
}
