package ru.mipt.java2016.homework.g595.manucharyan.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public class ConcreteStrategyIntegerRandomAccess implements SerializationStrategyRandomAccess<Integer> {
    @Override
    public void serializeToFile(Integer value, DataOutput output) throws IOException {
        output.writeInt(value);
    }

    @Override
    public Integer deserializeFromFile(DataInput input) throws IOException {
        return input.readInt();
    }
}
