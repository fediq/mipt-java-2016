package ru.mipt.java2016.homework.g597.zakharkin.task2;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Serialization strategy for Integer type
 *
 * @autor Ilya Zakharkin
 * @since 31.10.16.
 */
public class IntegerSerializer implements Serializer<Integer> {
    private IntegerSerializer() {
    }

    private static class InstanceHolder {
        public static final IntegerSerializer INSTANCE = new IntegerSerializer();
    }

    public static IntegerSerializer getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void write(DataOutput file, Integer data) throws IOException {
        file.writeInt(data);
    }

    @Override
    public Integer read(DataInput file) throws IOException {
        Integer data = file.readInt();
        return data;
    }
}
