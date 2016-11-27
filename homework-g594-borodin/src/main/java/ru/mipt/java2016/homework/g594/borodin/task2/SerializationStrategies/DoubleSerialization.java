package ru.mipt.java2016.homework.g594.borodin.task2.SerializationStrategies;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created by Maxim on 10/31/2016.
 */
public class DoubleSerialization implements  SerializationStrategy<Double> {

    @Override
    public void serialize(Double value, DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeDouble(value);
    }

    @Override
    public Double deserialize(DataInputStream dataInputStream) throws IOException {
        return dataInputStream.readDouble();
    }
}
