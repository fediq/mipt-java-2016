package ru.mipt.java2016.homework.g594.gorelick.task3;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

public class DateSerializer implements Serializer<Date> {
    @Override
    public Date read(RandomAccessFile file, long position) throws IOException {
        file.seek(position);
        Date date = new Date(file.readLong());
        return date;
    }

    @Override
    public void write(RandomAccessFile file, Date object, long position) throws IOException {
        file.seek(position);
        file.writeLong(object.getTime());
    }
}
