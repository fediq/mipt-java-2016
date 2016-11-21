package ru.mipt.java2016.homework.g595.manucharyan.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public class ConcreteStrategyDoubleRandomAccess implements SerializationStrategyRandomAccess<Double> {
    @Override
    public void serializeToFile(Double value, RandomAccessFile output) throws IOException {
        output.writeDouble(value);
    }

    @Override
    public Double deserializeFromFile(RandomAccessFile input) throws IOException {
        return input.readDouble();
    }
}