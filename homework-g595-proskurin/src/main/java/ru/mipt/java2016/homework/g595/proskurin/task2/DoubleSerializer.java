package ru.mipt.java2016.homework.g595.proskurin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DoubleSerializer implements MySerializer<Double> {
    public void output(DataOutputStream out, Double val) throws IOException {
        out.writeDouble(val);
    }

    public Double input(DataInputStream in) throws IOException {
        return in.readDouble();
    }
}
