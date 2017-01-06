package ru.mipt.java2016.homework.g595.ulyanin.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author ulyanin
 * @since 31.10.16.
 */
public class DoubleSerializer implements Serializer<Double> {
    private static DoubleSerializer ourInstance = new DoubleSerializer();

    public static DoubleSerializer getInstance() {
        return ourInstance;
    }

    private DoubleSerializer() { }

    @Override
    public void serialize(Double data, DataOutput dataOutputStream) throws IOException {
        dataOutputStream.writeDouble(data);
    }

    @Override
    public Double deserialize(DataInput dataInputStream) throws IOException {
        return dataInputStream.readDouble();
    }
}
