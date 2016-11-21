package ru.mipt.java2016.homework.g595.manucharyan.task3;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public class ConcreteStrategyIntegerRandomAccess implements SerializationStrategyRandomAccess<Integer> {
    @Override
    public void serializeToFile(Integer value, RandomAccessFile output) throws IOException {
        output.writeInt(value);
    }

    @Override
    public Integer deserializeFromFile(RandomAccessFile input) throws IOException {
        return input.readInt();
    }
}
