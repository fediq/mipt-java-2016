package ru.mipt.java2016.homework.g597.markov.task2;

/**
 * Created by Alexander on 30.10.2016.
 */

import java.io.RandomAccessFile;
import java.io.IOException;
import java.util.Date;

public class DateSerializator implements SerializationStrategy<Date> {

    public DateSerializator() {}

    @Override
    public Date read(RandomAccessFile fileName) throws IOException{
        long date = fileName.readLong();
        return new Date(date);
    }

    @Override
    public void write(RandomAccessFile fileName, Date data) throws IOException{
        fileName.writeLong(data.getTime());
    }
}
