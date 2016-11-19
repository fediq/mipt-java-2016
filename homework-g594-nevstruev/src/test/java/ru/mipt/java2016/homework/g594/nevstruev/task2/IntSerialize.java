package ru.mipt.java2016.homework.g594.nevstruev.task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Владислав on 30.10.2016.
 */
public class IntSerialize implements Serialize<Integer> {
    @Override
    public Integer read(BufferedReader input) throws IOException {
        return Integer.parseInt(input.readLine());
    }

    @Override
    public void write(PrintWriter output, Integer object) {
        output.write(object.toString());
        output.write('\n');
    }

}
