package ru.mipt.java2016.homework.g595.nosareva.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by maria on 19.11.16.
 */
public class SerializerForDouble implements Serializer<Double> {

    @Override
    public void serializeToStream(Double value, DataOutput outStream) throws IOException {
        outStream.writeDouble(value);
    }

    @Override
    public Double deserializeFromStream(DataInput inputStream) throws IOException {
        return inputStream.readDouble();
    }
}
