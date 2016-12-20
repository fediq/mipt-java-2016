package ru.mipt.java2016.homework.g594.anukhin.task3;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by clumpytuna on 29.10.16.
 */
public class DoubleSerializableImpl implements Serializable<Double> {

    @Override
    public void serialize(DataOutputStream output, Double obj) throws IOException {
        output.writeDouble(obj);
    }

    @Override
    public void serialize(RandomAccessFile output, Double obj) throws IOException {
        output.writeDouble(obj);
    }

    @Override
    public Double deserialize(DataInputStream input) throws IOException {
        return input.readDouble();
    }

    @Override
    public Double deserialize(RandomAccessFile input) throws IOException {
        return input.readDouble();
    }
}
