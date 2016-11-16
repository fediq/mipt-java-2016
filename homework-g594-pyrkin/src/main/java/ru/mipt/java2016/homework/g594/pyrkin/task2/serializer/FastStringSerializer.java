package ru.mipt.java2016.homework.g594.pyrkin.task2.serializer;

import java.nio.ByteBuffer;

/**
 * Created by randan on 11/16/16.
 */
public class FastStringSerializer implements SerializerInterface<String> {
    @Override
    public int sizeOfSerialize(String object) {
        return object.length();
    }

    @Override
    public ByteBuffer serialize(String object) {
        ByteBuffer resultBuffer = ByteBuffer.allocate(sizeOfSerialize(object));
        resultBuffer.put(object.getBytes());
        return resultBuffer;
    }

    @Override
    public String deserialize(ByteBuffer inputBuffer) {
        return new String(inputBuffer.array());
    }
}
