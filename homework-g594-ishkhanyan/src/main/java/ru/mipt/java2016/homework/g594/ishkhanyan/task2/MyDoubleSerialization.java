package ru.mipt.java2016.homework.g594.ishkhanyan.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by ${Semien} on ${30.10.16}.
 */
public class MyDoubleSerialization implements MySerialization<Double> {
    @Override
    public void writeToFile(Double object, DataOutputStream file) throws IOException {
        file.writeDouble(object);
    }

    @Override
    public Double readFromFile(DataInputStream file) throws IOException {
        return file.readDouble();
    }
}
