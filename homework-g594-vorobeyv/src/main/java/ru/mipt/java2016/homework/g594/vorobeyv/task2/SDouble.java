package ru.mipt.java2016.homework.g594.vorobeyv.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Morell on 30.10.2016.
 */
public class SDouble extends Serializator<Double> {
    @Override
    public Double read(DataInputStream input) throws IOException {
        return input.readDouble();
    }

    @Override
    public void write(DataOutputStream output, Double value) throws IOException {
        output.writeDouble(value);
    }
}
