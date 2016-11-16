package ru.mipt.java2016.homework.g597.kozlov.task2;

/**
 * Created by Alexander on 31.10.2016.
 */

import java.util.Date;
import java.io.IOException;
import java.io.RandomAccessFile;

public class SerializationDate implements Serialization<Date> {

    @Override
    public Date read(RandomAccessFile file) throws IOException {
        Date date = new Date(file.readLong());
        return date;
    }

    @Override
    public void write(RandomAccessFile file, Date object) throws IOException {
        file.writeLong(object.getTime());
    }
}