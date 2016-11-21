package ru.mipt.java2016.homework.g594.pyrkin.task2.serializer;

import java.nio.ByteBuffer;

/**
 * IntegerSerializer
 * Created by randan on 10/30/16.
 */
public class IntegerSerializer implements SerializerInterface<Integer> {

    private static final int SIZE = Integer.SIZE / 8;

    @Override
    public int sizeOfSerialize(Integer object) {
        return SIZE;
    }

    @Override
    public ByteBuffer serialize(Integer object) {
        ByteBuffer resultBuffer = ByteBuffer.allocate(sizeOfSerialize(object));
        resultBuffer.putInt(object);
        return resultBuffer;
    }

    @Override
    public Integer deserialize(ByteBuffer inputBuffer) {
        return inputBuffer.getInt();
    }
}
