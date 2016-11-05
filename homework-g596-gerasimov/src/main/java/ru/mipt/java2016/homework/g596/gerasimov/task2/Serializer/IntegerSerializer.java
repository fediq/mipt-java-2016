package ru.mipt.java2016.homework.g596.gerasimov.task2.Serializer;

import java.nio.ByteBuffer;

/**
 * Created by geras-artem on 30.10.16.
 */

public class IntegerSerializer implements ISerializer<Integer> {
    @Override
    public int sizeOfSerialization(Integer object) {
        return Integer.SIZE / 8;
    }

    @Override
    public ByteBuffer serialize(Integer object) {
        ByteBuffer result = ByteBuffer.allocate(this.sizeOfSerialization(object));
        result.putInt(object);
        return result;
    }

    @Override
    public Integer deserialize(ByteBuffer code) {
        return code.getInt();
    }
}
