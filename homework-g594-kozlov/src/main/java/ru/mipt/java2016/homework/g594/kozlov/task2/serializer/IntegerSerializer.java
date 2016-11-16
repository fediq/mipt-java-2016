package ru.mipt.java2016.homework.g594.kozlov.task2.serializer;

import java.nio.ByteBuffer;

/**
 * Created by Anatoly on 25.10.2016.
 */
public class IntegerSerializer implements SerializerInterface<Integer> {

    @Override
    public byte[] serialize(Integer objToSerialize) {
        if (objToSerialize == null) {
            return null;
        }
        return ByteBuffer.allocate(4).putInt(objToSerialize).array();
    }

    @Override
    public Integer deserialize(byte[] inputString) {
        if (inputString == null) {
            return null;
        }
        return ByteBuffer.wrap(inputString).getInt();
    }

    @Override
    public String getClassString() {
        return "Integer";
    }
}
