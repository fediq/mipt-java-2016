package ru.mipt.java2016.homework.g597.spirin.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by whoami on 11/28/16.
 */
public class LongSerializer implements SerializationStrategy<Long> {

    private static class SingletonHolder {
        static final LongSerializer HOLDER_INSTANCE = new LongSerializer();
    }

    static LongSerializer getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    @Override
    public Long read(DataInput file) throws IOException {
        return file.readLong();
    }

    @Override
    public void write(DataOutput file, Long object) throws IOException {
        file.writeLong(object);
    }
}
