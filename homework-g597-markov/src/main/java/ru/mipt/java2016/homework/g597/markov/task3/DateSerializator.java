package ru.mipt.java2016.homework.g597.markov.task3;

/**
 * Created by Alexander on 23.11.2016.
 */


import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public class DateSerializator implements SerializationStrategy<Date> {

    @Override
    public Date read(RandomAccessFile fileName) throws IOException {
        long date = fileName.readLong();
        return new Date(date);
    }

    @Override
    public void write(RandomAccessFile fileName, Date data) throws IOException {
        fileName.writeLong(data.getTime());
    }
}
