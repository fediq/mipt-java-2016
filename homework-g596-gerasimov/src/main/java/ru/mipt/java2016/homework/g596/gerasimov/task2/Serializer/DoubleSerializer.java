package ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer;

import java.nio.ByteBuffer;

/**
 * Created by geras-artem on 30.10.16.
 */

public class DoubleSerializer implements ISerializer<Double> {
    @Override
    public int sizeOfSerialization(Double object) {
        return Double.SIZE / 8;
    }

    @Override
    public ByteBuffer serialize(Double object) {
        ByteBuffer result = ByteBuffer.allocate(this.sizeOfSerialization(object));
        result.putDouble(object);
        return result;
    }

    @Override
    public Double deserialize(ByteBuffer code) {
        return code.getDouble();
    }
}
