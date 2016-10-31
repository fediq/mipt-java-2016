package ru.mipt.java2016.homework.g596.grebenshchikova.task2;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Created by liza on 31.10.16.
 */
public class DateSerializer implements MySerializerInterface<Date> {
    @Override
    public void write(RandomAccessFile file, Date object) throws IOException {
        file.writeLong(object.getTime());
    }

    @Override
    public Date read(RandomAccessFile file) throws IOException {
        return new Date(file.readLong());
    }

    private static final DateSerializer EXAMPLE = new DateSerializer();

    public static DateSerializer getExample() {
        return EXAMPLE;
    }

    private DateSerializer() {
    }
}

