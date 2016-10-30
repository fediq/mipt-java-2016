package ru.mipt.java2016.homework.g596.kozlova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MyDoubleSerialization extends MySerialization<Double> {

    @Override
    public Double read(DataInputStream read_from_file) throws IOException {
        return read_from_file.readDouble();
    }

    @Override
    public void write(DataOutputStream write_to_file, Double object) throws IOException {
        write_to_file.writeDouble(object);
    }
}
