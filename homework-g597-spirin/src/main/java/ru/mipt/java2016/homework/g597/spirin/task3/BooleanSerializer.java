package ru.mipt.java2016.homework.g597.spirin.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by whoami on 11/21/16.
 */
public class BooleanSerializer implements SerializationStrategy<Boolean> {

    private static class SingletonHolder {
        static final BooleanSerializer HOLDER_INSTANCE = new BooleanSerializer();
    }

    static BooleanSerializer getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    @Override
    public Boolean read(DataInput file) throws IOException {
        return file.readBoolean();
    }

    @Override
    public void write(DataOutput file, Boolean object) throws IOException {
        file.writeBoolean(object);
    }
}
