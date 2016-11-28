package ru.mipt.java2016.homework.g597.spirin.task3;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Date;

/**
 * Created by whoami on 11/21/16.
 */
public class DateSerializer implements SerializationStrategy<Date> {

    private static class SingletonHolder {
        static final DateSerializer HOLDER_INSTANCE = new DateSerializer();
    }

    static DateSerializer getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    @Override
    public Date read(DataInput file) throws IOException {
        return new Date(file.readLong());
    }

    @Override
    public void write(DataOutput file, Date object) throws IOException {
        file.writeLong(object.getTime());
    }
}
