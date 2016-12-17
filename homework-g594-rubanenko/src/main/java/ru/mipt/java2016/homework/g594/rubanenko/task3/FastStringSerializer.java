package ru.mipt.java2016.homework.g594.rubanenko.task3;

import java.nio.ByteBuffer;

/**
 * Created by king on 17.11.16.
 */

public class FastStringSerializer implements FastKeyValueStorageSerializer<String> {
    @Override
    public ByteBuffer serializeToStream(String value) {
        return ByteBuffer.wrap(value.getBytes());
    }

    @Override
    public String deserializeFromStream(ByteBuffer input) {
        return new String(input.array());
    }

    @Override
    public int serializeSize(String value) {
        return value.length();
    }
}
