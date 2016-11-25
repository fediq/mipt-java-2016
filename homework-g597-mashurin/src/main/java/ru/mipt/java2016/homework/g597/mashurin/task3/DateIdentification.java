package ru.mipt.java2016.homework.g597.mashurin.task3;

import java.util.Date;
import java.io.RandomAccessFile;
import java.io.IOException;

public class DateIdentification implements Identification<Date> {

    public static DateIdentification get() {
        return new DateIdentification();
    }

    @Override
    public void write(RandomAccessFile output, Date object) throws IOException {
        output.writeLong(object.getTime());
    }

    @Override
    public Date read(RandomAccessFile input) throws IOException {
        return new Date(input.readLong());
    }

}
