package ru.mipt.java2016.homework.g599.trotsiuk.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class SerializerDouble implements Serializer<Double> {

    @Override
    public void serializeWrite(Double value, DataOutput stream) throws IOException {
        stream.writeDouble(value);
    }

    @Override
    public Double deserializeRead(DataInput stream) throws IOException {
        return stream.readDouble();
    }
}
