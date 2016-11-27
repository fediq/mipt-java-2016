package ru.mipt.java2016.homework.g595.manucharyan.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public class ConcreteStrategyDoubleRandomAccess implements SerializationStrategyRandomAccess<Double> {
    @Override
    public void serializeToFile(Double value, DataOutput output) throws IOException {
        output.writeDouble(value);
    }

    @Override
    public Double deserializeFromFile(DataInput input) throws IOException {
        return input.readDouble();
    }
}