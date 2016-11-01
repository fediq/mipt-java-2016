package ru.mipt.java2016.homework.g597.spirin.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by whoami on 10/30/16.
 */
class DoubleSerializer implements SerializationStrategy<Double> {

    private static class SingletonHolder {
        static final DoubleSerializer HOLDER_INSTANCE = new DoubleSerializer();
    }

    static DoubleSerializer getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    @Override
    public Double read(RandomAccessFile file) throws IOException {
        return file.readDouble();
    }

    @Override
    public void write(RandomAccessFile file, Double object) throws IOException {
        file.writeDouble(object);
    }
}
