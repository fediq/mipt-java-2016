package ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer;

import java.nio.ByteBuffer;

/**
 * Created by geras-artem on 17.11.16.
 */
public class StringSerializerV2 implements ISerializer<String> {
    @Override
    public int sizeOfSerialization(String object) {
        return object.length();
    }

    @Override
    public ByteBuffer serialize(String object) {
        return ByteBuffer.wrap(object.getBytes());
    }

    @Override
    public String deserialize(ByteBuffer code) {
        return new String(code.array());
    }
}
