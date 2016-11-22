package ru.mipt.java2016.homework.g597.spirin.task3;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Created by whoami on 11/21/16.
 */
class DateSerializer implements SerializationStrategy<Date> {

    private static class SingletonHolder {
        static final DateSerializer HOLDER_INSTANCE = new DateSerializer();
    }

    static DateSerializer getInstance() {
        return SingletonHolder.HOLDER_INSTANCE;
    }

    @Override
    public Date read(RandomAccessFile file) throws IOException {
        return new Date(file.readLong());
    }

    @Override
    public void write(RandomAccessFile file, Date object) throws IOException {
        file.writeLong(object.getTime());
    }
}
