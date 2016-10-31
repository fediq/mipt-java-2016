package ru.mipt.java2016.homework.g596.kozlova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MyDoubleSerialization implements MySerialization<Double> {

    @Override
    public Double read(DataInputStream readFromFile) throws IOException {
        return readFromFile.readDouble();
    }

    @Override
    public void write(DataOutputStream writeToFile, Double object) throws IOException {
        writeToFile.writeDouble(object);
    }
}
