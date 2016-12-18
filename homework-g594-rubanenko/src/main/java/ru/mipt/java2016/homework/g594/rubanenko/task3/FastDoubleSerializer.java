package ru.mipt.java2016.homework.g594.rubanenko.task3;

import java.nio.ByteBuffer;

/**
 * Created by king on 17.11.16.
 */

public class FastDoubleSerializer implements FastKeyValueStorageSerializer<Double> {

    @Override
    public ByteBuffer serializeToStream(Double value) {
        ByteBuffer serialized = ByteBuffer.allocate(serializeSize(value));
        serialized.putDouble(value);
        return serialized;
    }

    @Override
    public Double deserializeFromStream(ByteBuffer input) {
        return input.getDouble();
    }

    @Override
    public int serializeSize(Double value) {
        return Double.SIZE / 8;
    }
}
