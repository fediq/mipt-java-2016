package ru.mipt.java2016.homework.g597.spirin.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by whoami on 11/21/16.
 */
public class IntegerSerializer implements SerializationStrategy<Integer> {

    private static class SingletonHolder {
        static final IntegerSerializer HOLDER_INSTANCE = new IntegerSerializer();
    }

    static IntegerSerializer getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    @Override
    public Integer read(DataInput file) throws IOException {
        return file.readInt();
    }

    @Override
    public void write(DataOutput file, Integer object) throws IOException {
        file.writeInt(object);
    }
}
