package ru.mipt.java2016.homework.g594.pyrkin.task2.serializer;

import java.nio.ByteBuffer;

/**
 * DoubleSerializer
 * Created by randan on 10/30/16.
 */
public class DoubleSerializer implements SerializerInterface<Double> {

    private static final int SIZE = Double.SIZE / 8;

    @Override
    public int sizeOfSerialize(Double object) {
        return SIZE;
    }

    @Override
    public ByteBuffer serialize(Double object) {
        ByteBuffer resultBuffer = ByteBuffer.allocate(sizeOfSerialize(object));
        resultBuffer.putDouble(object);
        return resultBuffer;
    }

    @Override
    public Double deserialize(ByteBuffer inputBuffer) {
        return inputBuffer.getDouble();
    }
}
