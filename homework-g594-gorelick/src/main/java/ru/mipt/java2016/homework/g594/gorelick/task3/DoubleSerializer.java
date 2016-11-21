package ru.mipt.java2016.homework.g594.gorelick.task2;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by alex on 10/31/16.
 */
public class DoubleSerializer implements Serializer<Double> {
    @Override
    public ByteBuffer serialize(Double object) throws IOException {
        ByteBuffer result = ByteBuffer.allocate(Double.BYTES);
        result.putDouble(object);
        return result;
    }

    @Override
    public Double deserialize(ByteBuffer array) throws IOException {
        return array.getDouble();
    }
}
