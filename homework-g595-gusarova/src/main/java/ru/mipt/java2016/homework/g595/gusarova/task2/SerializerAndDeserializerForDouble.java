package ru.mipt.java2016.homework.g595.gusarova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Дарья on 17.12.2016.
 */
public class SerializerAndDeserializerForDouble implements SerializerAndDeserializer<Double> {

    @Override
    public void serialize(Double data, DataOutput stream) throws IOException {
        stream.writeDouble(data);
    }

    @Override
    public Double deserialize(DataInput stream) throws IOException {
        return stream.readDouble();
    }
}