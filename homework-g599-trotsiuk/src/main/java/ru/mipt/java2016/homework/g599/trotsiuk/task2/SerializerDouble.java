package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import java.io.*;


public class SerializerDouble implements Serializer<Double> {

    @Override
    public void serializeWrite(Double value, DataOutputStream stream) throws IOException {
        stream.writeDouble(value);
    }

    @Override
    public Double deserializeRead(DataInputStream stream) throws IOException {
        return stream.readDouble();
    }
}
