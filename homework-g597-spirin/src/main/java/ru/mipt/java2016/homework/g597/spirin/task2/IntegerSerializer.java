package ru.mipt.java2016.homework.g597.spirin.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by whoami on 10/30/16.
 */
class IntegerSerializer implements SerializationStrategy<Integer> {

    private static class SingletonHolder {
        static final IntegerSerializer HOLDER_INSTANCE = new IntegerSerializer();
    }

    static IntegerSerializer getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    @Override
    public Integer read(RandomAccessFile file) throws IOException {
        return file.readInt();
    }

    @Override
    public void write(RandomAccessFile file, Integer object) throws IOException {
        file.writeInt(object);
    }
}
