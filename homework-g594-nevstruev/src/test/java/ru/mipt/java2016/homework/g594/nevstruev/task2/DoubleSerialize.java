package ru.mipt.java2016.homework.g594.nevstruev.task2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Владислав on 31.10.2016.
 */
public class DoubleSerialize implements Serialize<Double> {
    @Override
    public Double read(BufferedReader input) throws IOException {
        return Double.parseDouble(input.readLine());
    }

    @Override
    public void write(PrintWriter output, Double object) {
        output.write(object.toString());
        output.write('\n');
    }
}
