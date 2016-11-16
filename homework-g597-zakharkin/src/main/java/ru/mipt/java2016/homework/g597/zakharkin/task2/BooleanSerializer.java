package ru.mipt.java2016.homework.g597.zakharkin.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Serialization strategy for Boolean type
 *
 * @autor Ilya Zakharkin
 * @since 31.10.16.
 */
public class BooleanSerializer implements Serializer<Boolean> {
    private BooleanSerializer() {
    }

    private static class InstanceHolder {
        public static final BooleanSerializer INSTANCE = new BooleanSerializer();
    }

    public static BooleanSerializer getInstance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public void write(RandomAccessFile file, Boolean data) throws IOException {
        file.writeBoolean(data);
    }

    @Override
    public Boolean read(RandomAccessFile file) throws IOException {
        Boolean data = file.readBoolean();
        return data;
    }
}
