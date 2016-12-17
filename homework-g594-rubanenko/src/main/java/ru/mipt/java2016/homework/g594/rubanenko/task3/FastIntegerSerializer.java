package ru.mipt.java2016.homework.g594.rubanenko.task3;

import java.nio.ByteBuffer;

/**
 * Created by king on 17.11.16.
 */

public class FastIntegerSerializer implements FastKeyValueStorageSerializer<Integer> {
    @Override
    public ByteBuffer serializeToStream(Integer value) {
        ByteBuffer serialized = ByteBuffer.allocate(serializeSize(value));
        serialized.putInt(value);
        return serialized;
    }

    @Override
    public Integer deserializeFromStream(ByteBuffer input) {
        return input.getInt();
    }

    @Override
    public int serializeSize(Integer value) {
        return Integer.SIZE / 8;
    }
}
