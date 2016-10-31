package ru.mipt.java2016.homework.g594.vishnyakova.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Nina on 27.10.16.
 */
public class DoubleSerializationStrategy extends SerializationStrategy<Double> {

    @Override
    public Double read(DataInputStream rd) throws IOException {
        return rd.readDouble();
    }

    @Override
    public void write(DataOutputStream wr, Double obj) throws IOException {
        wr.writeDouble(obj);
    }
}
