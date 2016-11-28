package ru.mipt.java2016.homework.g597.spirin.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by whoami on 11/21/16.
 */
public class DoubleSerializer implements SerializationStrategy<Double> {

    private static class SingletonHolder {
        static final DoubleSerializer HOLDER_INSTANCE = new DoubleSerializer();
    }

    static DoubleSerializer getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    @Override
    public Double read(DataInput file) throws IOException {
        return file.readDouble();
    }

    @Override
    public void write(DataOutput file, Double object) throws IOException {
        file.writeDouble(object);
    }
}
