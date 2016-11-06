package ru.mipt.java2016.homework.g595.manucharyan.task2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @author Vardan Manucharyan
 * @since 30.10.16
 */
public class ConcreteStrategyInteger implements SerializationStrategy<Integer> {
    @Override
    public void serializeToStream(Integer value, DataOutputStream stream) throws IOException {
        stream.writeInt(value);
    }

    @Override
    public Integer deserializeFromStream(DataInputStream stream) throws IOException {
        return stream.readInt();
    }
}
