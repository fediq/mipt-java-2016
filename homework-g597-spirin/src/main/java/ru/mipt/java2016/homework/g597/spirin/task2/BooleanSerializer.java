package ru.mipt.java2016.homework.g597.spirin.task2;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by whoami on 10/30/16.
 */
class BooleanSerializer implements SerializationStrategy<Boolean> {

    private static class SingletonHolder {
        static final BooleanSerializer HOLDER_INSTANCE = new BooleanSerializer();
    }

    static BooleanSerializer getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    @Override
    public Boolean read(RandomAccessFile file) throws IOException {
        return file.readBoolean();
    }

    @Override
    public void write(RandomAccessFile file, Boolean object) throws IOException {
        file.writeBoolean(object);
    }
}
