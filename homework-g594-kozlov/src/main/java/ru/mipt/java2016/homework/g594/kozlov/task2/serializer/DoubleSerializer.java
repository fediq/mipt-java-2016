package ru.mipt.java2016.homework.g594.kozlov.task2.serializer;

import java.nio.ByteBuffer;

/**
 * Created by Anatoly on 25.10.2016.
 */
public class DoubleSerializer implements SerializerInterface<Double> {

    @Override
    public byte[] serialize(Double objToSerialize) {
        if (objToSerialize == null) {
            return null;
        }
        return ByteBuffer.allocate(8).putDouble(objToSerialize).array();
    }

    @Override
    public Double deserialize(byte[] inputString) {
        if (inputString == null) {
            return null;
        }
        return ByteBuffer.wrap(inputString).getDouble();
    }

    @Override
    public String getClassString() {
        return "Double";
    }
}
