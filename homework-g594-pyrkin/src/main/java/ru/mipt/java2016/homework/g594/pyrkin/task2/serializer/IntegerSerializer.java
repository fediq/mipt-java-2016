package ru.mipt.java2016.homework.g594.pyrkin.task2.serializer;

import java.nio.ByteBuffer;

/**
 * IntegerSerializer
 * Created by randan on 10/30/16.
 */
public class IntegerSerializer implements SerializerInterface<Integer> {

    @Override
    public int sizeOfSerialize(Integer object) {
        return 4;
    }

    @Override
    public ByteBuffer serialize(Integer object) {
        ByteBuffer resultBuffer = ByteBuffer.allocate(this.sizeOfSerialize(object));
        resultBuffer.putInt(object);
        return resultBuffer;
    }

    @Override
    public Integer deserialize(ByteBuffer inputBuffer) {
        return inputBuffer.getInt();
    }
}
