package ru.mipt.java2016.homework.g594.gorelick.task2;

import java.nio.ByteBuffer;
/**
 * Created by alex on 10/31/16.
 */
public class IntegerSerializer implements Serializer<Integer> {
    @Override
    public ByteBuffer serialize(Integer object) {
        ByteBuffer result = ByteBuffer.allocate(Integer.BYTES);
        result.putInt(object);
        return result;
    }

    @Override
    public Integer deserialize(ByteBuffer array) {
        return array.getInt();
    }
}
