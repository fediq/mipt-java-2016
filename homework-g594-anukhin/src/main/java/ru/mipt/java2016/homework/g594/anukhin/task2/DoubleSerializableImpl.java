package ru.mipt.java2016.homework.g594.anukhin.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by clumpytuna on 29.10.16.
 */
public class DoubleSerializableImpl implements Serializable<Double> {

    @Override
    public void serialize(DataOutputStream output, Double obj) throws IOException {
        output.writeDouble(obj);
    }

    @Override
    public Double deserialize(DataInputStream input) throws IOException {
        return input.readDouble();
    }
}
