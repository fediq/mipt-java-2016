package ru.mipt.java2016.homework.g597.zakharkin.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Serialization strategy for Double type
 *
 * @autor Ilya Zakharkin
 * @since 31.10.16.
 */
public class DoubleSerializer implements Serializer<Double> {
    private DoubleSerializer() {
    }

    private static class InstanceHolder {
        public static final DoubleSerializer INSTANCE = new DoubleSerializer();
    }

    public static DoubleSerializer getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void write(DataOutput file, Double data) throws IOException {
        file.writeDouble(data);
    }

    @Override
    public Double read(DataInput file) throws IOException {
        Double data = file.readDouble();
        return data;
    }
}
