package ru.mipt.java2016.homework.g596.grebenshchikova.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by liza on 31.10.16.
 */
public class DoubleSerializer implements MySerializerInterface<Double> {
    @Override
    public void write(DataOutput output, Double object) throws IOException {
        output.writeDouble(object);
    }

    @Override
    public Double read(DataInput input) throws IOException {
        return input.readDouble();
    }

    private static final DoubleSerializer EXAMPLE = new DoubleSerializer();

    public static DoubleSerializer getExample() {
        return EXAMPLE;
    }

    private DoubleSerializer() {
    }

}
