package ru.mipt.java2016.homework.g596.kravets.task2;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SerializationStrategyDouble
        implements MySerialization<Double> {

    @Override
    public Double read(DataInputStream input) throws IOException {
        return input.readDouble();
    }

    @Override
    public void write(DataOutputStream output, Double data) throws IOException {
        output.writeDouble(data);
    }
}
