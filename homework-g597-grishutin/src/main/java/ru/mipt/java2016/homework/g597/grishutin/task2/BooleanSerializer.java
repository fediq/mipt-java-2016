package ru.mipt.java2016.homework.g597.grishutin.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class BooleanSerializer implements SerializationStrategy<Boolean> {
    private static final BooleanSerializer INSTANCE = new BooleanSerializer();

    public static BooleanSerializer getInstance() {
        return INSTANCE;
    }

    @Override
    public void serialize(Boolean value, DataOutput raf) throws IOException {
        raf.writeBoolean(value);
    }

    @Override
    public Boolean deserialize(DataInput raf) throws IOException {
        return raf.readBoolean();
    }

    @Override
    public Long bytesSize(Boolean value) {
        return (long) 1;
    }
}
