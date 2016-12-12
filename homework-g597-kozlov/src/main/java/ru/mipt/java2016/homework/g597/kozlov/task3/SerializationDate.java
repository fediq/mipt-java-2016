package ru.mipt.java2016.homework.g597.kozlov.task3;

/**
 * Created by Alexander on 21.11.2016.
 */

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public class SerializationDate implements Serialization<Date> {

    @Override
    public Date read(RandomAccessFile file, long shift) throws IOException {
        file.seek(shift);
        Date date = new Date(file.readLong());
        return date;
    }

    @Override
    public void write(RandomAccessFile file, Date object, long shift) throws IOException {
        file.seek(shift);
        file.writeLong(object.getTime());
    }
}