package ru.mipt.java2016.homework.g595.manucharyan.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public class ConcreteStrategyDouble implements SerializationStrategy<Double> {
    @Override
    public void serializeToStream(Double value, DataOutputStream stream) throws IOException {
        stream.writeDouble(value);
    }

    @Override
    public Double deserializeFromStream(DataInputStream stream) throws IOException {
        return stream.readDouble();
    }
}